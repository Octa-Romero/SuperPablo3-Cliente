package com.prz.juego.entidades.jugadores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.Lanzable;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.enemigos.rango.Proyectil;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Sonido;

import java.util.ArrayList;

public class Pablo extends Jugador {

    private float anchoAtaque = 100;
    private float altoAtaque = 10;
    private float tiempoAtaque = 0;
    private float duracionAtaque = 0.3f;
    private float tiempoCooldown = 0.5f;
    private boolean cooldownAtaque = false;
    private boolean yaDanio = false;
    private ArrayList<Entidad> entidades;
    private ArrayList<Lanzable> proyectiles;
    private final ArrayList<Proyectil> PROYECTILES_GOLPEADOS = new ArrayList<>();

    public Pablo(float x, float y) {
        super(
                x,
                y,
                50,
                80,
                170,
                10,
                1,
                GestorRecursos.obtenerTextura("Personajes/Pablo/pablo.png"),
                GestorRecursos.obtenerTextura("Hud/pablo_hud.png"));
        actualizarOrientacionSprites();
    }

	@Override
	public void update(float delta) {

		super.update(delta);
		actualizarOrientacionSprites();

	}

    @Override
    public void actualizarEstadoAtaque(boolean estaAtacando) {
        this.atacando = estaAtacando;
    }

    private void actualizarOrientacionSprites() {
		orientarSprite(sprite, direccion);
	}

    public Rectangle getBoundsAtaque() {
        float ataqueY = y + (alto / 2f) - (altoAtaque / 2f);
        float centroJugador = x + (ancho / 2f);
        float ataqueX;

        if (direccion > 0) {
            ataqueX = centroJugador;
        } else {
            ataqueX = centroJugador - anchoAtaque;
        }

        return new Rectangle(ataqueX, ataqueY, anchoAtaque, altoAtaque);
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        super.dibujarHitbox(shapeRenderer);
        if (atacando) {
            Rectangle ataque = getBoundsAtaque();
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(ataque.x, ataque.y, ataque.width, ataque.height);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
