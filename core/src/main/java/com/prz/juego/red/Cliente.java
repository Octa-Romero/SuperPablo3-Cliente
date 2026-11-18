package com.prz.juego.red;

import com.prz.juego.entidades.Proyectil;
import com.prz.juego.entidades.Tumba;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.entidades.retumbar.Fueguito;
import com.prz.juego.entidades.retumbar.Retumbar;
import com.prz.juego.entidades.retumbar.Meteorito;

import java.util.ArrayList;

public class Cliente {

    private HiloCliente hilo;
    private Personajes personajeLocal;
    private int idCliente = 0;

    public void setClienteId(int id) {
        this.idCliente = id;
    }

    public void conectar() {
        if (hilo == null) {
            hilo = new HiloCliente(idCliente);
            hilo.start();
        }
    }

    public void desconectar() {
        if (hilo != null) {
            hilo.cerrar();
            hilo = null;
        }
    }

    public void enviarMensaje(String mensaje) {
        if (hilo != null) {
            hilo.enviarMensaje(mensaje);
        }
    }

    public boolean isConexionEstablecida() {
        return hilo != null && hilo.isConexionEstablecida();
    }

    public boolean busquedaFallida() {
        return hilo != null && hilo.busquedaFallida();
    }

    public boolean encontroServidor() {
        return hilo != null && hilo.encontroServidor();
    }

    public String getPersonajeOcupado() {
        return hilo.getPersonajeOcupado();
    }

    public String getPersonajeRemotoElegido() {
        return hilo != null ? hilo.getPersonajeRemotoElegido() : null;
    }

    public int getContadorComenzar() {
        return hilo != null ? hilo.getContadorComenzar() : 0;
    }

    public boolean isListoParaComenzar() {
        return hilo != null && hilo.isListoParaComenzar();
    }

    public void setListoParaComenzar(boolean listo) {
        if (hilo != null) {
            hilo.setListoParaComenzar(listo);
        }
    }

    public boolean sePuedeSeleccionarNivel() {
        return hilo.sePuedeSeleccionarNivel();
    }

    public int getVotoLocal() {
        return hilo.getMiVotoNivel();
    }

    public int getVotoRemoto() {
        return hilo.getVotoRemotoNivel();
    }

    public int getNivelIniciado() {
        return hilo.getNivelParaIniciar();
    }

    public void setPersonsajeLocal(Personajes personajeLocal) {
        this.personajeLocal = personajeLocal;
    }

    public Personajes getPersonajeLocal() {
        if (personajeLocal == null && hilo != null) {
            Personajes remoto = hilo.getPersonajeRemoto();
            if (remoto != null) {
                personajeLocal = remoto == Personajes.PABLO ? Personajes.WALTER : Personajes.PABLO;
            }
        }
        return personajeLocal;
    }

    public boolean consumirSeleccionCancelada() {
        return hilo != null && hilo.consumirSeleccionCancelada();
    }

    public Personajes getPersonajeRemoto() {
        return hilo.getPersonajeRemoto();
    }

    public JugadorEstado getEstadoLocal() {
        return hilo.getEstadoLocal();
    }

    public JugadorEstado getEstadoRemoto() {
        return hilo.getEstadoRemoto();
    }

    public ArrayList<Enemigo> getEnemigosRecibidos() {
        return hilo.getEnemigosRecibidos();
    }

    public ArrayList<Integer> getEnemigosEliminados() {
        return hilo.getEnemigosEliminados();
    }

    public void registrarEstadoEnemigo(Enemigo estado) {
        hilo.registrarEstadoEnemigo(estado);
    }

    public ArrayList<Proyectil> getProyectilesNuevos() {
        return hilo.getProyectilesNuevos();
    }

    public ArrayList<Proyectil> getEstadoProyectiles() {
        return hilo.getEstadoProyectiles();
    }

    public ArrayList<Integer> getProyectilesEliminados() {
        return hilo.getProyectilesEliminados();
    }

    public void registrarEstadoProyectil(Proyectil estado) {
        hilo.registrarEstadoProyectil(estado);
    }

    public ArrayList<Tumba> getEstadoTumbas() {
        return hilo.getEstadoTumbas();
    }

    public Retumbar getJefeRecibido() {
        return hilo.getJefeNuevo();
    }

    public ArrayList<Meteorito> getEstadoMeteoritos() {
        return hilo.getEstadoMeteoritos();
    }

    public ArrayList<Integer> getMeteoritosEliminados() {
        return hilo.getMeteoritosEliminados();
    }

    public ArrayList<Fueguito> getEstadoFueguitos() {
        return hilo.getEstadoFueguitos();
    }

    public ArrayList<Fueguito> getFueguitosEliminados() {
        return hilo.getFueguitosEliminados();
    }

    public void actualizarCamara(float x, float y) {
        if (hilo != null) {
            hilo.actualizarCamara(x, y);
        }
    }

    public boolean otroJugadorVaAlHub() {
        return hilo != null && hilo.getOtroJugadorVaAlHub();
    }

    public boolean ambosJugadoresVuelvenHub() {
        return hilo != null && hilo.getAmbosJugadoresVuelvenHub();
    }

    public void resetearVolverHub() {
        if (hilo != null) {
            hilo.resetearVolverHub();
        }
    }

    public void limpiarEntidades() {
        if (hilo != null) {
            hilo.limpiarEntidades();
        }
    }

    public void solicitarSnapshotEntidades() {
        if (hilo != null) {
            hilo.solicitarSnapshotEntidades();
        }
    }

    public boolean hayReliquiaNueva() {
        return hilo.hayReliquiaNueva();
    }

    public boolean fueRecogidaLaReliquia() {
        return hilo.fueRecogidaLaReliquia();
    }

    public float getReliquiaX() {
        return hilo.getReliquiaX();
    }

    public float getReliquiaY() {
        return hilo.getReliquiaY();
    }

    public boolean necesitaSnapshot() {
        return hilo != null && hilo.necesitaSnapshot();
    }

    public boolean isExpulsado() {
        return hilo != null && hilo.isExpulsado();
    }

    public int getSegundosReconexion() {
        return hilo != null ? hilo.getSegundosReconexion() : 0;
    }

    public boolean otroJugadorCanceloHub() {
        return hilo != null && hilo.getOtroJugadorCanceloHub();
    }

    public boolean getMostrarCreditos() {
        return hilo != null && hilo.getMostrarCreditos();
    }

    public boolean getEliminarAtaquesJefe() {
        return hilo != null && hilo.getEliminarAtaquesJefe();
    }

    public boolean isServidorPerdido() {
        return hilo != null && hilo.isServidorPerdido();
    }

    public boolean isJugadorRemotoDesconectado() {
        return hilo != null && hilo.isJugadorRemotoDesconectado();
    }

    public void resetearJugadorRemotoDesconectado() {
        if (hilo != null) {
            hilo.resetearJugadorRemotoDesconectado();
        }
    }

    public String getEstadoReconexion() {
        return hilo != null ? hilo.getEstadoReconexion() : null;
    }

    public boolean isJugadorRemotoReconecto() {
        return hilo != null && hilo.isJugadorRemotoReconecto();
    }

    public String getNivelReconexion() {
        return hilo != null ? hilo.getNivelReconexion() : null;
    }

    public void cerrar() {
        if (hilo != null) {
            hilo.cerrar();
        }
    }
}
