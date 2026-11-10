# 📚 Documentación Técnica — Control de Presencia (ULPGC)

Bienvenido al centro de documentación técnica del sistema distribuido de **Control de Presencia**, proyecto de la asignatura **Ingeniería del Software 2** en la Escuela de Ingeniería Informática de la ULPGC.

---

## 🗂️ Índice de Secciones

| Documento | Descripción |
| :--- | :--- |
| **[`roadmap.md`](roadmap.md)** | **Roadmap y Estado del Proyecto**: Planificación por fases, estado de microservicios, tareas pendientes y avance hacia la rúbrica oficial. |
| **[`diagrams/`](diagrams/README.md)** | **Diagramas de Clases UML** detallados de cada microservicio (`tracking-service`, `storer`, `employee-service`), explicando sus puertos y adaptadores. |
| **[`architecture.md`](architecture.md)** | **Arquitectura de Red y Flujos de Información** paso a paso, diseño de la red mesh, particionado del Data Lake y reglas para defender la arquitectura. |
| **[`agent.md`](agent.md)** | **Architecture Guardrails & Guidelines**: Especificaciones técnicas, clean code, Pure DI, prohibición de concurrencia manual y cumplimiento de la rúbrica oficial. |
| **[`images/`](images/)** | Esquemas conceptuales originales de pizarra y diseño de la red de microservicios. |

---

## 🏛️ Principios Arquitectónicos Fundamentales

1. **Arquitectura Hexagonal (Puertos y Adaptadores):**  
   El Core (`architecture`) está completamente aislado del exterior. No contiene librerías web (Javalin), mensajería (ActiveMQ/JMS), serializadores (Gson) ni acceso al sistema de archivos (`java.io.File`).
2. **Inversión de Dependencias Pura (Pure DI):**  
   Los adaptadores reciben todas sus dependencias técnicas por constructor (`private final`). El `Main.java` (Composition Root) de cada servicio es el único responsable de instanciar y ensamblar el grafo.
3. **Persistencia Garantizada y Event Sourcing:**  
   Cada fichaje es un evento inmutable (`When`, `Who`, `Where`, `What`). En `storer`, la confirmación JMS (`CLIENT_ACKNOWLEDGE`) solo se ejecuta tras confirmar la escritura física en disco (`Files.writeString`).
4. **Tolerancia a Fallos sin Concurrencia Manual:**  
   Se prohíben hilos manuales (`Thread`, `ExecutorService`). El drenaje de eventos de ActiveMQ se realiza de forma secuencial y limpia mediante el timeout nativo `consumer.receive(2000)`.
