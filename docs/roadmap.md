# 🗺️ Roadmap y Estado del Proyecto — Control de Presencia (v4.0.0)

Este documento detalla el estado actual de implementación, la planificación por fases y los requisitos pendientes para la entrega de la convocatoria extraordinaria de **Ingeniería del Software 2 (ULPGC)**.

---

## 📊 Resumen Ejecutivo del Estado

```
[██████████████████░░░░░░░░░░░░░░░░] 45% Completado (Fases 1 a 3 cerradas, Fase 4 en curso)
```

- **Fases Completadas (3/9):** `tracking-service`, `storer`, `employee-service`.
- **Fase en Curso (1/9):** `building-service` (modelo y contratos listos, faltan adaptadores y tests).
- **Fases Pendientes (5/9):** `schedule-service`, `reporting-service`, Frontends, Docker y Auditoría final.

---

## 🧭 Tabla General de Fases y Microservicios

| Fase | Módulo / Componente | Puerto | Estado | Responsabilidad y Entregables Clave | Dependencias |
| :--- | :--- | :---: | :---: | :--- | :--- |
| **Fase 1** | **`tracking-service`** | `7070` | ✅ **Completado** | • Recepción de eventos de fichaje (terminal / HTTP).<br>• Validación y parseo CSV.<br>• Publicación asíncrona al Broker JMS (ActiveMQ).<br>• Diagrama UML integrado en `docs/diagrams/`. | ActiveMQ (`tcp://localhost:61616`) |
| **Fase 2** | **`storer`** | `7071` | ✅ **Completado** | • Ingesta asíncrona de eventos desde ActiveMQ (sin hilos manuales).<br>• Almacenamiento append-only particionado en disco (`events/YYYY/MM/DD/...`).<br>• API HTTP de solo lectura (CQRS: `GET /events`).<br>• Diagrama UML integrado en `docs/diagrams/`. | ActiveMQ, Disco local |
| **Fase 3** | **`employee-service`** | `7072` | ✅ **Completado** | • Gestión de empleados, roles (`EMPLOYEE`, `MANAGER`, `ADMIN`).<br>• Contratos con asignación de régimen horario.<br>• API HTTP (`GET /employees`, `GET /employees?id=...`).<br>• Tests unitarios completos (JUnit 5 + AssertJ).<br>• Diagrama UML integrado en `docs/diagrams/`. | Dataset JSON (`employees.json`) |
| **Fase 4** | **`building-service`** | `7073` | 🟡 **En Curso** | • **Hecho:** Modelo inmutable, Dataset JSON (3 sedes), Puerto `BuildingReader`, Casos de uso (`Get`, `List`), Adaptadores HTTP y JSON, `Main.java` con Pure DI, Tests JUnit 5.<br>• **Pendiente:** Diagrama UML de clases. | Dataset JSON (`buildings.json`) |
| **Fase 5** | **`schedule-service`** | `7074` | 🔴 **Pendiente** | • Catálogo de los 3 regímenes horarios requeridos por la rúbrica:<br>&nbsp;&nbsp;1. Jornada continua oficina (8:00 - 16:00).<br>&nbsp;&nbsp;2. Jornada partida comercial (9:30-13:30 / 16:30-20:30).<br>&nbsp;&nbsp;3. Turnos rotativos hospitalarios (M / T / N - 24/7).<br>• Definición de márgenes de cortesía y tolerancias de entrada/salida. | Dataset JSON (`schedules.json`) |
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
- [ ] Generación e integración del Diagrama UML en `docs/diagrams/`.
