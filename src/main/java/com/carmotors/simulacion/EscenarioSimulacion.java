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
import java.util.ArrayList;
import java.util.List;

public class EscenarioSimulacion {

    private final RepuestoDao repuestoDao;
    private final ClienteDao clienteDao;
    private final VehiculoDao vehiculoDao;
    private final InventarioServicio inventarioServicio;
    private final TallerServicio tallerServicio;

    public EscenarioSimulacion() {
        this.repuestoDao = new RepuestoDaoImpl();
        this.clienteDao = new ClienteDaoImpl();
        this.vehiculoDao = new VehiculoDaoImpl();
        ServicioDao servicioDao = new ServicioDaoImpl();
        this.inventarioServicio = new InventarioServicio(repuestoDao);
        this.tallerServicio = new TallerServicio(servicioDao, inventarioServicio);
    }

    public RepuestoDao getRepuestoDao() {
        return repuestoDao;
    }

    public TallerServicio getTallerServicio() {
        return tallerServicio;
    }

    public Repuesto prepararRepuestoConStockUno() {
        List<Repuesto> repuestos = inventarioServicio.listarTodos();
        if (!repuestos.isEmpty()) {
            Repuesto repuesto = repuestos.get(0);
            repuestoDao.actualizarStock(repuesto.getId(), 1);
            repuesto.setStockActual(1);
            return repuesto;
        }
        return inventarioServicio.registrarRepuesto("FIL-XY-123", "Filtro de Aceite XY-123", 5, 1, 18000.0, 30000.0, 1, 1, "Estante Simulacion");
    }

    public List<Servicio> prepararDosOrdenesActivas() {
        List<Servicio> activas = new ArrayList<>(tallerServicio.listarOrdenesActivas());
        if (activas.size() >= 2) {
            return List.of(activas.get(0), activas.get(1));
        }
        Vehiculo vehiculo = obtenerOCrearVehiculo();
        while (activas.size() < 2) {
            Servicio orden = new Servicio(vehiculo.getId(), 1, LocalDateTime.now(), null, "Orden simulacion concurrencia", 50000.0, Servicio.ESTADO_EN_PROCESO);
            tallerServicio.registrarOrden(orden);
            activas.add(orden);
        }
        return List.of(activas.get(0), activas.get(1));
    }

    private Vehiculo obtenerOCrearVehiculo() {
        List<Vehiculo> vehiculos = vehiculoDao.listarTodos();
        if (!vehiculos.isEmpty()) {
            return vehiculos.get(0);
        }
        Cliente cliente = clienteDao.listarTodos().stream().findFirst().orElse(null);
        if (cliente == null) {
            cliente = new Cliente("Cliente Simulacion", "SIM-CONC-001", "3000000000", "simulacion@carmotors.local");
            clienteDao.guardar(cliente);
        }
        Vehiculo vehiculo = new Vehiculo(cliente.getId(), "SIM123", "Toyota", "Corolla", 2022, 25000);
        vehiculoDao.guardar(vehiculo);
        return vehiculo;
    }
}
