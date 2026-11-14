package com.prz.juego.red;

public class MeteoritoRed {

    public enum Tipo {
        NORMAL,
        GRANDE
    }

    private final int id;
    private final Tipo tipo;
    private final float x;
    private final float y;
    private final boolean impacto;
    private final float rotacion;
    private final float tiempoEfecto;
    private final boolean estadoRecibido;

    public MeteoritoRed(int id, Tipo tipo, float x, float y) {
        this(id, tipo, x, y, false, 0f, 0f, false);
    }

    public MeteoritoRed(
        int id, Tipo tipo, float x, float y,
        boolean impacto, float rotacion, float tiempoEfecto) {

        this(id, tipo, x, y, impacto, rotacion, tiempoEfecto, true);
    }

    private MeteoritoRed(
        int id, Tipo tipo, float x, float y,
        boolean impacto, float rotacion, float tiempoEfecto,
        boolean estadoRecibido) {

        this.id = id;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.impacto = impacto;
        this.rotacion = rotacion;
        this.tiempoEfecto = tiempoEfecto;
        this.estadoRecibido = estadoRecibido;
    }

    public int getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public float getX() { return x; }
    public float getY() { return y; }
    public boolean estaImpactando() { return impacto; }
    public float getRotacion() { return rotacion; }
    public float getTiempoEfecto() { return tiempoEfecto; }
    public boolean tieneEstadoRecibido() { return estadoRecibido; }
}
