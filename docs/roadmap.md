# 🗺️ Roadmap y Estrategia de Ejecución — Control de Presencia (v4.0.0)

Este documento detalla el estado actual, el orden de ataque paso a paso y la guía estratégica para abordar la implementación y reestructuración del sistema **Control de Presencia (ULPGC - IS2)**.

---

## 📊 Resumen Ejecutivo del Estado

```
[██████████████████████████░░░░░░░░] 65% Completado (Fases 1 a 5 cerradas)
```

- **Fases Completadas (5/11):** `tracking-service` (v1.0.0), `storer` (v2.0.0), `employee-service` y `building-service` (v4.0.0), `schedule-service`.
- **Fase Inmediata (Paso 1):** `reporting-service` (Fase 6).
- **Fase de Consolidación (Paso 2):** Reestructuración Arquitectónica Hexagonal & REST (`Output`, validación defensiva).
- **Fases Finales (Paso 3 en adelante):** Auth/Cuentas, Frontends, Nginx/Docker y Entrega.

---

## 🎯 Plan de Ataque y Orden de Ejecución Recomendado

¿Por dónde empezamos y cómo lo afrontamos sin romper nada? Este es el orden natural óptimo:

```
┌────────────────────────────────────────────────────────────────────────┐
│ PASO 1: Terminar la Funcionalidad Central (Fase 6: reporting-service)   │
│ -> 25% de la nota de la rúbrica. Cerramos la lógica analítica.         │
│ -> Nace directamente con la arquitectura limpia y desacoplada.         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ PASO 2: Estandarizar Contratos REST & Desacoplamiento (Fase 7)         │
│ -> Aplicar patrón Output<T> (200, 201, 400, 404).                      │
│ -> Validación defensiva en endpoints HTTP.                             │
│ -> Adaptadores EndPoint independientes del Context de Javalin.         │
│ -> Validar compilación y tests unitarios tras cada servicio.          │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ PASO 3: Cuentas, Autenticación y Persistencia Bidireccional (Fase 8)   │
│ -> AuthService / RegisterService desacoplado del Employee de fichaje.  │
│ -> Puertos Store/Repository para CRUD completo.                        │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ PASO 4: Frontends & UI de Administración y Fichaje (Fase 9)            │
│ -> Terminal de fichaje para empleados.                                 │
│ -> Dashboard administrativo con reportes y visualización.              │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ PASO 5: Despliegue con Docker y Nginx Reverse Proxy (Fase 10)          │
│ -> docker-compose unificado (ActiveMQ + microservicios + Nginx).       │
│ -> Enrutamiento único (/api/v1/...) y cabeceras X-Forwarded-*.         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ PASO 6: Auditoría Final contra Rúbrica y Tag v5.0.0 / Entrega          │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🧭 Tabla General de Fases y Microservicios

| Orden | Fase | Módulo / Componente | Puerto | Estado | Dificultad | Responsabilidad Clave |
| :---: | :--- | :--- | :---: | :---: | :---: | :--- |
| **1** | **Fase 1** | **`tracking-service`** | `7070` | ✅ **Completado** (`v1.0.0`) | Media | Ingesta de fichajes y publicación JMS a ActiveMQ. |
| **2** | **Fase 2** | **`storer`** | `7071` | ✅ **Completado** (`v2.0.0`) | Media | Persistencia append-only particionada en disco (`events/YYYY/MM/DD`). |
| **3** | **Fase 3** | **`employee-service`** | `7072` | ✅ **Completado** (`v4.0.0`) | Baja | Gestión y consulta de empleados, contratos y horarios. |
| **4** | **Fase 4** | **`building-service`** | `7073` | ✅ **Completado** (`v4.0.0`) | Baja | Consulta de sedes físicas y organizaciones. |
| **5** | **Fase 5** | **`schedule-service`** | `7074` | ✅ **Completado** | Baja | Regímenes horarios (continuo, partido, 24/7). |
| **6** | **Fase 6** | **`reporting-service`** | `7075` | 🔴 **SIGUIENTE (Paso 1)** | **Alta** | **Núcleo analítico**: Cruza eventos + contratos + horarios para balance e incidentes. |
| **7** | **Fase 7** | **Reestructuración REST** | - | 🟡 **Paso 2** | Media | Desacoplamiento Javalin, patrón `Output<T>`, validación defensiva en frontera. |
| **8** | **Fase 8** | **Cuentas & Repositorios**| - | 🟡 **Paso 3** | Media | `AuthService`/`RegisterService`, puertos `Store`/`Repository` bidireccionales. |
| **9** | **Fase 9** | **Frontends & UI** | - | 🔴 **Paso 4** | Media | Dashboard administrativo y terminal interactivo de fichaje. |
| **10**| **Fase 10**| **Docker, Nginx & DevOps**| `80/443` | 🔴 **Paso 5** | Media | Orquestación `docker-compose.yml`, proxy inverso Nginx y cabeceras proxy. |
| **11**| **Fase 11**| **Auditoría & Cierre** | - | 🔴 **Paso 6** | Baja | Chequeo 100% de la rúbrica ULPGC y tag final de release. |

---

## 🛠️ Detalle de Tareas: Paso 1 Inmediato (`reporting-service`)

### ¿Cómo afrontar `reporting-service`?
1. [x] **Diagrama UML:** Diagrama editable versionado en `docs/diagrams/reporting-service.drawio`.
2. [x] **Setup Maven:** Configurado `backend/reporting-service/pom.xml` con Javalin, Gson, JUnit 5, AssertJ.
3. [x] **Objetos de Negocio (`architecture.model`):** `IncidentType`, `Incident`, `DailyReport`, `MonthlyReport` (con Builders).
4. [ ] **Lógica de Dominio (`architecture.domain`):** Reglas puras y calculador de horas/incidencias que manejan los objetos de `model`.
5. [ ] **Puertos de Salida (`architecture.ports`):** `EventReader`, `EmployeeReader`, `ScheduleReader` (y futuro puerto `DatamartRepository` / `ReportStore`).
6. [ ] **Casos de Uso (`architecture.usecases`) con `Output`:**
   * `GenerateDailyReportUseCase` $\rightarrow$ devuelve `Output<DailyReport>` (`200 OK` si procesa, `404 Not Found` si no existe el empleado/horario, `400 Bad Request` si la fecha no es válida).
   * `GenerateMonthlyReportUseCase` $\rightarrow$ devuelve `Output<MonthlyReport>`.
7. [ ] **Adaptadores HTTP Asíncronos:** `HttpEventReader`, `HttpEmployeeReader`, `HttpScheduleReader` usando `HttpClient.sendAsync(...)`.
8. [ ] **Adaptadores HTTP de Entrada (Endpoints):** `GetDailyReportEndPoint`, `GetMonthlyReportEndPoint` con validación defensiva de query params (`employeeId`, `date`, `month`).
9. [ ] **Composition Root `Main.java`:** Pure DI en el puerto 7075.
10. [ ] **Tests Unitarios:** Suite de pruebas con mocks de los lectores para verificar cálculos de minutos, retrasos y ausencias.

---

## 🏗️ Alineación con Arquitectura de Sistema CQRS (Feeder $\rightarrow$ Datalake $\rightarrow$ Datamart)

Para cumplir al 100% con el patrón **CQRS** visto en clase:
```text
[Feeder (tracking-service)] ──(JMS)──▶ [ActiveMQ] ──▶ [Datalake (storer)]
                                                              │
                                                              ▼ (Proyector / Monitor)
                                                     [Datamart Analítico] ◀── [reporting-service]
