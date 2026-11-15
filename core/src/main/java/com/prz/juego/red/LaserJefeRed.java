package com.prz.juego.red;

public class LaserJefeRed {

    private boolean visible;
    private boolean aviso;
    private float angulo;
    private float tiempoAnimacion;
    private boolean estadoRecibido;

    public void actualizarEstado(
        boolean visible,
        boolean aviso,
        float angulo,
        float tiempoAnimacion) {

        this.visible = visible;
        this.aviso = aviso;
        this.angulo = angulo;
        this.tiempoAnimacion = tiempoAnimacion;
        this.estadoRecibido = true;
    }

    public boolean estaVisible() {
        return visible;
    }

    public boolean debeMostrarAviso() {
        return aviso;
    }

    public float getAngulo() {
        return angulo;
    }

    public float getTiempoAnimacion() {
        return tiempoAnimacion;
    }

    public boolean tieneEstadoRecibido() {
        return estadoRecibido;
    }

    public void reiniciar() {
        visible = false;
        aviso = false;
        angulo = 0f;
        tiempoAnimacion = 0f;
        estadoRecibido = false;
    }
}
