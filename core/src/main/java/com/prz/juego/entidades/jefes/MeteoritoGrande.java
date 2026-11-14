
package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.utilidades.Render;

public class MeteoritoGrande {

    private static final float TAMANO = 150f;
    private static final float VELOCIDAD_EXPANSION = 55f;
    private static final float INTERVALO_GENERACION = 0.08f;
    private static final float ANCHO_FUEGO_OLA = 90f;
    private static final float ALTO_FUEGO_OLA = 110f;
    private static final float DESPLAZAMIENTO_BASE_FUEGO = -40f;
    private static final float ALTURA_OSCILACION = 35f;
    private static final float DURACION_FUEGO_OLA = 0.6f;

    private final int ID;
    private final float X;
    private final float SUELO_Y;
    private final float ANCHO_MAPA;
    private final Animation<TextureRegion> ANIMACION_FUEGO;
    private final Sprite SPRITE;
    private final int CANTIDAD_PASOS_OLA;

    private float y;
    private boolean impacto;
    private float tiempoTotalOla;

    public MeteoritoGrande(
        int id, float x, float y, float sueloY,
        float anchoMapa, Texture textura,
        Animation<TextureRegion> animacionFuego) {

        this.ID = id;
        this.X = x;
        this.y = y;
        this.SUELO_Y = sueloY;
        this.ANCHO_MAPA = anchoMapa;
        this.ANIMACION_FUEGO = animacionFuego;

        CANTIDAD_PASOS_OLA = (int) Math.ceil(
            Math.max(x, anchoMapa - x) / VELOCIDAD_EXPANSION
        );

        SPRITE = new Sprite(textura);
        SPRITE.setSize(TAMANO, TAMANO);
        SPRITE.setOriginCenter();
        SPRITE.setPosition(X - TAMANO / 2f, y);
    }

    public void actualizarEstado(float y, boolean impacto, float rotacion, float tiempoTotalOla) {
        this.y = y;
        this.impacto = impacto;
        this.tiempoTotalOla = tiempoTotalOla;

        SPRITE.setRotation(rotacion);
        SPRITE.setPosition(X - TAMANO / 2f, y);
    }

    public int getId() {
        return ID;
    }

    public void dibujar() {
        if (impacto) {
            dibujarOlaFuego();
        } else {
            SPRITE.draw(Render.batch);
        }
    }

    private void dibujarOlaFuego() {
        TextureRegion frame =
            ANIMACION_FUEGO.getKeyFrame(tiempoTotalOla);

        // Llama inicial del impacto.
        dibujarLlama(X, tiempoTotalOla, frame);

        // Llamas que se expanden a ambos lados.
        for (int paso = 1; paso <= CANTIDAD_PASOS_OLA; paso++) {
            float tiempoAparicion = paso * INTERVALO_GENERACION;
            float edad = tiempoTotalOla - tiempoAparicion;

            if (edad < 0f || edad >= DURACION_FUEGO_OLA) {
                continue;
            }

            float desplazamiento = paso * VELOCIDAD_EXPANSION;

            dibujarLlama(X - desplazamiento, edad, frame);
            dibujarLlama(X + desplazamiento, edad, frame);
        }
    }

    private void dibujarLlama(
        float x, float edad, TextureRegion frame) {

        if (x < 0f || x > ANCHO_MAPA
            || edad < 0f || edad >= DURACION_FUEGO_OLA) {
            return;
        }

        float progreso = Math.min(
            1f, edad / DURACION_FUEGO_OLA
        );

        float yFuego = SUELO_Y
            + DESPLAZAMIENTO_BASE_FUEGO
            + ALTURA_OSCILACION
            * (float) Math.sin(Math.PI * progreso);

        Render.batch.draw(
            frame,
            x - ANCHO_FUEGO_OLA / 2f,
            yFuego,
            ANCHO_FUEGO_OLA,
            ALTO_FUEGO_OLA
        );
    }
}
