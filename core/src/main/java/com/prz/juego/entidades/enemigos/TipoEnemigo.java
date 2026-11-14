package com.prz.juego.entidades.enemigos;

import com.prz.juego.entidades.enemigos.melee.BichoMelee;
import com.prz.juego.entidades.enemigos.rango.BichoRango;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.red.EnemigoRed;

public enum TipoEnemigo {
    BICHO_MELEE,
    BICHO_RANGO;

    public Enemigo crear(EnemigoRed datos, Jugador jugador) {
        switch (this) {
            case BICHO_MELEE:
                return new BichoMelee(datos.getX(), datos.getY(), jugador);

            case BICHO_RANGO:
                return new BichoRango(datos.getX(), datos.getY(), jugador);
            default:
                return null;
        }
    }
}
