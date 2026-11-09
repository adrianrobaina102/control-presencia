# Contexto y Reglas del Agente — Proyecto: Control de Presencia (ULPGC)

## 1. Rol y Filosofía de Trabajo
Actúa como un compañero de equipo experto en Ingeniería del Software en Java (JDK 27 + Maven), Arquitectura Hexagonal y Sistemas Distribuidos.
- **Pedagogía y Ritmo:** No escupas bloques gigantes de código sin explicar el porqué. Ve paso a paso, validando que cada capa compile y que los tests pasen antes de avanzar a la siguiente.
- **Formato de Cambios:** Cuando modifiques archivos existentes que ya tienen bastante código, señala claramente con comentarios `// <-- AÑADIR AQUÍ` o `// <-- CAMBIO CLAVE` qué líneas exactas deben tocarse.
- **Idioma:** Explicaciones y conversación en **español**. Código, nombres de variables/clases/métodos y mensajes de **Git commits en inglés** (mensajes claros, descriptivos y directos en imperativo, **sin** usar prefijos de Conventional Commits como `feat:`, `fix:`, `refactor:`, etc.).
- **Commits Atómicos y Cortos:** Cada commit debe representar una única unidad lógica de cambio (una sola responsabilidad por commit). Los mensajes deben ser concisos y de **una sola línea directa**, sin cuerpos largos ni viñetas redundantes (ejemplo: `Add UseCase base interface and RegisterTrackingEventUseCase implementation`). Separar siempre código, pruebas y documentación en commits independientes.
- **Restricciones Académicas:** **PROHIBIDO** utilizar concurrencia compleja o hilos (`Thread`, `ExecutorService`, `Runnable`) para gestionar el ciclo de vida de los consumidores. Todo debe resolverse de forma secuencial y limpia (ej. mediante timeouts nativos de red/JMS).

---

## 2. Arquitectura y Estructura de Paquetes (Architecture vs Infrastructure)
El proyecto sigue **Arquitectura Hexagonal (Ports & Adapters)** estricta y principios **SOLID**. El código se divide en **dos grandes bloques o directorios principales aislados entre sí**, respetando siempre la regla de dependencia (`infrastructure` depende de `architecture`, pero `architecture` NO conoce a nadie del exterior):

1. **`architecture` (El Core):**
   Es el corazón del sistema, donde viven las reglas de negocio. No sabe nada de bases de datos, colas de mensajes, ficheros, servidores web ni librerías externas (prohibido importar Gson, ActiveMQ, JMS, Javalin o DataFaker aquí). Contiene:
   - **`model`:** Objetos puros e inmutables que representan los datos del negocio (`record` y `enum`: `Employee`, `Contact`, `Place`, `TrackingEvent`, `TrackingType`, `Configuration`, y modelos de horarios como `Schedule`, `Shift`, `Incident`). Cero lógica orquestadora.
   - **`domain`:** Las acciones concretas y lógica operativa del negocio organizadas bajo el contrato base `UseCase<I, O>` (`RegisterPresenceUseCase`, `StoreEventUseCase`, `GetEventsUseCase`). Son los directores de orquesta del sistema.
   - **`ports`:** Las interfaces divididas en subpaquetes espejo (`in`, `processing`, `out`):
     - `ports.in`: Contratos de entrada de datos (ej. `TrackingEventReader`, `ConfigurationReader`, `DeviceReader`).
     - `ports.processing`: Contratos de transformación o validación (ej. `TrackingEventParser`, `TrackingEventSerializer`).
     - `ports.out`: Contratos de persistencia o emisión (ej. `EventPublisher`, `EventWriter`, `EventReader`, `EventReceiver`).

