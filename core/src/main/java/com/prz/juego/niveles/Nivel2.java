package com.prz.juego.niveles;

import com.prz.juego.entidades.enemigos.rango.BichoRango;
import com.prz.juego.entidades.enemigos.rango.EnemigoRango;
import com.prz.juego.entidades.jugadores.Jugador;

public class Nivel2 extends Nivel {

    public static final String RUTA = "Niveles/Niveles/Nivel2.1.tmx";

    @Override
    protected String getRutaMapa() {
        return RUTA;
    }

    @Override
    protected float getPosicionInicialX() {
        return 500;
    }

    @Override
    protected float getPosicionInicialY() {
        return 150;
    }
}
