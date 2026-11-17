# 🗺️ Roadmap y Estado del Proyecto — Control de Presencia (v4.0.0)

Este documento detalla el estado actual de implementación, la planificación por fases y las tareas de reestructuración técnica y funcional para la entrega de la convocatoria extraordinaria de **Ingeniería del Software 2 (ULPGC)**.

---

## 📊 Resumen Ejecutivo del Estado

```
[██████████████████████████░░░░░░░░] 65% Completado (Fases 1 a 5 cerradas)
```

- **Fases Completadas (5/10):** `tracking-service` (v1.0.0), `storer` (v2.0.0), `employee-service` y `building-service` (v4.0.0), `schedule-service`.
- **Fase en Curso:** Fase 6 (`reporting-service`) y Plan de Reestructuración Arquitectónica / REST.
- **Fases Pendientes:** `reporting-service`, Frontends, Docker, Nginx / Reverse Proxy y Auditoría final.

---

## 🧭 Tabla General de Fases y Microservicios

| Fase | Módulo / Componente | Puerto | Estado | Responsabilidad y Entregables Clave | Dificultad | Dependencias |
| :--- | :--- | :---: | :---: | :--- | :---: | :--- |
| **Fase 1** | **`tracking-service`** | `7070` | ✅ **Completado** (`v1.0.0`) | • Recepción de eventos de fichaje (terminal / HTTP).<br>• Validación y parseo CSV.<br>• Publicación asíncrona al Broker JMS (ActiveMQ).<br>• Diagrama UML integrado en `docs/diagrams/`. | Media | ActiveMQ (`tcp://localhost:61616`) |
| **Fase 2** | **`storer`** | `7071` | ✅ **Completado** (`v2.0.0`) | • Ingesta asíncrona de eventos desde ActiveMQ (sin hilos manuales).<br>• Almacenamiento append-only particionado en disco (`events/YYYY/MM/DD/...`).<br>• API HTTP de solo lectura (CQRS: `GET /events`).<br>• Diagrama UML integrado en `docs/diagrams/`. | Media | ActiveMQ, Disco local |
| **Fase 3** | **`employee-service`** | `7072` | ✅ **Completado** (`v4.0.0`) | • Gestión de empleados, roles (`EMPLOYEE`, `MANAGER`, `ADMIN`).<br>• Contratos con asignación de régimen horario.<br>• API HTTP (`GET /employees`, `GET /employees?id=...`).<br>• Tests unitarios completos (JUnit 5 + AssertJ).<br>• Diagrama UML integrado en `docs/diagrams/`. | Baja | Dataset JSON (`employees.json`) |
| **Fase 4** | **`building-service`** | `7073` | ✅ **Completado** (`v4.0.0`) | • Modelo inmutable (`Building`, `Organization`), Dataset JSON (3 sedes), Puerto `BuildingReader`.<br>• Casos de uso (`Get`, `List`), Adaptadores HTTP y JSON, `Main.java` con Pure DI, Tests JUnit 5.<br>• Diagrama UML integrado en `docs/diagrams/`. | Baja | Dataset JSON (`buildings.json`) |
| **Fase 5** | **`schedule-service`** | `7074` | ✅ **Completado** | • Modelo inmutable (`Schedule`, `TimeInterval`, `ShiftType`, `DayOfWeek`).<br>• Dataset JSON (3 regímenes: continuo, partido, 24/7).<br>• Puerto `ScheduleReader`, Casos de uso (`Get`, `List`), Adaptadores HTTP y JSON.<br>• `Main.java` en 7074 con Pure DI, Tests JUnit 5 y Diagrama UML. | Baja | Dataset JSON (`schedules.json`) |
| **Fase 6** | **`reporting-service`** | `7075` | 🔴 **Pendiente** | • **Núcleo analítico del sistema (25% rúbrica)**.<br>• Cruce de datos: eventos de presencia (`storer`) + contratos (`employee-service`) + reglas horarias (`schedule-service`).<br>• Cálculo de presencias, cómputo de horas trabajadas, detección de retrasos, horas extra y ausencias. | **Alta** | `storer`, `employee-service`, `schedule-service` |
| **Fase 7** | **Reestructuración REST & Desacoplamiento Core** | - | 🟡 **Planificado** | • Desacoplamiento total del core respecto a Javalin/HTTP (`core/` vs `infrastructure/`).<br>• Patrón `Output<T>` con `status()` y `result()` en Casos de Uso.<br>• Validación defensiva en frontera HTTP (`400 Bad Request`).<br>• Serializadores/Deserializadores en puertos y adaptadores. | **Media** | Microservicios existentes |
| **Fase 8** | **Cuentas / Autenticación & Repositorios** | - | 🟡 **Planificado** | • Separación conceptual de cuentas/auth (`AuthService` / `RegisterService`) frente a empleados.<br>• Puertos de persistencia bidireccionales (`Repository` / `Store`). | **Media** | `employee-service` |
| **Fase 9** | **Frontends & UI** | - | 🔴 **Pendiente** | • **Dashboard administrativo** (visualización de empleados, sedes, reportes de puntualidad y balances mensuales).<br>• **Terminal de fichaje interactivo** (registro de entradas/salidas de trabajadores). | **Media** | Servicios Backend |
| **Fase 10** | **Despliegue, Nginx & DevOps** | - | 🔴 **Pendiente** | • Orquestación completa con `docker-compose.yml`.<br>• Contenedores para Apache ActiveMQ, microservicios Java y Nginx como Reverse Proxy / Balanceador.<br>• Soporte de cabeceras `X-Forwarded-*`. | **Media** | Docker, Docker Compose, Nginx |
| **Fase 11** | **Auditoría & Cierre** | - | 🔴 **Pendiente** | • Verificación exhaustiva de la rúbrica ULPGC.<br>• Tag de versión final y validación de entrega. | Baja | Todo el sistema |

