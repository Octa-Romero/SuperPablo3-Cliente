package com.prz.juego.niveles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.prz.juego.entidades.Proyectil;
import com.prz.juego.entidades.Tumba;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.red.JugadorEstado;
import com.prz.juego.entidades.retumbar.Fueguito;
import com.prz.juego.entidades.retumbar.Retumbar;
import com.prz.juego.entidades.retumbar.Meteorito;
import com.prz.juego.red.*;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.utilidades.Render;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;
import java.util.HashMap;

public class Nivel {

    private static final String[] RUTAS_NIVELES = {
        "Niveles/Niveles/Nivel1.tmx",
        "Niveles/Niveles/Nivel2.1.tmx",
        "Niveles/Niveles/Nivel3.tmx",
        "Niveles/Niveles/Nivel4.tmx",
        "Niveles/Niveles/Nivel5.tmx",
        "Niveles/Niveles/Nivel6.tmx",
        "Niveles/Niveles/Nivel7.tmx"
    };
    private static final int CANTIDAD_NIVELES = RUTAS_NIVELES.length;

    private final String RUTA_MAPA;
    private final ArrayList<Enemigo> ENEMIGOS = new ArrayList<>();
    private final ArrayList<Proyectil> PROYECTILES = new ArrayList<>();
    private final HashMap<String, Tumba> TUMBAS = new HashMap<>();

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;

    private JugadorEstado jugadorLocal;
    private JugadorEstado jugadorRemoto;
    private Retumbar jefe;

    private float anchoMapa;
    private float altoMapa;
    private boolean gameOver = false;

    private static final String TEXTURA_RELIQUIA = "Reliquia/reliquia.png";
    private static final float TAMANO_RELIQUIA = 48f;

    private static final float AMPLITUD_FLOTACION = 4f;
    private static final float VELOCIDAD_FLOTACION = 3f;
    private static final float VELOCIDAD_ROTACION = 90f;

    private float reliquiaX;
    private float reliquiaY;
    private float tiempoAnimacionReliquia;

    private Sprite reliquia;

    public Nivel(String rutaMapa) {
        if (rutaMapa == null) {
            throw new IllegalArgumentException("La ruta del mapa no puede ser null");
        }
        this.RUTA_MAPA = rutaMapa;
    }

    public static String getRuta(int numero) {
        if (numero < 1 || numero > CANTIDAD_NIVELES) {
            throw new IllegalArgumentException("No existe el nivel: " + numero);
        }
        return RUTAS_NIVELES[numero - 1];
    }

