package com.prz.juego.sistemas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.entidades.retumbar.Retumbar;
import com.prz.juego.red.JugadorEstado;
import com.prz.juego.entidades.retumbar.Mano;
import com.prz.juego.utilidades.BarraVida;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;

import java.util.ArrayList;

public class Hud {

    private final JugadorEstado JUGADOR;
    private final Stage STAGE;
    private final FitViewport VIEWPORT = new FitViewport(1280, 720, new OrthographicCamera());

    private boolean localMuerto = false;
    private boolean remotoMuerto = false;
    private String nombreJugadorVivo = "";
    private String nombreJugadorMuerto = "";
    private float tiempoMuerteLocal = -1f;
    private float tiempoMuerteRemoto = -1f;
    private static final float DURACION_NOTIFICACION = 3f;
    private static final float DURACION_FADE = 0.5f;
    private BitmapFont fuenteNotificacion;

    private final Texture CORAZON_LLENO = GestorRecursos.obtenerTextura("Hud/corazon_lleno.png");
    private final Texture CORAZON_MITAD = GestorRecursos.obtenerTextura("Hud/corazon_mitad.png");
    private final Texture CORAZON_VACIO = GestorRecursos.obtenerTextura("Hud/corazon_vacio.png");

    private final TextureRegionDrawable LLENO_DRAWABLE;
    private final TextureRegionDrawable MITAD_DRAWABLE;
    private final TextureRegionDrawable VACIO_DRAWABLE;

    private final ArrayList<Imagen> CORAZONES = new ArrayList<>();

    private Table tablaPrincipal;
    private Table contenedorCorazones;

    private final float TAMANO_CARA = 120;
    private final float TAMANO_CORAZON = 50;
    private final float ESPACIO_CORAZONES = 5;

    private double ultimaVida = -1;

    private final SpriteBatch BATCH;
    private final BitmapFont FUENTE_JEFE = FuenteJuego.generar(32);
    private final BitmapFont FUENTE_MANO = FuenteJuego.generar(24);
    private final float Y_BARRAS = 50;
    private final float ANCHO_BARRA_JEFE = 800;
    private final float ALTO_BARRA_JEFE = 24;
    private final float ANCHO_BARRA_MANO = 400;
    private final float ALTO_BARRA_MANO = 18;
    private final float ESPACIO_BARRAS_MANO = 60;
    private Retumbar jefe;

    public Hud(JugadorEstado JUGADOR, SpriteBatch batch) {
        this.JUGADOR = JUGADOR;
        BATCH = batch;

        STAGE = new Stage(VIEWPORT, batch);

        LLENO_DRAWABLE = new TextureRegionDrawable(new TextureRegion(CORAZON_LLENO));
        MITAD_DRAWABLE = new TextureRegionDrawable(new TextureRegion(CORAZON_MITAD));
        VACIO_DRAWABLE = new TextureRegionDrawable(new TextureRegion(CORAZON_VACIO));

        tablaPrincipal = new Table();
        tablaPrincipal.top().left();
        tablaPrincipal.setFillParent(true);
        tablaPrincipal.pad(20);

        Imagen imagenCara = new Imagen(JUGADOR.getTexturaHud(), true);

        contenedorCorazones = new Table();

        tablaPrincipal.add(imagenCara).size(TAMANO_CARA, TAMANO_CARA);
        tablaPrincipal.add(contenedorCorazones).padLeft(10).center();

        STAGE.addActor(tablaPrincipal);

        crearCorazones();
        actualizarCorazones();
    }

    private void crearCorazones() {
        int max = (int) Math.ceil(JUGADOR.getVidaMaxima());

        for (int i = 0; i < max; i++) {
            Imagen corazon = new Imagen(VACIO_DRAWABLE, true);

            CORAZONES.add(corazon);

            contenedorCorazones
                .add(corazon)
                .size(TAMANO_CORAZON, TAMANO_CORAZON)
                .padRight(ESPACIO_CORAZONES);
        }
    }

    private void actualizarCorazones() {

        double vida = JUGADOR.getVida();

        for (int i = 0; i < CORAZONES.size(); i++) {

            double vidaRestante = vida - i;

            if (vidaRestante >= 1.0) {
                CORAZONES.get(i).setDrawable(LLENO_DRAWABLE);
            } else if (vidaRestante >= 0.5) {
                CORAZONES.get(i).setDrawable(MITAD_DRAWABLE);
            } else {
                CORAZONES.get(i).setDrawable(VACIO_DRAWABLE);
            }
        }
    }

    public void setJefe(Retumbar jefe) {
        this.jefe = jefe;
    }

