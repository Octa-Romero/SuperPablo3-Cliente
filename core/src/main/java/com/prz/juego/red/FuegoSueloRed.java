package com.prz.juego.red;

public class FuegoSueloRed {

    private float x, y;
    private float alto;
    private boolean activo;
    private boolean estadoRecibido;

    public void actualizarEstado(float x, float y, float alto, boolean activo) {
        this.x = x;
        this.y = y;
        this.alto = alto;
        this.activo = activo;
        this.estadoRecibido = true;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAlto()
    {
        return this.alto;
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean tieneEstadoRecibido() {
        return estadoRecibido;
    }
}
