package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;

public class Tumba {

    private static final float ANCHO = 48f;
    private static final float ALTO = 64f;
    private static final String RUTA_TEXTURA = "Personajes/tumba.png";

    private final String PERSONAJE;

    private volatile float x;
    private volatile float y;
    private Sprite sprite;

    public Tumba(String personaje, float x, float y) {
        this.PERSONAJE = personaje;
        this.x = x;
        this.y = y;
    }

    public void dibujar() {
        if (sprite == null) {
            sprite = new Sprite(GestorRecursos.obtenerTextura(RUTA_TEXTURA));
            sprite.setSize(ANCHO, ALTO);
        }

        sprite.setPosition(x - ANCHO / 2f, y);
        sprite.draw(Render.batch);
    }

    public void actualizarEstado(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String getPersonaje() {
        return PERSONAJE;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
