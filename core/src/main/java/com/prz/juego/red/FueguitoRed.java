package com.prz.juego.red;

public class FueguitoRed {

    public enum Tipo {
        LLUVIA,
        RADIAL
    }

    private final int id;
    private final Tipo tipo;
    private final float x;
    private final float y;
    private final float tamano;
    private final float rotacion;

    public FueguitoRed(
        int id, Tipo tipo, float x, float y,
        float tamano, float rotacion) {

        this.id = id;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.tamano = tamano;
        this.rotacion = rotacion;
    }

    public int getId() {
        return id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getTamano() {
        return tamano;
    }

    public float getRotacion() {
        return rotacion;
    }
}
