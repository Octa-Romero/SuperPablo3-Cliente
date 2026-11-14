package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.red.FuegoSueloRed;
import com.prz.juego.red.ManoRed;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Retumbar extends Enemigo {

    private static final float ANCHO = 300;
    private static final float ALTO = 400;
    private static final float ANCHO_SPRITE = 370;
    private static final int CANTIDAD_FRAMES = 3;
    private static final float DURACION_FRAME = 0.12f;
    private final Mano MANO_IZQUIERDA;
    private final Mano MANO_DERECHA;
    private final FuegoSuelo FUEGO_SUELO;


    public Retumbar(float x, float y, double vida, float sueloXMin, float sueloXMax) {
        super(
            x, y,
            ANCHO, ALTO,
            0, vida, 0,
            null,
            GestorRecursos.obtenerTextura("Jefe/retumbar.png")
        );

        float offsetX = (ANCHO - ANCHO_SPRITE) / 2f;

        configurarSpriteVisual(
            ANCHO_SPRITE,
            ALTO + 100,
            offsetX,
            offsetX,
            0
        );

        sprite.setOriginCenter();

        Texture textura = GestorRecursos.obtenerTextura("Jefe/retumbar.png");

        ANIMACION.configurar(
            textura,
            textura.getWidth() / CANTIDAD_FRAMES,
            textura.getHeight(),
            new int[]{0, 1, 2},
            null, null, null,
            DURACION_FRAME
        );

        ANIMACION.actualizar(sprite, 0f, false, false, false);

        MANO_IZQUIERDA = new Mano(this, Mano.Lado.IZQUIERDA);
        MANO_DERECHA = new Mano(this, Mano.Lado.DERECHA);

        FUEGO_SUELO = new FuegoSuelo(sueloXMin, sueloXMax, y);
    }

    @Override
    public void update(float delta) {
        ANIMACION.actualizar(sprite, delta, false, false, false);
        actualizarSpriteVisual();
    }

    public boolean debeDibujarBarraVida() {
        return MANO_IZQUIERDA.estaMuerto()
            && MANO_DERECHA.estaMuerto();
    }

    public Mano getManoIzquierda()
    {
        return this.MANO_IZQUIERDA;
    }

    public Mano getManoDerecha()
    {
        return this.MANO_DERECHA;
    }

    public FuegoSuelo getFuegoSuelo()
    {
        return this.FUEGO_SUELO;
    }

    public void actualizarEstado(float x, float y, double vida,
                                 boolean visible, float escala) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.spriteVisible = visible;

        bounds.setPosition(x, y);
        sprite.setScale(escala);

        actualizarSpriteVisual();
        System.out.println("RETUMBAR X: " + x + " | Y: " + y);
    }

    public void actualizarEstadoMano(ManoRed estadoRed) {
        Mano mano;
        if(estadoRed.getLado() == Mano.Lado.IZQUIERDA)
        {
            mano = MANO_IZQUIERDA;
        }else {
            mano = MANO_DERECHA;
        }

        mano.actualizarEstado(
            estadoRed.getX(),
            estadoRed.getY(),
            estadoRed.getVida(),
            estadoRed.esVisible(),
            estadoRed.getEscalaSprite(),
            estadoRed.getEstadoAtaque(),
            estadoRed.getEstiloAtaque(),
            estadoRed.mostrarFuegoGolpe()
        );
    }

    public void actualizarEstadoFuegoSuelo(FuegoSueloRed estadoRed)
    {
        this.FUEGO_SUELO.actualizarEstado(estadoRed.getX(), estadoRed.getY(), estadoRed.getAlto(), estadoRed.estaActivo());
    }
}
