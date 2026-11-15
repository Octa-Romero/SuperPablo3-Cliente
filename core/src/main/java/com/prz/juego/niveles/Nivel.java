package com.prz.juego.niveles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.Lanzable;
import com.prz.juego.entidades.Reliquia;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.enemigos.TipoEnemigo;
import com.prz.juego.entidades.enemigos.rango.Proyectil;
import com.prz.juego.entidades.jefes.FuegoSuelo;
import com.prz.juego.entidades.jefes.Mano;
import com.prz.juego.entidades.jefes.Retumbar;
import com.prz.juego.entidades.jugadores.*;

import com.prz.juego.red.*;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Debug;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;

import java.util.ArrayList;

public abstract class Nivel {

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Jugador jugadorLocal;
    private Jugador jugadorRemoto;

    private Pablo pablo;
    private Walter walter;

    private Colisiones colision;
    private ShapeRenderer shapeRenderer;

    private final ArrayList<Entidad> ENTIDADES = new ArrayList<>();
    private final ArrayList<Entidad> ENTIDADES_PENDIENTES = new ArrayList<>();
    private final ArrayList<Entidad> ENTIDADES_PENDIENTES_ELIMINAR = new ArrayList<>();
    private final ArrayList<Lanzable> PROYECTILES = new ArrayList<>();
    private final ArrayList<Reliquia> RELIQUIAS = new ArrayList<>();

    private boolean enemigoReliquiaDerrotado = false;
    private boolean gameOver = false;
    private boolean tieneReliquia = false;

    protected Musica musica = Musica.NIVEL;

    public static Nivel crear(String ruta) {
        switch (ruta) {
            case Nivel1.RUTA:
                return new Nivel1();
            case Nivel2.RUTA:
                return new Nivel2();
            case Nivel3.RUTA:
                return new Nivel3();
            case Nivel4.RUTA:
                return new Nivel4();
            case Nivel5.RUTA:
                return new Nivel5();
            case Nivel6.RUTA:
                return new Nivel6();
            case Nivel7.RUTA:
                return new Nivel7();
            default:
                throw new IllegalArgumentException(
                    "No existe una clase para el nivel: " + ruta
                );
        }
    }

    public void cargar(Jugador jugadorLocal, Jugador jugadorRemoto) {
        camara = new Camara();

        this.jugadorLocal = jugadorLocal;
        this.jugadorRemoto = jugadorRemoto;

        ENTIDADES.add(jugadorLocal);
        ENTIDADES.add(jugadorRemoto);

        Jugador[] jugadores = {jugadorLocal, jugadorRemoto};

        for (Jugador jugador : jugadores) {
            if (jugador instanceof Pablo p) {
                pablo = p;
            } else if (jugador instanceof Walter w) {
                walter = w;
            }
        }

        shapeRenderer = new ShapeRenderer();

        TmxMapLoader mapLoader = new TmxMapLoader();
        mapa = mapLoader.load(getRutaMapa());

        colision = new Colisiones(mapa);

        // Los enemigos llegan desde el servidor.

        MapProperties props = mapa.getProperties();

        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);
        int mapWidthTiles = props.get("width", Integer.class);
        int mapHeightTiles = props.get("height", Integer.class);

        float mapTotalWidth = tileWidth * mapWidthTiles;
        float mapTotalHeight = tileHeight * mapHeightTiles;

        camara.setLimitesMapa(mapTotalWidth, mapTotalHeight);

        configurarEntornoEspecifico(mapTotalWidth, mapTotalHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);