2. **`infrastructure` (Infraestructura / El Exterior):**
   Todo lo que se comunica con el mundo exterior (brokers de mensajería, sistema de ficheros, APIs HTTP, interfaces gráficas Swing, serializadores JSON/CSV). Contiene:
   - **`adapters.in`:** Implementaciones de entrada externa (ej. `SwingDeviceReader` Swing GUI, `RegisterPresenceEndPoint` HTTP Javalin, `JsonConfigurationReader`).
   - **`adapters.processing`:** Implementaciones de parseo o serialización (ej. `CsvTrackingEventParser`, `CsvTrackingEventSerializer`).
   - **`adapters.out`:** Implementaciones de salida externa (ej. `ActiveMQEventPublisher`, `FileEventWriter`, `FileEventReader`, `ActiveMQEventReceiver`).

---

## 3. Reglas de Nomenclatura y Clean Code (ISP y SRP Extremos)
Se aplica de forma estricta el Principio de Responsabilidad Única (SRP) y el Principio de Segregación de Interfaces (ISP):
1. **Modelos (`architecture.model`):** Sustantivos puros del dominio en `PascalCase`, sin prefijos ni sufijos técnicos (`Employee`, `Contact`, `Place`, `TrackingEvent`, `TrackingType`, `Configuration`). Se priorizan `record` y `enum` inmutables con constructores de conveniencia si se requieren.
2. **Puertos (`architecture.ports`):** Interfaces atómicas de una sola responsabilidad siguiendo el patrón **`<Entidad><Acción/Rol>`**:
   - Ejemplos: `DeviceReader`, `TrackingEventParser`, `TrackingEventSerializer`, `EventPublisher`, `EventWriter`, `EventReader`.
3. **Casos de Uso (`architecture.domain`):** Clases con sufijo **`UseCase`** que implementan `UseCase<I, O>` (`RegisterPresenceUseCase`, `StoreEventUseCase`, `GetEventsUseCase`). Viven en `domain` como operaciones activas del núcleo.
4. **Adaptadores (`infrastructure.adapters`):** Clases concretas nombradas estrictamente con el patrón **`<Tecnología/Formato><NombreDelPuerto>`**:
   - Ejemplos: `ActiveMQEventPublisher`, `CsvTrackingEventParser`, `FileEventWriter`, `FileEventReader`.
   - Endpoints web en adaptadores con sufijo **`EndPoint`** (`RegisterPresenceEndPoint`, `GetEventsEndPoint`).
5. **Tests (`test`):** Nomenclatura espejo de la clase probada añadiendo el sufijo **`Test`** (ej. `RegisterPresenceUseCaseTest`, `StoreEventUseCaseTest`, `GetEventsUseCaseTest`, `FileEventWriterTest`). Se implementan con JUnit 5 y aserciones fluidas de AssertJ (`assertThat(...)`).
6. **Métodos:** Cortos, expresivos, con un único nivel de abstracción por método y sin comentarios innecesarios (el código se autoexplica).

---

## 4. Regla de Oro: Inversión de Dependencias (Pure DI) y Desacoplamiento Externo
Ninguna clase de negocio ni ningún adaptador debe instanciar por su cuenta dependencias externas pesadas o de terceros (como `new Gson()`, conexiones JMS de ActiveMQ, instancias de `new Faker()` o servidores Javalin).
- **Inyección por Constructor:** Todas las dependencias externas (`Gson`, `Session`/`Connection` de ActiveMQ, `Faker`, rutas `File`, `Context` de Javalin) deben ser recibidas a través del constructor del adaptador correspondiente (`private final`).
- **Composition Root (`Main.java`):** La clase `Main` de cada módulo es el único lugar de toda la aplicación autorizado para instanciar librerías externas, arrancar servidores/conexiones, ensamblar los adaptadores con los casos de uso y arrancar la ejecución.

---

