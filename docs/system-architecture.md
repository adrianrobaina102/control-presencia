# 🌐 Arquitectura del Sistema — Control de Presencia (ULPGC)

Este documento describe la **Arquitectura de Sistema** (nivel macro: cómo se organizan, comunican y persisten los datos entre todos los servicios distribuidos), diferenciándola de la **Arquitectura de Aplicación** (nivel micro: Arquitectura Hexagonal + Pure DI dentro de cada servicio).

---

## 1. Visión Global: Event-Driven + CQRS (Feeder $\rightarrow$ Datalake $\rightarrow$ Datamart)

El sistema combina una arquitectura **Orientada a Eventos (Event-Driven)** con el patrón **CQRS (Command Query Responsibility Segregation)** y **Microservicios REST**:

```text
  [ Terminales / Swing / Web ]
               │
               ▼ (Captura de eventos)
  ┌─────────────────────────┐
  │ FEEDER                  │  (tracking-service :7070)
  │ Captura y validación    │
  └────────────┬────────────┘
               │ Publicación asíncrona (JMS)
               ▼
  ┌─────────────────────────┐
  │ MESSAGE BROKER          │  (Apache ActiveMQ :61616 - fichajes.queue)
  │ Desacoplamiento y cola  │
  └────────────┬────────────┘
               │ Consumo seguro (CLIENT_ACKNOWLEDGE, sin hilos)
               ▼
  ┌─────────────────────────┐
  │ DATALAKE                │  (storer :7071)
  │ Almacén crudo append-   │  store/YYYY/MM/YYYYMMDD.log
  │ only particionado       │
  └────────────┬────────────┘
               │
               │ (Proyector / Monitor / Datamart Builder - Planificado)
               ▼
  ┌─────────────────────────┐      Consultas REST      ┌──────────────────────────┐
  │ DATAMART (Read Model)   │ ◀──────────────────────▶ │ reporting-service :7075  │
  │ Proyecciones analíticas │                          │ (Núcleo analítico)       │
  └─────────────────────────┘                          └────────────┬─────────────┘
                                                                    │ HTTP Async
                                           ┌────────────────────────┼────────────────────────┐
                                           ▼                        ▼                        ▼
                               [employee-service :7072] [building-service :7073] [schedule-service :7074]
```

---

## 2. Rol de Cada Componente en la Arquitectura de Sistema

| Rol en el Sistema | Módulo Real | Puerto | Estado | Responsabilidad Principal |
| :--- | :--- | :---: | :---: | :--- |
| **Feeder** | `tracking-service` | `7070` | ✅ Implementado | Puerta de entrada de fichajes (interfaz Swing o HTTP). Valida y serializa cada evento (`ts;employeeId;place;type`) y lo envía al Broker. |
| **Message Broker** | Apache ActiveMQ | `61616` | ✅ Integrado | Cola JMS (`fichajes.queue`) que desacopla el Feeder del Datalake y garantiza que no se pierdan fichajes ante caídas. |
| **Datalake** | `storer` | `7071` | ✅ Implementado | Almacén histórico inmutable (*append-only*) particionado por fecha en disco. Confirma mensajes JMS solo tras persistir en disco (`CLIENT_ACKNOWLEDGE`). |
| **Datamart Builder / Monitor** | *(En roadmap)* | - | 🔴 Planificado | Proceso encargado de leer los eventos del Datalake (o suscribirse al flujo), cruzarlos con contratos y horarios, y materializar los reportes en el Datamart. |
| **Datamart** | *(En roadmap)* | - | 🔴 Planificado | Almacén analítico optimizado para lectura rápida (Read Model de CQRS) con métricas diarias/mensuales e incidencias ya precalculadas. |
| **Servicios Maestros (Catálogo)** | `employee-service`, `building-service`, `schedule-service` | `7072`, `7073`, `7074` | ✅ Implementado | Microservicios REST autónomos que mantienen los datos maestros de empleados/contratos, sedes y regímenes horarios. |
| **Servicio Analítico / Aggregator** | `reporting-service` | `7075` | 🟡 En desarrollo | Expone los informes diarios y mensuales (`GET /reports/daily`, `GET /reports/monthly`), consultando al Datamart/Datalake y a los servicios maestros. |
| **API Gateway / Reverse Proxy** | Nginx *(Planificado)* | `80/443` | 🔴 Planificado | Punto de entrada único para los frontends, balanceo de carga y enrutamiento unificado hacia todos los microservicios. |

---

## 3. Separación CQRS (Command vs Query)

* **Lado Command (Escritura de eventos):**
  `Feeder (tracking-service)` $\rightarrow$ `ActiveMQ` $\rightarrow$ `Datalake (storer)`.
  Flujo asíncrono 1 a 1, tolerante a fallos e inmutable.
* **Lado Query (Consultas e informes):**
  `reporting-service` consulta los datos históricos (Datalake / Datamart) y los combina con los catálogos maestros (`employee-service`, `schedule-service`) de forma completamente independiente del flujo de escritura.