        musica.sonar();
    }

    protected abstract String getRutaMapa();

    protected float getPosicionInicialX() {
        return 50;
    }

    protected float getPosicionInicialY() {
        return 150;
    }

    protected void agregarEntidad(Entidad entidad) {
        ENTIDADES.add(entidad);
    }

    protected void agregarEntidadAlFondo(Entidad entidad) {
        ENTIDADES.add(0, entidad);
    }

    protected void eliminarEntidad(Entidad entidad) {
        ENTIDADES.remove(entidad);
    }
    protected void agregarEntidadDiferida(Entidad entidad) {
        ENTIDADES_PENDIENTES.add(entidad);
    }

    protected void eliminarEntidadDiferida(Entidad entidad) {
        if (!ENTIDADES_PENDIENTES_ELIMINAR.contains(entidad)) {
            ENTIDADES_PENDIENTES_ELIMINAR.add(entidad);
        }
    }

    protected void configurarEntornoEspecifico(float ancho, float alto) {
    }

    public Retumbar getJefe() {
        return null;
    }

    public void renderMapa() {
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
    }

    public void render() {
        Render.batch.setProjectionMatrix(camara.getCamera().combined);

        boolean dibujarJugadoresAlFrente = getJefe() != null;

        for (Entidad entidad : ENTIDADES) {
            if (dibujarJugadoresAlFrente
                && (entidad == pablo || entidad == walter)) {
                continue;
            }

            entidad.dibujar();
        }

        if (dibujarJugadoresAlFrente) {
            //getJefe().dibujarAtaquesExtra();
            pablo.dibujar();
            walter.dibujar();
        }

        for (Lanzable proyectil : PROYECTILES) {
            proyectil.dibujar();
        }

        for (Reliquia reliquia : RELIQUIAS) {
            reliquia.dibujar();
        }
    }

    public void renderDebug() {
        if (!Debug.mostrarHitboxes()) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camara.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        colision.dibujarHitboxes(shapeRenderer);

        for (Entidad entidad : ENTIDADES) {
            entidad.dibujarHitbox(shapeRenderer);
        }

        if (getJefe() != null) {
            //getJefe().dibujarHitboxesAtaquesExtra(shapeRenderer);
        }

        for (Lanzable proyectil : PROYECTILES) {
            proyectil.dibujarHitbox(shapeRenderer);
        }

        shapeRenderer.end();
    }

    public void update(float delta, Entrada entrada) {

        // Actualización visual de las entidades.
        for (Entidad entidad : ENTIDADES) {
            entidad.update(delta);
        }

        for (Entidad entidad : ENTIDADES_PENDIENTES_ELIMINAR) {
            if (ENTIDADES.remove(entidad)) {
                entidad.dispose();
            }
            ENTIDADES_PENDIENTES.remove(entidad);
        }

        ENTIDADES_PENDIENTES_ELIMINAR.clear();

        ENTIDADES.addAll(ENTIDADES_PENDIENTES);
        ENTIDADES_PENDIENTES.clear();

        for (Lanzable proyectil : PROYECTILES) {
            proyectil.update(delta);
        }

        camara.seguirPersonaje(
            jugadorLocal.getX() + jugadorLocal.getAncho() / 2f,
            jugadorLocal.getY() + jugadorLocal.getAlto() / 2f,
            delta
        );

        if (entrada.aprietaF1()) {
            Debug.cambiarHitboxes();
        }

        if (jugadorLocal.estaMuerto()) {
            gameOver = true;
        }
    }

    public void agregarEnemigos(EnemigoRed estadoRed) {

        for (Entidad entidad : ENTIDADES) {
            if (entidad instanceof Enemigo existente
                && !(entidad instanceof Retumbar)
                && !(entidad instanceof Mano)
                && !(entidad instanceof FuegoSuelo)
                && existente.getId() == estadoRed.getId()) {
                return;
            }
        }

        Enemigo enemigo = estadoRed.getTipo().crear(estadoRed, jugadorLocal);

        if (enemigo != null) {
            enemigo.setId(estadoRed.getId());
            agregarEntidad(enemigo);
        }
    }

    public void actualizarEstadoEnemigos(EnemigoRed estado) {

        for (Entidad entidad : ENTIDADES) {
            if (entidad instanceof Enemigo enemigo
                && !(entidad instanceof Retumbar)
                && !(entidad instanceof Mano)
                && !(entidad instanceof FuegoSuelo)
                && enemigo.getId() == estado.getId()) {

                enemigo.actualizarEstadoRed(
                    estado.getX(),
                    estado.getY(),
                    estado.getDireccion(),
                    estado.estaAtacando(),
                    estado.getVida()
                );

                return;
            }
        }
    }

    public void eliminarEnemigo(int id) {
        for (int i = 0; i < ENTIDADES.size(); i++) {
            Entidad entidad = ENTIDADES.get(i);

            if (entidad instanceof Enemigo enemigo
                && !(entidad instanceof Retumbar)
                && !(entidad instanceof Mano)
                && !(entidad instanceof FuegoSuelo)
                && enemigo.getId() == id) {

                enemigo.dispose();
                ENTIDADES.remove(i);
                return;
            }
        }
    }


    public void agregarProyectil(ProyectilRed datos) {

        for (Lanzable existente : PROYECTILES) {
            if (existente.getId() == datos.getId()) {
                return;
            }
        }

        Lanzable proyectil;

        if (datos.getOrigen().equals("WALTER")) {

            proyectil = new BolaFuego(
                datos.getId(),
                datos.getX(),
                datos.getY(),
                (int) datos.getDireccion()
            );

        } else if (datos.getOrigen().equals("ENEMIGO")) {

            proyectil = new Proyectil(
                datos.getId(),
                datos.getX(),
                datos.getY(),
                datos.getDireccion()
            );

        } else {
            return;
        }

        PROYECTILES.add(proyectil);
    }

    public void actualizarEstadoProyectil(ProyectilRed datos) {

        for (Lanzable proyectil : PROYECTILES) {
            if (proyectil.getId() == datos.getId()) {
                proyectil.actualizarEstado(
                    datos.getX(),
                    datos.getY()
                );
                return;
            }
        }
    }

    public void eliminarProyectil(int id) {

        for (int i = 0; i < PROYECTILES.size(); i++) {
            if (PROYECTILES.get(i).getId() == id) {
                PROYECTILES.remove(i);
                return;
            }
        }
    }

    public void crearJefe(JefeRed estadoRed) {}

    public void actualizarEstadoJefe(JefeRed estadoRed) {
        Retumbar jefe = getJefe();

        if (jefe != null) {
            jefe.actualizarEstado(
                estadoRed.getX(),
                estadoRed.getY(),
                estadoRed.getVida(),
                estadoRed.esVisible(),
                estadoRed.getEscalaSprite(),
                estadoRed.getFaseEspecial()
            );
        }
    }

    public void actualizarEstadoMano(ManoRed estadoRed) {
        Retumbar jefe = getJefe();

        if (jefe != null) {
            jefe.actualizarEstadoMano(estadoRed);
        }
    }

    public void actualizarEstadoFuegoSuelo(FuegoSueloRed estadoRed)
    {
        Retumbar jefe = getJefe();

        if(jefe != null)
        {
            jefe.actualizarEstadoFuegoSuelo(estadoRed);
        }
    }

    public void actualizarEstadoRayos(RayosRed datos) {
        Retumbar jefe = getJefe();

        if (jefe != null) {
            jefe.actualizarEstadoRayos(datos);
        }
    }

    public void actualizarEstadoLasers(LasersRed datos) {
        Retumbar jefe = getJefe();

        if (jefe != null) {
            jefe.actualizarEstadoLasers(datos);
        }
    }

    public void crearMeteorito(MeteoritoRed estado) {
        Retumbar jefe = getJefe();

        if (jefe != null) {
            jefe.crearMeteorito(estado);
        }
    }

    public void actualizarEstadoMeteorito(MeteoritoRed estado) {
        Retumbar jefe = getJefe();

        if (jefe != null) {
            jefe.actualizarEstadoMeteorito(estado);
        }
    }

    public void eliminarMeteorito(int id) {
        Retumbar jefe = getJefe();

        if (jefe != null) {
            jefe.eliminarMeteorito(id);
        }
    }

    public void actualizarEstadoLaserJefe(LaserJefeRed estado) {
        Retumbar jefe = getJefe();

        if (jefe != null) {
            jefe.actualizarEstadoLaserJefe(
                estado.estaVisible(),
                estado.debeMostrarAviso(),
                estado.getAngulo(),
                estado.getTiempoAnimacion()
            );
        }
    }

    public void crearFueguito(FueguitoRed estado) {
        Retumbar jefe = getJefe();

        if (jefe == null) {
            return;
        }

        if (estado.getTipo() == FueguitoRed.Tipo.RADIAL) {
            jefe.crearFueguitoRadial(
                estado.getId(),
                estado.getX(),
                estado.getY(),
                estado.getRotacion()
            );
        } else {
            jefe.crearFueguito(estado);
        }
    }

    public void actualizarEstadoFueguito(FueguitoRed estado) {
        Retumbar jefe = getJefe();

        if (jefe == null) {
            return;
        }

        if (estado.getTipo() == FueguitoRed.Tipo.RADIAL) {
            jefe.actualizarEstadoFueguitoRadial(
                estado.getId(),
                estado.getX(),
                estado.getY(),
                estado.getRotacion()
            );
        } else {
            jefe.actualizarEstadoFueguito(estado);
        }
    }
    public void eliminarFueguito(
        int id, FueguitoRed.Tipo tipo) {

        Retumbar jefe = getJefe();

        if (jefe == null) {
            return;
        }

        if (tipo == FueguitoRed.Tipo.RADIAL) {
            jefe.eliminarFueguitoRadial(id);
        } else {
            jefe.eliminarFueguito(id);
        }
    }

    public void eliminarJefe() {}

    public void resize(int width, int height) {
        camara.actualizarTamano(width, height);
    }

    public Camera getCamara() {
        return camara.getCamera();
    }

    public void guardarPosicionCamara() {
        camara.guardarPosicion();
    }

    public void restaurarPosicionCamara() {
        camara.restaurarPosicion();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean tieneReliquia() {
        return tieneReliquia;
    }

    public boolean enemigoReliquiaDerrotado() {
        return enemigoReliquiaDerrotado;
    }

    public void dispose() {
        if (mapa != null) {
            mapa.dispose();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }

        for (Entidad entidad : ENTIDADES) {
            entidad.dispose();
        }

        ENTIDADES.clear();
        ENTIDADES_PENDIENTES.clear();
        ENTIDADES_PENDIENTES_ELIMINAR.clear();
        PROYECTILES.clear();

        for (Reliquia reliquia : RELIQUIAS) {
            reliquia.dispose();
        }

        RELIQUIAS.clear();
    }
}
