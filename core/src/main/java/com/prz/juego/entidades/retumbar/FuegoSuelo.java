package com.prz.juego.entidades.retumbar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.utilidades.Frames;
import com.prz.juego.utilidades.Render;

public class FuegoSuelo {

    private static final float ALTO_MINIMO = 1f;
    private static final float SOLAPAMIENTO_LLAMA = 12f;
    private static final float MULTIPLICADOR_ALTURA_SPRITE = 2f;
    private static final int CANTIDAD_FRAMES = 2;
    private static final float DURACION_FRAME = 0.08f;

    private float x;
    private float y;
    private float alto;
    private boolean activo;
    private float tiempoAnimacion;
    private Animation<TextureRegion> animacion;

    public void actualizarEstado(float x, float y, float alto, boolean activo) {
        this.x = x;
        this.y = y;
        this.alto = alto;
        this.activo = activo;
    }

    public void actualizar(float delta) {
        if (activo) {
            tiempoAnimacion += delta;
        }
    }

    public void dibujar(float anchoMapa) {
        if (!activo) {
            return;
        }

        if (animacion == null) {
            animacion = Frames.animacion("Jefe/fuegoSuelo.png", CANTIDAD_FRAMES, DURACION_FRAME);
        }

        TextureRegion frame = animacion.getKeyFrame(tiempoAnimacion);
        float altoLlama = Math.max(ALTO_MINIMO, alto) * MULTIPLICADOR_ALTURA_SPRITE;
        float anchoLlama = altoLlama * frame.getRegionWidth() / frame.getRegionHeight();
        float paso = Math.max(1f, anchoLlama - SOLAPAMIENTO_LLAMA);
        float limite = x + anchoMapa - anchoLlama / 2f;

        for (float xLlama = x - anchoLlama / 2f; xLlama < limite; xLlama += paso) {
            Render.batch.draw(frame, xLlama, y, anchoLlama, altoLlama);
        }
    }

    public boolean estaActivo() {
        return activo;
    }
}
