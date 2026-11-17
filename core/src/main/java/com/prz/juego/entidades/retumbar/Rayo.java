package com.prz.juego.entidades.retumbar;

import com.badlogic.gdx.graphics.Texture;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;

public class Rayo {

    public enum Fase {
        AVISO,
        IMPACTO,
        PAUSA
    }

    private static final int CANTIDAD_RAYOS = 5;
    private static final float ANCHO_RAYO = 95f;

    private Fase fase;
    private boolean mostrarAviso;
    private float[] posiciones = new float[CANTIDAD_RAYOS];
    private Texture texturaRayo;
    private Texture texturaPixel;

    public void actualizarEstado(Fase fase, boolean mostrarAviso, float[] posiciones) {
        this.fase = fase;
        this.mostrarAviso = mostrarAviso;
        this.posiciones = posiciones;
    }

    public void reiniciar() {
        fase = null;
        mostrarAviso = false;
    }

    public void dibujar(float sueloY, float altoMapa) {
        if (fase != Fase.AVISO && fase != Fase.IMPACTO) {
            return;
        }

        if (fase == Fase.AVISO && !mostrarAviso) {
            return;
        }

        if (texturaRayo == null) {
            texturaRayo = GestorRecursos.obtenerTextura("Jefe/rayo.png");
            texturaPixel = GestorRecursos.obtenerPixel();
        }

        float altura = altoMapa - sueloY;
        float colorAnterior = Render.batch.getPackedColor();

        if (fase == Fase.AVISO) {
            Render.batch.setColor(1f, 0f, 0f, 0.35f);
        }

        Texture textura = fase == Fase.AVISO ? texturaPixel : texturaRayo;

        for (float posicion : posiciones) {
            Render.batch.draw(textura, posicion - ANCHO_RAYO / 2f, sueloY, ANCHO_RAYO, altura);
        }

        Render.batch.setPackedColor(colorAnterior);
    }
}
