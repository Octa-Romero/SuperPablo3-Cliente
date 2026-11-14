package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Animacion;
import com.prz.juego.utilidades.Render;

public class Mano extends Enemigo {

    public enum Lado {
        IZQUIERDA,
        DERECHA
    }

    public enum EstadoAtaque {
        NINGUNO,
        LEVANTANDO,
        BAJANDO,
        RECUPERANDO,
        DESAPARECIENDO,
        ESPERANDO_REAPARICION,
        REAPARECIENDO,
        SIGUIENDO_JUGADOR,
        ESPERANDO_GOLPE,
        MOVIENDO,
        EN_POSICION,
        VOLVIENDO
    }

    public enum EstiloAtaque {
        NORMAL,
        PUNIO,
        PALMA
    }

    private static final float ANCHO = 120f;
    private static final float ALTO = 100f;
    private static final float SEPARACION = 10f;
    private static final float ALTURA_REPOSO = 120f;
    private static final double VIDA_INICIAL = 20;

    private static final float ANCHO_SPRITE = 170f;
    private static final float ALTO_SPRITE = 170f;
    private static final float ANCHO_SPRITE_EXTENDIDO = 200f;
    private static final float ALTO_SPRITE_EXTENDIDO = 200f;
    private static final float ANCHO_FUEGO_GOLPE = 220f;
    private static final float ALTO_FUEGO_GOLPE = ALTO;
    private static final int CANTIDAD_FRAMES = 3;
    private static final int CANTIDAD_FRAMES_FUEGO = 2;
    private static final float DURACION_FRAME = 0.12f;

    private final Lado LADO;
    private final Animacion ANIMACION_PUNIO;
    private final Animacion ANIMACION_ABIERTA_ARRIBA;
    private final Sprite SPRITE_FUEGO_GOLPE;
    private final Animacion ANIMACION_FUEGO_GOLPE;
    private final Rectangle BOUNDS_FUEGO_GOLPE = new Rectangle();

    private EstadoAtaque estadoAtaque = EstadoAtaque.NINGUNO;
    private EstiloAtaque estiloAtaque = EstiloAtaque.NORMAL;
    private float escalaTeletransporte = 1f;
    private boolean mostrarFuegoGolpe;

    public Mano(Retumbar retumbar, Lado lado) {
        super(
            0,
            retumbar.getY(),
            ANCHO,
            ALTO,
            0,
            VIDA_INICIAL,
            0,
            null,
            GestorRecursos.obtenerTextura("Jefe/manoAbajo.png")
        );

        this.LADO = lado;

        Texture texturaAbajo = GestorRecursos.obtenerTextura("Jefe/manoAbajo.png");
        Texture texturaArriba = GestorRecursos.obtenerTextura("Jefe/manoArriba.png");
        Texture texturaPunio = GestorRecursos.obtenerTextura("Jefe/punio.png");
        Texture texturaFuego = GestorRecursos.obtenerTextura("Jefe/fuegoSuelo.png");

        configurarAnimacion(ANIMACION, texturaAbajo);
        ANIMACION_PUNIO = new Animacion(texturaPunio);
        configurarAnimacion(ANIMACION_PUNIO, texturaPunio);
        ANIMACION_ABIERTA_ARRIBA = new Animacion(texturaArriba);
        configurarAnimacion(ANIMACION_ABIERTA_ARRIBA, texturaArriba);

        SPRITE_FUEGO_GOLPE = new Sprite(texturaFuego);
        ANIMACION_FUEGO_GOLPE = new Animacion(texturaFuego);
        configurarAnimacion(ANIMACION_FUEGO_GOLPE, texturaFuego, CANTIDAD_FRAMES_FUEGO);
        SPRITE_FUEGO_GOLPE.setFlip(lado == Lado.IZQUIERDA, false);

        duracionInvencibilidad = 0f;
        configurarSpriteVisual(
            ANCHO_SPRITE,
            ALTO_SPRITE,
            (ANCHO - ANCHO_SPRITE) / 2f,
            (ANCHO - ANCHO_SPRITE) / 2f,
            (ALTO - ALTO_SPRITE) / 2f
        );

        if (lado == Lado.IZQUIERDA) {
            x = retumbar.getX() - SEPARACION - ANCHO;
        } else {
            x = retumbar.getX() + retumbar.getAncho() + SEPARACION;
        }
        y = retumbar.getY() + ALTURA_REPOSO;
        bounds.setPosition(x, y);
        actualizarSpriteVisual();
        actualizarTexturaSegunEstado(0f);
        spriteVisible = false; // Esperar el primer ESTADO_MANO real.
    }

    private void configurarAnimacion(Animacion animacion, Texture textura) {
        configurarAnimacion(animacion, textura, CANTIDAD_FRAMES);
    }

    private void configurarAnimacion(Animacion animacion, Texture textura, int cantidadFrames) {
        int[] indicesFrames = new int[cantidadFrames];
        for (int i = 0; i < cantidadFrames; i++) {
            indicesFrames[i] = i;
        }

        animacion.configurar(
            textura,
            textura.getWidth() / cantidadFrames,
            textura.getHeight(),
            indicesFrames,
            null, null, null,
            DURACION_FRAME
        );
    }