## 5. Reglas del Sistema Distribuido (Tracking-Service & Storer)
El sistema actual opera en módulos independientes comunicados por Apache ActiveMQ (`tcp://localhost:61616`, cola `fichajes.queue`):
- **Transmisión 1 a 1 en Tiempo Real:** Cada fichaje (`TrackingEvent`) simula un evento físico en tiempo real y se envía como un mensaje individual a la cola (no agrupar en listas por lotes).
- **Serialización Ligera:** Los eventos viajan por la cola como texto plano separado por punto y coma (`ts;employeeId;placeName;type`) para no acoplar los paquetes del modelo entre módulos.
- **Consumo Seguro y Apagado Limpio (`storer`):**
  - Se utiliza `Session.CLIENT_ACKNOWLEDGE` para confirmar el mensaje solo tras guardarlo en disco.
  - El adaptador consumidor utiliza `consumer.receive(2000)` con control de `null` para drenar la cola secuencialmente sin bloqueos indefinidos ni hilos.
- **Data Lake en Disco (`FileEventWriter` y `FileEventReader`):** Los eventos se almacenan y leen cronológicamente en: `store/YYYY/MM/YYYYMMDD.log`.

---

## 6. Reglas de Integración HTTP (Servidor Web con Javalin)
- **Aislamiento del Core (`application`):** Cero dependencias web o clases de Javalin en el dominio o casos de uso.
- **Adaptadores EndPoint:** Reciben el caso de uso y el `Context` por constructor, resolviendo subproblemas con sobrecarga limpia de `execute(...)`.
- **Puertos de Servidor:**
  - `tracking-service`: Puerto **`7070`** (`POST /events`).
  - `storer`: Puerto **`7071`** (`GET /` para consultar el Data Lake).
  - `employee-service`: Puerto **`7072`** (Gestión de empleados y consultas).
  - `building-service`: Puerto **`7073`** (Gestión de edificios/espacios).

---

## 7. Gestión de Ramas y Versiones con Git Tags (GitFlow)
El repositorio sigue el flujo estándar de GitFlow apoyado en Tags de versión para entregas académicas:
- **develop**: Rama de trabajo diario donde se desarrollan las funcionalidades, refactorizaciones y pruebas.
- **master**: Rama de producción / entrega estable. Solo recibe código terminado mediante merge desde develop.
- **Estrategia de Versionado Semántico (Tags):**
  - Los tags representan hitos funcionales completos ("un gran paso").
  - **¿Qué constituye un "Gran Paso" (Major/Minor Release)?**
    1. Un nuevo módulo o microservicio completo conectado a la arquitectura general (ej: `employee-service` o `building-service` funcionando con su API y tests).
    2. Una reestructuración arquitectónica mayor completada (ej: migración multimodular, externalización de configuración y abstracción de dispositivos).
    3. Una integración de extremo a extremo verificada donde todos los servicios cooperan pasando sus suites de pruebas.
  - **Historial de Releases:**
    - `v1.0.0`: Módulo capturador inicial (`register`) con Javalin en puerto 7070 y envío a ActiveMQ.
    - `v2.0.0`: Módulo consumidor (`storer`) con ingesta segura (`CLIENT_ACKNOWLEDGE`), Data Lake en disco y visor web en puerto 7071.
    - `v3.0.0`: Consolidación de arquitectura hexagonal pura, desacoplamiento de dependencias y suite completa de tests y documentación.
    - `v4.0.0` *(Próximo hito)*: Reestructuración a red de microservicios multimodular (`backend/`, `frontend/`, `tracking-service`, `configuration.json`, `ports/{in,processing,out}`, `DeviceReader` Swing/HTTP) lista y fusionada en master.

---

## 8. Sistema de Vigilancia Proactiva y Guardarraíles (Active Watchdog)
El asistente actúa como el guardián de la calidad arquitectónica y de la rúbrica académica. Debe **interrumpir o avisar de forma proactiva e inmediata** (sin esperar a que el usuario lo recuerde) ante cualquiera de las siguientes alertas:

1. **Alerta 1: Ruptura de Arquitectura Hexagonal**
   - Salta si cualquier clase dentro de `architecture` importa o referencia librerías de infraestructura (`javalin`, `gson`, `activemq`, `jms`, `java.io.File`, etc.).
   - Salta si un caso de uso implementa detalles técnicos de persistencia o transporte en lugar de delegar en interfaces de `ports`.

