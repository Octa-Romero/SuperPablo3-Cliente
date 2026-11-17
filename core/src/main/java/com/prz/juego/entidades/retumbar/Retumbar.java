package com.prz.juego.entidades.retumbar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.utilidades.Frames;
import com.prz.juego.utilidades.Render;

import java.util.ArrayList;

public class Retumbar {

    private static final float ANCHO = 300f;
    private static final float ALTO = 400f;
    private static final float ANCHO_SPRITE = 370f;
    private static final float ALTO_SPRITE = ALTO + 100f;
    private static final float ALTURA_PECHO = ALTO * 0.77f;
    private static final int CANTIDAD_FRAMES = 3;
    private static final float DURACION_FRAME = 0.12f;
    private static final float DURACION_PARPADEO = 2f;
    private static final float INTERVALO_PARPADEO = 0.1f;
    private static final String FASE_COMPLETADA = "COMPLETADA";

    private final float SUELO_Y;
    private final Mano MANO_IZQUIERDA = new Mano(Mano.Lado.IZQUIERDA);
    private final Mano MANO_DERECHA = new Mano(Mano.Lado.DERECHA);
    private final FuegoSuelo FUEGO_SUELO = new FuegoSuelo();
    private final Rayo RAYOS = new Rayo();
    private final Laser LASERS = new Laser();
    private final LaserJefe LASER_JEFE = new LaserJefe();
    private final ArrayList<Meteorito> METEORITOS = new ArrayList<>();
    private final ArrayList<Fueguito> FUEGUITOS = new ArrayList<>();

    private float x;
    private float y;
    private double vida;
    private double vidaMaxima;
    private boolean visible = true;
    private float escala = 1f;
    private String faseEspecial = "NORMAL";
    private float anchoMapa;
    private float altoMapa;
    private float tiempoDesdeMuerte = 0f;
    private float tiempoAnimacion = 0f;
    private Sprite sprite;
    private Animation<TextureRegion> animacion;

    public Retumbar(float x, float y, double vida) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.SUELO_Y = y;
    }

    public void configurar(float anchoMapa, float altoMapa) {
        this.anchoMapa = anchoMapa;
        this.altoMapa = altoMapa;
    }

    public void actualizarEstado(float x, float y, double vida,
                                 boolean visible, float escala, String faseEspecial) {
        boolean estabaMuerto = this.vida <= 0;

        this.x = x;
        this.y = y;
        this.vida = vida;
        if (vida > vidaMaxima) {
            vidaMaxima = vida;
        }
        this.visible = visible;
        this.escala = escala;
        this.faseEspecial = faseEspecial;

        if (vida <= 0 && !estabaMuerto) {
            tiempoDesdeMuerte = 0f;
        }
    }

    public void actualizar(float delta) {
        inicializarGraficos();

        tiempoAnimacion += delta;

        if (estaMuerto()) {
            tiempoDesdeMuerte += delta;
        }

        MANO_IZQUIERDA.actualizar(delta);
        MANO_DERECHA.actualizar(delta);
        FUEGO_SUELO.actualizar(delta);
        LASERS.actualizar(delta);

        for (Fueguito fueguito : FUEGUITOS) {
            fueguito.actualizar(delta);
        }
    }

    private void inicializarGraficos() {
        if (sprite != null) {
            return;
        }

        animacion = Frames.animacion("Jefe/retumbar.png", CANTIDAD_FRAMES, DURACION_FRAME);
        sprite = new Sprite(animacion.getKeyFrame(0f));
    }

    public void dibujar() {
        inicializarGraficos();

        if (estaMuerto()) {
            boolean parpadeaVisible = (int) (tiempoDesdeMuerte / INTERVALO_PARPADEO) % 2 == 0;
            if (tiempoDesdeMuerte < DURACION_PARPADEO && parpadeaVisible) {
                dibujarCuerpo();
            }
            return;
        }

        dibujarCuerpo();

        float sueloY = SUELO_Y;
        RAYOS.dibujar(sueloY, altoMapa);
        LASERS.dibujar(sueloY, altoMapa, anchoMapa);

        for (Meteorito meteorito : METEORITOS) {
            if (meteorito.getTipo() == Meteorito.Tipo.NORMAL) {
                meteorito.dibujar(sueloY, anchoMapa);
            }
        }

        for (Meteorito meteorito : METEORITOS) {
            if (meteorito.getTipo() == Meteorito.Tipo.GRANDE) {
                meteorito.dibujar(sueloY, anchoMapa);
            }
        }

        for (Fueguito fueguito : FUEGUITOS) {
            if (fueguito.getTipo() == Fueguito.Tipo.LLUVIA) {
                fueguito.dibujar();
            }
        }

        LASER_JEFE.dibujar(x + ANCHO / 2f, y + ALTURA_PECHO);

        for (Fueguito fueguito : FUEGUITOS) {
            if (fueguito.getTipo() == Fueguito.Tipo.RADIAL) {
                fueguito.dibujar();
            }
        }

        MANO_IZQUIERDA.dibujar();
        MANO_DERECHA.dibujar();
        FUEGO_SUELO.dibujar(anchoMapa);
    }

    private void dibujarCuerpo() {
        if (!visible) {
            return;
        }

        sprite.setRegion(animacion.getKeyFrame(tiempoAnimacion));
        sprite.setBounds(x + (ANCHO - ANCHO_SPRITE) / 2f, y, ANCHO_SPRITE, ALTO_SPRITE);
        sprite.setOriginCenter();
        sprite.setScale(escala);
        sprite.draw(Render.batch);
    }

    public void crearMeteorito(Meteorito meteorito) {
        for (Meteorito existente : METEORITOS) {
            if (existente.getId() == meteorito.getId()) {
                return;
            }
        }
        METEORITOS.add(meteorito);
    }

    public void eliminarMeteorito(int id) {
        METEORITOS.removeIf(meteorito -> meteorito.getId() == id);
    }

    public void crearFueguito(Fueguito fueguito) {
        for (Fueguito existente : FUEGUITOS) {
            if (existente.getId() == fueguito.getId() && existente.getTipo() == fueguito.getTipo()) {
                return;
            }
        }
        FUEGUITOS.add(fueguito);
    }

    public void eliminarFueguito(int id, Fueguito.Tipo tipo) {
        FUEGUITOS.removeIf(fueguito -> fueguito.getId() == id && fueguito.getTipo() == tipo);
    }

    public void eliminarAtaques() {
        METEORITOS.clear();
        FUEGUITOS.clear();
        RAYOS.reiniciar();
        LASERS.reiniciar();
        LASER_JEFE.reiniciar();
    }

    public Mano getMano(Mano.Lado lado) {
        return lado == Mano.Lado.IZQUIERDA ? MANO_IZQUIERDA : MANO_DERECHA;
    }

    public Mano getManoIzquierda() {
        return MANO_IZQUIERDA;
    }

    public Mano getManoDerecha() {
        return MANO_DERECHA;
    }

    public FuegoSuelo getFuegoSuelo() {
        return FUEGO_SUELO;
    }

    public Rayo getRayos() {
        return RAYOS;
    }

    public Laser getLasers() {
        return LASERS;
    }

    public LaserJefe getLaserJefe() {
        return LASER_JEFE;
    }

    public double getVida() {
        return vida;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public boolean estaMuerto() {
        return vida <= 0;
    }

    public boolean debeDibujarBarraVida() {
        return MANO_IZQUIERDA.estaMuerta()
            && MANO_DERECHA.estaMuerta()
            && FASE_COMPLETADA.equals(faseEspecial);
    }
}
