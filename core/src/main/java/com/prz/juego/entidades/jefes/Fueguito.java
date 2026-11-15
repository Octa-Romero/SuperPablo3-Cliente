
package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.utilidades.Render;

public class Fueguito {

    private static final int CANTIDAD_FRAMES = 2;
    private static final float DURACION_FRAME = 0.08f;
    private static final float ESCALA_ALTO_SPRITE = 1.5f;
    private static final float ANCHO_RADIAL = 42f;
    private static final float ALTO_RADIAL = 64f;

    private final int ID;
    private final Sprite SPRITE;
    private final Animation<TextureRegion> ANIMACION;

    private float tiempoAnimacion;

    public Fueguito(
        int id, float x, float y,
        float tamano, float rotacion,
        Texture textura) {

        this.ID = id;

        TextureRegion[][] frames = TextureRegion.split(
            textura,
            textura.getWidth() / CANTIDAD_FRAMES,
            textura.getHeight()
        );

        ANIMACION = new Animation<>(
            DURACION_FRAME,
            frames[0][0],
            frames[0][1]
        );

        ANIMACION.setPlayMode(Animation.PlayMode.LOOP);

        SPRITE = new Sprite(ANIMACION.getKeyFrame(0f));

        SPRITE.setSize(
            tamano,
            tamano * ESCALA_ALTO_SPRITE
        );

        actualizarEstado(x, y, rotacion);
    }

    public Fueguito(
        int id, float x, float y,
        float rotacion,
        Animation<TextureRegion> animacion) {

        this.ID = id;
        this.ANIMACION = animacion;

        SPRITE = new Sprite(animacion.getKeyFrame(0f));

        SPRITE.setSize(ANCHO_RADIAL, ALTO_RADIAL);
        SPRITE.setOriginCenter();

        actualizarEstado(x, y, rotacion);
    }

    public void actualizarEstado(
        float x, float y, float rotacion) {

        SPRITE.setPosition(x, y);
        SPRITE.setRotation(rotacion);
    }

    public void update(float delta) {
        tiempoAnimacion += delta;

        SPRITE.setRegion(
            ANIMACION.getKeyFrame(tiempoAnimacion, true)
        );
    }

    public int getId() {
        return ID;
    }

    public void dibujar() {
        SPRITE.draw(Render.batch);
    }
}
