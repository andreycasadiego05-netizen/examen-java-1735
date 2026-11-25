package com.carmotors.simulacion;

import com.carmotors.dao.ClienteDao;
import com.carmotors.dao.RepuestoDao;
import com.carmotors.dao.ServicioDao;
import com.carmotors.dao.VehiculoDao;
import com.carmotors.dao.impl.ClienteDaoImpl;
import com.carmotors.dao.impl.RepuestoDaoImpl;
import com.carmotors.dao.impl.ServicioDaoImpl;
import com.carmotors.dao.impl.VehiculoDaoImpl;
import com.carmotors.modelo.Repuesto;
import com.carmotors.modelo.Servicio;
import com.carmotors.servicio.InventarioServicio;
import com.carmotors.servicio.TallerServicio;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainReservaConcurrente {

    public static void main(String[] args) {
        RepuestoDao repuestoDao = new RepuestoDaoImpl();
        ServicioDao servicioDao = new ServicioDaoImpl();
        ClienteDao clienteDao = new ClienteDaoImpl();
        VehiculoDao vehiculoDao = new VehiculoDaoImpl();

        InventarioServicio inventarioServicio = new InventarioServicio(repuestoDao);
        TallerServicio tallerServicio = new TallerServicio(servicioDao, inventarioServicio);

        EscenarioSimulacion escenario = new EscenarioSimulacion(repuestoDao, clienteDao, vehiculoDao, inventarioServicio, tallerServicio);
        Repuesto repuesto = escenario.prepararRepuestoConStockUno();
        List<Servicio> ordenes = escenario.prepararDosOrdenesActivas();

        System.out.println("Iniciando simulacion de reserva concurrente para el repuesto '" + repuesto.getNombre() + "'...");
        System.out.println("Stock inicial: " + repuesto.getStockActual() + "\n");

        CountDownLatch inicio = new CountDownLatch(1);

        Thread hiloA = new Thread(new TareaReservaTecnico("Hilo-Tecnico-A", ordenes.get(0).getId(), repuesto, tallerServicio, repuestoDao, inicio), "Hilo-Tecnico-A");
        Thread hiloB = new Thread(new TareaReservaTecnico("Hilo-Tecnico-B", ordenes.get(1).getId(), repuesto, tallerServicio, repuestoDao, inicio), "Hilo-Tecnico-B");

        hiloA.start();
        hiloB.start();
        inicio.countDown();

        try {
            hiloA.join();
            hiloB.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La simulacion fue interrumpida: " + e.getMessage());
        }

        int stockFinal = repuestoDao.buscarPorId(repuesto.getId()).map(Repuesto::getStockActual).orElse(-1);
        System.out.println("\nSimulacion finalizada.");
        System.out.println("Stock final del repuesto: " + stockFinal);
    }
}
