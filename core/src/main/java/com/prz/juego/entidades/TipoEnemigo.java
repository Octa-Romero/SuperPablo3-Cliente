package com.prz.juego.entidades;

import com.prz.juego.red.EnemigoRed;

public enum TipoEnemigo {
    BICHO1,
    BICHO2;

    public Enemigo crear(EnemigoRed datos, Jugador jugador) {
        switch (this) {
            case BICHO1:
                return new Bicho1(datos.getId(), datos.getX(), datos.getY(), jugador);

            default:
                return null;
        }
    }
}