```

| Componente | Estado Actual | Qué falta para completarlo |
| :--- | :--- | :--- |
| **Feeder (`tracking-service`)** | ✅ Implementado | Documentar/etiquetar su rol como *Feeder* (captura eventos y los publica en ActiveMQ). |
| **Datalake (`storer`)** | ✅ Implementado | Almacén inmutable en disco (`store/YYYY/MM/YYYYMMDD.log`) + lectura histórica `GET /`. |
| **Proyector & Datamart (`reporting-service`)** | 🔴 Pendiente | Persistir las proyecciones/reportes calculados en un **Datamart** propio (en lugar de recalcular al vuelo en cada petición) y alimentarlo desde los eventos del Datalake/Broker. |

---

## 🛠️ Detalle de Tareas: Paso 2 (Reestructuración REST & Desacoplamiento)

Una vez cerrado `reporting-service`, aplicamos de forma incremental en los servicios existentes:

| Subtarea | Qué hacemos | Cómo afrontarlo |
| :--- | :--- | :--- |
| **7.1 Contrato Común `Output<T>`** | Crear `Output<T>` (`status`, `result`, `error`) y actualizar `UseCase<I, O>`. | Se crea en un paquete compartido o se replica limpiamente en cada servicio para independencia total. |
| **7.2 Desacoplar Endpoints de Javalin** | En vez de inyectar `Context` en el constructor del EndPoint, el EndPoint solo define `void register(Javalin app)` o procesa un `Request` agnóstico. | Modificar `GetEmployeeEndPoint`, `GetBuildingEndPoint`, `GetScheduleEndPoint`. |
| **7.3 Validación Defensiva** | Validar rigurosamente que los IDs y parámetros no vengan vacíos ni con caracteres inválidos antes del caso de uso. | Si falla $\rightarrow$ retorna inmediatamente `400 Bad Request` con mensaje de error claro en JSON. |
| **7.4 Validación de Compilación & Tests** | Ejecutar `mvn test` tras actualizar cada microservicio. | Garantizar que no haya regresiones paso a paso. |

---

## 🛠️ Detalle de Tareas: Paso 3 (Cuentas, Auth & Repositorios)

| Subtarea | Qué hacemos | Cómo afrontarlo |
| :--- | :--- | :--- |
| **8.1 Desacoplar Cuenta de Empleado** | Crear el modelo `UserAccount` / `Credential` (usuario, rol, hash/token) independiente de `Employee` (datos de fichaje/contrato). | Permite cambiar el sistema de autenticación sin alterar la lógica de presencia. |
| **8.2 `AuthService` / `RegisterService`** | Casos de uso de login/registro (`LoginUseCase`, `RegisterUseCase`). | Se definen puertos de entrada y adaptadores HTTP correspondientes. |
| **8.3 Puertos `Store` / `Repository`** | Ampliar los readers de solo lectura a interfaces `Repository` con métodos `save`, `findById`, `findAll`. | Permite crear y modificar empleados/sedes por API (`POST`, `PUT`, `DELETE`). |
