package com.prz.juego.entidades.enemigos.rango;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.prz.juego.entidades.Lanzable;
import com.prz.juego.utilidades.Render;

public class Proyectil implements Lanzable {

    private float x;
    private float y;

    private final float ANCHO;
    private final float ALTO;
    private final float VELOCIDAD;
    private final Vector2 DIRECCION;

    private final Sprite SPRITE;
    private final Polygon BOUNDS;

    private boolean activa = true;

    public Proyectil(
        float x,
        float y,
        Vector2 direccion,
        float ancho,
        float alto,
        float velocidad,
        Texture textura) {

        this.x = x;
        this.y = y;
        this.DIRECCION = new Vector2(direccion).nor();

        this.ANCHO = ancho;
        this.ALTO = alto;
        this.VELOCIDAD = velocidad;

        float angulo = this.DIRECCION.angleDeg();

        SPRITE = new Sprite(textura);
        SPRITE.setSize(ANCHO, ALTO);
        SPRITE.setOriginCenter();
        SPRITE.setRotation(angulo);

        BOUNDS = new Polygon(
            new float[]{0, 0, ANCHO, 0, ANCHO, ALTO, 0, ALTO}
        );

        BOUNDS.setOrigin(ANCHO / 2f, ALTO / 2f);
        BOUNDS.setRotation(angulo);

        actualizarPosicion();
    }

    @Override
    public void update(float delta) {
        if (!activa) {
            return;
        }

        // Movimiento exclusivamente visual.
        x += DIRECCION.x * VELOCIDAD * delta;
        y += DIRECCION.y * VELOCIDAD * delta;

        actualizarPosicion();
    }

    private void actualizarPosicion() {
        SPRITE.setPosition(x - ANCHO / 2f, y - ALTO / 2f);
        BOUNDS.setPosition(x - ANCHO / 2f, y - ALTO / 2f);
    }

    @Override
    public void dibujar() {
        if (activa) {
            SPRITE.draw(Render.batch);
        }
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        if (!activa) {
            return;
        }

        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.polygon(BOUNDS.getTransformedVertices());
    }

    @Override
    public boolean estaActiva() {
        return activa;
    }

    public void desactivar() {
        activa = false;
    }
}
