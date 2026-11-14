package com.prz.juego.red;

import com.prz.juego.entidades.jefes.AtaquesExtraRetumbar;

public class RayosRed {

    private AtaquesExtraRetumbar.FaseRayos fase;
    private boolean mostrarAviso;
    private float[] posiciones = new float[5];
    private boolean estadoRecibido;

    public void actualizarEstado(AtaquesExtraRetumbar.FaseRayos fase, boolean mostrarAviso, float[] posiciones) {

        this.fase = fase;
        this.mostrarAviso = mostrarAviso;
        this.posiciones = posiciones;
        this.estadoRecibido = true;
    }

    public AtaquesExtraRetumbar.FaseRayos getFase() {
        return fase;
    }

    public boolean mostrarAviso() {
        return mostrarAviso;
    }

    public float[] getPosiciones() {
        return posiciones.clone();
    }

    public boolean tieneEstadoRecibido() {
        return estadoRecibido;
    }
}
