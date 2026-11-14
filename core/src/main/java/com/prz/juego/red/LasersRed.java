package com.prz.juego.red;

import com.prz.juego.entidades.jefes.AtaquesExtraRetumbar;

public class LasersRed {

    private AtaquesExtraRetumbar.FaseLasers fase;
    private boolean mostrarAviso;
    private boolean[] activos;
    private boolean estadoRecibido;

    public void actualizarEstado(AtaquesExtraRetumbar.FaseLasers fase, boolean mostrarAviso, boolean[] activos) {

        this.fase = fase;
        this.mostrarAviso = mostrarAviso;
        this.activos = activos;
        this.estadoRecibido = true;
    }

    public AtaquesExtraRetumbar.FaseLasers getFase() {
        return fase;
    }

    public boolean mostrarAviso() {
        return mostrarAviso;
    }

    public boolean[] getActivos() {
        return activos;
    }

    public boolean tieneEstadoRecibido() {
        return estadoRecibido;
    }
}
