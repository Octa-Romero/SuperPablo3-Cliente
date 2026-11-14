
package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.utilidades.Render;

public class Meteorito {

    private static final float TAMANO = 82f;
    private static final float ESCALA_ANCHO_FUEGO = 1.35f;

    private final int ID;
    private final float X;
    private final float SUELO_Y;
    private final Animation<TextureRegion> ANIMACION_FUEGO;
    private final Sprite SPRITE;

    private final float ANCHO_FUEGO;
    private final float ALTO_FUEGO;

    private float y;
    private boolean impacto;
    private float tiempoFuego;

    public Meteorito(
        int id, float x, float y, float sueloY,
        Texture textura,
        Animation<TextureRegion> animacionFuego) {

        this.ID = id;
        this.X = x;
        this.y = y;
        this.SUELO_Y = sueloY;
        this.ANIMACION_FUEGO = animacionFuego;

        TextureRegion frameFuego = animacionFuego.getKeyFrame(0f);

        ANCHO_FUEGO = TAMANO * ESCALA_ANCHO_FUEGO;
        ALTO_FUEGO = ANCHO_FUEGO
            * frameFuego.getRegionHeight()
            / frameFuego.getRegionWidth();

        SPRITE = new Sprite(textura);
        SPRITE.setSize(TAMANO, TAMANO);
        SPRITE.setOriginCenter();
        SPRITE.setPosition(X - TAMANO / 2f, y);
    }

    public void actualizarEstado(float y, boolean impacto, float rotacion, float tiempoFuego) {

        this.y = y;
        this.impacto = impacto;
        this.tiempoFuego = tiempoFuego;

        if (impacto) {
            SPRITE.setRegion(ANIMACION_FUEGO.getKeyFrame(tiempoFuego));
            SPRITE.setSize(ANCHO_FUEGO, ALTO_FUEGO);
            SPRITE.setRotation(0f);
            SPRITE.setPosition(X - ANCHO_FUEGO / 2f, SUELO_Y);
        } else {
            SPRITE.setRotation(rotacion);
            SPRITE.setPosition(X - TAMANO / 2f, y);
        }
    }

    public int getId() {
        return ID;
    }

    public void dibujar() {
        SPRITE.draw(Render.batch);
    }
}
