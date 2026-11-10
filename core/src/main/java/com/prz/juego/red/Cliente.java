package com.prz.juego.red;

import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.hilos.HiloCliente;

import java.util.ArrayList;

public class Cliente {

    private HiloCliente hilo;
    private Personajes personajeLocal;

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

    public void setPersonsajeLocal(Personajes personajeLocal)
    {
        this.personajeLocal = personajeLocal;
    }

    public Personajes getPersonajeLocal() {
        return personajeLocal;
    }

    public Personajes getPersonajeRemoto()
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

    public ArrayList<BolaFuegoRed> getOrbesRecibidos() {
        return hilo.getOrbesRecibidos();
    }

    public ArrayList<BolaFuegoRed> getEstadoOrbes() {
        return hilo.getEstadoOrbes();
    }

    public ArrayList<Integer> getOrbesEliminados()
    {
        return hilo.getOrbesEliminados();
    }

    public void registrarEstadoOrbe(BolaFuegoRed estado)
    {
        hilo.registrarEstadoOrbe(estado);
    }

}
