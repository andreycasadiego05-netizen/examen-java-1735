package com.carmotors.vista;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;
    private final MenuCatalogo menuCatalogo;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.menuCatalogo = new MenuCatalogo(scanner);
    }

    public void iniciar() {
        int opcion = -1;
        do {
            System.out.println("\n==========================================");
            System.out.println("   SISTEMA DE GESTION CARMOTORS (SGT)     ");
            System.out.println("==========================================");
            System.out.println("1. Gestion de Clientes");
            System.out.println("2. Gestion de Catalogos del Sistema");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            String input = scanner.nextLine().trim();
            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.println("Modulo de Clientes disponible en su controlador respectivo.");
                    break;
                case 2:
                    menuCatalogo.mostrarMenu();
                    break;
                case 0:
                    System.out.println("Saliendo de CarMotors. Hasta pronto.");
                    break;
                default:
                    System.out.println("Opcion fuera de rango.");
                    break;
            }
        } while (opcion != 0);
    }

    public static void main(String[] args) {
        new MenuPrincipal().iniciar();
    }
}
