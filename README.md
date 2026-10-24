# control-presencia

Proyecto extraordinario de la asignatura Ingeniería del Software 2 (ULPGC). Sistema de control de presencia, gestión de horarios y registro de fichajes diseñado bajo arquitectura hexagonal y computación distribuida.

## 1. Objetivo de la entrega

El propósito de este sistema es gestionar el registro de asistencia (entradas y salidas), la definición de distintos regímenes horarios y la consulta del cumplimiento horario o incidencias de los empleados. La arquitectura separa los módulos de captura de datos de los módulos de almacenamiento para garantizar la escalabilidad. Actualmente, el sistema está dividido en un productor de eventos (`register`) y un consumidor (`Storer`), comunicados asíncronamente mediante un broker de mensajería (ActiveMQ), aislando la lógica de dominio y la persistencia mediante el patrón de Puertos y Adaptadores.

## 2. Dependencias y entorno de desarrollo
- **JDK:** Oracle OpenJDK 27 (o versión compatible).
- **Gestor de construcción:** Apache Maven (`pom.xml`).
- **Dependencias actuales:**
  - `JUnit 5 (Jupiter)`: Para la ejecución de pruebas unitarias automatizadas.
  - `AssertJ Core (3.27.7)`: Librería de aserciones fluidas para los tests.
  - `ActiveMQ Client (5.18.3)`: Para la conexión JMS con el broker de mensajería.
  - `SLF4J API (2.0.9)`: Fachada de logging requerida por ActiveMQ.
  - `DataFaker (2.0.2)`: Generación de datos falsos realistas (nombres, emails, teléfonos) para simulaciones de carga masiva.

## 3. Compilación y ejecución

### Requisitos previos
Es necesario tener un servidor **Apache ActiveMQ** ejecutándose en local en el puerto por defecto (`tcp://localhost:61616`).

### Compilación del proyecto y ejecución de pruebas
Desde el directorio raíz del proyecto, ejecutar:
```bash
mvn clean compile
mvn test
```

### Ejecución de la simulación de fichajes (Backend Distribuido)
El sistema actual se ejecuta en dos procesos separados para demostrar el desacoplamiento:

1. **Arrancar el Consumidor (Storer):** 
   Ejecutar la clase `Main` del módulo `Storer`. El sistema se quedará a la escucha en la cola `fichajes.queue`. Implementa un *timeout* de seguridad (`receive(2000)`), por lo que si la cola está vacía durante más de 2 segundos, el proceso finalizará limpiamente.
2. **Arrancar el Productor (Register):**
   Ejecutar la clase `Main` del módulo `register`. Esto disparará la `TrackingEventFactory` usando *DataFaker*, generando un lote de fichajes aleatorios hiperrealistas y enviándolos a la cola. El consumidor los procesará en tiempo real y generará el Data Lake en la carpeta `store/`.

*(En desarrollo: se detallarán los comandos de arranque de los módulos HTTP frontend conforme se implementen).*

## 4. Estructura del proyecto
El proyecto se divide en módulos independientes para garantizar el desacoplamiento físico y lógico:

```text
src/
├── register/                           # Módulo Productor (Captura de fichajes)
│   ├── main/java/software/ulpgc/controlpresencia/
│   │   ├── model/                      # Entidades del dominio (Employee, Place, TrackingEvent...)
│   │   ├── ports/                      # EventStore (Puerto de salida)
│   │   ├── adapters/                   # ActiveMQEventStore (Adaptador JMS)
│   │   ├── factories/                  # TrackingEventFactory (Generador con DataFaker)
│   │   └── Main.java                   # Simulador de emisión de fichajes
├── Storer/                             # Módulo Consumidor (Data Lake)
│   ├── main/java/software/ulpgc/controlpresencia/
│   │   ├── ports/                      # EventReceiver, EventRepository
│   │   ├── adapters/                   # ActiveMQEventReceiver, FileEventRepository
│   │   └── Main.java                   # Bucle de procesamiento de la cola
│   └── test/java/tests/                # Pruebas unitarias de persistencia
```

## 5. Arquitectura de sistemas
El diseño actual implementa un flujo unidireccional asíncrono:
1. **Generación (Register):** Los eventos de dominio se serializan a un formato de texto plano ligero (CSV-like: `ts;empId;place;type`) para evitar acoplar las clases de dominio con los consumidores.
2. **Transporte (ActiveMQ):** Los mensajes viajan por la cola `fichajes.queue`.
3. **Consumo (Storer):** El módulo lee el texto en bruto, confirma la recepción (`CLIENT_ACKNOWLEDGE`) para evitar pérdida de datos, y enruta la información.
4. **Persistencia (File System):** El texto se guarda en un sistema de archivos cronológico sin necesidad de ser deserializado a objetos complejos.

*(Pendiente de documentar: diagrama de arquitectura con los 3 módulos frontend HTTP).*

## 6. Arquitectura hexagonal del Backend

### Modelo de Dominio (`software.ulpgc.controlpresencia.model`)
Contiene las estructuras de datos inmutables (implementadas mediante `record` y `enum`):
- `Employee`: Representa al empleado.
- `Contact`: Información de contacto asociada.
- `Place`: Ubicación o centro de trabajo.
- `TrackingEvent`: Evento inmutable de fichaje.
- `TrackingType`: Enumerado de tipo de movimiento (In / Out).

### Puertos y Adaptadores
- **Puertos:** `EventStore` (envía), `EventReceiver` (escucha), `EventRepository` (guarda).
- **Adaptadores JMS (`ActiveMQEventStore` / `ActiveMQEventReceiver`):** Manejan la conexión con el broker. El consumidor utiliza `Session.CLIENT_ACKNOWLEDGE` para garantizar que ningún fichaje se elimine de la cola si ocurre un fallo de escritura en disco.
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
El repositorio incluye una suite de pruebas unitarias automatizadas con JUnit 5 y AssertJ en el paquete `tests` del módulo `Storer`.
- `FileEventRepositoryTest`: Valida que el consumidor procesa correctamente las cadenas de texto plano (strings) provenientes de la mensajería, creando automáticamente la estructura de directorios por fecha (ej. `2026/10/20261013.log`) y aplicando de forma segura múltiples eventos sucesivos (`APPEND`).

## 12. Límites conocidos
- En el estado actual, el sistema es un procesador de eventos distribuidos de alta disponibilidad. Se encuentra en desarrollo el motor de cruce de estos fichajes contra los horarios asignados para el cálculo automático de retrasos e incidencias.