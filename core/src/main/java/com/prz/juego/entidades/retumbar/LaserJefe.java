package com.prz.juego.entidades.retumbar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Frames;
import com.prz.juego.utilidades.Render;

public class LaserJefe {

    private static final float ANCHO_SPRITE = 96f;
    private static final float ANCHO_HITBOX = 56f;
    private static final float LARGO = 1000f;
    private static final int CANTIDAD_FRAMES = 2;
    private static final float DURACION_FRAME = 0.12f;

    private boolean visible;
    private boolean aviso;
    private float angulo;
    private float tiempoAnimacion;
    private Animation<TextureRegion> animacion;
    private TextureRegion regionPixel;

    public void actualizarEstado(
        boolean visible,
        boolean aviso,
        float angulo,
        float tiempoAnimacion) {

        this.visible = visible;
        this.aviso = aviso;
        this.angulo = angulo;
        this.tiempoAnimacion = tiempoAnimacion;
    }

    public void reiniciar() {
        visible = false;
        aviso = false;
        angulo = 0f;
        tiempoAnimacion = 0f;
    }

    private void inicializarGraficos() {
        if (animacion != null) {
            return;
        }

        TextureRegion[] frames = Frames.dividir("Jefe/laser.png", CANTIDAD_FRAMES);
        TextureRegion[] invertidos = new TextureRegion[frames.length];

        for (int i = 0; i < frames.length; i++) {
            invertidos[i] = new TextureRegion(frames[i]);
            invertidos[i].flip(false, true);
        }

        animacion = new Animation<>(DURACION_FRAME, invertidos);
        animacion.setPlayMode(Animation.PlayMode.LOOP);
        regionPixel = new TextureRegion(GestorRecursos.obtenerPixel());
    }

    public void dibujar(float origenX, float origenY) {
        if (!visible && !aviso) {
            return;
        }

        inicializarGraficos();

        if (aviso) {
            float colorAnterior = Render.batch.getPackedColor();
            Render.batch.setColor(1f, 0f, 0f, 0.45f);
            Render.batch.draw(
                regionPixel,
                origenX - ANCHO_HITBOX / 2f,
                origenY,
                ANCHO_HITBOX / 2f,
                0f,
                ANCHO_HITBOX,
                LARGO,
                1f,
                1f,
                angulo - 90f);
            Render.batch.setPackedColor(colorAnterior);
            return;
        }

        Render.batch.draw(
            animacion.getKeyFrame(tiempoAnimacion),
            origenX - ANCHO_SPRITE / 2f,
            origenY,
            ANCHO_SPRITE / 2f,
            0f,
            ANCHO_SPRITE,
            LARGO,
            1f,
            1f,
            angulo - 90f);
    }
}
