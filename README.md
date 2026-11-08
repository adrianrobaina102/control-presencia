# control-presencia

Proyecto extraordinario de la asignatura Ingeniería del Software 2 (ULPGC). Sistema de control de presencia, gestión de horarios y registro de fichajes diseñado bajo arquitectura hexagonal y computación distribuida.

*(Para diagramas de flujo interactivos y guía rápida para desarrolladores, consultar [`docs/architecture.md`](docs/architecture.md)).*

## 1. Objetivo de la entrega

El propósito de este sistema es gestionar el registro de asistencia (entradas y salidas), la definición de distintos regímenes horarios y la consulta del cumplimiento horario o incidencias de los empleados. La arquitectura separa los módulos de captura de datos de los módulos de almacenamiento para garantizar la escalabilidad. Actualmente, el sistema está dividido en un productor de eventos (`tracking-service`) y un consumidor/almacenador (`storer`), comunicados asíncronamente mediante un broker de mensajería (ActiveMQ) y expuestos al exterior mediante servidores web ligeros con Javalin, aislando la lógica de dominio y la persistencia mediante el patrón de Puertos y Adaptadores.

## 2. Dependencias y entorno de desarrollo
- **JDK:** Oracle OpenJDK 27 (o versión compatible).
- **Gestor de construcción:** Apache Maven multimodular (`pom.xml` padre en raíz y módulos bajo `backend/`).
- **Dependencias actuales:**
  - `Javalin (6.1.3)`: Servidor web HTTP ligero y sin reflexión para los endpoints.
  - `SLF4J Simple (2.0.9)`: Fachada de logging para Javalin y ActiveMQ.
  - `JUnit 5 (Jupiter 6.1.3)`: Para la ejecución de pruebas unitarias automatizadas.
  - `AssertJ Core (3.27.7)`: Librería de aserciones fluidas para los tests.
  - `ActiveMQ Client (5.18.3)`: Para la conexión JMS con el broker de mensajería.
  - `Gson (2.11.0)`: Para la serialización y lectura de configuración JSON.
  - `DataFaker (2.0.2)`: Generación de datos falsos realistas (nombres, emails, teléfonos) para simulaciones de carga masiva.

## 3. Compilación y ejecución

### Requisitos previos
Es necesario tener un servidor **Apache ActiveMQ** ejecutándose en local en el puerto por defecto (`tcp://localhost:61616`).

### Compilación del proyecto y ejecución de pruebas
```bash
# Compilar todos los módulos del backend:
mvn test-compile

# Ejecutar las pruebas unitarias:
mvn test
```

### Ejecución de los servicios (Backend Distribuido)
El sistema se ejecuta en procesos independientes para demostrar el desacoplamiento:

1. **Arrancar el Almacenador/Consumidor (`storer`):** 
   Ejecutar la clase `Main` del módulo `backend/storer`.
   - Drena de forma segura y secuencial los fichajes pendientes en la cola `fichajes.queue` con timeout de 2000 ms y confirmación manual (`CLIENT_ACKNOWLEDGE`), almacenándolos en el Data Lake (`store/`).
   - Mantiene activo un servidor HTTP en el **puerto 7071** para consultar el histórico de fichajes en el navegador.
2. **Arrancar el Productor/Capturador (`tracking-service`):**
   Ejecutar la clase `Main` del módulo `backend/tracking-service`.
   - Despliega una interfaz gráfica de escritorio Swing interactiva (`SwingDeviceReader`) con botón *"¡He llegado!"* para simular un lector físico de presencia.
   - Mantiene activo un servidor HTTP en el **puerto 7070** para recibir fichajes vía HTTP y emitirlos como eventos individuales a la cola de ActiveMQ a través de `EventPublisher`.

## 4. Estructura del proyecto
El proyecto sigue estrictamente la Arquitectura Hexagonal y la Inversión de Dependencias (Pure DI):

