package com.prz.juego.entidades.jugadores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.prz.juego.entidades.Lanzable;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;

public class BolaFuego implements Lanzable {

    private static final float RADIO = 12f;
    private static final float VELOCIDAD_ROTACION = -720f;

    private final int id;
    private float x;
    private float y;
    private final int DIRECCION;
    private final Sprite SPRITE;
    private final Circle BOUNDS;

    public BolaFuego(int id, float x, float y, int direccion) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.DIRECCION = direccion;

        SPRITE = new Sprite(
            GestorRecursos.obtenerTextura("Personajes/Walter/bola_fuego.png")
        );

        SPRITE.setSize(RADIO * 2, RADIO * 2);
        SPRITE.setOriginCenter();
        SPRITE.setPosition(x - RADIO, y - RADIO);

        BOUNDS = new Circle(x, y, RADIO);
    }

    @Override
    public void update(float delta) {
        SPRITE.rotate(VELOCIDAD_ROTACION * DIRECCION * delta);
    }

    @Override
    public void dibujar() {
        SPRITE.draw(Render.batch);
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(BOUNDS.x, BOUNDS.y, BOUNDS.radius);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void actualizarEstado(float x, float y) {
        this.x = x;
        this.y = y;

        BOUNDS.setPosition(x, y);
        SPRITE.setPosition(x - RADIO, y - RADIO);

    }
}
