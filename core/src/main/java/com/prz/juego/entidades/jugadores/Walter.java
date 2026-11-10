package com.prz.juego.entidades.jugadores;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Sonido;

import java.util.ArrayList;

public class Walter extends Jugador {

    private final ArrayList<BolaFuego> BOLAS_FUEGO = new ArrayList<>();

    public Walter(float x, float y) {
        super(
            x, y, 50, 80, 140, 5, 0.5,
            GestorRecursos.obtenerTextura("Personajes/Walter/walter.png"),
            GestorRecursos.obtenerTextura("Hud/walter_hud.png")
        );

        ANIMACION.configurar(
            GestorRecursos.obtenerTextura("Personajes/Walter/walter_spritesheet.png"),
            540,
            450,
            new int[]{0},
            new int[]{1, 2, 3, 4, 5, 3},
            null,
            new int[]{6},
            0.12f
        );

        configurarSpriteVisual(96f, 80f, 0f, -46f, 0f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void actualizarEstadoAtaque(boolean estaAtacando) {
        if (!this.atacando && estaAtacando) {
            Sonido.BOLA_FUEGO.sonar();
        }

        this.atacando = estaAtacando;
    }

    public void agregarBola(int id, float x, float y, int direccion) {
        BOLAS_FUEGO.add(new BolaFuego(id, x, y, direccion));
    }

    public void actualizarEstadoBola(int id, float x, float y) {
        for (BolaFuego bola : BOLAS_FUEGO) {
            if (bola.getId() == id) {
                bola.actualizarEstado(x, y);
                return;
            }
        }
    }

    public void eliminarBola(int id) {
        for (int i = 0; i < BOLAS_FUEGO.size(); i++) {
            if (BOLAS_FUEGO.get(i).getId() == id) {
                BOLAS_FUEGO.remove(i);
                return;
            }
        }
    }

    public void dibujarOrbes() {
        for (BolaFuego bola : BOLAS_FUEGO) {
            bola.dibujar();
        }
    }

    public void dibujarOrbesHitbox(ShapeRenderer shapeRenderer) {
        for (BolaFuego bola : BOLAS_FUEGO) {
            bola.dibujarHitbox(shapeRenderer);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        BOLAS_FUEGO.clear();
    }
}
