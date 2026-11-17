package com.prz.juego.entidades.retumbar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.utilidades.Frames;
import com.prz.juego.utilidades.Render;

public class Mano {

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

    private float x;
    private float y;
    private double vida;
    private double vidaMaxima;
    private boolean visible;
    private float escala = 1f;
    private EstadoAtaque estadoAtaque = EstadoAtaque.NINGUNO;
    private EstiloAtaque estiloAtaque = EstiloAtaque.NORMAL;
    private boolean mostrarFuegoGolpe;
    private boolean estadoRecibido = false;

    private float tiempoAnimacion;
    private Sprite sprite;
    private Sprite spriteFuego;
    private Animation<TextureRegion> animacionAbajo;
    private Animation<TextureRegion> animacionArriba;
    private Animation<TextureRegion> animacionPunio;
    private Animation<TextureRegion> animacionFuego;

    public Mano(Lado lado) {
        this.LADO = lado;
    }

    public void actualizarEstado(
        float x, float y, double vida,
        boolean visible, float escala,
        EstadoAtaque estadoAtaque,
        EstiloAtaque estiloAtaque,
        boolean mostrarFuegoGolpe) {

        this.x = x;
        this.y = y;
        this.vida = vida;
        if (vida > vidaMaxima) {
            vidaMaxima = vida;
        }
        this.visible = visible;
        this.escala = escala;
        this.estadoAtaque = estadoAtaque;
        this.estiloAtaque = estiloAtaque;
        this.mostrarFuegoGolpe = mostrarFuegoGolpe;
        this.estadoRecibido = true;
    }

    public void actualizar(float delta) {
        tiempoAnimacion += delta;
    }

    private void inicializarGraficos() {
        if (sprite != null) {
            return;
        }

        animacionAbajo = Frames.animacion("Jefe/manoAbajo.png", CANTIDAD_FRAMES, DURACION_FRAME);
        animacionArriba = Frames.animacion("Jefe/manoArriba.png", CANTIDAD_FRAMES, DURACION_FRAME);
        animacionPunio = Frames.animacion("Jefe/punio.png", CANTIDAD_FRAMES, DURACION_FRAME);
        animacionFuego = Frames.animacion("Jefe/fuegoSuelo.png", CANTIDAD_FRAMES_FUEGO, DURACION_FRAME);
        sprite = new Sprite(animacionAbajo.getKeyFrame(0f));
        spriteFuego = new Sprite(animacionFuego.getKeyFrame(0f));
    }

    private boolean esPunio() {
        return estiloAtaque == EstiloAtaque.PUNIO
            || (estiloAtaque == EstiloAtaque.NORMAL && mostrarFuegoGolpe);
    }

    public void dibujar() {
        if (!estadoRecibido || !visible || vida <= 0) {
            return;
        }

        inicializarGraficos();

        if (mostrarFuegoGolpe) {
            spriteFuego.setRegion(animacionFuego.getKeyFrame(tiempoAnimacion));
            spriteFuego.setFlip(LADO == Lado.IZQUIERDA, false);
            spriteFuego.setBounds(
                x + (ANCHO - ANCHO_FUEGO_GOLPE) / 2f,
                y + (ALTO - ALTO_FUEGO_GOLPE) / 2f,
                ANCHO_FUEGO_GOLPE,
                ALTO_FUEGO_GOLPE);
            spriteFuego.draw(Render.batch);
        }

        boolean extendido = true;
        TextureRegion frame;

        if (esPunio()) {
            frame = animacionPunio.getKeyFrame(tiempoAnimacion);
            extendido = false;
        } else if (estiloAtaque == EstiloAtaque.PALMA) {
            frame = animacionAbajo.getKeyFrame(tiempoAnimacion);
        } else {
            switch (estadoAtaque) {
                case LEVANTANDO:
                case MOVIENDO:
                case EN_POSICION:
                    frame = animacionArriba.getKeyFrame(tiempoAnimacion);
                    break;
                case RECUPERANDO:
                    frame = animacionPunio.getKeyFrame(tiempoAnimacion);
                    extendido = false;
                    break;
                default:
                    frame = animacionAbajo.getKeyFrame(tiempoAnimacion);
                    break;
            }
        }

        float anchoSprite = extendido ? ANCHO_SPRITE_EXTENDIDO : ANCHO_SPRITE;
        float altoSprite = extendido ? ALTO_SPRITE_EXTENDIDO : ALTO_SPRITE;

        sprite.setRegion(frame);
        sprite.setFlip(LADO == Lado.DERECHA, false);
        sprite.setBounds(
            x + (ANCHO - anchoSprite) / 2f,
            y + (ALTO - ALTO_SPRITE) / 2f,
            anchoSprite,
            altoSprite);
        sprite.setOriginCenter();
        sprite.setScale(escala);
        sprite.draw(Render.batch);
    }

    public Lado getLado() {
        return LADO;
    }

    public double getVida() {
        return vida;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public boolean estaMuerta() {
        return estadoRecibido && vida <= 0;
    }

    public boolean recibioEstado() {
        return estadoRecibido;
    }
}