    private void dibujarBarrasJefe() {
        if (jefe == null || jefe.estaMuerto()) {
            return;
        }

        VIEWPORT.apply();
        BATCH.setProjectionMatrix(VIEWPORT.getCamera().combined);
        BATCH.begin();

        if (jefe.debeDibujarBarraVida()) {
            BarraVida.dibujar(
                BATCH,
                FUENTE_JEFE,
                "El Retumbar",
                (Config.ANCHO_BASE - ANCHO_BARRA_JEFE) / 2f,
                Y_BARRAS,
                ANCHO_BARRA_JEFE,
                ALTO_BARRA_JEFE,
                jefe.getVida(),
                jefe.getVidaMaxima(),
                Color.RED);
        } else {
            float centro = Config.ANCHO_BASE / 2f;
            float xIzquierda = centro - ESPACIO_BARRAS_MANO / 2f - ANCHO_BARRA_MANO;
            float xDerecha = centro + ESPACIO_BARRAS_MANO / 2f;
            dibujarBarraMano(jefe.getManoIzquierda(), xIzquierda);
            dibujarBarraMano(jefe.getManoDerecha(), xDerecha);
        }

        BATCH.end();
    }

    private void dibujarBarraMano(Mano mano, float x) {
        if (!mano.recibioEstado() || mano.estaMuerta()) {
            return;
        }

        BarraVida.dibujar(
            BATCH,
            FUENTE_MANO,
            "Mano de El Retumbar",
            x,
            Y_BARRAS,
            ANCHO_BARRA_MANO,
            ALTO_BARRA_MANO,
            mano.getVida(),
            mano.getVidaMaxima(),
            Color.RED);
    }

    public void actualizarTamano(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    public void setEstadoMuerte(boolean localMuerto, boolean remotoMuerto, String nombreVivo, String nombreMuerto) {
        this.localMuerto = localMuerto;
        this.remotoMuerto = remotoMuerto;
        this.nombreJugadorVivo = nombreVivo;
        this.nombreJugadorMuerto = nombreMuerto;

        if (localMuerto && this.tiempoMuerteLocal < 0f) {
            this.tiempoMuerteLocal = 0f;
        }
        if (remotoMuerto && !localMuerto && this.tiempoMuerteRemoto < 0f) {
            this.tiempoMuerteRemoto = 0f;
        }
    }

    public void update(float delta) {
        if (tiempoMuerteLocal >= 0f) {
            tiempoMuerteLocal += delta;
        }
        if (tiempoMuerteRemoto >= 0f) {
            tiempoMuerteRemoto += delta;
        }
    }

    private void dibujarNotificaciones() {
        if (fuenteNotificacion == null) {
            fuenteNotificacion = FuenteJuego.generar(28);
        }

        VIEWPORT.apply();
        BATCH.setProjectionMatrix(VIEWPORT.getCamera().combined);
        BATCH.begin();

        float yBase = Config.ALTO_BASE * 0.2f;
        int indiceY = 0;
        float separacionY = 40f;

        if (localMuerto) {
            String texto = "Observando a " + nombreJugadorVivo;
            GlyphLayout layout = new GlyphLayout(fuenteNotificacion, texto);
            float x = (Config.ANCHO_BASE - layout.width) / 2f;
            float y = yBase + indiceY * separacionY;
            fuenteNotificacion.setColor(1f, 1f, 1f, 1f);
            fuenteNotificacion.draw(BATCH, texto, x, y);
            indiceY++;
        }

        if (remotoMuerto && !localMuerto && tiempoMuerteRemoto >= 0f && tiempoMuerteRemoto <= DURACION_NOTIFICACION) {
            String texto = nombreJugadorMuerto + " ha muerto";
            float alpha = calcularAlpha(tiempoMuerteRemoto, DURACION_NOTIFICACION);
            GlyphLayout layout = new GlyphLayout(fuenteNotificacion, texto);
            float x = (Config.ANCHO_BASE - layout.width) / 2f;
            float y = yBase + indiceY * separacionY;
            fuenteNotificacion.setColor(1f, 1f, 1f, alpha);
            fuenteNotificacion.draw(BATCH, texto, x, y);
        }

        BATCH.end();
    }

    private float calcularAlpha(float tiempo, float duracionTotal) {
        if (tiempo < DURACION_FADE) {
            return tiempo / DURACION_FADE;
        } else if (tiempo > duracionTotal - DURACION_FADE) {
            return (duracionTotal - tiempo) / DURACION_FADE;
        }
        return 1f;
    }

    public void dibujar() {

        double vidaActual = JUGADOR.getVida();

        if (vidaActual != ultimaVida) {
            actualizarCorazones();
            ultimaVida = vidaActual;
        }

        STAGE.act();
        STAGE.draw();

        dibujarBarrasJefe();
        dibujarNotificaciones();
    }

    public void dispose() {
        STAGE.dispose();
        FUENTE_JEFE.dispose();
        FUENTE_MANO.dispose();
        if (fuenteNotificacion != null) {
            fuenteNotificacion.dispose();
        }
    }
}
