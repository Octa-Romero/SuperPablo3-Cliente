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
import com.prz.juego.red.*;
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
    private final ArrayList<Fueguito> FUEGUITOS_DIRECCIONADOS =
        new ArrayList<>();
    private final Animation<TextureRegion> ANIMACION_FUEGUITO;
    private AtaquesExtraRetumbar ataquesExtra;
    private static final float ANCHO_SPRITE_LASER_JEFE = 96f;
    private static final float ANCHO_HITBOX_LASER_JEFE = 56f;
    private static final float LARGO_SPRITE_LASER_JEFE = 1000f;
    private static final float ALTURA_PECHO = 400f * 0.77f;

    private final TextureRegion REGION_PIXEL;
    private final Animation<TextureRegion> ANIMACION_LASER_JEFE;

    private boolean laserJefeVisible;
    private boolean avisoLaserJefe;
    private float anguloLaserJefe;
    private float tiempoAnimacionLaserJefe;

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

        Texture texturaFueguito = GestorRecursos.obtenerTextura("Jefe/fueguito.png");

        TextureRegion[] framesFueguito = TextureRegion.split(
            texturaFueguito,
            texturaFueguito.getWidth() / 2,
            texturaFueguito.getHeight()
        )[0];

        ANIMACION_FUEGUITO = new Animation<>(
            0.08f,
            framesFueguito
        );

        ANIMACION_FUEGUITO.setPlayMode(
            Animation.PlayMode.LOOP
        );

        REGION_PIXEL = new TextureRegion(GestorRecursos.obtenerPixel());

        Texture texturaLaser = GestorRecursos.obtenerTextura("Jefe/laser.png");

        TextureRegion[] framesLaser = TextureRegion.split(
            texturaLaser,
            texturaLaser.getWidth() / 2,
            texturaLaser.getHeight()
        )[0];

        ANIMACION_LASER_JEFE = new Animation<>(
            0.12f,
            framesLaser
        );

        ANIMACION_LASER_JEFE.setPlayMode(
            Animation.PlayMode.LOOP
        );
    }

    @Override
    public void update(float delta) {
        ANIMACION.actualizar(sprite, delta, false, false, false);
        actualizarSpriteVisual();

        if (ataquesExtra != null) {
            ataquesExtra.update(delta);
        }

        for (Fueguito fueguito : FUEGUITOS_DIRECCIONADOS) {
            fueguito.update(delta);
        }
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
        this.muerto = vida <= 0;
        this.spriteVisible = visible;

        bounds.setPosition(x, y);
        sprite.setScale(escala);

        actualizarSpriteVisual();
        System.out.println("RETUMBAR X: " + x + " | Y: " + y);
    }

    public void configurarAtaquesExtra(
        float anchoMapa, float altoMapa, float sueloY) {

        ataquesExtra = new AtaquesExtraRetumbar(anchoMapa, altoMapa, sueloY);
    }

    public void crearMeteorito(MeteoritoRed estado) {
        if (ataquesExtra != null) {
            ataquesExtra.crearMeteorito(estado);
        }
    }

    public void actualizarEstadoMeteorito(MeteoritoRed estado) {
        if (ataquesExtra != null) {
            ataquesExtra.actualizarEstadoMeteorito(estado);
        }
    }

    public void eliminarMeteorito(int id) {
        if (ataquesExtra != null) {
            ataquesExtra.eliminarMeteorito(id);
        }
    }

    public void actualizarEstadoRayos(RayosRed datos) {
        if (ataquesExtra != null) {
            ataquesExtra.actualizarEstadoRayos(datos);
        }
    }

    public void actualizarEstadoLasers(LasersRed datos) {
        if (ataquesExtra != null) {
            ataquesExtra.actualizarEstadoLasers(datos);
        }
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

    public void actualizarEstadoLaserJefe(boolean visible, boolean aviso, float angulo, float tiempoAnimacion) {

        this.laserJefeVisible = visible;
        this.avisoLaserJefe = aviso;
        this.anguloLaserJefe = angulo;
        this.tiempoAnimacionLaserJefe = tiempoAnimacion;
    }

    private void dibujarLaserSegundaFase() {
        if (!laserJefeVisible && !avisoLaserJefe) {
            return;
        }

        float origenX = x + ancho / 2f;
        float origenY = y + ALTURA_PECHO;
        float largo = LARGO_SPRITE_LASER_JEFE;

        if (avisoLaserJefe) {
            Color colorAnterior = Render.batch.getColor().cpy();

            Render.batch.setColor(1f, 0f, 0f, 0.45f);

            Render.batch.draw(
                REGION_PIXEL,
                origenX - ANCHO_HITBOX_LASER_JEFE / 2f,
                origenY,
                ANCHO_HITBOX_LASER_JEFE / 2f,
                0f,
                ANCHO_HITBOX_LASER_JEFE,
                largo,
                1f,
                1f,
                anguloLaserJefe - 90f
            );

            Render.batch.setColor(colorAnterior);
            return;
        }

        TextureRegion frame = new TextureRegion(
            ANIMACION_LASER_JEFE.getKeyFrame(
                tiempoAnimacionLaserJefe
            )
        );

        frame.flip(false, true);

        Render.batch.draw(
            frame,
            origenX - ANCHO_SPRITE_LASER_JEFE / 2f,
            origenY,
            ANCHO_SPRITE_LASER_JEFE / 2f,
            0f,
            ANCHO_SPRITE_LASER_JEFE,
            largo,
            1f,
            1f,
            anguloLaserJefe - 90f
        );
    }

    public void crearFueguito(FueguitoRed estado) {
        if (ataquesExtra != null) {
            ataquesExtra.crearFueguito(estado);
        }
    }

    public void crearFueguitoRadial(int id, float x, float y, float rotacion) {

        if (buscarFueguitoRadial(id) != null) {
            return;
        }

        Fueguito fueguito = new Fueguito(
            id,
            x,
            y,
            rotacion,
            ANIMACION_FUEGUITO
        );

        FUEGUITOS_DIRECCIONADOS.add(fueguito);
    }

    public void actualizarEstadoFueguitoRadial(int id, float x, float y, float rotacion) {

        Fueguito fueguito = buscarFueguitoRadial(id);

        if (fueguito == null) {
            crearFueguitoRadial(id, x, y, rotacion);
        } else {
            fueguito.actualizarEstado(x, y, rotacion);
        }
    }

    public void eliminarFueguitoRadial(int id) {
        FUEGUITOS_DIRECCIONADOS.removeIf(
            f -> f.getId() == id
        );
    }

    public void actualizarEstadoFueguito(FueguitoRed estado) {
        if (ataquesExtra != null) {
            ataquesExtra.actualizarEstadoFueguito(estado);
        }
    }

    public void configurarAnimacionesFueguitos(
        Texture texturaLluvia,
        Animation<TextureRegion> animacionRadial) {

        if (ataquesExtra != null) {
            ataquesExtra.configurarAnimacionesFueguitos(
                texturaLluvia, animacionRadial
            );
        }
    }

    public void eliminarFueguito(int id) {
        if (ataquesExtra != null) {
            ataquesExtra.eliminarFueguito(id);
        }
    }

    private Fueguito buscarFueguitoRadial(int id) {
        for (Fueguito fueguito : FUEGUITOS_DIRECCIONADOS) {
            if (fueguito.getId() == id) {
                return fueguito;
            }
        }

        return null;
    }

    @Override
    public void dibujar() {
        super.dibujar();

        if (ataquesExtra != null) {
            ataquesExtra.dibujar();
        }

        dibujarLaserSegundaFase();

        for (Fueguito fueguito : FUEGUITOS_DIRECCIONADOS) {
            fueguito.dibujar();
        }
    }
}
