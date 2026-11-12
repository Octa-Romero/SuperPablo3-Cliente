package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Lanzable {

    void update(float delta);

    void dibujar();

    void dibujarHitbox(ShapeRenderer shapeRenderer);

    int getId();

    void actualizarEstado(float x, float y);
}
