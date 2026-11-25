package com.carmotors.simulacion;

import com.carmotors.dao.ClienteDao;
import com.carmotors.dao.RepuestoDao;
import com.carmotors.dao.ServicioDao;
import com.carmotors.dao.VehiculoDao;
import com.carmotors.dao.impl.ClienteDaoImpl;
import com.carmotors.dao.impl.RepuestoDaoImpl;
import com.carmotors.dao.impl.ServicioDaoImpl;
import com.carmotors.dao.impl.VehiculoDaoImpl;
import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.Repuesto;
import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.servicio.InventarioServicio;
import com.carmotors.servicio.TallerServicio;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class MainReservaConcurrente {

    private static final String CODIGO_REPUESTO = "FIL-XY-123";
    private static final String NOMBRE_REPUESTO = "Filtro de Aceite XY-123";

    public static void main(String[] args) {
        RepuestoDao repuestoDao = new RepuestoDaoImpl();
        ServicioDao servicioDao = new ServicioDaoImpl();
        ClienteDao clienteDao = new ClienteDaoImpl();
        VehiculoDao vehiculoDao = new VehiculoDaoImpl();
        InventarioServicio inventarioServicio = new InventarioServicio(repuestoDao);
        TallerServicio tallerServicio = new TallerServicio(servicioDao, inventarioServicio);

        Repuesto repuesto = prepararRepuestoConStockUno(repuestoDao);
        Vehiculo vehiculo = prepararVehiculoPrueba(clienteDao, vehiculoDao);
        Servicio ordenA = crearOrdenPrueba(servicioDao, vehiculo.getId(), "Orden simulacion Tecnico A");
        Servicio ordenB = crearOrdenPrueba(servicioDao, vehiculo.getId(), "Orden simulacion Tecnico B");

        System.out.println("Iniciando simulacion de reserva para " + repuesto.getNombre());
        System.out.println("Stock inicial " + repuesto.getStockActual() + "\n");

        CountDownLatch inicio = new CountDownLatch(1);
        Thread tecnicoA = new Thread(() -> reservarRepuesto("Tecnico A", ordenA.getId(), repuesto, tallerServicio, repuestoDao, inicio));
        Thread tecnicoB = new Thread(() -> reservarRepuesto("Tecnico B", ordenB.getId(), repuesto, tallerServicio, repuestoDao, inicio));
        tecnicoA.start();
        tecnicoB.start();
        inicio.countDown();

        try {
            tecnicoA.join();
            tecnicoB.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Se interrumpio la simulacion " + e.getMessage());
        }

        int stockFinal = repuestoDao.buscarPorId(repuesto.getId()).map(Repuesto::getStockActual).orElse(-1);
        System.out.println("\nSimulacion terminada");
        System.out.println("Stock final del repuesto " + stockFinal);
    }

    private static Repuesto prepararRepuestoConStockUno(RepuestoDao repuestoDao) {
        Optional<Repuesto> existente = repuestoDao.buscarPorCodigo(CODIGO_REPUESTO);
        if (existente.isPresent()) {
            Repuesto r = existente.get();
            repuestoDao.actualizarStock(r.getId(), 1);
            r.setStockActual(1);
            return r;
        }
        Repuesto nuevo = new Repuesto(CODIGO_REPUESTO, NOMBRE_REPUESTO, 5, 1, 18000.0, 30000.0, 1, 1, "Estante Simulacion");
        repuestoDao.guardar(nuevo);
        return nuevo;
    }

    private static Vehiculo prepararVehiculoPrueba(ClienteDao clienteDao, VehiculoDao vehiculoDao) {
        Cliente cliente = clienteDao.buscarPorDocumento("SIM-CONC-001").orElse(null);
        if (cliente == null) {
            cliente = new Cliente("Cliente Simulacion", "SIM-CONC-001", "3000000000", "simulacion@carmotors.local");
            clienteDao.guardar(cliente);
        }
        Vehiculo vehiculo = vehiculoDao.buscarPorPlaca("SIM123").orElse(null);
        if (vehiculo == null) {
            vehiculo = new Vehiculo(cliente.getId(), "SIM123", "Toyota", "Corolla", 2022, 25000);
            vehiculoDao.guardar(vehiculo);
        }
        return vehiculo;
    }

    private static Servicio crearOrdenPrueba(ServicioDao servicioDao, int vehiculoId, String descripcion) {
        Servicio orden = new Servicio(vehiculoId, 1, LocalDateTime.now(), null, descripcion, 50000.0, Servicio.ESTADO_EN_PROCESO);
        servicioDao.guardar(orden);
        return orden;
    }

    private static void reservarRepuesto(String tecnico, int servicioId, Repuesto repuesto, TallerServicio tallerServicio, RepuestoDao repuestoDao, CountDownLatch inicio) {
        try {
            inicio.await();
            System.out.println(tecnico + " intentando reservar para orden " + servicioId);
            tallerServicio.agregarRepuestoAOrden(servicioId, repuesto.getId(), 1, repuesto.getPrecioVenta());
            int stockActual = repuestoDao.buscarPorId(repuesto.getId()).map(Repuesto::getStockActual).orElse(-1);
            System.out.println(tecnico + " logro reservar para orden " + servicioId + " stock actual " + stockActual);
        } catch (IllegalStateException e) {
            int stockActual = repuestoDao.buscarPorId(repuesto.getId()).map(Repuesto::getStockActual).orElse(-1);
            System.out.println(tecnico + " no pudo reservar para orden " + servicioId + " sin stock disponible " + stockActual);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(tecnico + " hilo interrumpido");
        }
    }
}
