package com.prz.juego.entidades.jugadores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class OrbeMagica {

    private static final float RADIO = 12f;
    private int id;
    private float x;
    private float y;
    private final int DIRECCION;
    private final Sprite SPRITE;
    private final Circle BOUNDS;

    public OrbeMagica(int id, float x, float y, int DIRECCION) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.DIRECCION = DIRECCION;
        SPRITE = new Sprite(
            GestorRecursos.obtenerTextura("Personajes/Walter/orbe.png")
        );
        SPRITE.setSize(RADIO * 2, RADIO * 2);
        SPRITE.setPosition(x - RADIO, y - RADIO);
        BOUNDS = new Circle(x, y, RADIO);
    }

    public void actualizarEstado(float x, float y)
    {
        this.x = x;
        this.y = y;

        BOUNDS.setPosition(x, y);
        SPRITE.setPosition(x - RADIO, y - RADIO);
    }

    public void dibujar() {
        SPRITE.draw(Render.batch);
    }

    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(BOUNDS.x, BOUNDS.y, BOUNDS.radius);
    }

    public int getId()
    {
        return this.id;
    }

}
