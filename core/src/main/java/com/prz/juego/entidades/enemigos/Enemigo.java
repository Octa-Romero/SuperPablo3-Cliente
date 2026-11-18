package com.prz.juego.entidades.enemigos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.sistemas.Animacion;
import com.prz.juego.utilidades.Render;

public class Enemigo {

    private final int ID;
    private final Enemigos TIPO;
    private final float ANCHO;
    private final float ALTO;

    private float x;
    private float y;
    private int direccion = 1;
    private int tipoAnimacion = 0;
    private float tiempoAnimacion = 0f;
    private boolean spriteVisible = true;
    private double vida = 1;
    private Rectangle ataque;
    private Sprite sprite;
    private Animacion animacion;

    public Enemigo(int id, Enemigos tipo, float x, float y, float ancho, float alto) {
        ID = id;
        TIPO = tipo;
        ANCHO = ancho;
        ALTO = alto;
        this.x = x;
        this.y = y;
    }

    public void actualizarEstado(
        float x,
        float y,
        int direccion,
        double vida,
        boolean spriteVisible,
        int tipoAnimacion,
        float tiempoAnimacion,
        Rectangle ataque) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.vida = vida;
        this.spriteVisible = spriteVisible;
        this.tipoAnimacion = tipoAnimacion;
        this.tiempoAnimacion = tiempoAnimacion;
        this.ataque = ataque;
    }

    public boolean estaMuerto() {
        return vida <= 0;
    }

    public void dibujar() {
        if (!spriteVisible || estaMuerto()) {
            return;
        }

        if (sprite == null) {
            crearSprite();
        }

        tiempoAnimacion += Gdx.graphics.getDeltaTime();
        animacion.actualizarConEstado(sprite, tipoAnimacion, tiempoAnimacion);

        float offsetX = direccion > 0 ? TIPO.getOffsetDerecha() : TIPO.getOffsetIzquierda();
        sprite.setFlip(direccion < 0, false);
        sprite.setPosition(x + offsetX, y + TIPO.getOffsetY());
        sprite.draw(Render.batch);
    }

    private void crearSprite() {
        sprite = new Sprite(GestorRecursos.obtenerTextura(TIPO.getRutaTextura()));
        sprite.setSize(TIPO.getAnchoSprite(), TIPO.getAltoSprite());

        animacion = new Animacion(GestorRecursos.obtenerTextura(TIPO.getRutaTextura()));
        if (TIPO.tieneSpritesheet()) {
            animacion.configurar(
                GestorRecursos.obtenerTextura(TIPO.getRutaSheet()),
                TIPO.getAnchoFrame(),
                TIPO.getAltoFrame(),
                TIPO.getReposo(),
                TIPO.getCaminar(),
                TIPO.getSalto(),
                TIPO.getCaida(),
                TIPO.getAtaque(),
                TIPO.getPreparar(),
                TIPO.getDuracionFrame());
        }
    }

    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        if (estaMuerto()) {
            return;
        }

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(x, y, ANCHO, ALTO);

        if (ataque != null) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(ataque.x, ataque.y, ataque.width, ataque.height);
        }
    }

    public int getId() {
        return ID;
    }
}
