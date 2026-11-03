# Contexto y Reglas del Agente — Proyecto: Control de Presencia (ULPGC)

## 1. Rol y Filosofía de Trabajo
Actúa como un compañero de equipo experto en Ingeniería del Software en Java (JDK 27 + Maven), Arquitectura Hexagonal y Sistemas Distribuidos.
- **Pedagogía y Ritmo:** No escupas bloques gigantes de código sin explicar el porqué. Ve paso a paso, validando que cada capa compile y que los tests pasen antes de avanzar a la siguiente.
- **Formato de Cambios:** Cuando modifiques archivos existentes que ya tienen bastante código, señala claramente con comentarios `// <-- AÑADIR AQUÍ` o `// <-- CAMBIO CLAVE` qué líneas exactas deben tocarse.
- **Idioma:** Explicaciones y conversación en **español**. Código, nombres de variables/clases/métodos y mensajes de **Git commits en inglés** (mensajes claros, descriptivos y directos en imperativo, **sin** usar prefijos de Conventional Commits como `feat:`, `fix:`, `refactor:`, etc.).
- **Commits Atómicos y Cortos:** Cada commit debe representar una única unidad lógica de cambio (una sola responsabilidad por commit). Los mensajes deben ser concisos y de **una sola línea directa**, sin cuerpos largos ni viñetas redundantes (ejemplo: `Add UseCase base interface and RegisterTrackingEventUseCase implementation`). Separar siempre código, pruebas y documentación en commits independientes.
- **Restricciones Académicas:** **PROHIBIDO** utilizar concurrencia compleja o hilos (`Thread`, `ExecutorService`, `Runnable`) para gestionar el ciclo de vida de los consumidores. Todo debe resolverse de forma secuencial y limpia (ej. mediante timeouts nativos de red/JMS).

---

## 2. Arquitectura y Estructura de Paquetes (Application vs Infrastructure)
El proyecto sigue **Arquitectura Hexagonal (Ports & Adapters)** estricta y principios **SOLID**. El código se divide en **dos grandes bloques o directorios principales aislados entre sí**, respetando siempre la regla de dependencia (`infrastructure` depende de `application`, pero `application` NO conoce a nadie del exterior):

1. **`application` (Arquitectura / El Core):**
   Es el corazón del sistema, donde viven las reglas de negocio. No sabe nada de bases de datos, colas de mensajes, ficheros, servidores web ni librerías externas (prohibido importar Gson, ActiveMQ, JMS, Javalin o DataFaker aquí). Contiene:
   - **`model` (`domain`):** Objetos puros e inmutables que representan el negocio (`record` y `enum`: `Employee`, `Contact`, `Place`, `TrackingEvent`, `TrackingType`, y futuros modelos de horarios como `Schedule` o `Shift`).
   - **`usecases`:** Las acciones concretas del sistema organizadas bajo el contrato base `UseCase<I, O>` (`RegisterTrackingEventUseCase`, `StoreEventUseCase`, `GetEventsUseCase`). Son los "directores de orquesta" que ejecutan la lógica comunicándose únicamente con las interfaces de `ports`.
   - **`ports`:** Las interfaces (`EventStore`, `EventReceiver`, `EventRepository`, `EventReader`). Actúan como "enchufes" que definen qué conexiones (entradas y salidas) admite el sistema, sin saber qué se conectará por fuera.

2. **`infrastructure` (Infraestructura / El Exterior):**
   Todo lo que se comunica con el mundo exterior (brokers de mensajería, sistema de ficheros, APIs HTTP, serializadores JSON, generadores de datos). Contiene:
   - **`adapters`:** El código concreto que implementa los `ports` o traduce peticiones externas (`ActiveMQEventStore`, `ActiveMQEventReceiver`, `FileEventRepository`, `FileEventReader`, `ActiveMQBrokerConnection`).
   - **`adapters.endpoints`:** Adaptadores de entrada web (`RegisterTrackingEndPoint`, `StoreEventEndPoint`, `GetEventsEndPoint`).

---

## 3. Reglas de Nomenclatura y Clean Code (ISP y SRP Extremos)
Se aplica de forma estricta el Principio de Responsabilidad Única (SRP) y el Principio de Segregación de Interfaces (ISP):
1. **Modelos (`application.model`):** Sustantivos puros del dominio en `PascalCase`, sin prefijos ni sufijos técnicos (`Employee`, `Contact`, `Place`, `TrackingEvent`, `TrackingType`). Se priorizan `record` y `enum` inmutables con constructores de conveniencia si se requieren.
2. **Puertos (`application.ports`):** Interfaces atómicas de una sola responsabilidad siguiendo el patrón **`<Entidad><Acción/Rol>`**:
   - Para entrada/salida de eventos: `EventStore`, `EventReceiver`, `EventRepository`, `EventReader`.
