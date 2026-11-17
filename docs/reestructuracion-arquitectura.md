# 🏛️ Propuesta de Reestructuración Arquitectónica y Alineación REST

Este documento analiza el estado real del repositorio `control-presencia` y define la reestructuración necesaria para garantizar una Arquitectura Hexagonal estricta, desacoplamiento absoluto del core respecto a frameworks web (Javalin, HTTP, Gson, Jackson) y persistencia, soporte de API REST con el patrón `Output`, validación defensiva en frontera y preparación para autenticación/autorización y proxies inversos (Nginx).

---

## 1. Diagnóstico del Estado Actual de Paquetes

### Situación actual en `employee-service`, `building-service`, `schedule-service`:
* **`architecture/model`**: Records de dominio puros e inmutables (`Employee`, `Building`, `Schedule`, etc.). **100% limpio**.
* **`architecture/ports`**: Interfaces de salida (`EmployeeReader`, `BuildingReader`, `ScheduleReader`). **100% limpio**.
* **`architecture/domain`**: 
  * `UseCase<P, R>` devuelve directamente el modelo de dominio o `Optional<T>`:
    ```java
    public class GetEmployeeUseCase implements UseCase<String, Optional<Employee>>
    ```
  * El caso de uso no expresa la semántica de la operación en el contexto de un servicio distribuido o de negocio (código de estado, resultado vs error).
* **`infrastructure/adapters`**:
  * Los Endpoints (`GetEmployeeEndPoint`, etc.) reciben directamente el `Context` de Javalin en el constructor o en la llamada:
    ```java
    public class GetEmployeeEndPoint implements EndPoint {
        private final Context context;
        ...
        public void execute() {
            String id = context.queryParam("id");
            ...
            context.status(404).result("Employee not found");
        }
    }
    ```
  * **Problema de acoplamiento:** Los adaptadores de entrada están fuertemente ligados a Javalin (`io.javalin.http.Context`). Si se cambia el framework web (por ejemplo, a HttpServer nativo de Java o Spark/Helidon) o se testea sin simular Javalin, el código queda acoplado.

---

## 2. Ajuste de Paquetes y Desacoplamiento Estricto

### Estructura de Paquetes Propuesta por Servicio:
```
software.ulpgc.controlpresencia/
│
├── architecture/                                  <-- Core de negocio (100% Java puro, sin librerías externas)
│   ├── domain/                                    <-- Objetos puros del negocio (records, enums, entidades y value objects)
│   ├── model/                                     <-- Cómo funciona el negocio (reglas puras, cálculos, políticas de dominio)
│   ├── usecases/                                  <-- Acciones del sistema (UseCase<Input, Output<T>> e implementaciones)
│   └── ports/
│       ├── in/                                    <-- Contratos de entrada
│       ├── processing/                            <-- Contratos de transformación / serialización
│       └── out/                                   <-- Puertos de salida: Repositories/Stores, EventPublishers, Readers
│
├── infrastructure/                                <-- Adaptadores y frontera técnica
│   ├── adapters/
│   │   ├── in/
│   │   │   ├── http/                              <-- Javalin Controllers / Endpoints, DTOs de entrada/salida
│   │   │   └── validation/                        <-- Validadores defensivos de frontera HTTP
│   │   └── out/
│   │       ├── persistence/                       <-- Adaptadores de base de datos / JSON Store
│   │       └── serialization/                     <-- Gson/Jackson serializadores
│   └── config/                                    <-- Opcional para propiedades
│
└── Main.java                                      <-- Composition Root (Pure DI manual)
```

---

## 3. Patrón `Output` para Respuestas REST en Casos de Uso

Para que el dominio comunique el resultado de una operación de forma agnóstica pero fácilmente traducible a códigos HTTP (`200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found`, `409 Conflict`, `500 Internal Error`), se define un contrato `Output<T>` genérico en `core/ports/in`:

```java
package software.ulpgc.controlpresencia.core.ports.in;

public record Output<T>(int status, T result, String error) {
    public static <T> Output<T> ok(T result) {
        return new Output<>(200, result, null);
    }
    public static <T> Output<T> created(T result) {
        return new Output<>(201, result, null);
    }
    public static <T> Output<T> badRequest(String error) {
        return new Output<>(400, null, error);
    }
    public static <T> Output<T> notFound(String error) {
        return new Output<>(404, null, error);
    }
    public static <T> Output<T> conflict(String error) {
        return new Output<>(409, null, error);
    }
}
```

### Contrato del Caso de Uso:
```java
public interface UseCase<I, O> {
    Output<O> execute(I input);
}
```

Así, el Caso de Uso contiene la regla de negocio y decide si algo es un `ok`, `notFound` o `badRequest`, mientras que el adaptador HTTP (EndPoint) solo mapea `output.status()` y serializa `output.result()` o `output.error()`.

---

## 4. Validación Defensiva en Frontera HTTP y Puertos de Serialización

1. **Frontera HTTP (Adaptador de entrada):**
   * Validador defensivo antes de llegar al caso de uso: comprobación de nulos, cadenas vacías, rangos válidos, formatos de fecha (`LocalDate`, `YearMonth`), IDs permitidos.
   * Si la entrada es inválida a nivel formato HTTP $\rightarrow$ `400 Bad Request` inmediato con mensaje descriptivo estructurado en JSON.
2. **Serialización desacoplada:**
   * Puerto `Serializer` / `Deserializer` en `ports/out` o adaptadores de serialización dedicados para no dispersar `Gson` o `Jackson` por el código de negocio.

---

## 5. Separación de Autenticación (`AuthService`/`RegisterService`) y Persistencia

1. **Desacoplamiento de Cuentas vs Entidades de Negocio:**
   * La entidad `Employee` es el perfil de presencia (horario, sede, rol operativo).
   * La entidad `UserAccount` / `Credential` pertenece a un contexto de autenticación (`AuthService`), que gestiona tokens/credenciales.
2. **Puertos de Persistencia:**
   * Sustituir los readers aislados por `EmployeeRepository` / `EmployeeStore` (con métodos `find`, `save`, `update`, `delete` para un CRUD completo si se requiere persistencia bidireccional).
3. **Compatibilidad con Proxy Inverso / Balanceador (Nginx):**
   * Configuración de Javalin para respetar cabeceras `X-Forwarded-For`, `X-Forwarded-Proto`, `X-Forwarded-Host`.
   * Enrutamiento prefijado (`/api/v1/...`) o por microservicio (`/employees`, `/buildings`, `/schedules`, `/reports`).
   * Endpoints de salud (`/health` o `/ready`) en cada microservicio para chequeos de balanceador.
