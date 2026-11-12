package com.prz.juego.red;

public class ProyectilRed {

    private int id;
    private String origen;
    private float x, y;
    private float direccion;

    public ProyectilRed(int id, String origen, float x, float y, float direccion)
    {
        this.id = id;
        this.origen = origen;
        this.x = x;
        this.y = y;
        this.direccion = direccion;
    }

    public int getId()
    {
        return this.id;
    }

    public String getOrigen()
    {
        return this.origen;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDireccion() {
        return direccion;
    }

    public void actualizarEstado(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
}
