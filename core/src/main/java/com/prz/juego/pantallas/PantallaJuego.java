package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.principal.Navegable;
import com.prz.juego.niveles.Hub;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.red.Cliente;
import com.prz.juego.red.JugadorEstado;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.sistemas.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class PantallaJuego implements Screen {

    private static final float INTERVALO_MENSAJE = 0.25f;
    private static final int CANTIDAD_ENTRADAS = 4;
    private static final String RUTA_HUB = "Niveles/Niveles/Hub.tmx";
    private static final float X_INICIAL = 50f;
    private static final float Y_INICIAL = 150f;
    private static final String[] MENSAJES_ENTRADA_NULA = {
        "INPUT;NOIZQUIERDA",
        "INPUT;NODERECHA",
        "INPUT;NOARRIBA",
        "INPUT;NOABAJO"
    };

    private final Cliente CLIENTE;
    private final Navegable NAV;
    private final Entrada ENTRADA;
    private final Hud HUD;
    private final CartelDesconexion CARTEL_DESCONEXION;
    private final JugadorEstado ESTADO_RED_LOCAL;
    private final JugadorEstado ESTADO_RED_REMOTO;
    private final String[] ULTIMO_ESTADO_ENTRADA = new String[CANTIDAD_ENTRADAS];

    private Nivel nivel;
    private Hub hub;
    private MenuPausa menuPausa;
    private boolean pausado = false;
    private boolean cambiarASeleccionNivel = false;
    private boolean envioEntradaActivo = true;
    private boolean mostrarHitboxes = false;
    private float tiempoMensaje = 0f;

    public PantallaJuego(Navegable NAV, Cliente cliente, String rutaNivel) {
        this.NAV = NAV;
        this.CLIENTE = cliente;
        this.ENTRADA = new Entrada();

        cliente.limpiarEntidades();
        this.nivel = rutaNivel == null ? null : new Nivel(rutaNivel);
        this.menuPausa = new MenuPausa(NAV, this, cliente);
        this.CARTEL_DESCONEXION = new CartelDesconexion(NAV);

        ESTADO_RED_LOCAL = cliente.getEstadoLocal();
        ESTADO_RED_REMOTO = cliente.getEstadoRemoto();
        prepararJugadores();

        if (rutaNivel == null) {
            hub = new Hub();
            hub.cargar(RUTA_HUB, ESTADO_RED_LOCAL, ESTADO_RED_REMOTO);
            Musica.HUB.sonar();
        } else {
            hub = null;
            nivel.cargar(ESTADO_RED_LOCAL, ESTADO_RED_REMOTO);
        }

        HUD = new Hud(ESTADO_RED_LOCAL, Render.batch);
    }

    private void prepararJugadores() {
        ESTADO_RED_LOCAL.configurar(CLIENTE.getPersonajeLocal());
        ESTADO_RED_REMOTO.configurar(CLIENTE.getPersonajeRemoto());
        ESTADO_RED_LOCAL.reiniciar(X_INICIAL, Y_INICIAL);
        ESTADO_RED_REMOTO.reiniciar(X_INICIAL, Y_INICIAL);
    }

    @Override
    public void show() {
        configurarInput();
    }

    private void configurarInput() {
        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(CARTEL_DESCONEXION.getStage());
        mux.addProcessor(menuPausa.getStage());
        mux.addProcessor(ENTRADA);
        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        verificarConexion();

        if (ENTRADA.aprietaF1()) {
            mostrarHitboxes = !mostrarHitboxes;
        }

        if (CLIENTE.isExpulsado()) {
            if (CARTEL_DESCONEXION.estaActivo()) {
                CARTEL_DESCONEXION.desactivar();
            }
            NAV.volverAMenu();
            return;
        }

        if (CARTEL_DESCONEXION.estaActivo()) {
            ENTRADA.limpiar();
            dibujarEscena();
            if (hub == null) {
                HUD.dibujar();
            }
            CARTEL_DESCONEXION.actualizarTiempoReconexion(CLIENTE.getSegundosReconexion());
            CARTEL_DESCONEXION.update(delta);
            CARTEL_DESCONEXION.render();
            return;
        }

        if (hub == null) {
            if (CLIENTE.ambosJugadoresVuelvenHub()) {
                cargarHub();
                return;
            }

            int siguienteNivel = CLIENTE.getNivelIniciado();
            if (siguienteNivel != 0) {
                NAV.iniciarJuego(Nivel.getRuta(siguienteNivel));
                return;
            }
        }

        manejarPausa();

        if (pausado) {
            ENTRADA.limpiar();
        } else if (envioEntradaActivo) {
            actualizarEnvioEntrada(delta);
        }

        if (hub != null) {
            actualizarHub(delta);
        } else {
            nivel.update(delta);
            if (sincronizarNivel()) {
                return;
            }
        }

        Camera camara = hub != null ? hub.getCamara() : nivel.getCamara();
        CLIENTE.actualizarCamara(camara.position.x, camara.position.y);

        dibujarEscena();

        if (hub == null) {
            if (nivel.isGameOver()) {
                NAV.cambiarPantalla(new GameOver(NAV, nivel, CLIENTE));
                return;
            }
            actualizarHud(delta);
            HUD.dibujar();
        }

        if (pausado) {
            menuPausa.update(delta);
            menuPausa.render();
        }

        if (cambiarASeleccionNivel) {
            cambiarASeleccionNivel = false;
            NAV.cambiarPantalla(new SeleccionNivel(NAV, CLIENTE, this));
        }
    }

    private void verificarConexion() {
        if (CLIENTE.consumirSeleccionCancelada()) {
            envioEntradaActivo = true;
            ENTRADA.limpiar();
        }

        if (CLIENTE.necesitaSnapshot()) {
            if (nivel != null) {
                nivel.limpiarEntidades();
            }
            CLIENTE.solicitarSnapshotEntidades();
        }

        if (CLIENTE.isServidorPerdido() && !CARTEL_DESCONEXION.estaActivo()) {
            CARTEL_DESCONEXION.mostrarServidorPerdido();
            limpiarPorDesconexion();
        }

        if (CLIENTE.isJugadorRemotoDesconectado() && !CARTEL_DESCONEXION.estaActivo()) {
            CARTEL_DESCONEXION.mostrarJugadorDesconectado();
            CLIENTE.resetearJugadorRemotoDesconectado();
            limpiarPorDesconexion();
        }

        if (CLIENTE.isJugadorRemotoReconecto() && CARTEL_DESCONEXION.estaActivo()) {
            CARTEL_DESCONEXION.desactivar();
        }
    }

    private void manejarPausa() {
        if (!ENTRADA.aprietaEscape()) {
            return;
        }

        setPausa(!pausado);

        if (pausado) {
            menuPausa.activar();
            enviarEntradaNula();
        } else {
            menuPausa.desactivar();
            menuPausa.cancelarVolverHub();
        }
    }

    private void actualizarHub(float delta) {
        hub.update(delta);

        if (!pausado && ENTRADA.aprietaE()) {
            manejarInteraccionHub();
        }

        if (ESTADO_RED_LOCAL.estaEsperandoSeleccion()) {
            ESTADO_RED_LOCAL.forzarIdle();
        }

        if (!pausado && CLIENTE.sePuedeSeleccionarNivel()) {
            guardarPosicionCamara();
            ENTRADA.limpiar();
            enviarEntradaNula();
            ESTADO_RED_LOCAL.forzarIdle();
            ESTADO_RED_REMOTO.forzarIdle();
            hub.cerrarDialogo();
            cambiarASeleccionNivel = true;
        }
    }

    private void manejarInteraccionHub() {
        if (ESTADO_RED_LOCAL.estaEsperandoSeleccion()) {
            CLIENTE.enviarMensaje("SELECCION_NIVEL;false");
            envioEntradaActivo = true;
            ENTRADA.limpiar();
        } else if (hub.puedeInteractuarPancrasio()) {
            hub.interactuarPancrasio();
        } else if (ESTADO_RED_LOCAL.estaEnCentroHub()) {
            CLIENTE.enviarMensaje("SELECCION_NIVEL;true");
            envioEntradaActivo = false;
            ENTRADA.limpiar();
            enviarEntradaNula();
            ESTADO_RED_LOCAL.forzarIdle();
            ESTADO_RED_LOCAL.resetearAnimacionRecibida();
        }
    }

    private boolean sincronizarNivel() {
        boolean mostrarCreditos = nivel.sincronizar(CLIENTE);

        if (nivel.getJefe() != null) {
            HUD.setJefe(nivel.getJefe());
        }

        if (mostrarCreditos) {
            NAV.cambiarPantalla(new PantallaFinal(NAV));
            return true;
        }

        return false;
    }

    private void actualizarHud(float delta) {
        HUD.update(delta);

        boolean localMuerto = ESTADO_RED_LOCAL.estaMuerto();
        boolean remotoMuerto = ESTADO_RED_REMOTO.estaMuerto();
        String nombreLocal = nombreDe(CLIENTE.getPersonajeLocal());
        String nombreRemoto = nombreDe(CLIENTE.getPersonajeRemoto());
        String nombreVivo = localMuerto ? nombreRemoto : nombreLocal;
        String nombreMuerto = remotoMuerto ? nombreRemoto : "";
        HUD.setEstadoMuerte(localMuerto, remotoMuerto, nombreVivo, nombreMuerto);
    }

    private String nombreDe(Personajes personaje) {
        return personaje == Personajes.PABLO ? "Pablo" : "Walter";
    }

    private void dibujarEscena() {
        if (hub != null) {
            hub.renderMapa();

            Render.begin(hub.getCamara());
            hub.render();
            hub.dibujarAlertaCentro(
                ESTADO_RED_LOCAL.estaEnCentroHub(), ESTADO_RED_LOCAL.estaEsperandoSeleccion());
            Render.end();
        } else {
            nivel.renderMapa();

            Render.begin(nivel.getCamara());
            nivel.render();
            Render.end();

            if (mostrarHitboxes) {
                nivel.dibujarHitboxes();
            }
        }
    }

    private void actualizarEnvioEntrada(float delta) {
        tiempoMensaje += delta;
        boolean refrescar = tiempoMensaje >= INTERVALO_MENSAJE;

        enviarEstadoEntrada(0, ENTRADA.mueveIzquierda() ? "INPUT;IZQUIERDA" : "INPUT;NOIZQUIERDA", refrescar);
        enviarEstadoEntrada(1, ENTRADA.mueveDerecha() ? "INPUT;DERECHA" : "INPUT;NODERECHA", refrescar);
        enviarEstadoEntrada(2, ENTRADA.mueveArriba() ? "INPUT;ARRIBA" : "INPUT;NOARRIBA", refrescar);
        enviarEstadoEntrada(3, ENTRADA.mueveAbajo() ? "INPUT;ABAJO" : "INPUT;NOABAJO", refrescar);

        boolean ataque = ENTRADA.ataca();
        if (ataque && hub == null) {
            CLIENTE.enviarMensaje("INPUT;ATACA");
        }

        if (ENTRADA.aprietaERed()) {
            CLIENTE.enviarMensaje("INPUT;E");
        }

        if (refrescar) {
            tiempoMensaje = 0f;
        }
    }

    private void enviarEstadoEntrada(int indice, String estado, boolean refrescar) {
        if (refrescar || !estado.equals(ULTIMO_ESTADO_ENTRADA[indice])) {
            CLIENTE.enviarMensaje(estado);
            ULTIMO_ESTADO_ENTRADA[indice] = estado;
        }
    }

    private void enviarEntradaNula() {
        for (String mensaje : MENSAJES_ENTRADA_NULA) {
            CLIENTE.enviarMensaje(mensaje);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (hub != null) {
            hub.resize(width, height);
        } else {
            nivel.resize(width, height);
        }

        menuPausa.resize(width, height);
        CARTEL_DESCONEXION.resize(width, height);
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
            nivel.guardarPosicionCamara();
        }
    }

    public void restaurarPosicionCamara() {
        if (hub != null) {
            hub.restaurarPosicionCamara();
        } else {
            nivel.restaurarPosicionCamara();
        }
    }

    public boolean estaEnHub() {
        return nivel == null;
    }

    public void reanudarDespuesDeSeleccion() {
        envioEntradaActivo = true;
        ENTRADA.limpiar();
        restaurarPosicionCamara();
    }

    public void cargarHub() {
        if (pausado) {
            setPausa(false);
            menuPausa.desactivar();
        }

        if (nivel != null) {
            nivel.dispose();
            nivel = null;
        }

        if (hub != null) {
            hub.dispose();
        }

        menuPausa.dispose();

        prepararJugadores();
        CLIENTE.limpiarEntidades();

        hub = new Hub();
        hub.cargar(RUTA_HUB, ESTADO_RED_LOCAL, ESTADO_RED_REMOTO);
        hub.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Musica.HUB.sonar();

        menuPausa = new MenuPausa(NAV, this, CLIENTE);
        menuPausa.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        HUD.setJefe(null);
        ENTRADA.limpiar();
        CLIENTE.resetearVolverHub();

        configurarInput();
        envioEntradaActivo = true;
    }

    private void limpiarPorDesconexion() {
        if (hub != null) {
            hub.cerrarDialogo();
        }
        ENTRADA.limpiar();
        enviarEntradaNula();
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
        }
        if (nivel != null) {
            nivel.dispose();
        }
        menuPausa.dispose();
        CARTEL_DESCONEXION.dispose();
        HUD.dispose();
    }
}
