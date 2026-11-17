package com.prz.juego.entidades.enemigos;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;

public class Enemigo {

    private final int ID;
    private final TipoEnemigo TIPO;

    private float x;
    private float y;
    private int direccion = 1;
    private boolean spriteVisible = true;
    private Sprite sprite;

    public Enemigo(int id, TipoEnemigo tipo, float x, float y) {
        ID = id;
        TIPO = tipo;
        this.x = x;
        this.y = y;
    }

    public void actualizarEstado(float x, float y, int direccion, boolean spriteVisible) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.spriteVisible = spriteVisible;
    }

    public void dibujar() {
        if (!spriteVisible) {
            return;
        }

        if (sprite == null) {
            sprite = new Sprite(GestorRecursos.obtenerTextura(TIPO.getRutaTextura()));
            sprite.setSize(TIPO.getAncho(), TIPO.getAlto());
        }

        sprite.setFlip(direccion < 0, false);
        sprite.setPosition(x, y);
        sprite.draw(Render.batch);
    }

    public int getId() {
        return ID;
    }
}
