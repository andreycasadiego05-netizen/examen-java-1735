package com.carmotors.vista;

import com.carmotors.controlador.ClienteControlador;
import com.carmotors.controlador.FacturaControlador;
import com.carmotors.controlador.ProveedorControlador;
import com.carmotors.controlador.ServicioControlador;
import com.carmotors.dao.impl.ClienteDaoImpl;
import com.carmotors.dao.impl.FacturaDaoImpl;
import com.carmotors.dao.impl.ProveedorDaoImpl;
import com.carmotors.dao.impl.ServicioDaoImpl;
import com.carmotors.servicio.ClienteServicio;
import com.carmotors.servicio.FacturaServicio;
import com.carmotors.servicio.InventarioServiceMock;
import com.carmotors.servicio.ProveedorServicio;
import com.carmotors.servicio.ServicioResumenMock;
import com.carmotors.servicio.TallerServicio;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;
    private final ClienteView clienteView;
    private final MenuProveedor menuProveedor;
    private final MenuServicio menuServicio;
    private final MenuFactura menuFactura;
    private final MenuCatalogo menuCatalogo;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);

        // Inicializar componentes del módulo Clientes
        ClienteDaoImpl clienteDao = new ClienteDaoImpl();
        ClienteServicio clienteServicio = new ClienteServicio(clienteDao);
        ClienteControlador clienteControlador = new ClienteControlador(clienteServicio);
        this.clienteView = new ClienteView(clienteControlador);

        // Inicializar componentes del módulo Proveedores
        ProveedorDaoImpl proveedorDao = new ProveedorDaoImpl();
        ProveedorServicio proveedorServicio = new ProveedorServicio(proveedorDao);
        ProveedorControlador proveedorControlador = new ProveedorControlador(proveedorServicio);
        this.menuProveedor = new MenuProveedor(proveedorControlador);

        // Inicializar componentes del módulo Servicios y Taller
        ServicioDaoImpl servicioDao = new ServicioDaoImpl();
        InventarioServiceMock inventarioMock = new InventarioServiceMock();
        TallerServicio tallerServicio = new TallerServicio(servicioDao, inventarioMock);
        ServicioControlador servicioControlador = new ServicioControlador(tallerServicio);
        this.menuServicio = new MenuServicio(servicioControlador);

        // Inicializar componentes del módulo Facturación (opción 5)
        FacturaDaoImpl facturaDao = new FacturaDaoImpl();
        ServicioResumenMock servicioResumen = new ServicioResumenMock();
        FacturaServicio facturaServicio = new FacturaServicio(facturaDao, servicioResumen);
        FacturaControlador facturaControlador = new FacturaControlador(facturaServicio);
        this.menuFactura = new MenuFactura(scanner, facturaControlador);

        // Inicializar componentes del módulo Catálogos
        this.menuCatalogo = new MenuCatalogo(scanner);
    }

    public void iniciar() {
        int opcion = -1;
        do {
            System.out.println("\n=========================================");
            System.out.println("   SISTEMA DE GESTIÓN TALLER CARMOTORS  ");
            System.out.println("=========================================");
            System.out.println("1. Gestión de Clientes");
            System.out.println("2. Gestión de Vehículos (Próximamente)");
            System.out.println("3. Gestión de Proveedores");
            System.out.println("4. Gestión de Servicios y Taller");
            System.out.println("5. Facturación y Cobros");
            System.out.println("6. Gestión de Catálogos del Sistema");
            System.out.println("0. Salir");
            System.out.println("=========================================");
            System.out.print("Seleccione una opción: ");

            try {
                String input = scanner.nextLine().trim();
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Debe ingresar un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    clienteView.mostrarMenu();
                    break;
                case 2:
                    System.out.println("[Aviso] Módulo en desarrollo.");
                    break;
                case 3:
                    menuProveedor.mostrarMenu();
                    break;
                case 4:
                    menuServicio.mostrarMenu();
                    break;
                case 5:
                    menuFactura.mostrarMenu();
                    break;
                case 6:
                    menuCatalogo.mostrarMenu();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema. ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("[Error] Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }
}
