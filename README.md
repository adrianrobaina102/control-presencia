# control-presencia

Proyecto extraordinario de la asignatura Ingeniería del Software 2 (ULPGC). Sistema de control de presencia, gestión de horarios y registro de fichajes diseñado bajo arquitectura hexagonal y computación distribuida.

*(Para diagramas de flujo interactivos y guía rápida para desarrolladores, consultar [`docs/architecture.md`](docs/architecture.md)).*

## 1. Objetivo de la entrega

El propósito de este sistema es gestionar el registro de asistencia (entradas y salidas), la definición de distintos regímenes horarios y la consulta del cumplimiento horario o incidencias de los empleados. La arquitectura separa los módulos de captura de datos de los módulos de almacenamiento para garantizar la escalabilidad. Actualmente, el sistema está dividido en un productor de eventos (`tracking-service`) y un consumidor/almacenador (`storer`), comunicados asíncronamente mediante un broker de mensajería (ActiveMQ) y expuestos al exterior mediante servidores web ligeros con Javalin, aislando la lógica de dominio y la persistencia mediante el patrón de Puertos y Adaptadores.

## 2. Dependencias y entorno de desarrollo
- **JDK:** Oracle OpenJDK 27 (o versión compatible).
- **Gestor de construcción:** Apache Maven (módulos independientes `tracking-service/pom.xml` y `storer/pom.xml`).
- **Dependencias actuales:**
  - `Javalin (6.1.3)`: Servidor web HTTP ligero y sin reflexión para los endpoints.
  - `SLF4J Simple (2.0.9)`: Fachada de logging para Javalin y ActiveMQ.
  - `JUnit 5 (Jupiter 6.1.3)`: Para la ejecución de pruebas unitarias automatizadas.
  - `AssertJ Core (3.27.7)`: Librería de aserciones fluidas para los tests.
  - `ActiveMQ Client (5.18.3)`: Para la conexión JMS con el broker de mensajería.
  - `DataFaker (2.0.2)`: Generación de datos falsos realistas (nombres, emails, teléfonos) para simulaciones de carga masiva.

## 3. Compilación y ejecución

### Requisitos previos
Es necesario tener un servidor **Apache ActiveMQ** ejecutándose en local en el puerto por defecto (`tcp://localhost:61616`).

### Compilación del proyecto y ejecución de pruebas
Cada módulo cuenta con su propio ciclo de vida Maven independiente:
```bash
# Para el módulo tracking-service:
mvn test -f tracking-service/pom.xml

# Para el módulo storer:
mvn test -f storer/pom.xml
```

### Ejecución de los servicios (Backend Distribuido)
El sistema se ejecuta en dos procesos separados para demostrar el desacoplamiento:

1. **Arrancar el Almacenador/Consumidor (`storer`):** 
   Ejecutar la clase `Main` del módulo `storer`.
   - Drena de forma segura y secuencial los fichajes pendientes en la cola `fichajes.queue` con timeout de 2000 ms y confirmación manual (`CLIENT_ACKNOWLEDGE`), almacenándolos en el Data Lake (`store/`).
   - Mantiene activo un servidor HTTP en el **puerto 7071** para consultar el histórico de fichajes en el navegador.
2. **Arrancar el Productor/Capturador (`tracking-service`):**
   Ejecutar la clase `Main` del módulo `tracking-service`.
   - Mantiene activo un servidor HTTP en el **puerto 7070** para recibir fichajes vía HTTP y emitirlos como eventos individuales a la cola de ActiveMQ.

## 4. Estructura del proyecto
El proyecto se divide en módulos independientes siguiendo estrictamente la Arquitectura Hexagonal y la Inversión de Dependencias (Pure DI):

```text
├── docs/                                               # Documentación técnica y flujos del sistema
│   ├── architecture.md                                 # Flujos detallados con diagramas y mapa de componentes
│   └── agent.md                                        # Reglas y contexto operativo del asistente
├── tracking-service/                                           # Módulo Productor (Captura de fichajes)
│   ├── src/main/java/software/ulpgc/controlpresencia/
│   │   ├── application/                                # Core / Dominio (Sin dependencias externas)
│   │   │   ├── model/                                  # Entidades inmutables (Employee, Place, TrackingEvent...)
│   │   │   ├── ports/                                  # EventStore (Puerto de salida)
│   │   │   └── usecases/                               # UseCase<I, O>, RegisterTrackingEventUseCase
│   │   ├── infrastructure/                             # Infraestructura y Adaptadores
│   │   │   └── adapters/
│   │   │       ├── ActiveMQBrokerConnection.java       # Gestor AutoCloseable de conexión JMS
│   │   │       ├── ActiveMQEventStore.java             # Adaptador de envío JMS
│   │   │       └── endpoints/                          # EndPoint, RegisterTrackingEndPoint
│   │   ├── factories/                                  # TrackingEventFactory (Generador con DataFaker)
│   │   └── Main.java                                   # Composition Root en puerto 7070
│   ├── src/test/java/tests/                            # RegisterTrackingEventUseCaseTest, ActiveMQEventStoreTest
│   └── pom.xml
├── storer/                                             # Módulo Consumidor (Data Lake y Consulta)
│   ├── src/main/java/software/ulpgc/controlpresencia/
│   │   ├── application/                                # Core / Dominio (Sin dependencias externas)
│   │   │   ├── ports/                                  # EventReceiver, EventRepository, EventReader
│   │   │   └── usecases/                               # UseCase<I, O>, StoreEventUseCase, GetEventsUseCase
│   │   ├── infrastructure/                             # Infraestructura y Adaptadores
│   │   │   └── adapters/
│   │   │       ├── ActiveMQBrokerConnection.java       # Gestor AutoCloseable de conexión JMS
│   │   │       ├── ActiveMQEventReceiver.java          # Adaptador de lectura JMS con timeout seguro
│   │   │       ├── FileEventRepository.java            # Adaptador de escritura en Data Lake
│   │   │       ├── FileEventReader.java                # Adaptador de lectura del Data Lake
│   │   │       └── endpoints/                          # EndPoint, StoreEventEndPoint, GetEventsEndPoint
│   │   └── Main.java                                   # Composition Root en puerto 7071
│   ├── src/test/java/tests/                            # StoreEventUseCaseTest, GetEventsUseCaseTest, FileEventRepositoryTest, ActiveMQEventReceiverTest
│   └── pom.xml
└── store/                                              # Data Lake particionado cronológicamente
    └── YYYY/
        └── MM/
            └── YYYYMMDD.log
```

