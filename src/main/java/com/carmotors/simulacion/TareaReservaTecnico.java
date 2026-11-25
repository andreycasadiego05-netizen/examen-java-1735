package com.carmotors.simulacion;

import com.carmotors.dao.RepuestoDao;
import com.carmotors.modelo.Repuesto;
import com.carmotors.servicio.TallerServicio;
import java.util.concurrent.CountDownLatch;

/**
 * Representa la tarea concurrente de un técnico que intenta reservar
 * un repuesto para una orden de servicio.
 *
 * <p>Implementa {@link Runnable} para ser ejecutada en un hilo independiente.
 * La sección crítica (verificar y descontar stock) está protegida por
 * {@code synchronized} dentro de {@link com.carmotors.servicio.InventarioServicio}.</p>
 */
public class TareaReservaTecnico implements Runnable {

    private final String nombreTecnico;
    private final int servicioId;
    private final Repuesto repuesto;
    private final TallerServicio tallerServicio;
    private final RepuestoDao repuestoDao;
    private final CountDownLatch inicio;

    public TareaReservaTecnico(String nombreTecnico, int servicioId, Repuesto repuesto,
                               TallerServicio tallerServicio, RepuestoDao repuestoDao,
                               CountDownLatch inicio) {
        this.nombreTecnico = nombreTecnico;
        this.servicioId = servicioId;
        this.repuesto = repuesto;
        this.tallerServicio = tallerServicio;
        this.repuestoDao = repuestoDao;
        this.inicio = inicio;
    }

    @Override
    public void run() {
        try {
            // Esperar a que todos los hilos estén listos antes de intentar la reserva
            inicio.await();

            System.out.println(nombreTecnico + ": Intentando reservar el repuesto...");

            // Delega en TallerServicio → InventarioServicio.verificarYDescontarStock (synchronized)
            tallerServicio.agregarRepuestoAOrden(servicioId, repuesto.getId(), 1, repuesto.getPrecioVenta());

            int stockTrasReserva = repuestoDao.buscarPorId(repuesto.getId())
                    .map(Repuesto::getStockActual).orElse(-1);
            System.out.println(nombreTecnico + ": Verificando stock. Hay " + (stockTrasReserva + 1) + " disponible.");
            System.out.println(nombreTecnico + ": ¡Reserva exitosa! Stock actualizado a " + stockTrasReserva + ".");

        } catch (IllegalStateException e) {
            int stockActual = repuestoDao.buscarPorId(repuesto.getId())
                    .map(Repuesto::getStockActual).orElse(-1);
            System.out.println(nombreTecnico + ": Verificando stock. Hay " + stockActual + " disponible.");
            System.out.println(nombreTecnico + ": Fallo en la reserva. No hay suficiente stock.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(nombreTecnico + ": hilo interrumpido.");
        }
    }
}