3. **Casos de Uso (`application.usecases`):** Clases con sufijo **`UseCase`** que implementan `UseCase<I, O>` (`RegisterTrackingEventUseCase`, `StoreEventUseCase`, `GetEventsUseCase`).
4. **Adaptadores (`infrastructure.adapters`):** Clases concretas nombradas estrictamente con el patrón **`<Tecnología/Formato><NombreDelPuerto>`**:
   - Ejemplos: `ActiveMQEventStore`, `ActiveMQEventReceiver`, `FileEventRepository`, `FileEventReader`.
   - Endpoints web en `infrastructure.adapters.endpoints` con sufijo **`EndPoint`** (`RegisterTrackingEndPoint`, `StoreEventEndPoint`, `GetEventsEndPoint`), utilizando sobrecarga de métodos `execute(...)` para dividir responsabilidades.
5. **Tests (`test`):** Nomenclatura espejo de la clase probada añadiendo el sufijo **`Test`** (ej. `RegisterTrackingEventUseCaseTest`, `StoreEventUseCaseTest`, `GetEventsUseCaseTest`, `FileEventRepositoryTest`). Se implementan con JUnit 5 y aserciones fluidas de AssertJ (`assertThat(...)`).
6. **Métodos:** Cortos, expresivos, con un único nivel de abstracción por método y sin comentarios innecesarios (el código se autoexplica).

---

## 4. Regla de Oro: Inversión de Dependencias (Pure DI) y Desacoplamiento Externo
Ninguna clase de negocio ni ningún adaptador debe instanciar por su cuenta dependencias externas pesadas o de terceros (como `new Gson()`, conexiones JMS de ActiveMQ, instancias de `new Faker()` o servidores Javalin).
- **Inyección por Constructor:** Todas las dependencias externas (`Gson`, `Session`/`Connection` de ActiveMQ, `Faker`, rutas `File`, `Context` de Javalin) deben ser recibidas a través del constructor del adaptador correspondiente (`private final`).
- **Composition Root (`Main.java`):** La clase `Main` de cada módulo es el único lugar de toda la aplicación autorizado para instanciar librerías externas, arrancar servidores/conexiones, ensamblar los adaptadores con los casos de uso y arrancar la ejecución.

---

## 5. Reglas del Sistema Distribuido (Register & Storer)
El sistema actual opera en dos módulos independientes comunicados por Apache ActiveMQ (`tcp://localhost:61616`, cola `fichajes.queue`):
- **Transmisión 1 a 1 en Tiempo Real:** Cada fichaje (`TrackingEvent`) simula un evento físico en tiempo real y se envía como un mensaje individual a la cola (no agrupar en listas por lotes).
- **Serialización Ligera:** Los eventos viajan por la cola como texto plano separado por punto y coma (`ts;employeeId;placeName;type`) para no acoplar los paquetes del modelo entre módulos.
- **Consumo Seguro y Apagado Limpio (`storer`):**
  - Se utiliza `Session.CLIENT_ACKNOWLEDGE` para confirmar el mensaje solo tras guardarlo en disco.
  - El adaptador `ActiveMQEventReceiver` utiliza `consumer.receive(2000)` con control de `null` para drenar la cola secuencialmente sin bloqueos indefinidos ni hilos.
- **Data Lake en Disco (`FileEventRepository` y `FileEventReader`):** Los eventos se almacenan y leen cronológicamente en: `store/YYYY/MM/YYYYMMDD.log`.

---

## 6. Reglas de Integración HTTP (Servidor Web con Javalin)
- **Aislamiento del Core (`application`):** Cero dependencias web o clases de Javalin en el dominio o casos de uso.
- **Adaptadores EndPoint (`infrastructure.adapters.endpoints`):** Reciben el caso de uso y el `Context` por constructor, resolviendo subproblemas con sobrecarga limpia de `execute(...)`.
- **Puertos de Servidor:**
  - `tracking-service`: Puerto **`7070`** (`POST /events`).
  - `storer`: Puerto **`7071`** (`GET /` para consultar el Data Lake, `POST /events` para almacenar).

---

## 7. Estado Actual del Proyecto y Siguiente Tarea Inmediata (TODO)
- **Completado:**
  - Modelo de dominio inmutable para fichajes (`Employee`, `Contact`, `Place`, `TrackingEvent`, `TrackingType`) con constructores ligeros.
  - Productor (`tracking-service`) enviando mensajes a ActiveMQ y sirviendo HTTP en puerto 7070.
  - Consumidor (`storer`) drenando la cola de ActiveMQ con timeout seguro de 2000ms y persistiendo en el Data Lake.
  - Separación total en bloques `application` e `infrastructure` con Pure DI en ambos módulos.
  - Integración completa de Casos de Uso (`RegisterTrackingEventUseCase`, `StoreEventUseCase`, `GetEventsUseCase`) y Endpoints Javalin en ambos módulos.
  - Adaptador `FileEventReader` para consultar el histórico de fichajes del Data Lake desde la web en `GET http://localhost:7071/`.
  - Cobertura completa de tests unitarios puros con JUnit 5 y AssertJ en ambos módulos.
- **Siguiente tarea inmediata (TODO):**
  1. **Refactorizar `TrackingEventFactory`:** Mover la generación simulada de eventos con DataFaker a la capa `infrastructure` e inyectar la instancia de `Faker` por constructor (Pure DI).
  2. **Modelado de horarios y turnos (Schedules, Shifts):** Incorporar los nuevos modelos de negocio para control de asistencia y verificación de horarios.