```text
├── docs/                                               # Documentación técnica, rúbrica y reglas del agente
│   ├── architecture.md                                 # Flujos detallados con diagramas y mapa de componentes
│   └── agent.md                                        # Reglas y contexto operativo del asistente
├── frontend/                                           # Directorio raíz para las aplicaciones cliente de usuario
├── backend/                                            # Módulos del backend
│   ├── tracking-service/                               # Microservicio Productor (Captura y emisión de fichajes)
│   │   ├── src/main/java/software/ulpgc/controlpresencia/
│   │   │   ├── architecture/                           # Core / Núcleo
│   │   │   │   ├── model/                              # Entidades inmutables (Employee, Place, TrackingEvent, Configuration...)
│   │   │   │   ├── domain/                             # Casos de uso (RegisterPresenceUseCase, UseCase)
│   │   │   │   └── ports/                              # Contratos divididos en in, processing y out
│   │   │   │       ├── in/                             # DeviceReader, ConfigurationReader
│   │   │   │       ├── processing/                     # TrackingEventParser, TrackingEventSerializer
│   │   │   │       └── out/                            # EventPublisher
│   │   │   ├── infrastructure/                         # Infraestructura y Adaptadores
│   │   │   │   └── adapters/
│   │   │   │       ├── in/                             # SwingDeviceReader, JsonConfigurationReader, RegisterPresenceEndPoint
│   │   │   │       ├── processing/                     # CsvTrackingEventParser, CsvTrackingEventSerializer
│   │   │   │       └── out/                            # ActiveMQBrokerConnection, ActiveMQEventPublisher
│   │   │   ├── factories/                              # TrackingEventFactory (Generador con DataFaker)
│   │   │   └── Main.java                               # Composition Root en puerto 7070 y GUI Swing
│   │   └── src/test/java/tests/                        # RegisterPresenceUseCaseTest, JsonConfigurationReaderTest, ActiveMQEventPublisherTest
│   ├── storer/                                         # Microservicio Consumidor (Data Lake y Consulta)
│   │   ├── src/main/java/software/ulpgc/controlpresencia/
│   │   │   ├── architecture/                           # Core / Núcleo
│   │   │   │   ├── domain/                             # Casos de uso (StoreEventUseCase, GetEventsUseCase, UseCase)
│   │   │   │   └── ports/                              # EventWriter, EventReader, EventReceiver
│   │   │   ├── infrastructure/                         # Infraestructura y Adaptadores
│   │   │   │   └── adapters/
│   │   │   │       ├── ActiveMQBrokerConnection.java   # Gestor AutoCloseable de conexión JMS
│   │   │   │       ├── ActiveMQEventReceiver.java      # Adaptador de lectura JMS con timeout seguro
│   │   │   │       ├── FileEventWriter.java            # Adaptador de escritura en Data Lake
│   │   │   │       ├── FileEventReader.java            # Adaptador de lectura del Data Lake
│   │   │   │       └── endpoints/                      # StoreEventEndPoint, GetEventsEndPoint
│   │   │   └── Main.java                               # Composition Root en puerto 7071
│   │   └── src/test/java/tests/                        # FileEventWriterTest, GetEventsUseCaseTest, StoreEventUseCaseTest, ActiveMQEventReceiverTest
│   ├── employee-service/                               # Microservicio de empleados (en desarrollo)
│   ├── employee-admin/                                 # Panel de administración de empleados (en desarrollo)
│   ├── building-service/                               # Microservicio de edificios (en desarrollo)
│   └── building-admin/                                 # Panel de administración de edificios (en desarrollo)
└── store/                                              # Data Lake particionado cronológicamente
    └── YYYY/
        └── MM/
            └── YYYYMMDD.log
```

## 5. Arquitectura de sistemas
El diseño actual implementa un flujo asíncrono y desacoplado:
1. **Captura (tracking-service - Puerto 7070 y Swing GUI):** 
   - A través de la interfaz gráfica interactiva `SwingDeviceReader` o mediante peticiones HTTP a `RegisterPresenceEndPoint`.
   - Se invoca `RegisterPresenceUseCase`, que lee los datos del dispositivo, parsea el formato con `TrackingEventParser` y lo publica a ActiveMQ mediante `EventPublisher`.
2. **Transporte Asíncrono (ActiveMQ):** El mensaje se envía a la cola `fichajes.queue` a través de `ActiveMQEventPublisher`.
3. **Consumo y Persistencia (storer - Puerto 7071):** `ActiveMQEventReceiver` lee de la cola con timeout de 2000 ms. Al recibir cada evento, `StoreEventUseCase` delega en `FileEventWriter` para guardarlo en disco de forma particionada y solo entonces se confirma (`CLIENT_ACKNOWLEDGE`), garantizando cero pérdida de eventos.
4. **Consulta Web (storer - Puerto 7071):** Al acceder a la raíz web, `GetEventsEndPoint` ejecuta `GetEventsUseCase`, leyendo mediante `FileEventReader` todos los fichajes persistidos en el Data Lake y mostrándolos en pantalla.

## 6. Arquitectura hexagonal del Backend