    public void cargar(JugadorEstado jugadorLocal, JugadorEstado jugadorRemoto) {
        camara = new Camara();

        this.jugadorLocal = jugadorLocal;
        this.jugadorRemoto = jugadorRemoto;

        mapa = new TmxMapLoader().load(RUTA_MAPA);

        MapProperties props = mapa.getProperties();
        anchoMapa = props.get("tilewidth", Integer.class) * props.get("width", Integer.class);
        altoMapa = props.get("tileheight", Integer.class) * props.get("height", Integer.class);

        camara.setLimitesMapa(anchoMapa, altoMapa);

        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);
    }

    public Retumbar getJefe() {
        return jefe;
    }

    public void limpiarEntidades() {
        jefe = null;
        ENEMIGOS.clear();
        PROYECTILES.clear();
        TUMBAS.clear();
        reliquia = null;
        reliquiaX = 0f;
        reliquiaY = 0f;
        tiempoAnimacionReliquia = 0f;
    }

    public void renderMapa() {
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
    }

    public void render() {
        Render.batch.setProjectionMatrix(camara.getCamera().combined);

        if (jefe != null) {
            jefe.dibujar();
        }

        for (Enemigo enemigo : ENEMIGOS) {
            enemigo.dibujar();
        }

        jugadorRemoto.dibujar();
        jugadorLocal.dibujar();

        for (Proyectil proyectil : PROYECTILES) {
            proyectil.dibujar();
        }

        for (Tumba tumba : TUMBAS.values()) {
            tumba.dibujar();
        }

        if (reliquia != null) {
            float flotacion = (float) Math.sin(
                tiempoAnimacionReliquia * VELOCIDAD_FLOTACION
            ) * AMPLITUD_FLOTACION;

            reliquia.setPosition(reliquiaX, reliquiaY - 150f + flotacion);
            reliquia.draw(Render.batch);
        }
    }

    public void update(float delta) {
        jugadorLocal.actualizar(delta);
        jugadorRemoto.actualizar(delta);

        if (jefe != null) {
            jefe.actualizar(delta);
        }

        for (Proyectil proyectil : PROYECTILES) {
            proyectil.update(delta);
        }

        JugadorEstado seguido = getJugadorParaCamara();
        camara.seguirPersonaje(
            seguido.getX() + seguido.getAncho() / 2f,
            seguido.getY() + seguido.getAlto() / 2f,
            delta);

        if (jugadorLocal.estaMuerto() && jugadorRemoto.estaMuerto()) {
            gameOver = true;
        }

        if (reliquia != null) {
            tiempoAnimacionReliquia += delta;

            reliquia.rotate(VELOCIDAD_ROTACION * delta);
        }

    }

    public boolean sincronizar(Cliente cliente) {
        if (cliente.hayReliquiaNueva()) {
            crearReliquia(cliente.getReliquiaX(), cliente.getReliquiaY());
        }

        if (cliente.fueRecogidaLaReliquia()) {
            eliminarReliquia();
        }

        for (Enemigo e : cliente.getEnemigosRecibidos()) {
            agregarEnemigos(e);
            cliente.registrarEstadoEnemigo(e);
        }

        for (Integer id : cliente.getEnemigosEliminados()) {
            eliminarEnemigo(id);
        }

        for (Proyectil p : cliente.getProyectilesNuevos()) {
            agregarProyectil(p);
            cliente.registrarEstadoProyectil(p);
        }

        for (Proyectil p : cliente.getEstadoProyectiles()) {
            actualizarEstadoProyectil(p);
        }

        for (Integer id : cliente.getProyectilesEliminados()) {
            eliminarProyectil(id);
        }

        for (Tumba tumba : cliente.getEstadoTumbas()) {
            actualizarEstadoTumba(tumba);
        }

        Retumbar jefeRecibido = cliente.getJefeRecibido();
        if (jefeRecibido != null) {
            crearJefe(jefeRecibido);
        }

        for (Meteorito meteorito : cliente.getEstadoMeteoritos()) {
            crearMeteorito(meteorito);
        }

        for (int id : cliente.getMeteoritosEliminados()) {
            eliminarMeteorito(id);
        }

        for (Fueguito fueguito : cliente.getEstadoFueguitos()) {
            crearFueguito(fueguito);
        }

        for (Fueguito fueguito : cliente.getFueguitosEliminados()) {
            eliminarFueguito(fueguito.getId(), fueguito.getTipo());
        }

        if (cliente.getEliminarAtaquesJefe()) {
            eliminarAtaquesJefe();
        }

        return cliente.getMostrarCreditos();
    }

    private JugadorEstado getJugadorParaCamara() {
        boolean localMuerto = jugadorLocal.estaMuerto();

        if (localMuerto && !jugadorRemoto.estaMuerto()) {
            return jugadorRemoto;
        }
        if (localMuerto) {
            return jugadorLocal.getX() >= jugadorRemoto.getX() ? jugadorLocal : jugadorRemoto;
        }
        return jugadorLocal;
    }

    private void agregarEnemigos(Enemigo estadoRed) {
        for (Enemigo existente : ENEMIGOS) {
            if (existente.getId() == estadoRed.getId()) {
                return;
            }
        }
        ENEMIGOS.add(estadoRed);
    }

    private void eliminarEnemigo(int id) {
        ENEMIGOS.removeIf(enemigo -> enemigo.getId() == id);
    }

    private void agregarProyectil(Proyectil proyectil) {
        for (Proyectil existente : PROYECTILES) {
            if (existente.getId() == proyectil.getId()) {
                return;
            }
        }
        PROYECTILES.add(proyectil);
    }

    private void actualizarEstadoProyectil(Proyectil datos) {
        for (Proyectil proyectil : PROYECTILES) {
            if (proyectil.getId() == datos.getId()) {
                if (proyectil != datos) {
                    proyectil.actualizarEstado(datos.getX(), datos.getY());
                }
                return;
            }
        }
    }

    private void eliminarProyectil(int id) {
        PROYECTILES.removeIf(proyectil -> proyectil.getId() == id);
    }

    private void actualizarEstadoTumba(Tumba datos) {
        Tumba existente = TUMBAS.get(datos.getPersonaje());

        if (existente == null) {
            TUMBAS.put(datos.getPersonaje(), datos);
        } else if (existente != datos) {
            existente.actualizarEstado(datos.getX(), datos.getY());
        }
    }

    private void crearJefe(Retumbar estadoRed) {
        jefe = estadoRed;
        jefe.configurar(anchoMapa, altoMapa);
    }

    private void crearMeteorito(Meteorito estado) {
        if (jefe != null) {
            jefe.crearMeteorito(estado);
        }
    }

    private void eliminarMeteorito(int id) {
        if (jefe != null) {
            jefe.eliminarMeteorito(id);
        }
    }

    private void crearFueguito(Fueguito estado) {
        if (jefe != null) {
            jefe.crearFueguito(estado);
        }
    }

    private void eliminarFueguito(int id, Fueguito.Tipo tipo) {
        if (jefe != null) {
            jefe.eliminarFueguito(id, tipo);
        }
    }

    private void eliminarAtaquesJefe() {
        if (jefe != null) {
            jefe.eliminarAtaques();
        }
    }

    private void crearReliquia(float x, float y) {
        reliquia = new Sprite(
            GestorRecursos.obtenerTextura(TEXTURA_RELIQUIA)
        );

        reliquia.setSize(TAMANO_RELIQUIA, TAMANO_RELIQUIA);
        reliquia.setOriginCenter();

        reliquiaX = x;
        reliquiaY = y;
        tiempoAnimacionReliquia = 0f;

        reliquia.setPosition(x, y);
    }

    private void eliminarReliquia() {
        reliquia = null;
        tiempoAnimacionReliquia = 0f;
    }

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

    public void dispose() {
        if (mapa != null) {
            mapa.dispose();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        jefe = null;
        ENEMIGOS.clear();
        PROYECTILES.clear();
        TUMBAS.clear();
        reliquia = null;
    }
}
