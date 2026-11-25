package com.carmotors.servicio;

import com.carmotors.dao.RepuestoDao;
import com.carmotors.modelo.Repuesto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InventarioServicioConcurrenciaTest {

    @Test
    @DisplayName("Prueba de funcionamiento")
    void verificarFuncionamiento() throws InterruptedException {
        InMemoryRepuestoDao repuestoDao = new InMemoryRepuestoDao();
        repuestoDao.guardar(new Repuesto(1, "FIL-XY-123", "Filtro de Aceite XY-123", 5, 1, 18000, 30000, 1, 1, "A-1"));
        InventarioServicio inventarioServicio = new InventarioServicio(repuestoDao);

        CountDownLatch inicio = new CountDownLatch(1);
        CountDownLatch fin = new CountDownLatch(2);
        AtomicInteger exitosos = new AtomicInteger(0);
        AtomicInteger fallidos = new AtomicInteger(0);

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try {
                    inicio.await();
                    if (inventarioServicio.verificarYDescontarStock(1, 1)) exitosos.incrementAndGet();
                    else fallidos.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    fin.countDown();
                }
            }).start();
        }

        inicio.countDown();
        fin.await();
        assertEquals(1, exitosos.get());
        assertEquals(1, fallidos.get());
        assertEquals(0, repuestoDao.buscarPorId(1).orElseThrow().getStockActual());
    }

    private static class InMemoryRepuestoDao implements RepuestoDao {
        private final Map<Integer, Repuesto> almacen = new ConcurrentHashMap<>();

        @Override
        public void guardar(Repuesto repuesto) {
            int id = repuesto.getId() != null ? repuesto.getId() : almacen.size() + 1;
            repuesto.setId(id);
            almacen.put(id, copiar(repuesto));
        }

        @Override
        public Optional<Repuesto> buscarPorId(int id) {
            Repuesto encontrado = almacen.get(id);
            if (encontrado == null) return Optional.empty();
            Thread.yield();
            return Optional.of(copiar(encontrado));
        }

        @Override
        public Optional<Repuesto> buscarPorCodigo(String codigo) {
            return almacen.values().stream().filter(r -> r.getCodigo().equalsIgnoreCase(codigo)).findFirst().map(this::copiar);
        }

        @Override
        public List<Repuesto> listarTodos() {
            List<Repuesto> lista = new ArrayList<>();
            for (Repuesto r : almacen.values()) lista.add(copiar(r));
            return lista;
        }

        @Override
        public void actualizarStock(int id, int nuevoStock) {
            Repuesto r = almacen.get(id);
            if (r != null) r.setStockActual(nuevoStock);
        }

        @Override
        public List<Repuesto> listarBajoStockMinimo() {
            return listarTodos().stream().filter(r -> r.getStockActual() <= r.getStockMinimo()).toList();
        }

        private Repuesto copiar(Repuesto r) {
            return new Repuesto(r.getId(), r.getCodigo(), r.getNombre(), r.getRepuestoTipoId(), r.getProveedorId(), r.getPrecioCompra(), r.getPrecioVenta(), r.getStockActual(), r.getStockMinimo(), r.getUbicacion());
        }
    }
}
