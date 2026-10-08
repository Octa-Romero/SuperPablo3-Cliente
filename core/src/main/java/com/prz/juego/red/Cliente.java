package com.prz.juego.red;

import com.prz.juego.hilos.HiloCliente;

import java.util.Scanner;

public class Cliente {

    private HiloCliente hc;
    private Scanner s;
    private boolean fin = false;

    public Cliente()
    {
        hc = new HiloCliente();
        s = new Scanner(System.in);
        hc.start();
        do{
            System.out.println("Ingrese una opcion:");
            System.out.println("1. Ingresar un mensaje");
            System.out.println("2. Salir del programa");
            int opcion = s.nextInt();
            s.nextLine();
            if (opcion == 2) {
                fin = true;
            } else {
                System.out.println("Ingrese el mensaje: ");
                String msj = s.nextLine();
                hc.enviarMensaje(msj);
            }
        }while(!fin);
    }
}
