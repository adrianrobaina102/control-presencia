# control-presencia

Proyecto extraordinario de la asignatura Ingeniería del Software 2 (ULPGC). Sistema de control de presencia, gestión de horarios y registro de fichajes diseñado bajo arquitectura hexagonal y computación distribuida.

## 1. Objetivo de la entrega

El propósito de este sistema es gestionar el registro de asistencia (entradas y salidas), la definición de distintos regímenes horarios y la consulta del cumplimiento horario o incidencias de los empleados. La arquitectura separa los módulos de captura de datos de los módulos de almacenamiento para garantizar la escalabilidad. Actualmente, el sistema está dividido en un productor de eventos (`register`) y un consumidor (`storer`), comunicados asíncronamente mediante un broker de mensajería (ActiveMQ), aislando la lógica de dominio y la persistencia mediante el patrón de Puertos y Adaptadores.

## 2. Dependencias y entorno de desarrollo
- **JDK:** Oracle OpenJDK 27 (o versión compatible).
- **Gestor de construcción:** Apache Maven (módulos independientes `register/pom.xml` y `storer/pom.xml`).
- **Dependencias actuales:**
  - `JUnit 5 (Jupiter 6.1.3)`: Para la ejecución de pruebas unitarias automatizadas.
  - `AssertJ Core (3.27.7)`: Librería de aserciones fluidas para los tests.
  - `ActiveMQ Client (5.18.3)`: Para la conexión JMS con el broker de mensajería.
  - `DataFaker (2.0.2)`: Generación de datos falsos realistas (nombres, emails, teléfonos) para simulaciones de carga masiva.

## 3. Compilación y ejecución

### Requisitos previos
Es necesario tener un servidor **Apache ActiveMQ** ejecutándose en local en el puerto por defecto (`tcp://localhost:61616`).

### Compilación del proyecto y ejecución de pruebas
Cada módulo cuenta con su propio ciclo de vida Maven. Para compilar y ejecutar las pruebas:
```bash
# Para el módulo register:
mvn test -f register/pom.xml

# Para el módulo storer:
mvn test -f storer/pom.xml
```

### Ejecución de la simulación de fichajes (Backend Distribuido)
El sistema actual se ejecuta en dos procesos separados para demostrar el desacoplamiento:

1. **Arrancar el Consumidor (storer):** 
   Ejecutar la clase `Main` del módulo `storer`. El sistema se quedará a la escucha en la cola `fichajes.queue`, procesará los mensajes entrantes y confirmará (`CLIENT_ACKNOWLEDGE`) únicamente tras persistir en el Data Lake en la carpeta `store/`.
2. **Arrancar el Productor (register):**
   Ejecutar la clase `Main` del módulo `register`. Esto disparará la emisión de un fichaje (`TrackingEvent`) a través del puerto `EventStore` usando la conexión administrada por `ActiveMQBrokerConnection` hacia la cola.

*(En desarrollo: se detallarán los comandos de arranque de los módulos HTTP frontend conforme se implementen).*

## 4. Estructura del proyecto
El proyecto se divide en módulos independientes para garantizar el desacoplamiento físico y lógico, siguiendo la Arquitectura Hexagonal:

```text
├── register/                                           # Módulo Productor (Captura de fichajes)
│   ├── src/main/java/software/ulpgc/controlpresencia/
│   │   ├── application/                                # Core / Dominio (Sin dependencias externas)
│   │   │   ├── model/                                  # Entidades inmutables (Employee, Place, TrackingEvent...)
│   │   │   └── ports/                                  # EventStore (Puerto de salida)
│   │   ├── infrastructure/                             # Adaptadores y conexiones
│   │   │   └── adapters/                               # ActiveMQBrokerConnection, ActiveMQEventStore
│   │   ├── factories/                                  # TrackingEventFactory (Generador con DataFaker)
│   │   └── Main.java                                   # Composition Root del productor
│   ├── src/test/java/tests/                            # ActiveMQEventStoreTest
│   └── pom.xml
├── storer/                                             # Módulo Consumidor (Data Lake)
│   ├── src/main/java/software/ulpgc/controlpresencia/
│   │   ├── application/                                # Core / Dominio (Sin dependencias externas)
│   │   │   └── ports/                                  # EventReceiver, EventRepository
│   │   ├── infrastructure/                             # Adaptadores y conexiones
│   │   │   └── adapters/                               # ActiveMQBrokerConnection, ActiveMQEventReceiver, FileEventRepository
│   │   └── Main.java                                   # Composition Root del consumidor
│   ├── src/test/java/tests/                            # FileEventRepositoryTest, ActiveMQEventReceiverTest
│   └── pom.xml
└── store/                                              # Data Lake particionado cronológicamente
    └── YYYY/
        └── MM/
            └── YYYYMMDD.log
```

