# proyecto-java-grupo05

Sistema de Gestión de Taller Automotriz (SGT) - **CarMotors**.


## Historial de Cambios y Soluciones Técnicas

### 1. Módulos base del taller
Implementación de gestión de clientes, vehículos, proveedores, inventario de repuestos, órdenes de servicio y facturación bajo arquitectura en capas (`vista`, `controlador`, `servicio`, `dao`, `modelo`).

### 2. Reserva Concurrente de Repuestos (Condición de Carrera y Sincronización)
- **Problema (Condición de carrera):** En `InventarioServicio.verificarYDescontarStock`, la secuencia de lectura del stock, validación de disponibilidad y actualización no estaba protegida contra concurrencia. Si dos técnicos intentaban reservar al mismo tiempo la última unidad, ambos podían leer el stock antes de descontarlo y reservar un mismo repuesto físico.
- **Solución implementada:** Se aplicó sincronización en Java (`synchronized`) sobre `verificarYDescontarStock` en `InventarioServicio`. Solo un hilo puede ejecutar la sección crítica leer-validar-escribir a la vez, garantizando que únicamente un técnico reserve la última unidad; el segundo recibe rechazo por stock insuficiente.
- **Simulación y pruebas:**
  - `src/main/java/com/carmotors/simulacion/MainReservaConcurrente.java`: clase ejecutable independiente (paquete `com.carmotors.simulacion`) con su propio `main`. Ajusta un repuesto a stock `1`, toma dos órdenes activas, lanza dos hilos (`Hilo-Tecnico-A` y `Hilo-Tecnico-B`) sincronizados con `CountDownLatch` que invocan `TallerServicio.agregarRepuestoAOrden(...)` — el mismo servicio usado por la aplicación real —, y verifica en base de datos que el stock final sea `0`.
  - `src/main/java/com/carmotors/simulacion/EscenarioSimulacion.java`: prepara el estado inicial (repuesto con stock 1, dos órdenes activas) reutilizando los DAO reales.
  - `src/main/java/com/carmotors/simulacion/TareaReservaTecnico.java`: implementa `Runnable`; cada hilo representa un técnico distinto y delega la reserva en el servicio sincronizado.
  - `src/test/java/com/carmotors/servicio/InventarioServicioConcurrenciaTest.java`: prueba unitaria concurrente con JUnit 5 que valida que exactamente un hilo reserve la última unidad disponible.

### 3. Limpieza de código redundante (refactor)
- Se eliminaron los archivos vacíos `com.carmotors.dao.reservaconcurrente` y `com.carmotors.controlador.reserva` que no aportaban funcionalidad y contaminaban el paquete.
- La lógica de concurrencia quedó correctamente distribuida: sección crítica en la capa **servicio** (`InventarioServicio`), acceso a datos en la capa **dao** (`RepuestoDaoImpl`) y simulación en el paquete dedicado **`com.carmotors.simulacion`**.

