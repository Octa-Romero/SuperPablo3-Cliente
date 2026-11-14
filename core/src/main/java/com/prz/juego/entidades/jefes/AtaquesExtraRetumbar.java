package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.red.LasersRed;
import com.prz.juego.red.MeteoritoRed;
import com.prz.juego.red.RayosRed;
import com.prz.juego.utilidades.Render;

import java.util.ArrayList;
import java.util.List;

public class AtaquesExtraRetumbar {

    public enum FaseRayos {
        AVISO,
        IMPACTO,
        PAUSA
    }

    public enum FaseLasers {
        AVISO,
        IMPACTO,
        PAUSA
    }

    private static final int CANTIDAD_RAYOS = 5;
    private static final float ANCHO_RAYO = 95f;

    private final float ALTO_MAPA;
    private final float SUELO_Y;

    private final Texture TEXTURA_RAYO;
    private final Texture TEXTURA_PIXEL;

    private final float[] POSICIONES_RAYOS =
        new float[CANTIDAD_RAYOS];

    private FaseRayos faseRayos;

    private boolean mostrarAvisoRayo;
    private static final int CANTIDAD_LASERES = 15;
    private static final float ANCHO_LASER = 60f;
    private static final float ANCHO_SPRITE_LASER = 90f;
    private static final float DURACION_FRAME_LASER = 0.12f;

    private final float ANCHO_MAPA;

    private final float[] POSICIONES_LASERES =
        new float[CANTIDAD_LASERES];

    private boolean[] lasersActivos;
    private FaseLasers faseLasers;
    private boolean mostrarAvisoLaser;

    private final Animation<TextureRegion> ANIMACION_LASER;
    private float tiempoAnimacionLaser;

    private final List<Meteorito> METEORITOS = new ArrayList<>();
    private final List<MeteoritoGrande> METEORITOS_GRANDES = new ArrayList<>();

    private final Texture TEXTURA_METEORITO;
    private final Animation<TextureRegion> ANIMACION_FUEGO;

    public AtaquesExtraRetumbar(float anchoMapa, float altoMapa, float sueloY) {

        this.ANCHO_MAPA = anchoMapa;
        this.ALTO_MAPA = altoMapa;
        this.SUELO_Y = sueloY;

        TEXTURA_RAYO = GestorRecursos.obtenerTextura("Jefe/rayo.png");
        TEXTURA_PIXEL = GestorRecursos.obtenerPixel();

        Texture texturaLaser =
            GestorRecursos.obtenerTextura("Jefe/laser.png");

        TextureRegion[][] framesLaser = TextureRegion.split(
            texturaLaser,
            texturaLaser.getWidth() / 2,
            texturaLaser.getHeight()
        );

        ANIMACION_LASER =
            new Animation<>(DURACION_FRAME_LASER, framesLaser[0]);

        ANIMACION_LASER.setPlayMode(Animation.PlayMode.LOOP);

        inicializarPosicionesLasers();

        TEXTURA_METEORITO =
            GestorRecursos.obtenerTextura("Jefe/meteorito.png");

        Texture texturaFuego =
            GestorRecursos.obtenerTextura("Jefe/fuegoMeteorito.png");

        TextureRegion[] framesFuego = TextureRegion.split(
            texturaFuego,
            texturaFuego.getWidth() / 2,
            texturaFuego.getHeight()
        )[0];

        ANIMACION_FUEGO = new Animation<>(0.14f, framesFuego);
        ANIMACION_FUEGO.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        if (faseLasers == FaseLasers.IMPACTO) {
            tiempoAnimacionLaser += delta;
        }
    }

    private void inicializarPosicionesLasers() {
        float margen = ANCHO_SPRITE_LASER / 2f;
        float anchoUtil = Math.max(0f, ANCHO_MAPA - 2f * margen);

        for (int i = 0; i < POSICIONES_LASERES.length; i++) {
            POSICIONES_LASERES[i] =
                margen + i * anchoUtil / (POSICIONES_LASERES.length - 1f);
        }
    }

    public void crearMeteorito(MeteoritoRed estado) {
        int id = estado.getId();

        if (buscarMeteorito(id) != null
            || buscarMeteoritoGrande(id) != null) {
            return;
        }

        if (estado.getTipo() == MeteoritoRed.Tipo.NORMAL) {
            METEORITOS.add(new Meteorito(
                id,
                estado.getX(),
                estado.getY(),
                SUELO_Y,
                TEXTURA_METEORITO,
                ANIMACION_FUEGO
            ));
        } else {
            METEORITOS_GRANDES.add(new MeteoritoGrande(
                id,
                estado.getX(),
                estado.getY(),
                SUELO_Y,
                ANCHO_MAPA,
                TEXTURA_METEORITO,
                ANIMACION_FUEGO
            ));
        }
    }