## 5. Arquitectura de sistemas
El diseño actual implementa un flujo unidireccional asíncrono:
1. **Generación (register):** Los eventos de dominio se serializan a un formato de texto plano ligero (CSV-like: `ts;empId;place;type`) para evitar acoplar las clases de dominio con los consumidores.
2. **Transporte (ActiveMQ):** Los mensajes viajan por la cola `fichajes.queue`. La conexión se gestiona mediante `ActiveMQBrokerConnection`.
3. **Consumo (storer):** El módulo lee el texto en bruto, confirma la recepción (`CLIENT_ACKNOWLEDGE`) solo tras garantizar la escritura en disco, y enruta la información.
4. **Persistencia (File System):** El texto se guarda en un sistema de archivos cronológico sin necesidad de ser deserializado a objetos complejos.

## 6. Arquitectura hexagonal del Backend

### Modelo de Dominio (`application.model`)
Contiene las estructuras de datos inmutables (implementadas mediante `record` y `enum`):
- `Employee`: Representa al empleado.
- `Contact`: Información de contacto asociada.
- `Place`: Ubicación o centro de trabajo.
- `TrackingEvent`: Evento inmutable de fichaje.
- `TrackingType`: Enumerado de tipo de movimiento (In / Out).

### Puertos (`application.ports`)
Interfaces atómicas y puras, totalmente desacopladas de excepciones de terceros como JMS:
- `EventStore`: Contrato para el envío de eventos de fichaje.
- `EventReceiver`: Contrato para la recepción de mensajes.
- `EventRepository`: Contrato para la persistencia del texto plano de eventos.

### Adaptadores (`infrastructure.adapters`)
- **Gestores de Conexión (`ActiveMQBrokerConnection`):** Administran el ciclo de vida de la factoría, conexión y sesión JMS implementando `AutoCloseable`.
- **Adaptadores JMS (`ActiveMQEventStore` / `ActiveMQEventReceiver`):** Reciben la sesión por inyección de dependencias en constructor (Pure DI). `storer` utiliza `Session.CLIENT_ACKNOWLEDGE` para garantizar cero pérdidas de eventos.
- **Adaptador de persistencia (`FileEventRepository`):** Extrae la fecha del string crudo y organiza los logs de forma auditable (ej. `store/2026/10/20261024.log`).

## 7. Modelo de datos y tipos de horario soportados
- **Trazabilidad de fichajes:** Cada evento registrado almacena la marca temporal, el empleado, la ubicación y el tipo de fichaje, transportados como texto plano para máxima interoperabilidad.
- **Modelado de horarios (Corrido, Partido y Turnos/Guardias):** *(En fase de diseño e implementación).*

## 8. Contrato de comunicación HTTP
*(Pendiente de definir las rutas HTTP, estructuras JSON de petición/respuesta y códigos de tratamiento de errores para los frontends).*

## 9. Aportación propia
*(Pendiente de documentar e implementar).*

## 10. Capturas del sistema
*(Espacio reservado para las capturas de pantalla obligatorias de los tres módulos frontend).*

## 11. Verificación
El repositorio incluye una suite de pruebas unitarias automatizadas con JUnit 5 y AssertJ en ambos módulos:
- `FileEventRepositoryTest`: Valida que el consumidor procesa correctamente las cadenas de texto plano (strings) provenientes de la mensajería, creando automáticamente la estructura de directorios por fecha (ej. `2026/10/20261013.log`) y aplicando de forma segura múltiples eventos sucesivos (`APPEND`).
- `ActiveMQEventStoreTest` y `ActiveMQEventReceiverTest`: Pruebas de integración sobre colas de mensajería usando la inyección por constructor de las sesiones de ActiveMQ.

## 12. Límites conocidos
- En el estado actual, el sistema es un procesador de eventos distribuidos de alta disponibilidad. Se encuentra en desarrollo el motor de cruce de estos fichajes contra los horarios asignados para el cálculo automático de retrasos e incidencias.
