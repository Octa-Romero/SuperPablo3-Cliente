package com.prz.juego.red;

public class JefeRed {

    private float x, y;
    private double vida;
    private boolean visible;
    private float escala;

    public JefeRed(float x, float y, double vida) {
        this.x = x;
        this.y = y;
        this.vida = vida;
    }

    public void actualizarEstado(float x, float y, double vida,
                                 boolean visible, float escala) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.visible = visible;
        this.escala = escala;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public double getVida() {
        return vida;
    }

    public boolean esVisible() {
        return visible;
    }

    public float getEscalaSprite() {
        return escala;
    }
}