    private Meteorito buscarMeteorito(int id) {
        for (Meteorito meteorito : METEORITOS) {
            if (meteorito.getId() == id) {
                return meteorito;
            }
        }

        return null;
    }

    private MeteoritoGrande buscarMeteoritoGrande(int id) {
        for (MeteoritoGrande meteorito : METEORITOS_GRANDES) {
            if (meteorito.getId() == id) {
                return meteorito;
            }
        }

        return null;
    }

    private void dibujarAvisosLasers() {
        if (!mostrarAvisoLaser) {
            return;
        }

        Color colorAnterior = Render.batch.getColor().cpy();
        Render.batch.setColor(1f, 0f, 0f, 0.35f);

        for (int i = 0; i < POSICIONES_LASERES.length; i++) {
            if (!lasersActivos[i]) {
                continue;
            }

            Render.batch.draw(
                TEXTURA_PIXEL,
                POSICIONES_LASERES[i] - ANCHO_LASER / 2f,
                SUELO_Y,
                ANCHO_LASER,
                ALTO_MAPA - SUELO_Y
            );
        }

        Render.batch.setColor(colorAnterior);
    }

    private void dibujarLasers() {
        TextureRegion frame =
            ANIMACION_LASER.getKeyFrame(tiempoAnimacionLaser);

        float alturaLaser = ALTO_MAPA - SUELO_Y;

        for (int i = 0; i < POSICIONES_LASERES.length; i++) {
            if (!lasersActivos[i]) {
                continue;
            }

            Render.batch.draw(
                frame,
                POSICIONES_LASERES[i] - ANCHO_SPRITE_LASER / 2f,
                SUELO_Y,
                ANCHO_SPRITE_LASER,
                alturaLaser
            );
        }
    }

    public void actualizarEstadoRayos(RayosRed datos) {
        faseRayos = datos.getFase();
        mostrarAvisoRayo = datos.mostrarAviso();

        System.arraycopy(
            datos.getPosiciones(), 0,
            POSICIONES_RAYOS, 0,
            CANTIDAD_RAYOS
        );
    }

    public void actualizarEstadoLasers(LasersRed datos) {
        faseLasers = datos.getFase();
        mostrarAvisoLaser = datos.mostrarAviso();
        lasersActivos = datos.getActivos();
    }

    public void actualizarEstadoMeteorito(MeteoritoRed estado) {
        int id = estado.getId();

        if (estado.getTipo() == MeteoritoRed.Tipo.NORMAL) {
            Meteorito meteorito = buscarMeteorito(id);

            if (meteorito == null) {
                crearMeteorito(estado);
                meteorito = buscarMeteorito(id);
            }

            if (meteorito != null) {
                meteorito.actualizarEstado(estado.getY(), estado.estaImpactando(), estado.getRotacion(), estado.getTiempoEfecto());
            }

        } else {
            MeteoritoGrande meteorito = buscarMeteoritoGrande(id);

            if (meteorito == null) {
                crearMeteorito(estado);
                meteorito = buscarMeteoritoGrande(id);
            }

            if (meteorito != null) {
                meteorito.actualizarEstado(estado.getY(), estado.estaImpactando(), estado.getRotacion(), estado.getTiempoEfecto());
            }
        }
    }

    public void eliminarMeteorito(int id) {
        METEORITOS.removeIf(m -> m.getId() == id);
        METEORITOS_GRANDES.removeIf(m -> m.getId() == id);
    }

    public void dibujar() {

        if (faseRayos == FaseRayos.AVISO) {
            dibujarAvisosRayos();
        } else if (faseRayos == FaseRayos.IMPACTO) {
            dibujarRayos();
        }

        if (faseLasers == FaseLasers.AVISO) {
            dibujarAvisosLasers();
        } else if (faseLasers == FaseLasers.IMPACTO) {
            dibujarLasers();
        }

        for (Meteorito meteorito : METEORITOS) {
            meteorito.dibujar();
        }

        for (MeteoritoGrande meteorito : METEORITOS_GRANDES) {
            meteorito.dibujar();
        }
    }

    private void dibujarAvisosRayos() {
        if (!mostrarAvisoRayo) {
            return;
        }

        Color colorAnterior = Render.batch.getColor().cpy();
        Render.batch.setColor(1f, 0f, 0f, 0.35f);

        for (float x : POSICIONES_RAYOS) {
            Render.batch.draw(
                TEXTURA_PIXEL,
                x - ANCHO_RAYO / 2f,
                SUELO_Y,
                ANCHO_RAYO,
                ALTO_MAPA - SUELO_Y
            );
        }

        Render.batch.setColor(colorAnterior);
    }

    private void dibujarRayos() {
        for (float x : POSICIONES_RAYOS) {
            Render.batch.draw(
                TEXTURA_RAYO,
                x - ANCHO_RAYO / 2f,
                SUELO_Y,
                ANCHO_RAYO,
                ALTO_MAPA - SUELO_Y
            );
        }
    }
}
