package com.prz.juego.red;

import com.prz.juego.hilos.HiloCliente;

import java.util.Scanner;

public class Cliente {

    private Scanner s = new Scanner(System.in);
    private HiloCliente hc;
    private boolean fin = false;
    private volatile boolean empiezaChat = false;

    public Cliente()
    {
        hc = new HiloCliente(this);
        hc.start();
        System.out.println("Esperando al otro participante...");
        synchronized (this) {
            while (!empiezaChat) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        do {
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
        } while (!fin);
    }

    public synchronized void empezarChat(boolean valor) {
        this.empiezaChat = valor;
        notify();
    }
}
