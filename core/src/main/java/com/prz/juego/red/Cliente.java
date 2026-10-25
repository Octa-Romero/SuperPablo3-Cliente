package com.prz.juego.red;

import com.prz.juego.hilos.HiloCliente;

import java.util.Scanner;

public class Cliente {

    private HiloCliente hilo;

    public void conectar() {
        if (hilo == null) {
            hilo = new HiloCliente(this);
            hilo.start();
        }
    }

    public void enviarMensaje(String mensaje) {
        if (hilo != null) {
            hilo.enviarMensaje(mensaje);
        }
    }

    public boolean isConexionEstablecida()
    {
        return hilo.isConexionEstablecida();
    }

    public void cerrar()
    {
        if(hilo != null) {
            hilo.cerrar();
        }
    }

    public String getPersonajeRemoto()
    {
        return hilo.getPersonajeRemoto();
    }

    public float getX()
    {
        return hilo.getXRecibida();
    }

    public float getY()
    {
        return hilo.getYRecibida();
    }

    public boolean llegoPosicionRecibida()
    {
        return hilo.isPosicionRecibida();
    }
}