## 5. Arquitectura de sistemas
El diseño actual implementa un flujo asíncrono y desacoplado:
1. **Captura Web (tracking-service - Puerto 7070):** La petición HTTP llega a `RegisterTrackingEndPoint`, que invoca `RegisterTrackingEventUseCase` y serializa el evento a texto plano ligero (`ts;empId;place;type`).
2. **Transporte Asíncrono (ActiveMQ):** El mensaje se envía a la cola `fichajes.queue` a través de `ActiveMQBrokerConnection`.
3. **Consumo y Persistencia (storer - Puerto 7071):** `ActiveMQEventReceiver` lee de la cola con timeout de 2000 ms. Al recibir cada evento, `StoreEventUseCase` delega en `FileEventRepository` para guardarlo en disco y solo entonces se confirma (`CLIENT_ACKNOWLEDGE`), garantizando cero pérdida de eventos.
4. **Consulta Web (storer - Puerto 7071):** Al acceder a la raíz web, `GetEventsEndPoint` ejecuta `GetEventsUseCase`, leyendo mediante `FileEventReader` todos los fichajes persistidos en el Data Lake y mostrándolos en pantalla.

## 6. Arquitectura hexagonal del Backend

### Modelo de Dominio (`application.model`)
Objetos puros e inmutables implementados mediante `record` y `enum`:
- `Employee`: Representa al empleado (con constructor ligero por ID).
- `Contact`: Información de contacto asociada.
- `Place`: Ubicación o centro de trabajo (con constructor ligero por nombre).
- `TrackingEvent`: Evento de fichaje (con constructor por defecto a hora actual).
- `TrackingType`: Enumerado de tipo de movimiento (`In` / `Out`).

### Puertos (`application.ports`)
- `EventStore`: Contrato para el registro y envío de eventos de fichaje.
- `EventReceiver`: Contrato para la recepción de mensajes desde el broker.
- `EventRepository`: Contrato para el almacenamiento físico de eventos.
- `EventReader`: Contrato para la lectura de eventos desde el Data Lake.

### Casos de Uso (`application.usecases`)
Orquestan la lógica pura comunicándose exclusivamente mediante los puertos bajo la interfaz `UseCase<I, O>`:
- `RegisterTrackingEventUseCase`: Recibe un `TrackingEvent` y lo envía a través del puerto `EventStore`.
- `StoreEventUseCase`: Recibe el texto de un evento y lo persiste mediante `EventRepository`.
- `GetEventsUseCase`: Obtiene el listado completo de eventos persistidos a través de `EventReader`.

### Adaptadores (`infrastructure.adapters`)
- `ActiveMQBrokerConnection`: Encapsula la conexión JMS con `AutoCloseable`.
- `ActiveMQEventStore`: Adaptador productor JMS.
- `ActiveMQEventReceiver`: Adaptador consumidor con parada limpia tras 2000 ms sin mensajes.
- `FileEventRepository`: Escribe eventos en la estructura cronológica `store/YYYY/MM/YYYYMMDD.log`.
- `FileEventReader`: Explora recursivamente el Data Lake para recuperar todos los eventos registrados.
- `endpoints`: Adaptadores HTTP de Javalin con sobrecarga de métodos `execute(...)` para un solo nivel de abstracción.

## 7. Contrato de comunicación HTTP

### Módulo `tracking-service` (Puerto 7070)
- **Registrar Fichaje:**
  - **Método:** `POST` / `GET`
  - **Ruta:** `/events?employeeId={id}&placeId={place}&type={In|Out}`
  - **Respuesta:** `201 Created` — `"Tracking event tracking-serviceed"`

### Módulo `storer` (Puerto 7071)
- **Consultar Data Lake:**
  - **Método:** `GET`
  - **Ruta:** `/`
  - **Respuesta:** `200 OK` — Listado de todos los fichajes guardados en disco.
- **Registrar Evento Directo:**
  - **Método:** `POST` / `GET`
  - **Ruta:** `/events?event={texto}` o body con el texto del evento.
  - **Respuesta:** `201 Created` — `"Event stored successfully"`

## 8. Verificación y Pruebas Unitarias
El proyecto cuenta con cobertura de pruebas automatizadas con JUnit 5 y AssertJ en ambos módulos:
- `RegisterTrackingEventUseCaseTest`: Prueba unitaria pura del caso de uso de registro mediante mock en memoria de `EventStore`.
- `StoreEventUseCaseTest`: Prueba unitaria pura del guardado de eventos mediante mock de `EventRepository`.
- `GetEventsUseCaseTest`: Prueba unitaria pura de la recuperación de eventos mediante mock de `EventReader`.
- `FileEventRepositoryTest`: Prueba unitaria que valida la creación automática de directorios cronológicos y modo `APPEND` sobre archivos temporales.
- `ActiveMQEventStoreTest` y `ActiveMQEventReceiverTest`: Pruebas de integración sobre colas de mensajería usando sesiones inyectadas.
