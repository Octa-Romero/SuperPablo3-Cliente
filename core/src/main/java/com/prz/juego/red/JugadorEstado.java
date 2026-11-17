package com.prz.juego.red;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.sistemas.Animacion;
import com.prz.juego.utilidades.Render;

public class JugadorEstado {

    private final Rectangle BOUNDS = new Rectangle();

    private Personajes personaje;
    private Sprite sprite;
    private Animacion animacion;
    private Texture texturaHud;
    private float x;
    private float y;
    private float xAnterior;
    private float yAnterior;
    private int direccion = 1;
    private float ancho;
    private float alto;
    private float anchoSprite;
    private float altoSprite;
    private float offsetDerecha;
    private float offsetIzquierda;
    private float offsetY;
    private double vida;
    private double vidaMaxima;
    private boolean muerto = false;
    private boolean atacando;
    private boolean estaEnSuelo = true;
    private boolean estaTrepando;
    private boolean spriteVisible = true;
    private boolean posicionRecibida = false;
    private boolean vidaRecibida = false;
    private boolean animacionRecibida = false;
    private int tipoAnimacion;
    private float tiempoAnimacion;
    private volatile boolean enCentroHub;
    private volatile boolean esperandoSeleccion;

    public void actualizarPosicion(float x, float y, int direccion, boolean estaEnSuelo, boolean estaTrepando) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.estaEnSuelo = estaEnSuelo;
        this.estaTrepando = estaTrepando;
        this.posicionRecibida = true;
    }

    public void actualizarVida(double vida) {
        this.vida = vida;
        this.vidaRecibida = true;
    }

    public void actualizarAtaque(boolean atacando, int tipoAnimacion, float tiempoAnimacion) {
        this.atacando = atacando;
        this.tipoAnimacion = tipoAnimacion;
        this.tiempoAnimacion = tiempoAnimacion;
        this.animacionRecibida = true;
    }

    public void actualizarInvencibilidad(boolean invencible, boolean spriteVisible) {
        this.spriteVisible = spriteVisible;
    }

    public void actualizarCentroHub(boolean valor) {
        this.enCentroHub = valor;
    }

    public void actualizarEstadoSeleccion(boolean valor) {
        this.esperandoSeleccion = valor;
    }

    public void configurar(Personajes personaje) {
        if (this.personaje != personaje || sprite == null) {
            this.personaje = personaje;

            Texture texturaIdle = GestorRecursos.obtenerTextura(personaje.getRutaIdle());
            sprite = new Sprite(texturaIdle);
            animacion = new Animacion(texturaIdle);
            animacion.configurar(
                GestorRecursos.obtenerTextura(personaje.getRutaSheet()),
                personaje.getAnchoFrame(),
                personaje.getAltoFrame(),
                personaje.getReposo(),
                personaje.getCaminar(),
                personaje.getSalto(),
                personaje.getCaida(),
                personaje.getAtaque(),
                personaje.getDuracionFrame());
            texturaHud = GestorRecursos.obtenerTextura(personaje.getRutaHud());

            vidaMaxima = personaje.getVida();
            if (!vidaRecibida) {
                vida = vidaMaxima;
            }
            muerto = false;
        }

        ancho = personaje.getAnchoCaja();
        alto = personaje.getAltoCaja();
        anchoSprite = personaje.getAnchoSprite();
        altoSprite = personaje.getAltoSprite();
        offsetDerecha = personaje.getOffsetDerecha();
        offsetIzquierda = personaje.getOffsetIzquierda();
        offsetY = personaje.getOffsetY();

        aplicarVisual();
    }

    public void actualizar(float delta) {
        if (sprite == null) {
            return;
        }

        if (vidaRecibida && vida <= 0) {
            vida = 0;
            muerto = true;
        }

        if (animacionRecibida) {
            animacion.actualizarConEstado(sprite, tipoAnimacion, tiempoAnimacion);
        } else {
            boolean enAire = !estaEnSuelo && !estaTrepando;
            animacion.actualizar(
                sprite,
                delta,
                x != xAnterior || (estaTrepando && y != yAnterior),
                false,
                enAire,
                atacando);
        }

        aplicarVisual();
        xAnterior = x;
        yAnterior = y;
    }

    public void dibujar() {
        if (sprite != null && posicionRecibida && !muerto && spriteVisible) {
            sprite.draw(Render.batch);
        }
    }

    private void aplicarVisual() {
        sprite.setFlip(direccion < 0, false);
        sprite.setSize(anchoSprite, altoSprite);

        float offsetX = sprite.isFlipX() ? offsetIzquierda : offsetDerecha;

        sprite.setPosition(x + offsetX, y + offsetY);
    }

    public void escalar(float escala) {
        if (escala <= 0) {
            throw new IllegalArgumentException("La escala debe ser positiva");
        }

        ancho *= escala;
        alto *= escala;
        anchoSprite *= escala;
        altoSprite *= escala;
        offsetDerecha *= escala;
        offsetIzquierda *= escala;
        offsetY *= escala;

        if (sprite != null) {
            aplicarVisual();
        }
    }

    public void forzarIdle() {
        if (sprite == null) {
            return;
        }

        animacion.forzarIdle(sprite);
        aplicarVisual();
    }

    public void reiniciar(float x, float y) {
        muerto = false;
        spriteVisible = true;
        vida = vidaMaxima;
        vidaRecibida = false;
        atacando = false;
        animacionRecibida = false;
        tipoAnimacion = 0;
        tiempoAnimacion = 0f;
        enCentroHub = false;
        esperandoSeleccion = false;
        estaEnSuelo = true;
        estaTrepando = false;
        this.x = x;
        this.y = y;
        xAnterior = x;
        yAnterior = y;
        forzarIdle();
    }

    public void resetearAnimacionRecibida() {
        this.animacionRecibida = false;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    public Rectangle getBounds() {
        BOUNDS.set(x, y, ancho, alto);
        return BOUNDS;
    }

    public double getVida() {
        return vida;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public boolean estaMuerto() {
        return muerto;
    }

    public Texture getTexturaHud() {
        return texturaHud;
    }

    public boolean estaEnCentroHub() {
        return this.enCentroHub;
    }

    public boolean estaEsperandoSeleccion() {
        return this.esperandoSeleccion;
    }
}
