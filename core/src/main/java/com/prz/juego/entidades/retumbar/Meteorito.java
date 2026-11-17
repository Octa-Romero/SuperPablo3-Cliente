package com.prz.juego.entidades.retumbar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Frames;
import com.prz.juego.utilidades.Render;

public class Meteorito {

    public enum Tipo {
        NORMAL,
        GRANDE
    }

    private static final float TAMANO = 82f;
    private static final float TAMANO_GRANDE = 150f;
    private static final float ESCALA_ANCHO_FUEGO = 1.35f;
    private static final float VELOCIDAD_EXPANSION = 55f;
    private static final float INTERVALO_GENERACION = 0.08f;
    private static final float ANCHO_FUEGO_OLA = 90f;
    private static final float ALTO_FUEGO_OLA = 110f;
    private static final float DESPLAZAMIENTO_BASE_FUEGO = -40f;
    private static final float ALTURA_OSCILACION = 35f;
    private static final float DURACION_FUEGO_OLA = 0.6f;
    private static final int CANTIDAD_FRAMES_FUEGO = 2;
    private static final float DURACION_FRAME_FUEGO = 0.14f;

    private final int ID;
    private final Tipo TIPO;
    private final float X;

    private float y;
    private boolean impacto;
    private float rotacion;
    private float tiempoEfecto;
    private Sprite sprite;
    private Animation<TextureRegion> animacionFuego;

    public Meteorito(int id, Tipo tipo, float x, float y) {
        this.ID = id;
        this.TIPO = tipo;
        this.X = x;
        this.y = y;
    }

    public void actualizarEstado(float y, boolean impacto, float rotacion, float tiempoEfecto) {
        this.y = y;
        this.impacto = impacto;
        this.rotacion = rotacion;
        this.tiempoEfecto = tiempoEfecto;
    }

    private void inicializarGraficos() {
        if (sprite != null) {
            return;
        }

        float tamano = TIPO == Tipo.NORMAL ? TAMANO : TAMANO_GRANDE;
        sprite = new Sprite(GestorRecursos.obtenerTextura("Jefe/meteorito.png"));
        sprite.setSize(tamano, tamano);
        sprite.setOriginCenter();
        animacionFuego = Frames.animacion(
            "Jefe/fuegoMeteorito.png", CANTIDAD_FRAMES_FUEGO, DURACION_FRAME_FUEGO);
    }

    public void dibujar(float sueloY, float anchoMapa) {
        inicializarGraficos();

        if (!impacto) {
            float tamano = TIPO == Tipo.NORMAL ? TAMANO : TAMANO_GRANDE;
            sprite.setRotation(rotacion);
            sprite.setPosition(X - tamano / 2f, y);
            sprite.draw(Render.batch);
            return;
        }

        TextureRegion frame = animacionFuego.getKeyFrame(tiempoEfecto);

        if (TIPO == Tipo.NORMAL) {
            float anchoFuego = TAMANO * ESCALA_ANCHO_FUEGO;
            float altoFuego = anchoFuego * frame.getRegionHeight() / frame.getRegionWidth();
            Render.batch.draw(frame, X - anchoFuego / 2f, sueloY, anchoFuego, altoFuego);
        } else {
            dibujarOlaFuego(frame, sueloY, anchoMapa);
        }
    }

    private void dibujarOlaFuego(TextureRegion frame, float sueloY, float anchoMapa) {
        int cantidadPasos = (int) Math.ceil(Math.max(X, anchoMapa - X) / VELOCIDAD_EXPANSION);

        dibujarLlama(frame, X, tiempoEfecto, sueloY, anchoMapa);

        for (int paso = 1; paso <= cantidadPasos; paso++) {
            float edad = tiempoEfecto - paso * INTERVALO_GENERACION;

            if (edad < 0f || edad >= DURACION_FUEGO_OLA) {
                continue;
            }

            float desplazamiento = paso * VELOCIDAD_EXPANSION;
            dibujarLlama(frame, X - desplazamiento, edad, sueloY, anchoMapa);
            dibujarLlama(frame, X + desplazamiento, edad, sueloY, anchoMapa);
        }
    }

    private void dibujarLlama(TextureRegion frame, float x, float edad, float sueloY, float anchoMapa) {
        if (x < 0f || x > anchoMapa || edad < 0f || edad >= DURACION_FUEGO_OLA) {
            return;
        }

        float progreso = Math.min(1f, edad / DURACION_FUEGO_OLA);
        float yFuego = sueloY
            + DESPLAZAMIENTO_BASE_FUEGO
            + ALTURA_OSCILACION * (float) Math.sin(Math.PI * progreso);

        Render.batch.draw(frame, x - ANCHO_FUEGO_OLA / 2f, yFuego, ANCHO_FUEGO_OLA, ALTO_FUEGO_OLA);
    }

    public int getId() {
        return ID;
    }

    public Tipo getTipo() {
        return TIPO;
    }
}
