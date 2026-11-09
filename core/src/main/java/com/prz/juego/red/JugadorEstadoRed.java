package com.prz.juego.red;


public class JugadorEstadoRed {

    private float x;
    private float y;
    private int direccion;
    private double vida;
    private boolean atacando;
    private boolean posicionRecibida = false;
    private boolean vidaRecibida = false;
    private boolean estadoAtaqueRecibida = false;

    public void actualizarPosicion(float x, float y, int direccion) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.posicionRecibida = true;
    }

    public void actualizarVida(double vida) {
        this.vida = vida;
        this.vidaRecibida = true;
    }

    public void actualizarAtaque(boolean atacando) {
        this.atacando = atacando;
        this.estadoAtaqueRecibida = true;
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

    public double getVida() {
        return vida;
    }

    public boolean estaAtacando() {
        return atacando;
    }

    public boolean recibioPosicion() {
        return this.posicionRecibida;
    }

    public boolean recibioVida()
    {
        return this.vidaRecibida;
    }

    public boolean recibioEstadoAtaque()
    {
        return this.estadoAtaqueRecibida;
    }
}
