package com.prz.juego.red;

public class BolaFuegoRed {

    private int id;
    private float x, y;
    private int direccion;

    public BolaFuegoRed(int id, float x, float y, int direccion)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direccion = direccion;
    }

    public int getId()
    {
        return this.id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getDireccion() {
        return direccion;
    }

    public void actualizarEstado(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
}
