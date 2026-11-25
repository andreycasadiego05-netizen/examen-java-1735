package com.carmotors.simulacion;

import com.carmotors.modelo.Repuesto;
import com.carmotors.modelo.Servicio;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainReservaConcurrente {

    public static void main(String[] args) {
        EscenarioSimulacion escenario = new EscenarioSimulacion();
        Repuesto repuesto = escenario.prepararRepuestoConStockUno();
        List<Servicio> ordenes = escenario.prepararDosOrdenesActivas();

        System.out.println("Iniciando simulacion de reserva concurrente para el repuesto '" + repuesto.getNombre() + "'...");
        System.out.println("Stock inicial: " + repuesto.getStockActual() + "\n");

        CountDownLatch inicio = new CountDownLatch(1);

        Thread hiloA = new Thread(new TareaReservaTecnico("Hilo-Tecnico-A", ordenes.get(0).getId(), repuesto, escenario.getTallerServicio(), escenario.getRepuestoDao(), inicio), "Hilo-Tecnico-A");
        Thread hiloB = new Thread(new TareaReservaTecnico("Hilo-Tecnico-B", ordenes.get(1).getId(), repuesto, escenario.getTallerServicio(), escenario.getRepuestoDao(), inicio), "Hilo-Tecnico-B");

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

        int stockFinal = escenario.getRepuestoDao().buscarPorId(repuesto.getId()).map(Repuesto::getStockActual).orElse(-1);
        System.out.println("\nSimulacion finalizada.");
        System.out.println("Stock final del repuesto: " + stockFinal);
    }
}