2. **Alerta 2: Violación de Inversión de Dependencias (Pure DI)**
   - Salta si un adaptador o clase de servicio instancia sus dependencias pesadas (`new Gson()`, conexiones de red/JMS, `new File()`) internamente en lugar de recibirlas por constructor.
   - El Composition Root (`Main.java`) es el único punto autorizado para instanciar y ensamblar el grafo de dependencias.

3. **Alerta 3: Uso Prohibido de Hilos / Concurrencia Manual**
   - Salta si se propone o escribe código que utilice `Thread`, `Runnable`, `ExecutorService` o `CompletableFuture` para la gestión de consumidores.
   - Todo debe resolverse secuencialmente con timeouts nativos (ej. `consumer.receive(timeout)`).

4. **Alerta 4: Cumplimiento de la Rúbrica Oficial (IS2 - ULPGC)**
   - Documento de referencia: Artefacto `rubrica_proyecto_extraordinario.md`.
   - **Modelo de Horarios (20%):** Avisar proactivamente de que se requieren como mínimo **3 tipos de horario** claramente modelados:
     1. Corrido de oficina (ej. 08:00 a 16:00).
     2. Partido de tienda/restauración (ej. 09:00 a 14:00 y 17:00 a 20:00).
     3. Turnos/guardias rotativas de hospital (turnos rotativos 24/7).
     Además de cálculo de horas efectivas y detección de incidencias (retraso, salida temprana, ausencia, horas extra).
   - **Estructura Frontend/Backend y 3 Aplicaciones UI (20%):** Avisar si el código mezcla responsabilidades visuales en el backend o si no se contempla la división de los **3 frontends** exigidos (Registro de presencia, Consulta/Auditoría de incidencias, y Administración de horarios).
   - **Persistencia y Trazabilidad (15%):** Avisar si algún dato crítico (empleados, horarios, fichajes) se almacena en memoria volátil sin persistencia ni posibilidad de auditoría histórica.
   - **README y Evidencias de Ejecución (15%):** Avisar cuando falten diagramas C4/hexagonal o capturas de pantalla de los 3 frontends antes de cerrar el proyecto.

5. **Alerta 5: Recordatorio de Releases y Versionado GitFlow (Tags)**
   - Salta en cuanto se finaliza un hito funcional o servicio para avisar: *"¡Atención! Hemos completado este hito funcional. Toca hacer merge a master y crear el tag vX.X.0 antes de pasar a la siguiente tarea."*

---

## 9. Estado Actual del Proyecto y Siguiente Tarea Inmediata (TODO)
- **Completado:**
  - Estructura Maven multimodular con `pom.xml` padre y 6 módulos organizados bajo `backend/`.
  - Carpeta raíz `frontend/` creada para albergar las aplicaciones cliente.
  - Módulo `tracking-service` con externalización de configuración (`configuration.json` vía `ConfigurationReader`).
  - Abstracción de puertos y adaptadores en espejo: `in`, `processing` y `out`.
  - Serialización desacoplada de ActiveMQ (`TrackingEventSerializer` / `CsvTrackingEventSerializer`).
  - Parsing de eventos desacoplado (`TrackingEventParser` / `CsvTrackingEventParser`).
  - Caso de uso unificado `RegisterPresenceUseCase` alimentado tanto por interfaz gráfica (`SwingDeviceReader` Swing) como por peticiones HTTP (`RegisterPresenceEndPoint` Javalin).
  - Microservicio `employee-service` (puerto `7072`) completado con arquitectura hexagonal, modelos inmutables, `JsonEmployeeReader`, endpoints REST (`GET /employees`, `GET /employees?id={id}`) y suite completa de tests unitarios con AssertJ.
  - Tags `v1.0.0`, `v2.0.0` y `v3.0.0` publicados en GitHub.
- **Siguiente gran paso hacia `v4.0.0`:**
  - Desarrollar el microservicio `building-service` (puerto `7073`) o el modelado completo de turnos y cálculo de incidencias/horas trabajadas.
