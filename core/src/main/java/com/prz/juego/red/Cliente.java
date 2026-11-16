package com.prz.juego.red;

import com.prz.juego.entidades.jefes.Mano;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.hilos.HiloCliente;

import java.util.ArrayList;
import java.util.List;

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

    public boolean busquedaFallida()
    {
        return hilo.busquedaFallida();
    }

    public boolean encontroServidor()
    {
        return hilo.encontroServidor();
    }

    public String getPersonajeOcupado() {
        return hilo.getPersonajeOcupado();
    }

    public boolean sePuedeSeleccionarNivel()
    {
        return hilo.sePuedeSeleccionarNivel();
    }

    public int getVotoLocal()
    {
        return hilo.getMiVotoNivel();
    }

    public int getVotoRemoto()
    {
        return hilo.getVotoRemotoNivel();
    }

    public int getNivelIniciado()
    {
        return hilo.getNivelParaIniciar();
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

    public ArrayList<ProyectilRed> getProyectilesNuevos() {
        return hilo.getProyectilesNuevos();
    }

    public ArrayList<ProyectilRed> getEstadoProyectiles() {
        return hilo.getEstadoProyectiles();
    }

    public ArrayList<Integer> getProyectilesEliminados()
    {
        return hilo.getProyectilesEliminados();
    }

    public void registrarEstadoProyectil(ProyectilRed estado)
    {
        hilo.registrarEstadoProyectil(estado);
    }

    public JefeRed getJefeRecibido()
    {
        return hilo.getJefeNuevo();
    }

    public JefeRed getEstadoJefe()
    {
        return hilo.getEstadoJefe();
    }

    public boolean jefeMuerto()
    {
        return hilo.getJefeEliminado();
    }

    public ManoRed getEstadoMano(Mano.Lado lado)
    {
        return hilo.getEstadoMano(lado);
    }

    public FuegoSueloRed getEstadoFuegoSuelo()
    {
        return hilo.getEstadoFuegoSuelo();
    }

    public RayosRed getEstadoRayos() {
        return hilo.getEstadoRayos();
    }

    public LasersRed getEstadoLasers() {
        return hilo.getEstadoLasers();
    }

    public ArrayList<MeteoritoRed> getMeteoritosNuevos()
    {
        return hilo.getMeteoritosNuevos();
    }

    public ArrayList<MeteoritoRed> getEstadoMeteoritos()
    {
        return hilo.getEstadoMeteoritos();
    }

    public ArrayList<Integer> getMeteoritosEliminados()
    {
        return hilo.getMeteoritosEliminados();
    }

    public LaserJefeRed getEstadoLaserJefe() {
        return hilo.getEstadoLaserJefe();
    }

    public ArrayList<FueguitoRed> getFueguitosNuevos() {
        return hilo.getFueguitosNuevos();
    }

    public ArrayList<FueguitoRed> getEstadoFueguitos() {
        return hilo.getEstadoFueguitos();
    }

    public ArrayList<FueguitoRed> getFueguitosEliminados() {
        return hilo.getFueguitosEliminados();
    }

    public void cerrar()
    {
        if(hilo != null) {
            hilo.cerrar();
        }
    }
}
