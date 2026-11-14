package com.prz.juego.entidades.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.utilidades.Render;

public abstract class Enemigo extends Entidad {

    protected int id;
    protected float velocidad;
    protected float distanciaDeteccion = 500;
    protected float distanciaDeteccionVertical = 150;
    protected float tiempoPatrulla = 0;
    protected boolean caminandoPatrulla = false;
    protected boolean atacando = false;
    protected Jugador jugador;
    protected boolean sueltaReliquia = false;

    protected Enemigo(
            float x,
            float y,
            float ancho,
            float alto,
            int velocidadX,
            double vida,
            double danio,
            Jugador jugador,
            Texture textura) {
        super(x, y, ancho, alto, velocidadX, vida, danio, textura);
        this.id = id;
        this.jugador = jugador;
        velocidad = velocidadX;
        duracionInvencibilidad = 1f;
        orientarSprite(sprite, direccion);
    }

    @Override
    public void update(float delta) {
        if (muerto) return;

        actualizarInvencibilidad(delta);

        ANIMACION.actualizar(
            sprite,
            delta,
            x != xAnterior,
            !enSuelo,
            atacando
        );

        actualizarSpriteVisual();

        guardarPosicionAnterior();
    }

    public int getId()
    {
        return this.id;
    }

    protected void actualizarOrientacionSprites() {
        orientarSprite(sprite, direccion);
    }

    public void actualizarEstadoRed(float x, float y, int direccion, boolean estaAtacando, double vida)
    {
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

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public float getDireccion() {
        return direccion;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setSueltaReliquia(boolean valor) {
        this.sueltaReliquia = valor;
    }

    public boolean sueltaReliquia() {
        return sueltaReliquia;
    }

	@Override
	public void dispose() {
		super.dispose();
	}
}
