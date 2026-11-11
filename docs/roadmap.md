# 🗺️ Roadmap y Estado del Proyecto — Control de Presencia (v4.0.0)

Este documento detalla el estado actual de implementación, la planificación por fases y los requisitos pendientes para la entrega de la convocatoria extraordinaria de **Ingeniería del Software 2 (ULPGC)**.

---

## 📊 Resumen Ejecutivo del Estado

```
[██████████████████████░░░░░░░░░░░░] 55% Completado (Fases 1 a 4 cerradas)
```

- **Fases Completadas (4/9):** `tracking-service` (v1.0.0), `storer` (v2.0.0), `employee-service` y `building-service` (v4.0.0).
- **Fase en Curso (0/9):** Preparando inicio de Fase 5 (`schedule-service`).
- **Fases Pendientes (5/9):** `schedule-service`, `reporting-service`, Frontends, Docker y Auditoría final.

---

## 🧭 Tabla General de Fases y Microservicios

| Fase | Módulo / Componente | Puerto | Estado | Responsabilidad y Entregables Clave | Dependencias |
| :--- | :--- | :---: | :---: | :--- | :--- |
| **Fase 1** | **`tracking-service`** | `7070` | ✅ **Completado** (`v1.0.0`) | • Recepción de eventos de fichaje (terminal / HTTP).<br>• Validación y parseo CSV.<br>• Publicación asíncrona al Broker JMS (ActiveMQ).<br>• Diagrama UML integrado en `docs/diagrams/`. | ActiveMQ (`tcp://localhost:61616`) |
| **Fase 2** | **`storer`** | `7071` | ✅ **Completado** (`v2.0.0`) | • Ingesta asíncrona de eventos desde ActiveMQ (sin hilos manuales).<br>• Almacenamiento append-only particionado en disco (`events/YYYY/MM/DD/...`).<br>• API HTTP de solo lectura (CQRS: `GET /events`).<br>• Diagrama UML integrado en `docs/diagrams/`. | ActiveMQ, Disco local |
| **Fase 3** | **`employee-service`** | `7072` | ✅ **Completado** (`v4.0.0`) | • Gestión de empleados, roles (`EMPLOYEE`, `MANAGER`, `ADMIN`).<br>• Contratos con asignación de régimen horario.<br>• API HTTP (`GET /employees`, `GET /employees?id=...`).<br>• Tests unitarios completos (JUnit 5 + AssertJ).<br>• Diagrama UML integrado en `docs/diagrams/`. | Dataset JSON (`employees.json`) |
| **Fase 4** | **`building-service`** | `7073` | ✅ **Completado** (`v4.0.0`) | • Modelo inmutable (`Building`, `Organization`), Dataset JSON (3 sedes), Puerto `BuildingReader`.<br>• Casos de uso (`Get`, `List`), Adaptadores HTTP y JSON, `Main.java` con Pure DI, Tests JUnit 5.<br>• Diagrama UML integrado en `docs/diagrams/`. | Dataset JSON (`buildings.json`) |
| **Fase 5** | **`schedule-service`** | `7074` | 🟡 **En Curso** | • **Hecho:** Diagrama UML de clases integrado en `docs/diagrams/`.<br>• **Pendiente:** Dataset `schedules.json`, Modelos (`Schedule`, `TimeInterval`, `ShiftType`), Puerto `ScheduleReader`, Casos de uso, Adaptadores HTTP/JSON, `Main.java` y Tests. | Dataset JSON (`schedules.json`) |
| **Fase 6** | **`reporting-service`** | `7075` | 🔴 **Pendiente** | • **Núcleo analítico del sistema (25% rúbrica)**.<br>• Cruce de datos: eventos de presencia (`storer`) + contratos (`employee-service`) + reglas horarias (`schedule-service`).<br>• Cálculo de presencias, cómputo de horas trabajadas, detección de retrasos, horas extra y ausencias. | `storer`, `employee-service`, `schedule-service` |
| **Fase 7** | **Frontends & UI** | - | 🔴 **Pendiente** | • **Dashboard administrativo** (visualización de empleados, sedes, reportes de puntualidad y balances mensuales).<br>• **Terminal de fichaje interactivo** (registro de entradas/salidas de trabajadores). | Servicios Backend |
| **Fase 8** | **Despliegue & DevOps** | - | 🔴 **Pendiente** | • Orquestación completa con `docker-compose.yml`.<br>• Contenedores para Apache ActiveMQ y microservicios Java.<br>• Instrucciones de ejecución con un solo comando. | Docker, Docker Compose |
| **Fase 9** | **Auditoría & Cierre** | - | 🔴 **Pendiente** | • Verificación exhaustiva de la rúbrica ULPGC (25% funcionalidad, 25% arquitectura, 20% frontend, 15% persistencia, 10% README, 5% tests).<br>• Tag de versión final `v4.0.0` y validación de entrega. | Todo el sistema |

