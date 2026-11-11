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

    public enum Modo {
        ESTATICO,
        MOVIL
    }

    private final Modo MODO;

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
        Texture textura,
        Texture texturaProyectil,
        float anchoProyectil,
        float altoProyectil,
        Modo modo) {

        super(
            id, x, y, ancho, alto,
            velocidadX, vida, danio,
            jugador, textura
        );

        this.MODO = modo;
    }

    public void configurarDireccionInicial(float direccion) {
        if (direccion == 0f) {
            throw new IllegalArgumentException(
                "La direccion inicial no puede ser cero"
            );
        }

        this.direccion = (int)Math.signum(direccion);
        orientarSprite(sprite, this.direccion);
    }

    public Modo getModo() {
        return MODO;
    }
}