### Modelo de Datos (`architecture.model`)
Objetos puros e inmutables implementados mediante `record` y `enum`:
- `Employee`: Representa al empleado (con constructor ligero por ID).
- `Contact`: Información de contacto asociada.
- `Place`: Ubicación o centro de trabajo (con constructor ligero por nombre).
- `TrackingEvent`: Evento de fichaje (con constructor por defecto a hora actual).
- `TrackingType`: Enumerado de tipo de movimiento (`In` / `Out`).
- `Configuration`: Configuración tipada del microservicio (broker URL, colas, etc.).

### Casos de Uso (`architecture.domain`)
Orquestan la lógica pura comunicándose exclusivamente mediante los puertos bajo la interfaz `UseCase<I, O>`:
- `RegisterPresenceUseCase`: Lee del dispositivo, parsea y emite el evento mediante el puerto `EventPublisher`.
- `StoreEventUseCase`: Recibe el texto de un evento y lo persiste mediante `EventWriter`.
- `GetEventsUseCase`: Obtiene el listado completo de eventos persistidos a través de `EventReader`.

### Puertos (`architecture.ports`)
- `EventPublisher`: Contrato para la publicación de eventos hacia el exterior.
- `EventReceiver`: Contrato para la recepción de mensajes desde el broker.
- `EventWriter`: Contrato para la escritura física de eventos en almacenamiento persistente.
- `EventReader`: Contrato para la lectura de eventos desde el Data Lake.
- `DeviceReader`: Contrato de lectura de datos crudos desde un dispositivo físico o simulado.
- `ConfigurationReader`: Contrato de carga de configuración de servicio.
- `TrackingEventParser` / `TrackingEventSerializer`: Contratos de transformación y serialización de eventos.

### Adaptadores (`infrastructure.adapters`)
- `ActiveMQBrokerConnection`: Encapsula la conexión JMS con `AutoCloseable`.
- `ActiveMQEventPublisher`: Adaptador productor JMS que publica eventos en colas ActiveMQ.
- `ActiveMQEventReceiver`: Adaptador consumidor con parada limpia tras 2000 ms sin mensajes.
- `SwingDeviceReader`: Interfaz gráfica interactiva de escritorio con Swing para simular fichajes.
- `FileEventWriter`: Escribe eventos en la estructura cronológica `store/YYYY/MM/YYYYMMDD.log`.
- `FileEventReader`: Explora recursivamente el Data Lake para recuperar todos los eventos registrados.
- `JsonConfigurationReader`: Carga la configuración del servicio desde un archivo JSON con Pure DI.
- `RegisterPresenceEndPoint`, `StoreEventEndPoint`, `GetEventsEndPoint`: Adaptadores HTTP de Javalin.

## 7. Contrato de comunicación HTTP

### Módulo `tracking-service` (Puerto 7070)
- **Registrar Fichaje:**
  - **Método:** `POST` / `GET`
  - **Ruta:** `/events?event={ts;empId;place;type}`
  - **Respuesta:** `201 Created` — `"Presence registered successfully"`

### Módulo `storer` (Puerto 7071)
- **Consultar Data Lake:**
  - **Método:** `GET`
  - **Ruta:** `/`
  - **Respuesta:** `200 OK` — Listado de todos los fichajes guardados en disco.
- **Registrar Evento Directo:**
  - **Método:** `POST` / `GET`
  - **Ruta:** `/events?event={texto}` o body con el texto del evento.
  - **Respuesta:** `201 Created` — `"Event stored successfully"`

### Módulo `employee-service` (Puerto 7072)
- **Listar Empleados:**
  - **Método:** `GET`
  - **Ruta:** `/employees`
  - **Respuesta:** `200 OK` — Array JSON con todos los empleados, roles, contactos y contratos vigentes.
- **Consultar Empleado por ID:**
  - **Método:** `GET`
  - **Ruta:** `/employees/{id}` o `/employees?id={id}`
  - **Respuesta:** `200 OK` — Ficha completa del empleado en formato JSON, o `404 Not Found`.

## 8. Verificación y Pruebas Unitarias
El proyecto cuenta con cobertura de pruebas automatizadas con JUnit 5 y AssertJ en todos sus módulos:
- **`tracking-service`:** `RegisterPresenceUseCaseTest`, `JsonConfigurationReaderTest`, `ActiveMQEventPublisherTest`.
- **`storer`:** `StoreEventUseCaseTest`, `GetEventsUseCaseTest`, `FileEventWriterTest`, `ActiveMQEventReceiverTest`.
- **`employee-service`:** `GetEmployeeUseCaseTest`, `ListEmployeesUseCaseTest`, `JsonEmployeeReaderTest`.
