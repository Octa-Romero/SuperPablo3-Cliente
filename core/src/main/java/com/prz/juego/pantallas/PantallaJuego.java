package com.prz.juego.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.prz.juego.entidades.Enemigo;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.entidades.Personajes;
import com.prz.juego.principal.Navegable;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.red.Cliente;
import com.prz.juego.red.EnemigoRed;
import com.prz.juego.red.OrbeRed;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.sistemas.Hud;

import java.util.ArrayList;

public class PantallaJuego implements Screen {

    private Cliente cliente;
    private final Navegable nav;
    private Jugador jugador;
    private Jugador jugadorRemoto;
    private final Nivel nivel;
    private final Entrada entrada;
    private final MenuPausa menuPausa;
    private boolean pausado = false;
    private final Hud hud;

    private float tiempoMensaje = 0f;
    private static final float INTERVALO_MENSAJE = 0.25f;

    private String ultimoEstadoIzquierda = "";
    private String ultimoEstadoDerecha = "";
    private String ultimoEstadoArriba = "";
    private String ultimoEstadoAtaque = "";

    public PantallaJuego(Navegable nav, Personajes personajeElegido, Personajes personajeRemoto, Cliente cliente) {
        this.nav = nav;
        this.entrada = new Entrada();
        this.nivel = new Nivel();
        this.menuPausa = new MenuPausa(nav, this);
        this.cliente = cliente;

        jugador = personajeElegido.crear(50, 150);

        jugador.setEntrada(entrada);

        jugadorRemoto = personajeRemoto.crear(75, 150);

        nivel.setJugadorRemoto(jugadorRemoto);

        nivel.cargar("Niveles/Niveles/Level1.tmx", jugador);

        hud = new Hud(jugador, Render.batch);
    }

    @Override
    public void show() {
        InputMultiplexer mux = new InputMultiplexer();

        mux.addProcessor(menuPausa.getStage());
        mux.addProcessor(entrada);

        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        if (entrada.aprietaEscape()) {
            pausado = !pausado;
            if (pausado) {
                menuPausa.activar();
            } else {
                menuPausa.desactivar();
            }
        }

        if (!pausado) {
            nivel.update(delta, entrada);
            actualizarEnvioEntrada(delta);

            if(cliente.llegoPosicionRecibida()){
                jugador.actualizarPosicion(cliente.getX(), cliente.getY(), cliente.getDireccionRecibida());
            }

            jugador.actualizarEstadoAtaque(cliente.getEstadoAtaque());
            jugador.actualizarEstadoVida(cliente.getEstadoVida());

            for(EnemigoRed e : cliente.getEnemigosRecibidos())
            {
                nivel.agregarEnemigos(e);
                cliente.registrarEstadoEnemigo(e);
            }

            for(EnemigoRed e : cliente.getEstadoEnemigos())
            {
                nivel.actualizarEstadoEnemigos(e);
            }

            for (Integer id : cliente.getEnemigosEliminados()) {
                nivel.eliminarEnemigo(id);
            }

            for(OrbeRed o : cliente.getOrbesRecibidos())
            {
                nivel.agregarOrbes(o);
                cliente.registrarEstadoOrbe(o);
            }

            for(OrbeRed o : cliente.getEstadoOrbes())
            {
                nivel.actualizarEstadoOrbes(o);
            }

            for (Integer id : cliente.getOrbesEliminados()) {
                nivel.eliminarOrbes(id);
            }
        }

        if (pausado) {
            Gdx.input.setInputProcessor(menuPausa.getStage());
        } else {
            Gdx.input.setInputProcessor(entrada);
        }

        nivel.renderMapa();

        Render.begin(nivel.getCamara());
        nivel.render();
        Render.end();

        nivel.renderDebug();

        hud.dibujar();

        if (pausado) {
            menuPausa.update(delta);
            menuPausa.render();
        }

        if(nivel.isGameOver())
        {
            Musica.parar();
            nav.cambiarPantalla(new GameOver(nav));
        }
    }

    private void actualizarEnvioEntrada(float delta) {
        tiempoMensaje += delta;

        String estadoIzquierda = obtenerEstadoIzquierda();
        String estadoDerecha = obtenerEstadoDerecha();
        String estadoArriba = obtenerEstadoArriba();
        String estadoAtaque = obtenerEstadoAtaque();

        if (!estadoIzquierda.equals(ultimoEstadoIzquierda)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoIzquierda);
            ultimoEstadoIzquierda = estadoIzquierda;
        }

        if (!estadoDerecha.equals(ultimoEstadoDerecha)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoDerecha);
            ultimoEstadoDerecha = estadoDerecha;
        }

        if (!estadoArriba.equals(ultimoEstadoArriba)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoArriba);
            ultimoEstadoArriba = estadoArriba;
        }

        if (!estadoAtaque.equals(ultimoEstadoAtaque)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoAtaque);
            ultimoEstadoAtaque = estadoAtaque;
        }

        if (tiempoMensaje >= INTERVALO_MENSAJE) {
            tiempoMensaje = 0f;
        }
    }


    private String obtenerEstadoIzquierda() {
        if (entrada.mueveIzquierda()) {
            return "INPUT;IZQUIERDA";
        }
        return "INPUT;NOIZQUIERDA";
    }

    private String obtenerEstadoDerecha() {
        if (entrada.mueveDerecha()) {
            return "INPUT;DERECHA";
        }
        return "INPUT;NODERECHA";
    }

    private String obtenerEstadoArriba() {
        if (entrada.mueveArriba()) {
            return "INPUT;ARRIBA";
        }
        return "INPUT;NOARRIBA";
    }

    private String obtenerEstadoAtaque() {
        if (entrada.ataca()) {
            return "INPUT;ATACA";
        }
        return "INPUT;NOATACA";
    }

    @Override
    public void resize(int width, int height) {
        nivel.resize(width, height);
        menuPausa.resize(width, height);
        hud.actualizarTamano(width, height);
    }

    public void setPausa(boolean valor)
    {
        this.pausado = valor;
    }

    public void guardarPosicionCamara() {
        nivel.guardarPosicionCamara();
    }

    public void restaurarPosicionCamara() {
        nivel.restaurarPosicionCamara();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        nivel.dispose();
        menuPausa.dispose();
    }
}