---

## 🔍 Detalle de la Fase Actual: `building-service`

### Arquitectura de Componentes
```
[buildings.json]
       │
       ▼
[JsonBuildingReader] ────implements────▷ [BuildingReader (Port)]
                                                 │
                        ┌────────────────────────┴────────────────────────┐
                        ▼                                                 ▼
             [GetBuildingUseCase]                              [ListBuildingsUseCase]
                        ▲                                                 ▲
                        │                                                 │
             [GetBuildingEndPoint]                             [ListBuildingsEndPoint]
                        │                                                 │
                        └─────────────── [Main.java] ─────────────────────┘
                                      (Port 7073)
```

### Lista de Tareas para Cerrar la Fase 4:
- [x] Configuración de Maven `pom.xml` (Javalin, Gson, SLF4J, JUnit 5, AssertJ).
- [x] Creación del dataset `buildings.json` con 3 sedes reales vinculadas a los regímenes horarios.
- [x] Creación de modelos de dominio inmutables: `Building.java` y `Organization.java`.
- [x] Creación del puerto del dominio: `BuildingReader.java`.
- [x] Creación del contrato de caso de uso: `UseCase.java`.
- [x] Implementación de `GetBuildingUseCase.java` (búsqueda por ID).
- [x] Implementación de `ListBuildingsUseCase.java` (listado completo).
- [x] Implementación de `JsonBuildingReader.java` (deserialización Gson).
- [x] Implementación de `EndPoint.java` y adaptadores `GetBuildingEndPoint.java` y `ListBuildingsEndPoint.java`.
- [x] Implementación de `Main.java` (inyección manual pura en puerto 7073).
- [x] Batería de pruebas unitarias (`GetBuildingUseCaseTest`, `ListBuildingsUseCaseTest`, `JsonBuildingReaderTest`).
- [x] Generación e integración del Diagrama UML en `docs/diagrams/`.


---

## 🔍 Detalle del Módulo en Curso: `schedule-service`

### Checklist de Tareas para la Fase 5:
- [x] Generación e integración del Diagrama UML en `docs/diagrams/schedule-service-class-diagram.png`.
- [x] Configuración de Maven `pom.xml` (Javalin, Gson, SLF4J, JUnit 5, AssertJ).
- [x] Creación del dataset `schedules.json` con los 3 regímenes obligatorios (`SCH-OFFICE-CONTINUOUS`, `SCH-STORE-SPLIT`, `SCH-HOSPITAL-SHIFT`).
- [x] Creación del contrato base de caso de uso: `UseCase.java`.
- [x] Creación de modelos de dominio inmutables (`Schedule`, `TimeInterval`, `ShiftType`, `DayOfWeek`).
- [ ] Creación del puerto del dominio: `ScheduleReader.java`.
- [ ] Implementación de `GetScheduleUseCase.java` (búsqueda por ID).
- [ ] Implementación de `ListSchedulesUseCase.java` (listado completo).
- [ ] Implementación de `JsonScheduleReader.java` (deserialización Gson).
- [ ] Implementación de `EndPoint.java` y adaptadores `GetScheduleEndPoint.java` y `ListSchedulesEndPoint.java`.
- [ ] Implementación de `Main.java` (inyección manual pura en puerto 7074).
- [ ] Batería de pruebas unitarias (`GetScheduleUseCaseTest`, `ListSchedulesUseCaseTest`, `JsonScheduleReaderTest`).
