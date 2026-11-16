package com.prz.juego.red;

import com.prz.juego.entidades.enemigos.TipoEnemigo;

public class EnemigoRed {

    private int id;
    private TipoEnemigo tipo;
    private float x, y;
    private double vida;
    private int direccion;
    private boolean estaAtacando;
    private boolean spriteVisible = true;
    private int tipoAnimacion;
    private float tiempoAnimacion;
    private boolean animacionRecibida = false;

    public EnemigoRed(int id, TipoEnemigo tipo, float x, float y, double vida)
    {
        this.id = id;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.vida = vida;
    }

    public void actualizarEstado(float x, float y, int direccion, boolean estaAtacando, double vida, boolean spriteVisible, int tipoAnimacion, float tiempoAnimacion) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.estaAtacando = estaAtacando;
        this.vida = vida;
        this.spriteVisible = spriteVisible;
        this.tipoAnimacion = tipoAnimacion;
        this.tiempoAnimacion = tiempoAnimacion;
        this.animacionRecibida = true;
    }

    public int getId()
    {
        return this.id;
    }

    public TipoEnemigo getTipo()
    {
        return this.tipo;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public int getDireccion()
    {
        return this.direccion;
    }

    public boolean estaAtacando()
    {
        return this.estaAtacando;
    }

    public double getVida()
    {
        return this.vida;
    }

    public boolean esSpriteVisible() {
        return spriteVisible;
    }

    public int getTipoAnimacion() {
        return tipoAnimacion;
    }

    public float getTiempoAnimacion() {
        return tiempoAnimacion;
    }

    public boolean recibioAnimacion() {
        return animacionRecibida;
    }
}
