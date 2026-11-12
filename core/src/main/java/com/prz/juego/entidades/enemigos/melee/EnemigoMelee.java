package com.prz.juego.entidades.enemigos.melee;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.utilidades.Sonido;

public abstract class EnemigoMelee extends Enemigo {

    protected float anchoAtaque = 100;
    protected float altoAtaque = 10;

    protected EnemigoMelee(
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

        super(id, x, y, ancho, alto, velocidadX,
            vida, danio, jugador, textura);
    }

    protected Rectangle getBoundsAtaque() {
        float ataqueY = y + (alto / 2f) - (altoAtaque / 2f);
        float centroEnemigo = x + ancho / 2f;

        float ataqueX = direccion > 0
            ? centroEnemigo
            : centroEnemigo - anchoAtaque;

        return new Rectangle(
            ataqueX, ataqueY, anchoAtaque, altoAtaque
        );
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        super.dibujarHitbox(shapeRenderer);

        if (atacando) {
            Rectangle ataque = getBoundsAtaque();

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(
                ataque.x,
                ataque.y,
                ataque.width,
                ataque.height
            );
        }
    }

}
