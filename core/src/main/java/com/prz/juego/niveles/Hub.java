package com.prz.juego.niveles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import com.prz.juego.red.JugadorEstado;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.CuadroDialogo;
import com.prz.juego.sistemas.Dialogo;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Render;

public class Hub {

    private static final int TAMANO_FUENTE_ALERTA = 28;
    private static final float ESCALA_JUGADORES = 2f;
    private static final float ALTURA_ALERTA_CENTRO = 100f;
    private static final float MARGEN_ALERTA_PANCRASIO = 80f;
    private static final String CAPA_PANCRASIO = "pancrasio";
    private static final String CAPA_CENTRO = "centroHub";
    private static final String MENSAJE_ESPERANDO = "Esperando al otro jugador... E para cancelar";
    private static final String MENSAJE_ELEGIR_NIVEL = "Presioná E para elegir nivel";
    private static final String MENSAJE_HABLAR = "Presioná E para hablar";

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;

    private JugadorEstado jugadorLocal;
    private JugadorEstado jugadorRemoto;

    private Rectangle centroHub;
    private Rectangle zonaPancrasio;
    private float mapaAncho;

    private BitmapFont fontAlerta;
    private Dialogo dialogoPancrasio;
    private CuadroDialogo cuadroDialogo;

    public void cargar(String ruta, JugadorEstado jugadorLocal, JugadorEstado jugadorRemoto) {
        camara = new Camara();

        this.jugadorLocal = jugadorLocal;
        this.jugadorRemoto = jugadorRemoto;

        jugadorLocal.escalar(ESCALA_JUGADORES);
        jugadorRemoto.escalar(ESCALA_JUGADORES);

        fontAlerta = FuenteJuego.generar(TAMANO_FUENTE_ALERTA);

        mapa = new TmxMapLoader().load(ruta);

        zonaPancrasio = buscarRectangulo(CAPA_PANCRASIO);
        centroHub = buscarRectangulo(CAPA_CENTRO);

        dialogoPancrasio = new Dialogo(
            "¡Bienvenidos, aventureros!",
            "Pronto tendré mercancía para ustedes.",
            "Vuelvan cuando esté todo preparado.");

        cuadroDialogo = new CuadroDialogo();

        MapProperties props = mapa.getProperties();
        mapaAncho = props.get("tilewidth", Integer.class) * props.get("width", Integer.class);
        float mapaAlto = props.get("tileheight", Integer.class) * props.get("height", Integer.class);

        camara.setLimitesMapa(mapaAncho, mapaAlto);

        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);
    }

    private Rectangle buscarRectangulo(String capa) {
        if (mapa.getLayers().get(capa) == null) {
            return null;
        }

        for (MapObject object : mapa.getLayers().get(capa).getObjects()) {
            if (object instanceof RectangleMapObject rectangulo) {
                return rectangulo.getRectangle();
            }
        }

        return null;
    }

    private boolean estaCercaPancrasio() {
        return zonaPancrasio != null && zonaPancrasio.overlaps(jugadorLocal.getBounds());
    }

    public void renderMapa() {
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
    }

    public void render() {
        Render.batch.setProjectionMatrix(camara.getCamera().combined);

        jugadorRemoto.dibujar();
        jugadorLocal.dibujar();

        if (cuadroDialogo.estaVisible()) {
            cuadroDialogo.dibujar(Render.batch, camara.getCamera());
        } else if (estaCercaPancrasio()) {
            dibujarAlertaPancrasio();
        }
    }

    public void update(float delta) {
        jugadorLocal.actualizar(delta);
        jugadorRemoto.actualizar(delta);

        restringirBordes(jugadorLocal);
        restringirBordes(jugadorRemoto);

        camara.seguirPersonaje(
            jugadorLocal.getX() + jugadorLocal.getAncho() / 2f,
            jugadorLocal.getY() + jugadorLocal.getAlto() / 2f,
            delta);
    }

    private void restringirBordes(JugadorEstado jugador) {
        if (jugador.getX() < 0) {
            jugador.setX(0f);
        }

        float bordeDerecho = mapaAncho - jugador.getAncho();
        if (jugador.getX() > bordeDerecho) {
            jugador.setX(bordeDerecho);
        }
    }

    public boolean puedeInteractuarPancrasio() {
        return cuadroDialogo.estaVisible() || estaCercaPancrasio();
    }

    public void interactuarPancrasio() {
        if (cuadroDialogo.estaVisible()) {
            dialogoPancrasio.avanzar();

            if (dialogoPancrasio.estaActivo()) {
                cuadroDialogo.mostrar(dialogoPancrasio.getMensajeActual());
            } else {
                cuadroDialogo.ocultar();
            }
        } else if (estaCercaPancrasio()) {
            dialogoPancrasio.iniciar();
            cuadroDialogo.mostrar(dialogoPancrasio.getMensajeActual());
        }
    }

    public void dibujarAlertaCentro(boolean enCentroHub, boolean esperandoSeleccion) {
        if (!enCentroHub && !esperandoSeleccion) {
            return;
        }

        String mensaje = esperandoSeleccion ? MENSAJE_ESPERANDO : MENSAJE_ELEGIR_NIVEL;
        GlyphLayout layout = new GlyphLayout(fontAlerta, mensaje);

        float x = centroHub.x + centroHub.width / 2f - layout.width / 2f;
        float y = centroHub.y + centroHub.height + ALTURA_ALERTA_CENTRO;

        fontAlerta.draw(Render.batch, mensaje, x, y);
    }

    private void dibujarAlertaPancrasio() {
        GlyphLayout layout = new GlyphLayout(fontAlerta, MENSAJE_HABLAR);

        float ancho = getCamara().viewportWidth;
        float alto = getCamara().viewportHeight;

        float x = getCamara().position.x - ancho / 2f + (ancho - layout.width) / 2f;
        float y = getCamara().position.y - alto / 2f + MARGEN_ALERTA_PANCRASIO;

        fontAlerta.draw(Render.batch, MENSAJE_HABLAR, x, y);
    }

    public void resize(int width, int height) {
        camara.actualizarTamano(width, height);
    }

    public Camera getCamara() {
        return camara.getCamera();
    }

    public void guardarPosicionCamara() {
        camara.guardarPosicion();
    }

    public void restaurarPosicionCamara() {
        camara.restaurarPosicion();
    }

    public void dispose() {
        if (mapa != null) {
            mapa.dispose();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        if (fontAlerta != null) {
            fontAlerta.dispose();
        }

        if (cuadroDialogo != null) {
            cuadroDialogo.dispose();
        }
    }
}