---

## 🔍 Detalle del Módulo en Curso: `reporting-service` (Fase 6)

### Checklist de Tareas para Fase 6:
- [ ] Diagrama UML de clases en `docs/diagrams/reporting-service-class-diagram.png`.
- [ ] Configuración de Maven `backend/reporting-service/pom.xml`.
- [ ] Modelos de dominio inmutables: `IncidentType`, `Incident`, `DailyReport`, `MonthlyReport`.
- [ ] Puertos de salida: `EventReader`, `EmployeeReader`, `ScheduleReader`.
- [ ] Casos de uso: `GenerateDailyReportUseCase` y `GenerateMonthlyReportUseCase` (con contrato `Output` agnóstico).
- [ ] Adaptadores HTTP asíncronos (`HttpClient.sendAsync(...)`): `HttpEventReader`, `HttpEmployeeReader`, `HttpScheduleReader`.
- [ ] Adaptadores de entrada (Endpoints Javalin): `GetDailyReportEndPoint`, `GetMonthlyReportEndPoint`.
- [ ] Composition root `Main.java` (Pure DI en puerto 7075).
- [ ] Suite de pruebas unitarias con mocks/stubs de los puertos.

---

## 🛠️ Plan de Reestructuración Arquitectónica y REST (Fases 7 y 8)

Para más detalles consultar la guía de reestructuración en [docs/reestructuracion-arquitectura.md](file:///c:/Users/adri0/IdeaProjects/control-presencia/docs/reestructuracion-arquitectura.md).

| Tarea | Descripción | Dificultad | Impacto |
| :--- | :--- | :---: | :--- |
| **T1. Paquetes Core / Infraestructura** | Mover de `architecture/` a `core/` (`model`, `ports/in`, `ports/out`, `domain`) y `infrastructure/` (`adapters/in/http`, `adapters/out/persistence`). | Media | Alto (mantiene core 100% independiente de frameworks). |
| **T2. Patrón `Output<T>`** | Implementar `Output<T>(int status, T result, String error)` en `UseCase` para respuestas semánticas (`200`, `201`, `400`, `404`). | Baja | Alto (habilita APIs REST estándar y testing puro del caso de uso sin Javalin). |
| **T3. Validación Defensiva en Frontera** | Validadores de entrada en controladores HTTP antes de delegar al caso de uso (query params, path params, JSON payload). | Baja | Medio (robusta ante inputs erróneos, `400 Bad Request`). |
| **T4. Puertos de Serialización** | Aislar Gson/Jackson en puertos de serialización o adaptadores dedicados. | Baja | Medio (desacopla librerías de serialización). |
| **T5. AuthService / RegisterService** | Separar credenciales/cuentas de la entidad `Employee` de fichaje y preparar puertos `Store`/`Repository`. | Media | Medio (seguridad desacoplada). |
| **T6. Reverse Proxy Nginx & Headers** | Añadir configuración para `X-Forwarded-*` y preparar enrutamiento unificado de endpoints en Nginx. | Media | Alto (preparado para producción y balanceo). |
