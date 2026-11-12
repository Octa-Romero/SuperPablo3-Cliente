package com.prz.juego.entidades.enemigos.rango;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.prz.juego.entidades.Lanzable;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;

public class Proyectil implements Lanzable {

    private int id;
    private float x;
    private float y;


    private final float ANCHO = 50;
    private final float ALTO = 10;

    private final Sprite SPRITE;
    private final Polygon BOUNDS;

    public Proyectil(int id, float x, float y, float angulo) {
        this.id = id;
        this.x = x;
        this.y = y;

        SPRITE = new Sprite(GestorRecursos.obtenerTextura("Enemigos/BichoRango/flecha.png"));
        SPRITE.setSize(ANCHO, ALTO);
        SPRITE.setOriginCenter();
        SPRITE.setRotation(angulo);

        BOUNDS = new Polygon(
            new float[]{0, 0, ANCHO, 0, ANCHO, ALTO, 0, ALTO}
        );

        BOUNDS.setOrigin(ANCHO / 2f, ALTO / 2f);
        BOUNDS.setRotation(angulo);

        SPRITE.setPosition(x - ANCHO / 2f, y - ALTO / 2f);
        BOUNDS.setPosition(x - ANCHO / 2f, y - ALTO / 2f);    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dibujar() {
        SPRITE.draw(Render.batch);
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.polygon(BOUNDS.getTransformedVertices());
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void actualizarEstado(float x, float y) {
        this.x = x;
        this.y = y;

        SPRITE.setPosition(x - ANCHO / 2f, y - ALTO / 2f);
        BOUNDS.setPosition(x - ANCHO / 2f, y - ALTO / 2f);
    }

}
