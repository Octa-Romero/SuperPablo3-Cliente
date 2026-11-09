package com.prz.juego.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.principal.Navegable;
import com.prz.juego.niveles.Hub;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.red.Cliente;
import com.prz.juego.red.EnemigoRed;
import com.prz.juego.red.OrbeRed;
import com.prz.juego.principal.Navegable;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class PantallaJuego implements Screen {

    private Cliente cliente;
    private final Navegable NAV;
    private Jugador jugadorLocal;
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
    private String ultimoEstadoAtaque = "";

    public PantallaJuego(Navegable NAV, Personajes PERSONAJE_ELEGIDO) {
        this(NAV, PERSONAJE_ELEGIDO, null);
    }

    public PantallaJuego(Navegable nav, Personajes personajeElegido, Personajes personajeRemoto, Cliente cliente, String rutaNivel) {
        this.NAV = nav;
        this.ENTRADA = new Entrada();
        this.NIVEL = new Nivel();
        this.MENU_PAUSA = new MenuPausa(nav, this);
        this.cliente = cliente;

        jugadorLocal = personajeElegido.crear(50, 150);
        jugadorLocal.setEntrada(entrada);
        jugadorLocal.setEstadoRed(cliente.getEstadoLocal());

        jugadorRemoto = personajeRemoto.crear(75, 150);
        jugadorRemoto.setEstadoRed(cliente.getEstadoRemoto());

        if (rutaNivel == null) {
            hub = new Hub();
            hub.cargar("Niveles/Niveles/Hub.tmx", jugador);
        } else {
            hub = null;
            NIVEL.cargar(jugador);
        }

        HUD = new Hud(jugador, Render.batch);

        if (NIVEL != null) {
            HUD.setJefe(NIVEL.getJefe());
        }
    }

    @Override
    public void show() {
        Gdx.input.setCursorCatched(!pausado);

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
            if (hub != null) {
                hub.update(delta, ENTRADA);
            } else {
                NIVEL.update(delta, ENTRADA);

                actualizarEnvioEntrada(delta);

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

                for (OrbeRed o : cliente.getOrbesRecibidos()) {
                    NIVEL.agregarOrbes(o);
                    cliente.registrarEstadoOrbe(o);
                }

                for (OrbeRed o : cliente.getEstadoOrbes()) {
                    NIVEL.actualizarEstadoOrbes(o);
                }

                for (Integer id : cliente.getOrbesEliminados()) {
                    NIVEL.eliminarOrbes(id);
                }

                if (NIVEL.enemigoReliquiaDerrotado()) {
                    NAV.cambiarPantalla(new PantallaFinal(NAV));
                    return;
                }
            }
        }

        // Ir a la seleccion de niveles desde el Hub
        if (!pausado && hub != null && hub.estaEnCentroHub() && ENTRADA.aprietaE()) {

            guardarPosicionCamara();
            ENTRADA.limpiar();
            NAV.cambiarPantalla(new SeleccionNivel(NAV, PERSONAJE_ELEGIDO, this));
            return;
        }

        // Dibujar el Hub o el NIVEL seleccionado
        if (hub != null) {
            hub.renderMapa();

            Render.begin(hub.getCamara());
            hub.render();
            hub.dibujarAlertaCentro();
            Render.end();

        } else {
            NIVEL.renderMapa();

            Render.begin(NIVEL.getCamara());
            NIVEL.render();
            Render.end();

            NIVEL.renderDebug();

            if (NIVEL.isGameOver()) {
                NAV.cambiarPantalla(new GameOver(NAV, PERSONAJE_ELEGIDO));
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

    public void volverAlHub() {
        Musica.MENU.sonar();
        NAV.cambiarPantalla(new PantallaJuego(NAV, PERSONAJE_ELEGIDO));
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