    public void actualizarEstado(
        float x, float y, double vida, boolean visible, float escala,
        EstadoAtaque estadoAtaque, EstiloAtaque estiloAtaque,
        boolean mostrarFuegoGolpe) {

        this.x = x;
        this.y = y;
        this.vida = vida;
        this.muerto = vida <= 0;
        this.spriteVisible = visible && !muerto;
        this.escalaTeletransporte = escala;
        this.estadoAtaque = estadoAtaque;
        this.estiloAtaque = estiloAtaque;
        this.mostrarFuegoGolpe = mostrarFuegoGolpe && !muerto;

        bounds.setPosition(x, y);
        actualizarSpriteVisual();
        actualizarBoundsFuegoGolpe();

        System.out.println(
            "MANO: " + LADO +
                " | vida: " + vida +
                " | visible: " + visible +
                " | spriteVisible: " + spriteVisible +
                " | escala: " + escala
        );
    }

    @Override
    public void update(float delta) {
        if (muerto) {
            return;
        }

        // Solamente animación. Nada de IA, movimiento o daño local.
        actualizarTexturaSegunEstado(delta);
        ANIMACION_FUEGO_GOLPE.actualizar(SPRITE_FUEGO_GOLPE, delta, false, false, false);
        actualizarSpriteVisual();
        actualizarBoundsFuegoGolpe();
    }

    private void actualizarTexturaSegunEstado(float delta) {
        // El efecto de golpe puede convertir momentáneamente la mano normal en puño.
        if (estiloAtaque == EstiloAtaque.PUNIO
            || (estiloAtaque == EstiloAtaque.NORMAL && mostrarFuegoGolpe)) {
            ANIMACION_PUNIO.actualizar(sprite, delta, false, false, false);
            sprite.setFlip(LADO == Lado.DERECHA, false);
            ajustarTamanoSprite(ANCHO_SPRITE, ALTO_SPRITE);
            return;
        }

        if (estiloAtaque == EstiloAtaque.PALMA) {
            ANIMACION.actualizar(sprite, delta, false, false, false);
            sprite.setFlip(LADO == Lado.DERECHA, false);
            ajustarTamanoSprite(ANCHO_SPRITE_EXTENDIDO, ALTO_SPRITE_EXTENDIDO);
            return;
        }

        switch (estadoAtaque) {
            case LEVANTANDO:
            case MOVIENDO:
            case EN_POSICION:
                ANIMACION_ABIERTA_ARRIBA.actualizar(sprite, delta, false, false, false);
                sprite.setFlip(LADO == Lado.DERECHA, false);
                ajustarTamanoSprite(ANCHO_SPRITE_EXTENDIDO, ALTO_SPRITE_EXTENDIDO);
                break;

            case RECUPERANDO:
                ANIMACION_PUNIO.actualizar(sprite, delta, false, false, false);
                sprite.setFlip(LADO == Lado.DERECHA, false);
                ajustarTamanoSprite(ANCHO_SPRITE, ALTO_SPRITE);
                break;

            default:
                ANIMACION.actualizar(sprite, delta, false, false, false);
                sprite.setFlip(LADO == Lado.DERECHA, false);
                ajustarTamanoSprite(ANCHO_SPRITE_EXTENDIDO, ALTO_SPRITE_EXTENDIDO);
                break;
        }
    }

    private void ajustarTamanoSprite(float anchoSprite, float altoSprite) {
        if (this.anchoSprite != anchoSprite || this.altoSprite != altoSprite) {
            configurarSpriteVisual(
                anchoSprite,
                altoSprite,
                (ANCHO - anchoSprite) / 2f,
                (ANCHO - anchoSprite) / 2f,
                (ALTO - ALTO_SPRITE) / 2f
            );
        }
    }

    @Override
    protected void actualizarSpriteVisual() {
        super.actualizarSpriteVisual();
        if (sprite != null) {
            sprite.setOriginCenter();
            sprite.setScale(escalaTeletransporte);
        }
    }

    private void actualizarBoundsFuegoGolpe() {
        BOUNDS_FUEGO_GOLPE.set(
            x + (ANCHO - ANCHO_FUEGO_GOLPE) / 2f,
            y + (ALTO - ALTO_FUEGO_GOLPE) / 2f,
            ANCHO_FUEGO_GOLPE,
            ALTO_FUEGO_GOLPE
        );
    }

    @Override
    public void dibujar() {
        if (!spriteVisible || muerto) {
            return;
        }

        if (mostrarFuegoGolpe) {
            SPRITE_FUEGO_GOLPE.setBounds(
                BOUNDS_FUEGO_GOLPE.x,
                BOUNDS_FUEGO_GOLPE.y,
                BOUNDS_FUEGO_GOLPE.width,
                BOUNDS_FUEGO_GOLPE.height
            );
            SPRITE_FUEGO_GOLPE.draw(Render.batch);
        }

        super.dibujar();
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        if (muerto || !spriteVisible) {
            return;
        }

        if (mostrarFuegoGolpe) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(
                BOUNDS_FUEGO_GOLPE.x,
                BOUNDS_FUEGO_GOLPE.y,
                BOUNDS_FUEGO_GOLPE.width,
                BOUNDS_FUEGO_GOLPE.height
            );
        } else {
            super.dibujarHitbox(shapeRenderer);
        }
    }

    @Override
    public boolean debeVerificarColisionesMapa() {
        return false;
    }

    public Lado getLado() {
        return LADO;
    }

    @Override
    public void dispose() {
        ANIMACION_PUNIO.dispose();
        ANIMACION_ABIERTA_ARRIBA.dispose();
        ANIMACION_FUEGO_GOLPE.dispose();
        super.dispose();
    }
}
