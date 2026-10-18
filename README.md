# control-presencia

Proyecto extraordinario de la asignatura Ingeniería del Software 2 (ULPGC). Sistema de control de presencia, gestión de horarios y registro de fichajes diseñado bajo arquitectura hexagonal.

## 1. Objetivo de la entrega
El propósito de este sistema es gestionar el registro de asistencia (entradas y salidas), la definición de distintos regímenes horarios y la consulta del cumplimiento horario o incidencias de los empleados. La arquitectura separa el servidor (backend) de los módulos de cliente (frontend: fichaje, consulta y administración de horarios), aislando la lógica de dominio y la persistencia mediante el patrón de Puertos y Adaptadores.

## 2. Dependencias y entorno de desarrollo
<!-- NOTA: Si añades librerías en el pom.xml (ej. Javalin, SparkJava, Gson, Jackson, SQLite, JUnit, AssertJ...), añádelas a esta lista -->
* **JDK:** Oracle OpenJDK 27 (o versión compatible).
* **Gestor de construcción:** Apache Maven (`pom.xml`).
* **Dependencias actuales:**
    * *(Pendiente de documentar librerías de red, serialización y pruebas conforme se incorporen al `pom.xml`).*

## 3. Compilación y ejecución

### Compilación del proyecto
Desde el directorio raíz del proyecto, ejecutar:

```bash
mvn clean compile
```

### Ejecución del Backend y Frontends
<!-- NOTA: Documenta aquí cómo arrancar el backend y los 3 módulos frontend (registro, consulta y administración), indicando puertos, base de datos y carga de datos iniciales de prueba -->
*(En desarrollo: se detallarán los comandos de arranque, configuración de puertos y carga de datos iniciales conforme se implementen los servicios y las interfaces).*

## 4. Estructura del proyecto
<!-- NOTA: Actualiza este árbol conforme dividas el proyecto en paquetes de casos de uso, controladores HTTP y módulos frontend -->
Actualmente, el núcleo del proyecto (`software.ulpgc.controlpresencia`) se organiza separando el modelo de datos de la capa de entrada/salida y persistencia:

```text
src/
└── main/java/software/ulpgc/controlpresencia/
    ├── model/          # Entidades y registros inmutables del dominio
    ├── io/             # Puertos y adaptadores de persistencia de eventos
    └── Main.java       # Punto de entrada de la aplicación
```

## 5. Arquitectura de sistemas
<!-- NOTA: Incluye aquí un diagrama simple que muestre:
     1. Los 3 módulos Frontend (Registro de asistencia, Consulta de presencia/incidencias, Definición de horarios).
     2. El Backend.
     3. El sistema de almacenamiento (Archivos / Base de datos).
     4. Los contratos de comunicación (HTTP/JSON). -->
*(Pendiente de documentar: diagrama de arquitectura de sistemas con la relación entre los tres módulos frontend, el backend y la capa de persistencia).*

## 6. Arquitectura hexagonal del Backend

### Modelo de Dominio (`software.ulpgc.controlpresencia.model`)
<!-- NOTA: Cuando añadas las clases de horarios (Schedule, Shift, Guard, TimeSlot, Incidence...), añádelas a esta lista -->
Contiene las estructuras de datos inmutables (implementadas mediante `record` y `enum`) que representan los conceptos fundamentales del control de presencia:
* `Employee`: Representa al empleado o persona sujeta al control horario.
* `Contact`: Información de contacto asociada al empleado.
* `Place`: Ubicación o centro de trabajo donde se registra el evento.
* `TrackingEvent`: Evento inmutable de fichaje que vincula empleado, fecha/hora, ubicación y tipo de movimiento.
* `TrackingType`: Enumerado que define la naturaleza del evento de presencia (ej. entrada, salida).

### Puertos y Adaptadores (`software.ulpgc.controlpresencia.io`)
<!-- NOTA: Cuando separes formalmente en paquetes `ports` y `adapters` o añadas repositorios de horarios y controladores web, actualízalo aquí -->
* **Puerto de salida (`EventStore`):** Interfaz que define el contrato para el almacenamiento y recuperación de los eventos de presencia, desacoplando el dominio del soporte físico.
* **Adaptador de persistencia (`FileEventStore`):** Implementación de `EventStore` que registra los eventos de forma trazable y auditable en ficheros de log organizados cronológicamente por directorios de año y mes (ej. `store/YYYY/MM/YYYYMMDD.log`).

## 7. Modelo de datos y tipos de horario soportados
<!-- NOTA: Este apartado vale un 20% de la rúbrica. Cuando programes los horarios, explica aquí cómo los modelaste y pon los 3 ejemplos obligatorios:
     1. Horario corrido (ej. Oficina: 08:00 a 15:00).
     2. Horario partido (ej. Restaurante/Tienda: 10:00 a 14:00 y 17:00 a 21:00).
     3. Turnos o guardias (ej. Hospital: turnos rotatorios o guardias de 24h). -->
* **Trazabilidad de fichajes:** Cada evento registrado almacena la marca temporal, el empleado, la ubicación y el tipo de fichaje.
* **Modelado de horarios (Corrido, Partido y Turnos/Guardias):** *(En fase de diseño e implementación).*

## 8. Contrato de comunicación
<!-- NOTA: Documenta aquí las rutas HTTP (ej. POST /trackings, GET /presence, POST /schedules), ejemplos de cuerpos JSON enviados, respuestas y códigos de error (400, 404, 409...). -->
*(Pendiente de definir las rutas HTTP, estructuras JSON de petición/respuesta y códigos de tratamiento de errores).*

## 9. Aportación propia
<!-- NOTA: Describe aquí tu idea propia ajustada al dominio (ej. gestión de alertas por retraso, calendario de festivos, justificación de incidencias, exportación de informes o reglas por centro de trabajo `Place`). -->
*(Pendiente de documentar e implementar).*

## 10. Capturas del sistema
<!-- NOTA: Obligatorio incluir capturas de los 3 módulos funcionando:
     1. Interfaz de registro de asistencia (fichaje de entrada/salida).
     2. Interfaz de consulta de presencia, horas trabajadas e incidencias.
     3. Interfaz de administración y definición de horarios. -->
*(Espacio reservado para las capturas de pantalla obligatorias de los tres módulos frontend).*

## 11. Verificación
<!-- NOTA: Documenta aquí los tests unitarios que vayas creando y las pruebas manuales obligatorias:
     - Prueba de los 3 tipos de horario (oficina, restaurante, hospital).
     - Registro de retrasos, ausencias o salidas tempranas.
     - Pruebas de error: persona inexistente, fichaje duplicado o salida sin entrada previa. -->
* **Verificación actual:** Comprobación de escritura y persistencia de eventos de fichaje en el sistema de archivos local (`store/2026/10/`).
* *(Pendiente de añadir suite de pruebas unitarias automatizadas y validación de casos de uso horarios).*

## 12. Límites conocidos
<!-- NOTA: Detalla aquí cualquier límite del cálculo horario, concurrencia o reglas que queden fuera del alcance. -->
* En el estado actual, el sistema registra eventos de presencia en disco (`FileEventStore`), encontrándose en desarrollo el motor de cruce de fichajes contra los horarios asignados para el cálculo automático de retrasos e incidencias.