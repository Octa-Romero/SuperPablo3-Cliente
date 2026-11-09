package com.prz.juego.entidades.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.utilidades.Render;

public abstract class Enemigo extends Entidad {

    protected int id;
	protected float anchoAtaque = 100;
	protected float altoAtaque = 10;
	protected float velocidad;
	protected float distanciaDeteccion = 500;
	protected float distanciaAtaque = 15;
	protected float direccion = 1; // 1 derecha, -1 izquierda
	protected float tiempoPatrulla = 0;
	protected boolean caminandoPatrulla = false;
	protected float tiempoEstado = 0;
	protected float tiempoAntesDeAtacar = 0.3f;
	protected float tiempoEntreAtaques = 2f;
	protected boolean esperandoAtaque = false;
	protected boolean atacando = false;
	protected boolean cooldownAtaque = false;
	protected boolean yaDanio = false;
	protected Jugador jugador;
	protected Sprite spriteAtaque;

	protected Enemigo(int id,  float x, float y, float ancho, float alto, int velocidadX, double vida, double danio, Jugador jugador, Texture textura, Texture texturaAtaque) {
		super(x, y, ancho, alto, velocidadX, vida, danio, textura);
        this.id = id;
		this.jugador = jugador;
		velocidad = velocidadX;
		duracionInvencibilidad = 1f;
		spriteAtaque = new Sprite(texturaAtaque);
		spriteAtaque.setSize(anchoAtaque, altoAtaque);
		actualizarOrientacionSprites();
	}

	@Override
	public void update(float delta) {
		if (muerto) {
			return;
		}

		actualizarInvencibilidad(delta);

		actualizarAnimacion(delta, x != xAnterior, atacando);
		actualizarSpriteVisual();
		if (atacando) {
			Rectangle ataque = getBoundsAtaque();
			spriteAtaque.setPosition(ataque.x, ataque.y);
		}
	}

	protected Rectangle getBoundsAtaque() {
		float ataqueY =	y + (alto / 2f) - (altoAtaque / 2f);
		float centroEnemigo = x + ancho / 2f;
		float ataqueX;

		if (direccion > 0) {
			ataqueX = centroEnemigo;
		} else {
			ataqueX = centroEnemigo - anchoAtaque;
		}

		return new Rectangle(ataqueX, ataqueY, anchoAtaque, altoAtaque);
	}

    public int getId()
    {
        return this.id;
    }

	private void actualizarOrientacionSprites() {
		orientarSprite(sprite, direccion);
		orientarSprite(spriteAtaque, direccion);
	}

    public void actualizarEstadoRed(float x, float y, int direccion, boolean estaAtacando, double vida)
    {
        guardarPosicionAnterior();

        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.atacando = estaAtacando;
        setVida(vida);

        bounds.setPosition(x, y);
        actualizarOrientacionSprites();
    }

	@Override
	public void dibujar() {
		if (spriteVisible) {
			sprite.draw(Render.batch);
		}
	}

	public void dibujarAtaque() {
		if (!atacando) {
			return;
		}

		Rectangle ataque = getBoundsAtaque();
		spriteAtaque.setPosition(ataque.x, ataque.y);
		spriteAtaque.draw(Render.batch);
	}

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);

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
