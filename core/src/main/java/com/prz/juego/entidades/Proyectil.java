package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;

public class Proyectil {

    private static final String ORIGEN_WALTER = "WALTER";
    private static final String TEXTURA_BOLA_FUEGO = "Personajes/Walter/bola_fuego.png";
    private static final String TEXTURA_FLECHA = "Enemigos/BichoRango/flecha.png";
    private static final float RADIO_BOLA_FUEGO = 12f;
    private static final float VELOCIDAD_ROTACION = -720f;
    private static final float ANCHO_FLECHA = 50f;
    private static final float ALTO_FLECHA = 10f;

    private final int ID;
    private final String ORIGEN;
    private final float DIRECCION;

    private volatile float x;
    private volatile float y;
    private Sprite sprite;
    private float ancho;
    private float alto;

    public Proyectil(int id, String origen, float x, float y, float direccion) {
        this.ID = id;
        this.ORIGEN = origen;
        this.x = x;
        this.y = y;
        this.DIRECCION = direccion;
    }

    private boolean esBolaFuego() {
        return ORIGEN_WALTER.equals(ORIGEN);
    }

    private void configurar() {
        boolean bolaFuego = esBolaFuego();
        ancho = bolaFuego ? RADIO_BOLA_FUEGO * 2 : ANCHO_FLECHA;
        alto = bolaFuego ? RADIO_BOLA_FUEGO * 2 : ALTO_FLECHA;

        sprite = new Sprite(GestorRecursos.obtenerTextura(bolaFuego ? TEXTURA_BOLA_FUEGO : TEXTURA_FLECHA));
        sprite.setSize(ancho, alto);
        sprite.setOriginCenter();

        if (!bolaFuego) {
            sprite.setRotation(DIRECCION);
        }
    }

    public void update(float delta) {
        if (sprite != null && esBolaFuego()) {
            sprite.rotate(VELOCIDAD_ROTACION * (int) DIRECCION * delta);
        }
    }

    public void dibujar() {
        if (sprite == null) {
            configurar();
        }

        sprite.setPosition(x - ancho / 2f, y - alto / 2f);
        sprite.draw(Render.batch);
    }

    public int getId() {
        return ID;
    }

    public String getOrigen() {
        return ORIGEN;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDireccion() {
        return DIRECCION;
    }

    public void actualizarEstado(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
