package com.prz.juego.entidades.retumbar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.utilidades.Frames;
import com.prz.juego.utilidades.Render;

public class Fueguito {

    public enum Tipo {
        LLUVIA,
        RADIAL
    }

    private static final int CANTIDAD_FRAMES = 2;
    private static final float DURACION_FRAME = 0.08f;
    private static final float ESCALA_ALTO_SPRITE = 1.5f;
    private static final float ANCHO_RADIAL = 42f;
    private static final float ALTO_RADIAL = 64f;

    private final int ID;
    private final Tipo TIPO;

    private float x;
    private float y;
    private float tamano;
    private float rotacion;
    private float tiempoAnimacion;
    private Sprite sprite;
    private Animation<TextureRegion> animacion;

    public Fueguito(
        int id, Tipo tipo, float x, float y,
        float tamano, float rotacion) {

        this.ID = id;
        this.TIPO = tipo;
        actualizarEstado(x, y, tamano, rotacion);
    }

    public Fueguito(int id, Tipo tipo) {
        this(id, tipo, 0f, 0f, 0f, 0f);
    }

    public void actualizarEstado(float x, float y, float tamano, float rotacion) {
        this.x = x;
        this.y = y;
        this.tamano = tamano;
        this.rotacion = rotacion;
    }

    public void actualizar(float delta) {
        tiempoAnimacion += delta;
    }

    public void dibujar() {
        if (animacion == null) {
            animacion = Frames.animacion("Jefe/fueguito.png", CANTIDAD_FRAMES, DURACION_FRAME);
            sprite = new Sprite(animacion.getKeyFrame(0f));
        }

        sprite.setRegion(animacion.getKeyFrame(tiempoAnimacion));

        if (TIPO == Tipo.RADIAL) {
            sprite.setSize(ANCHO_RADIAL, ALTO_RADIAL);
        } else {
            sprite.setSize(tamano, tamano * ESCALA_ALTO_SPRITE);
        }

        sprite.setOriginCenter();
        sprite.setPosition(x, y);
        sprite.setRotation(rotacion);
        sprite.draw(Render.batch);
    }

    public int getId() {
        return ID;
    }

    public Tipo getTipo() {
        return TIPO;
    }
}
