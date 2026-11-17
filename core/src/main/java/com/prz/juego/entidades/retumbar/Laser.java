package com.prz.juego.entidades.retumbar;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Frames;
import com.prz.juego.utilidades.Render;

public class Laser {

    public enum Fase {
        AVISO,
        IMPACTO,
        PAUSA
    }

    private static final int CANTIDAD_LASERS = 15;
    private static final float ANCHO_LASER = 60f;
    private static final float ANCHO_SPRITE_LASER = 90f;
    private static final int CANTIDAD_FRAMES = 2;
    private static final float DURACION_FRAME = 0.12f;

    private Fase fase;
    private boolean mostrarAviso;
    private boolean[] activos = new boolean[CANTIDAD_LASERS];
    private float tiempoAnimacion;
    private Animation<TextureRegion> animacion;
    private Texture texturaPixel;

    public void actualizarEstado(Fase fase, boolean mostrarAviso, boolean[] activos) {
        this.fase = fase;
        this.mostrarAviso = mostrarAviso;
        this.activos = activos;
    }

    public void reiniciar() {
        fase = null;
        mostrarAviso = false;
        tiempoAnimacion = 0f;
    }

    public void actualizar(float delta) {
        if (fase == Fase.IMPACTO) {
            tiempoAnimacion += delta;
        }
    }

    public void dibujar(float sueloY, float altoMapa, float anchoMapa) {
        if (fase != Fase.AVISO && fase != Fase.IMPACTO) {
            return;
        }

        if (fase == Fase.AVISO && !mostrarAviso) {
            return;
        }

        if (animacion == null) {
            animacion = Frames.animacion("Jefe/laser.png", CANTIDAD_FRAMES, DURACION_FRAME);
            texturaPixel = GestorRecursos.obtenerPixel();
        }

        float altura = altoMapa - sueloY;
        float margen = ANCHO_SPRITE_LASER / 2f;
        float anchoUtil = Math.max(0f, anchoMapa - 2f * margen);
        float colorAnterior = Render.batch.getPackedColor();
        TextureRegion frame = animacion.getKeyFrame(tiempoAnimacion);

        if (fase == Fase.AVISO) {
            Render.batch.setColor(1f, 0f, 0f, 0.35f);
        }

        for (int i = 0; i < activos.length; i++) {
            if (!activos[i]) {
                continue;
            }

            float centro = margen + i * anchoUtil / (CANTIDAD_LASERS - 1f);

            if (fase == Fase.AVISO) {
                Render.batch.draw(texturaPixel, centro - ANCHO_LASER / 2f, sueloY, ANCHO_LASER, altura);
            } else {
                Render.batch.draw(frame, centro - ANCHO_SPRITE_LASER / 2f, sueloY, ANCHO_SPRITE_LASER, altura);
            }
        }

        Render.batch.setPackedColor(colorAnterior);
    }
}
