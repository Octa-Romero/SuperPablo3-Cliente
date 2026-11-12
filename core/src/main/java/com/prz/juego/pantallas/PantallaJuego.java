package com.prz.juego.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.principal.Navegable;
import com.prz.juego.niveles.Hub;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.red.Cliente;
import com.prz.juego.red.EnemigoRed;
import com.prz.juego.red.ProyectilRed;
import com.prz.juego.red.JugadorEstadoRed;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class PantallaJuego implements Screen {

    private Cliente cliente;
    private final Navegable NAV;
    private Jugador jugadorLocal;
    private JugadorEstadoRed estadoRedLocal;
    private Jugador jugadorRemoto;

    private final Nivel NIVEL;
    private final Entrada ENTRADA;
    private final MenuPausa MENU_PAUSA;
    private boolean pausado = false;
    private final Hud HUD;
    private Hub hub;

    private float tiempoMensaje = 0f;
    private static final float INTERVALO_MENSAJE = 0.25f;

    private String ultimoEstadoIzquierda = "";
    private String ultimoEstadoDerecha = "";
    private String ultimoEstadoArriba = "";
    private String ultimoEstadoAbajo = "";
    private String ultimoEstadoAtaque = "";
    private String ultimoEstadoE = "";

    public PantallaJuego(Navegable NAV, Cliente cliente, String rutaNivel) {
        this.NAV = NAV;
        this.ENTRADA = new Entrada();
        this.NIVEL = rutaNivel == null ? null : Nivel.crear(rutaNivel);
        this.MENU_PAUSA = new MenuPausa(NAV, this);
        this.cliente = cliente;

        Personajes personajeElegido = cliente.getPersonajeLocal();
        Personajes personajeRemoto = cliente.getPersonajeRemoto();

        jugadorLocal = personajeElegido.crear(50, 150);
        jugadorLocal.setEntrada(ENTRADA);
        jugadorLocal.setEstadoRed(cliente.getEstadoLocal());

        this.estadoRedLocal = jugadorLocal.getEstadoRed();

        jugadorRemoto = personajeRemoto.crear(75, 150);
        jugadorRemoto.setEstadoRed(cliente.getEstadoRemoto());

        if (rutaNivel == null) {
            hub = new Hub();
            hub.cargar("Niveles/Niveles/Hub.tmx", jugadorLocal, jugadorRemoto);
        } else {
            hub = null;
            NIVEL.cargar(jugadorLocal, jugadorRemoto);
        }

        HUD = new Hud(jugadorLocal, Render.batch);

        if (NIVEL != null) {
            HUD.setJefe(NIVEL.getJefe());
        }
    }

    @Override
    public void show() {
        //Gdx.input.setCursorCatched(!pausado);

        InputMultiplexer mux = new InputMultiplexer();

        mux.addProcessor(MENU_PAUSA.getStage());
        mux.addProcessor(ENTRADA);

        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        if (ENTRADA.aprietaEscape()) {
            setPausa(!pausado);

            if (pausado) {
                MENU_PAUSA.activar();
            } else {
                MENU_PAUSA.desactivar();
            }
        }

        if (!pausado) {
            actualizarEnvioEntrada(delta);

            if (hub != null) {
                hub.update(delta, ENTRADA);

                if (ENTRADA.aprietaE()) {

                    if (estadoRedLocal.estaEsperandoSeleccion()) {

                        cliente.enviarMensaje("SELECCION_NIVEL;false");

                    } else if (hub.puedeInteractuarPancrasio()) {

                        hub.interactuarPancrasio();

                    } else if (estadoRedLocal.estaEnCentroHub()) {

                        cliente.enviarMensaje("SELECCION_NIVEL;true");
                    }
                }

                if (cliente.sePuedeSeleccionarNivel()) {

                    guardarPosicionCamara();
                    ENTRADA.limpiar();

                    NAV.cambiarPantalla(new SeleccionNivel(NAV, cliente, this));
                    return;
                }
            } else {
                NIVEL.update(delta, ENTRADA);

                for (EnemigoRed e : cliente.getEnemigosRecibidos()) {
                    NIVEL.agregarEnemigos(e);
                    cliente.registrarEstadoEnemigo(e);
                }

                for (EnemigoRed e : cliente.getEstadoEnemigos()) {
                    NIVEL.actualizarEstadoEnemigos(e);
                }

                for (Integer id : cliente.getEnemigosEliminados()) {
                    NIVEL.eliminarEnemigo(id);
                }

                for (ProyectilRed p : cliente.getProyectilesNuevos()) {
                    NIVEL.agregarProyectil(p);
                    cliente.registrarEstadoProyectil(p);
                }

                for (ProyectilRed p : cliente.getEstadoProyectiles()) {
                    NIVEL.actualizarEstadoProyectil(p);
                }

                for (Integer id : cliente.getProyectilesEliminados()) {
                    NIVEL.eliminarProyectil(id);
                }

                if (NIVEL.enemigoReliquiaDerrotado()) {
                    NAV.cambiarPantalla(new PantallaFinal(NAV));
                    return;
                }
            }
        }

        // Dibujar el Hub o el NIVEL seleccionado
        if (hub != null) {
            hub.renderMapa();

            Render.begin(hub.getCamara());
            hub.render();
            hub.dibujarAlertaCentro(estadoRedLocal.estaEnCentroHub(), estadoRedLocal.estaEsperandoSeleccion());
            Render.end();

        } else {
            NIVEL.renderMapa();

            Render.begin(NIVEL.getCamara());
            NIVEL.render();
            Render.end();

            NIVEL.renderDebug();

            if (NIVEL.isGameOver()) {
                NAV.cambiarPantalla(new GameOver(NAV));
                return;
            }
        }

        if (hub == null) {
            HUD.setTieneReliquia(NIVEL.tieneReliquia());
            HUD.dibujar();
        }

        if (pausado) {
            MENU_PAUSA.update(delta);
            MENU_PAUSA.render();
        }
    }

    private void actualizarEnvioEntrada(float delta) {
        tiempoMensaje += delta;

        String estadoIzquierda = obtenerEstadoIzquierda();
        String estadoDerecha = obtenerEstadoDerecha();
        String estadoArriba = obtenerEstadoArriba();
        String estadoAtaque = obtenerEstadoAtaque();
        String estadoAbajo = obtenerEstadoAbajo();
        String estadoE = obtenerEstadoE();

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

        if (!estadoAbajo.equals(ultimoEstadoAbajo)
            || tiempoMensaje >= INTERVALO_MENSAJE) {

            cliente.enviarMensaje(estadoAbajo);
            ultimoEstadoAbajo = estadoAbajo;
        }

        if (!estadoAtaque.equals(ultimoEstadoAtaque)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoAtaque);
            ultimoEstadoAtaque = estadoAtaque;
        }

        if (!estadoE.equals(ultimoEstadoE)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoE);
            ultimoEstadoE = estadoE;
        }

        if (tiempoMensaje >= INTERVALO_MENSAJE) {
            tiempoMensaje = 0f;
        }
    }


    private String obtenerEstadoIzquierda() {
        if (ENTRADA.mueveIzquierda()) {
            return "INPUT;IZQUIERDA";
        }
        return "INPUT;NOIZQUIERDA";
    }

    private String obtenerEstadoDerecha() {
        if (ENTRADA.mueveDerecha()) {
            return "INPUT;DERECHA";
        }
        return "INPUT;NODERECHA";
    }

    private String obtenerEstadoArriba() {
        if (ENTRADA.mueveArriba()) {
            return "INPUT;ARRIBA";
        }
        return "INPUT;NOARRIBA";
    }

    private String obtenerEstadoAbajo() {
        if (ENTRADA.mueveAbajo()) {
            return "INPUT;ABAJO";
        }
        return "INPUT;NOABAJO";
    }

    private String obtenerEstadoAtaque() {
        if (ENTRADA.ataca()) {
            return "INPUT;ATACA";
        }
        return "INPUT;NOATACA";
    }

    private String obtenerEstadoE() {
        if (ENTRADA.ataca()) {
            return "INPUT;E";
        }
        return "INPUT;NOE";
    }

    @Override
    public void resize(int width, int height) {
        if (hub != null) {
            hub.resize(width, height);
        } else {
            NIVEL.resize(width, height);
        }

        MENU_PAUSA.resize(width, height);
        HUD.actualizarTamano(width, height);
    }


    public void setPausa(boolean valor) {
        this.pausado = valor;
        Gdx.input.setCursorCatched(!valor);
    }

    public void guardarPosicionCamara() {
        if (hub != null) {
            hub.guardarPosicionCamara();
        } else {
            NIVEL.guardarPosicionCamara();
        }
    }

    public boolean estaEnHub() {
        return NIVEL == null;
    }

    public void restaurarPosicionCamara() {
        if (hub != null) {
            hub.restaurarPosicionCamara();
        } else {
            NIVEL.restaurarPosicionCamara();
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setCursorCatched(false);
        Sonido.FUEGO_SOSTENIDO.detenerLoop();
    }

    @Override
    public void dispose() {
        if (hub != null) {
            hub.dispose();
        } else {
            NIVEL.dispose();
        }
        MENU_PAUSA.dispose();
        HUD.dispose();
    }
}
