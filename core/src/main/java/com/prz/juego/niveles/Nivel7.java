package com.prz.juego.niveles;

import com.prz.juego.entidades.jefes.Retumbar;
import com.prz.juego.red.JefeRed;
import com.prz.juego.utilidades.Musica;

public class Nivel7 extends Nivel {

    public static final String RUTA = "Niveles/Niveles/Nivel7.tmx";
    private static final float CENTRO_MAPA = 688;
    private float altoMapa;
    private static final float ANCHO_MAPA = CENTRO_MAPA * 2;
    private static final float SUELO = 210;
    private static final float X_INICIAL_JUGADOR = 100;
    private static final float Y_INICIAL_JUGADOR = 210;

    private Retumbar jefe;

    public Nivel7() {
        musica = Musica.RETUMBAR;
    }

    @Override
    protected String getRutaMapa() {
        return RUTA;
    }

    @Override
    public void crearJefe(JefeRed estadoRed) {
        if (jefe == null) {
            jefe = new Retumbar(
                estadoRed.getX(),
                estadoRed.getY(),
                estadoRed.getVida(),
                0,
                ANCHO_MAPA
            );

            jefe.configurarAtaquesExtra(
                ANCHO_MAPA,
                altoMapa,
                estadoRed.getY()
            );

            jefe.configurarAnimacionesFueguitos(
                texturaLluvia,
                null
            );

            agregarEntidadAlFondo(jefe);
            agregarEntidad(jefe.getManoIzquierda());
            agregarEntidad(jefe.getManoDerecha());
            agregarEntidad(jefe.getFuegoSuelo());
        }
    }

    @Override
    protected void configurarEntornoEspecifico(float ancho, float alto) {
        this.altoMapa = ancho;
    }


    @Override
    protected float getPosicionInicialX() {
        return X_INICIAL_JUGADOR;
    }

    @Override
    protected float getPosicionInicialY() {
        return Y_INICIAL_JUGADOR;
    }

    @Override
    public Retumbar getJefe()
    {
        return this.jefe;
    }
}
