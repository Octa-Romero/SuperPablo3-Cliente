package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Sonido;

public class Pablo extends Jugador {

    private float anchoAtaque = 100;
	private float altoAtaque = 10;
	private final Sprite SPRITE_ATAQUE;

	public Pablo(float x, float y) {
		super(x, y, 50, 80, 170, 10, 1, GestorRecursos.obtenerTextura("Personajes/Pablo/pablo.png"), GestorRecursos.obtenerTextura("Hud/pablo_hud.png"));
        SPRITE_ATAQUE = new Sprite(
            GestorRecursos.obtenerTextura("Personajes/Pablo/ataque_pablo.png")
        );
		SPRITE_ATAQUE.setSize(anchoAtaque, altoAtaque);
		actualizarOrientacionSprites();
	}

	@Override
	public void update(float delta) {

		super.update(delta);
		actualizarOrientacionSprites();

		if (atacando) {
			Rectangle ataque = getBoundsAtaque();
			SPRITE_ATAQUE.setPosition(ataque.x, ataque.y);
		}
	}

    @Override
    public void actualizarEstadoAtaque(boolean estaAtacando) {
        if (!this.atacando && estaAtacando) {
            Sonido.ESPADA.sonar();
        }

        this.atacando = estaAtacando;
    }

    private void actualizarOrientacionSprites() {
		orientarSprite(sprite, direccion);
		orientarSprite(SPRITE_ATAQUE, direccion);
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

	public void dibujarAtaque() {
		if (!atacando) {
			return;
		}

		Rectangle ataque = getBoundsAtaque();
		SPRITE_ATAQUE.setPosition(ataque.x, ataque.y);
		SPRITE_ATAQUE.draw(com.prz.juego.utilidades.Render.batch);
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
