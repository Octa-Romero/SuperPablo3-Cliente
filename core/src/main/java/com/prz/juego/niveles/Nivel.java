package com.prz.juego.niveles;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.prz.juego.entidades.*;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.enemigos.TipoEnemigo;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.entidades.jugadores.Pablo;
import com.prz.juego.entidades.jugadores.Walter;
import com.prz.juego.red.EnemigoRed;
import com.prz.juego.red.OrbeRed;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Debug;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;

public class Nivel {

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Jugador jugadorLocal;
    private Jugador jugadorRemoto;
    private Pablo pablo;
    private Walter walter;
    private Colisiones colision;
    private final ArrayList<Entidad> entidades = new ArrayList<>();
    private ShapeRenderer shapeRenderer;
    private boolean gameOver = false;

    public void cargar(String ruta, Jugador jugadorLocal, Jugador jugadorRemoto) {
        camara = new Camara();

        this.jugadorLocal = jugadorLocal;
        this.jugadorRemoto = jugadorRemoto;

        entidades.add(jugadorLocal);
        entidades.add(jugadorRemoto);

        Jugador[] jugadores = new Jugador[]{jugadorLocal, jugadorRemoto};

        for (Jugador jugador : jugadores) {
            if (jugador instanceof Pablo p) {
                pablo = p;
            } else if (jugador instanceof Walter w) {
                walter = w;
            }
        }

        shapeRenderer = new ShapeRenderer();

        TmxMapLoader mapLoader = new TmxMapLoader();
        mapa = mapLoader.load(ruta);

        colision = new Colisiones(mapa);


        MapProperties props = mapa.getProperties();

        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);
        int mapWidthTiles = props.get("width", Integer.class);
        int mapHeightTiles = props.get("height", Integer.class);

        float mapTotalWidth = tileWidth * mapWidthTiles;
        float mapTotalHeight = tileHeight * mapHeightTiles;

        camara.setLimitesMapa(mapTotalWidth, mapTotalHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);

        Musica.NIVEL1.sonar();
    }

    public void renderMapa() {
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
    }

    public void render() {
        Render.batch.setProjectionMatrix(camara.getCamera().combined);

        for (Entidad entidad : entidades) {
            entidad.dibujar();

            if (entidad instanceof Enemigo) {
                ((Enemigo) entidad).dibujarAtaque();
            }
        }

        pablo.dibujarAtaque();

        walter.dibujarOrbes();
    }

    public void renderDebug() {
        if (!Debug.mostrarHitboxes()) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camara.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        colision.dibujarHitboxes(shapeRenderer);

        for (Entidad entidad : entidades) {
            entidad.dibujarHitbox(shapeRenderer);
        }

        walter.dibujarOrbesHitbox(shapeRenderer);

        shapeRenderer.end();
    }

    public void update(float delta, Entrada entrada) {

        // Actualizar entidades
        for (Entidad entidad : entidades) {
            entidad.update(delta);
        }

        // Eliminar enemigos muertos
        for (int i = entidades.size() - 1; i >= 0; i--) {
            Entidad entidad = entidades.get(i);

            if (entidad instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) entidad;

                if (enemigo.estaMuerto()) {
                    enemigo.dispose();
                    entidades.remove(i);
                }
            }
        }

        // Cámara
        camara.seguirPersonaje(
            jugadorLocal.getX() + jugadorLocal.getAncho() / 2f,
            jugadorLocal.getY() + jugadorLocal.getAlto() / 2f,
            delta
        );

        // Alternar hitboxes
        if (entrada.aprietaF1()) {
            Debug.cambiarHitboxes();
        }

        if (jugadorLocal.estaMuerto()) {
            gameOver = true;
        }

    }

    public void resize(int width, int height) {
        camara.actualizarTamano(width, height);
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

        for(Entidad e : entidades)
        {
            e.dispose();
        }
        entidades.clear();
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

    public boolean isGameOver(){return this.gameOver;}

    public void agregarEnemigos(EnemigoRed estadoRed) {
        TipoEnemigo tipo = estadoRed.getTipo();
        Enemigo enemigo = tipo.crear(estadoRed, jugadorLocal);

        if (enemigo != null) {
            entidades.add(enemigo);
        }
    }

    public void actualizarEstadoEnemigos(EnemigoRed estado) {
        for (Entidad e : entidades) {
            if (e instanceof Enemigo enemigo && enemigo.getId() == estado.getId()) {
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
        for (int i = 0; i < entidades.size(); i++) {
            Entidad entidad = entidades.get(i);

            if (entidad instanceof Enemigo enemigo && enemigo.getId() == id) {
                enemigo.dispose();
                entidades.remove(i);
                return;
            }
        }
    }

    public void agregarOrbes(OrbeRed estadoRed)
    {
        walter.agregarOrbes(estadoRed.getId(), estadoRed.getX(), estadoRed.getY(), estadoRed.getDireccion());
    }

    public void actualizarEstadoOrbes(OrbeRed estadoRed)
    {
        walter.actualizarEstadoOrbes(estadoRed.getId(), estadoRed.getX(), estadoRed.getY());
    }

    public void eliminarOrbes(int id)
    {
        walter.eliminarOrbe(id);
    }
}
