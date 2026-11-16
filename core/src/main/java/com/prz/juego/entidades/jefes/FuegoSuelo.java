
package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;

public class FuegoSuelo extends Enemigo {

    private static final float ALTO = 50f;
    private static final float ALTO_MINIMO = 1f;
    private static final float SOLAPAMIENTO_LLAMA = 12f;
    private static final float MULTIPLICADOR_ALTURA_SPRITE = 2f;
    private static final int CANTIDAD_FRAMES_FUEGO = 2;
    private static final float DURACION_FRAME_FUEGO = 0.08f;

    private final float ANCHO_FRAME_FUEGO;
    private final float ALTO_FRAME_FUEGO;

    private boolean activa = false;

    public FuegoSuelo(float xMin, float xMax, float ySuelo) {
        super(
            xMin,
            ySuelo,
            xMax - xMin,
            ALTO,
            0,
            1,
            0,
            null,
            GestorRecursos.obtenerTextura("Jefe/fuegoSuelo.png")
        );

        Texture textura = GestorRecursos.obtenerTextura("Jefe/fuegoSuelo.png");

        ANCHO_FRAME_FUEGO =
            textura.getWidth() / (float) CANTIDAD_FRAMES_FUEGO;
        ALTO_FRAME_FUEGO = textura.getHeight();

        ANIMACION.configurar(
            textura,
            textura.getWidth() / CANTIDAD_FRAMES_FUEGO,
            textura.getHeight(),
            new int[]{0, 1},
            null,
            null,
            null,
            null,
            DURACION_FRAME_FUEGO
        );

        ANIMACION.actualizar(sprite, 0f, false, false, false, false);
        bounds.set(x, y, ancho, alto);
    }

    public void actualizarEstado(float x, float y, float alto, boolean activa) {

        this.x = x;
        this.y = y;
        this.alto = alto;
        this.activa = activa;

        bounds.set(x, y, ancho, alto);
    }

    @Override
    public void update(float delta) {
        if (!activa) {
            return;
        }

        ANIMACION.actualizar(sprite, delta, false, false, false, false);
    }

    @Override
    public void dibujar() {
        if (!activa) {
            return;
        }

        float altoLlama =
            Math.max(ALTO_MINIMO, alto) * MULTIPLICADOR_ALTURA_SPRITE;

        float anchoLlama =
            altoLlama * ANCHO_FRAME_FUEGO / ALTO_FRAME_FUEGO;

        float paso = Math.max(1f, anchoLlama - SOLAPAMIENTO_LLAMA);
        float inicio = x - anchoLlama / 2f;

        for (float xLlama = inicio;
             xLlama < x + ancho - anchoLlama / 2f;
             xLlama += paso) {

            sprite.setBounds(xLlama, y, anchoLlama, altoLlama);
            sprite.draw(Render.batch);
        }
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        if (!activa) {
            return;
        }

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(
            bounds.x,
            bounds.y,
            bounds.width,
            bounds.height
        );
    }

    @Override
    public boolean debeVerificarColisionesMapa() {
        return false;
    }

    public boolean estaActiva() {
        return activa;
    }
}
