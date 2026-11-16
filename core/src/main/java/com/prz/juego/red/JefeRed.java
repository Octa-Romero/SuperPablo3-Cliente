package com.prz.juego.red;

public class JefeRed {

    private float x, y;
    private double vida;
    private boolean visible;
    private float escala;
    private String faseEspecial;

    public JefeRed(float x, float y, double vida) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.faseEspecial = "NORMAL";
    }

    public void actualizarEstado(float x, float y, double vida,
                                 boolean visible, float escala, String faseEspecial) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.visible = visible;
        this.escala = escala;
        this.faseEspecial = faseEspecial;
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

    public String getFaseEspecial() {
        return faseEspecial;
    }
}
