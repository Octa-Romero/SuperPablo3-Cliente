package com.prz.juego.red;

import com.prz.juego.hilos.HiloCliente;

import java.util.ArrayList;
import java.util.Scanner;

public class Cliente {

    private HiloCliente hilo;

    public void conectar() {
        if (hilo == null) {
            hilo = new HiloCliente();
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

    public JugadorEstadoRed getEstadoLocal()
    {
        return hilo.getEstadoLocal();
    }

    public JugadorEstadoRed getEstadoRemoto()
    {
        return hilo.getEstadoRemoto();
    }

    public ArrayList<EnemigoRed> getEnemigosRecibidos() {
        return hilo.getEnemigosRecibidos();
    }

    public ArrayList<EnemigoRed> getEstadoEnemigos() {
        return hilo.getEstadoEnemigos();
    }

    public ArrayList<Integer> getEnemigosEliminados()
    {
        return hilo.getEnemigosEliminados();
    }

    public void registrarEstadoEnemigo(EnemigoRed estado)
    {
        hilo.registrarEstadoEnemigo(estado);
    }

    public ArrayList<OrbeRed> getOrbesRecibidos() {
        return hilo.getOrbesRecibidos();
    }

    public ArrayList<OrbeRed> getEstadoOrbes() {
        return hilo.getEstadoOrbes();
    }

    public ArrayList<Integer> getOrbesEliminados()
    {
        return hilo.getOrbesEliminados();
    }

    public void registrarEstadoOrbe(OrbeRed estado)
    {
        hilo.registrarEstadoOrbe(estado);
    }

}
