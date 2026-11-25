# proyecto-java-grupo05

Sistema de Gestión de Taller Automotriz (SGT) - **CarMotors**.


## Historial de Cambios y Soluciones Técnicas

### 1. Módulos base del taller
Implementación de gestión de clientes, vehículos, proveedores, inventario de repuestos, órdenes de servicio y facturación bajo arquitectura en capas (`vista`, `controlador`, `servicio`, `dao`, `modelo`).

### 2. Reserva Concurrente de Repuestos (Condición de Carrera y Sincronización)
- **Problema (Condición de carrera):** En `InventarioServicio.verificarYDescontarStock`, la secuencia de lectura del stock, validación de disponibilidad y actualización no estaba protegida contra concurrencia. Si dos técnicos intentaban reservar al mismo tiempo la última unidad, ambos podían leer el stock antes de descontarlo y reservar un mismo repuesto físico.
- **Solución implementada:** Se aplicó sincronización en Java (`synchronized`) sobre `verificarYDescontarStock` en `InventarioServicio`, se implemento la solucion requerida para que solo un hilo pueda reservar la última unidad y el segundo sea rechazado por stock insuficiente.
- **Simulación y pruebas:**
  - `src/main/java/com/carmotors/simulacion/MainReservaConcurrente.java`: clase independiente del menú principal que prepara un repuesto con stock `1` y dos órdenes de servicio, lanza dos hilos (`Tecnico A` y `Tecnico B`) sincronizados con `CountDownLatch` usando `TallerServicio.agregarRepuestoAOrden(...)`, y verifica en base de datos que el stock final sea `0`.
  - `src/test/java/com/carmotors/servicio/InventarioServicioConcurrenciaTest.java`: prueba unitaria concurrente con JUnit 5 que valida que exactamente un hilo reserve la última unidad disponible.
