package com.prz.juego.entidades.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;

public class Enemigo {

    private static final float ANCHO_ATAQUE_MELEE = 100f;
    private static final float ALTO_ATAQUE_MELEE = 10f;

    private final int ID;
    private final TipoEnemigo TIPO;

    private float x;
    private float y;
    private int direccion = 1;
    private boolean atacando = false;
    private boolean spriteVisible = true;
    private double vida = 1;
    private Sprite sprite;
    private static final float ESCALA_MINIJEFE = 1.5f;

    public Enemigo(int id, TipoEnemigo tipo, float x, float y) {
        ID = id;
        TIPO = tipo;
        this.x = x;
        this.y = y;
    }

    public void actualizarEstado(
        float x, float y, int direccion, boolean atacando, boolean spriteVisible, double vida) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.atacando = atacando;
        this.spriteVisible = spriteVisible;
        this.vida = vida;
    }

    public boolean estaMuerto() {
        return vida <= 0;
    }

    public void dibujar() {
        if (!spriteVisible || estaMuerto()) {
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

    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        if (estaMuerto()) {
            return;
        }

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(x, y, TIPO.getAncho(), TIPO.getAlto());

        if (atacando && (TIPO == TipoEnemigo.BICHO_MELEE || TIPO == TipoEnemigo.MINIJEFE_MELEE)) {
            float escala = TIPO == TipoEnemigo.MINIJEFE_MELEE ? ESCALA_MINIJEFE : 1f;
            float anchoAtaque = ANCHO_ATAQUE_MELEE * escala;
            float altoAtaque = ALTO_ATAQUE_MELEE * escala;
            float centro = x + TIPO.getAncho() / 2f;
            float ataqueX = direccion > 0 ? centro : centro - anchoAtaque;
            float ataqueY = y + TIPO.getAlto() / 2f - altoAtaque / 2f;

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(ataqueX, ataqueY, anchoAtaque, altoAtaque);
        }
    }

    public int getId() {
        return ID;
    }
}
