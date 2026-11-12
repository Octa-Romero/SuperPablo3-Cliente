package com.prz.juego.entidades.enemigos.rango;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.prz.juego.entidades.Lanzable;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.sistemas.Colisiones;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Jugador;

public abstract class EnemigoRango extends Enemigo {

    protected EnemigoRango(
        int id,
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
            id, x, y, ancho, alto,
            velocidadX, vida, danio,
            jugador, textura
        );
    }

}
