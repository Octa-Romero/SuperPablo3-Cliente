package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public abstract class Jugador extends Entidad {

	protected Entrada entrada;
	private final Texture TEXTURA_HUD;
	protected Hud hud;
	protected boolean atacando;
    protected float direccion = 1;

	protected Jugador(float x, float y, float ancho, float alto, int velocidadX, double vida, double danio, Texture textura, Texture TEXTURA_HUD) {
		super(x, y, ancho, alto, velocidadX, vida, danio, textura);
		this.TEXTURA_HUD = TEXTURA_HUD;
	}

	public void setEntrada(Entrada entrada) {
		this.entrada = entrada;
	}

	@Override
	public void update(float delta) {
        actualizarAnimacion(delta, x != xAnterior, atacando);
        actualizarOrientacionSegunMovimiento();
        actualizarSpriteVisual();
		actualizarInvencibilidad(delta);
	}

	public void dibujar() {
		if (spriteVisible && sprite != null) {
			sprite.draw(Render.batch);
		}
	}

    public void actualizarPosicion(float x, float y, float direccion) {
        guardarPosicionAnterior();

        this.x = x;
        this.y = y;
        this.direccion = direccion;

        bounds.setPosition(x, y);
    }

    public abstract void actualizarEstadoAtaque(boolean estaAtacando);


    public void actualizarEstadoVida(double cantidad)
    {
        setVida(cantidad);
    }

	public Hud getHud() {
		return hud;
	}

	public Texture getTEXTURA_HUD() {
		return TEXTURA_HUD;
	}

	public boolean estaAtacando() {
		return atacando;
	}
}
