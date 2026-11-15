package com.prz.juego.entidades.jugadores;

import com.badlogic.gdx.graphics.Texture;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.red.JugadorEstadoRed;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public abstract class Jugador extends Entidad {

	protected Entrada entrada;
    protected JugadorEstadoRed estadoRed;
	private final Texture TEXTURA_HUD;
	protected boolean atacando;
    private boolean trepando = false;
    private boolean accionesHabilitadas = true;


    protected Jugador(
            float x,
            float y,
            float ancho,
            float alto,
            int velocidadX,
            double vida,
            double danio,
            Texture textura,
            Texture TEXTURA_HUD) {
        super(x, y, ancho, alto, velocidadX, vida, danio, textura);
        this.TEXTURA_HUD = TEXTURA_HUD;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public void setEstadoRed(JugadorEstadoRed estadoRed)
    {
        this.estadoRed = estadoRed;
    }

	@Override
	public void update(float delta) {
        actualizarEstadoRed();

        if (estadoRed != null && estadoRed.recibioAnimacion()) {
            ANIMACION.actualizarConEstado(
                sprite,
                estadoRed.getTipoAnimacion(),
                estadoRed.getTiempoAnimacion());
        } else {
            ANIMACION.actualizar(
                sprite,
                delta,
                x != xAnterior || (trepando && y != yAnterior),
                !enSuelo && !trepando,
                atacando);
        }

        actualizarOrientacionSegunMovimiento();
        actualizarSpriteVisual();
		// No actualizar invencibilidad local si viene de red
		if (estadoRed == null) {
			actualizarInvencibilidad(delta);
		}
	}

    public void setAccionesHabilitadas(boolean habilitadas) {
        accionesHabilitadas = habilitadas;
        if (!habilitadas) {
            atacando = false;
        }
    }

    protected boolean accionesHabilitadas() {
        return accionesHabilitadas;
    }

    public void dibujar() {
        if (spriteVisible && sprite != null) {
            sprite.draw(Render.batch);
        }
    }

    protected void actualizarEstadoRed() {
        if (estadoRed == null) {
            return;
        }

        if (estadoRed.recibioPosicion()) {
            actualizarPosicion(
                estadoRed.getX(),
                estadoRed.getY(),
                estadoRed.getDireccion(),
                estadoRed.estaEnSuelo(),
                estadoRed.estaTrepando()
            );
        }

        if (estadoRed.recibioEstadoAtaque()) {
            actualizarEstadoAtaque(estadoRed.estaAtacando());
        }

        if (estadoRed.recibioVida()) {
            actualizarEstadoVida(estadoRed.getVida());
        }

        if (estadoRed.recibioInvencibilidad()) {
            this.spriteVisible = estadoRed.esSpriteVisible();
        }
    }

    protected void actualizarPosicion(float x, float y, int direccion, boolean estaEnSuelo, boolean estaTrepando) {
        guardarPosicionAnterior();

        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.enSuelo = estaEnSuelo;
        this.trepando = estaTrepando;

        bounds.setPosition(x, y);
    }

    protected abstract void actualizarEstadoAtaque(boolean estaAtacando);


    public void actualizarEstadoVida(double cantidad)
    {
        setVida(cantidad);
    }

    public JugadorEstadoRed getEstadoRed()
    {
        return this.estadoRed;
    }

	public Texture getTEXTURA_HUD() {
		return TEXTURA_HUD;
	}
}
