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
    private boolean estaEnSuelo;
    private boolean estaTrepando;
    private volatile boolean enCentroHub;
    private volatile boolean esperandoSeleccion;
    private boolean invencible;
    private boolean spriteVisible;
    private boolean invencibilidadRecibida = false;
    private int tipoAnimacion;
    private float tiempoAnimacion;
    private boolean animacionRecibida = false;


    public void actualizarPosicion(float x, float y, int direccion, boolean estaEnSuelo, boolean estaTrepando) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.estaEnSuelo = estaEnSuelo;
        this.estaTrepando = estaTrepando;
        this.posicionRecibida = true;
    }

    public void actualizarVida(double vida) {
        this.vida = vida;
        this.vidaRecibida = true;
    }

    public void actualizarAtaque(boolean atacando, int tipoAnimacion, float tiempoAnimacion) {
        this.atacando = atacando;
        this.tipoAnimacion = tipoAnimacion;
        this.tiempoAnimacion = tiempoAnimacion;
        this.estadoAtaqueRecibida = true;
        this.animacionRecibida = true;
    }

    public void actualizarInvencibilidad(boolean invencible, boolean spriteVisible) {
        this.invencible = invencible;
        this.spriteVisible = spriteVisible;
        this.invencibilidadRecibida = true;
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

    public boolean estaEnSuelo()
    {
        return this.estaEnSuelo;
    }

    public boolean estaTrepando()
    {
        return this.estaTrepando;
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

    public void actualizarCentroHub(boolean valor) {
        this.enCentroHub = valor;
    }

    public void actualizarEstadoSeleccion(boolean valor) {
        this.esperandoSeleccion = valor;
    }

    public boolean estaEnCentroHub() {
        return this.enCentroHub;
    }

    public boolean estaEsperandoSeleccion() {
        return this.esperandoSeleccion;
    }

    public boolean esInvencible() {
        return invencible;
    }

    public boolean esSpriteVisible() {
        return spriteVisible;
    }

    public boolean recibioInvencibilidad() {
        return invencibilidadRecibida;
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
