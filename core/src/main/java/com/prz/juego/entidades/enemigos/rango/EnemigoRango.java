package com.prz.juego.entidades.enemigos.rango;

import com.badlogic.gdx.graphics.Texture;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Jugador;

public abstract class EnemigoRango extends Enemigo {

    protected EnemigoRango(
        float x,
        float y,
        float ancho,
        float alto,
        int velocidadX,
        double vida,
        double danio,
        Jugador jugador,
        Texture textura) {

        super(
            x, y, ancho, alto,
            velocidadX, vida, danio,
            jugador, textura
        );
    }

}
