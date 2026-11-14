package com.prz.juego.red;

import com.prz.juego.entidades.jefes.Mano;

public class ManoRed {

    private Mano.Lado lado;
    private float x, y;
    private double vida;
    private boolean visible;
    private float escala;
    private Mano.EstadoAtaque estadoAtaque;
    private Mano.EstiloAtaque estiloAtaque;
    private boolean mostrarFuegoGolpe;
    private boolean estadoRecibido = false;

    public ManoRed(Mano.Lado lado) {
        this.lado = lado;
    }

    public void actualizarEstado(
        float x, float y, double vida,
        boolean visible, float escala,
        Mano.EstadoAtaque estadoAtaque,
        Mano.EstiloAtaque estiloAtaque,
        boolean mostrarFuegoGolpe) {

        this.x = x;
        this.y = y;
        this.vida = vida;
        this.visible = visible;
        this.escala = escala;
        this.estadoAtaque = estadoAtaque;
        this.estiloAtaque = estiloAtaque;
        this.mostrarFuegoGolpe = mostrarFuegoGolpe;
        this.estadoRecibido = true;
    }

    public Mano.Lado getLado()
    {
        return this.lado;
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

    public Mano.EstadoAtaque getEstadoAtaque() {
        return estadoAtaque;
    }

    public Mano.EstiloAtaque getEstiloAtaque() {
        return estiloAtaque;
    }

    public boolean mostrarFuegoGolpe() {
        return mostrarFuegoGolpe;
    }

    public boolean recibioEstado() {
        return estadoRecibido;
    }
}
