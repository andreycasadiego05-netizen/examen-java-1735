package com.carmotors.simulacion;

import com.carmotors.dao.RepuestoDao;
import com.carmotors.modelo.Repuesto;
import com.carmotors.servicio.TallerServicio;
import java.util.concurrent.CountDownLatch;

public class TareaReservaTecnico implements Runnable {

    private final String nombreTecnico;
    private final int servicioId;
    private final Repuesto repuesto;
    private final TallerServicio tallerServicio;
    private final RepuestoDao repuestoDao;
    private final CountDownLatch inicio;

    public TareaReservaTecnico(String nombreTecnico, int servicioId, Repuesto repuesto, TallerServicio tallerServicio, RepuestoDao repuestoDao, CountDownLatch inicio) {
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
            inicio.await();
            System.out.println(nombreTecnico + ": Intentando reservar el repuesto...");
            tallerServicio.agregarRepuestoAOrden(servicioId, repuesto.getId(), 1, repuesto.getPrecioVenta());
            int stock = repuestoDao.buscarPorId(repuesto.getId()).map(Repuesto::getStockActual).orElse(-1);
            System.out.println(nombreTecnico + ": Verificando stock. Hay " + (stock + 1) + " disponible.");
            System.out.println(nombreTecnico + ": ¡Reserva exitosa! Stock actualizado a " + stock + ".");
        } catch (IllegalStateException e) {
            int stock = repuestoDao.buscarPorId(repuesto.getId()).map(Repuesto::getStockActual).orElse(-1);
            System.out.println(nombreTecnico + ": Verificando stock. Hay " + stock + " disponible.");
            System.out.println(nombreTecnico + ": Fallo en la reserva. No hay suficiente stock.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(nombreTecnico + ": hilo interrumpido.");
        }
    }
}
