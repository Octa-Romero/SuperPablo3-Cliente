package com.prz.juego.entidades.jugadores;

import com.prz.juego.entidades.Lanzable;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Sonido;

import java.util.ArrayList;

public class Walter extends Jugador {

    private ArrayList<Lanzable> proyectiles;

    public Walter(float x, float y) {
        super(
                x,
                y,
                50,
                80,
                140,
                5,
                0.5,
                GestorRecursos.obtenerTextura("Personajes/Walter/walter.png"),
                GestorRecursos.obtenerTextura("Hud/walter_hud.png"));
        ANIMACION.configurar(
                GestorRecursos.obtenerTextura("Personajes/Walter/walter_spritesheet.png"),
                540,
                450,
                new int[] {0},
                new int[] {1, 2, 3, 4, 5, 3},
                null,
                new int[] {6},
                0.12f);
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

    public void agregarOrbes(int id, float x, float y, int direccion)
    {
        ORBES.add(new OrbeMagica(id, x, y, direccion));
    }

    public void actualizarEstadoOrbes(int id, float x, float y)
    {
        for(OrbeMagica o : ORBES)
        {
            if(o.getId() == id)
            {
                o.actualizarEstado(x, y);
                return;
            }
        }
    }

    public void eliminarOrbe(int id) {
        for (int i = 0; i < ORBES.size(); i++) {
            OrbeMagica orbe = ORBES.get(i);

            if (orbe.getId() == id) {
                ORBES.remove(i);
                return;
            }
        }
    }

    public void dibujarOrbes() {
        for (OrbeMagica orbe : ORBES) {
            orbe.dibujar();
        }
    }

    public void dibujarOrbesHitbox(ShapeRenderer shapeRenderer) {
        for (OrbeMagica orbe : ORBES) {
            orbe.dibujarHitbox(shapeRenderer);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        ORBES.clear();
    }
}
