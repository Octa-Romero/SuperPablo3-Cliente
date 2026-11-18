package com.prz.juego.niveles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Render;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;
import java.util.HashMap;

public class Nivel {

    private static final String[] RUTAS_NIVELES = {
        "Niveles/Niveles/Nivel1.tmx",
        "Niveles/Niveles/Nivel2.tmx",
        "Niveles/Niveles/Nivel3.tmx",
        "Niveles/Niveles/Nivel4.tmx",
        "Niveles/Niveles/Nivel5.tmx",
        "Niveles/Niveles/Nivel6.tmx",
        "Niveles/Niveles/Nivel7.tmx"
    };
    private static final int CANTIDAD_NIVELES = RUTAS_NIVELES.length;

    private static final String CAPA_AVANCE = "avance";
    private static final String TEXTURA_PORTAL = "Menu/Portales/portalHub.png";
    private static final String MENSAJE_VOLVER_HUB = "Presiona E para volver al hub";
    private static final String MENSAJE_AVANZAR = "Presiona E para avanzar";
    private static final String MENSAJE_ESPERANDO = "Esperando al otro jugador... E para cancelar";
    private static final int TAMANO_FUENTE_PORTAL = 28;
    private static final float ALTURA_TEXTO_PORTAL = 40f;
    private static final float ALTURA_MAXIMA_REFERENCIA_TEXTO = 160f;
    private static final float MARGEN_TEXTO_PORTAL = 10f;

    private final String RUTA_MAPA;
    private final ArrayList<Enemigo> ENEMIGOS = new ArrayList<>();
    private final ArrayList<Proyectil> PROYECTILES = new ArrayList<>();
    private final HashMap<String, Tumba> TUMBAS = new HashMap<>();
    private final GlyphLayout LAYOUT_PORTAL = new GlyphLayout();

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ShapeRenderer shapeRenderer;

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

    private float reliquiaX;
    private float reliquiaY;
    private float tiempoAnimacionReliquia;

    private Sprite reliquia;

    private Rectangle zonaPortal;
    private boolean portalEsAvance = false;
    private boolean esperandoPortal = false;
    private BitmapFont fontPortal;

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

        zonaPortal = buscarRectangulo(CAPA_AVANCE);
        portalEsAvance = zonaPortal != null;
        fontPortal = FuenteJuego.generar(TAMANO_FUENTE_PORTAL);
    }

    private Rectangle buscarRectangulo(String nombreCapa) {
        MapLayer capa = mapa.getLayers().get(nombreCapa);
        if (capa == null) {
            return null;
        }

        for (MapObject objeto : capa.getObjects()) {
            if (objeto instanceof RectangleMapObject rectangulo) {
                return rectangulo.getRectangle();
            }
        }
        return null;
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
        esperandoPortal = false;
        if (!portalEsAvance) {
            zonaPortal = null;
        }
    }

    public void renderMapa() {
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
    }

    public void render() {
        Render.batch.setProjectionMatrix(camara.getCamera().combined);

        dibujarImagenPortal();

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

            reliquia.setPosition(reliquiaX, reliquiaY + flotacion);
            reliquia.draw(Render.batch);
        }

        dibujarTextoPortal();
    }

    private void dibujarImagenPortal() {
        if (zonaPortal == null || portalEsAvance) {
            return;
        }

        Render.batch.draw(
            GestorRecursos.obtenerTextura(TEXTURA_PORTAL),
            zonaPortal.x,
            zonaPortal.y,
            zonaPortal.width,
            zonaPortal.height);
    }

    private void dibujarTextoPortal() {
        if (zonaPortal == null) {
            return;
        }

        String mensaje = obtenerMensajePortal();
        if (mensaje == null) {
            return;
        }

        LAYOUT_PORTAL.setText(fontPortal, mensaje);

        float x = zonaPortal.x + zonaPortal.width / 2f - LAYOUT_PORTAL.width / 2f;
        x = MathUtils.clamp(
            x,
            MARGEN_TEXTO_PORTAL,
            Math.max(MARGEN_TEXTO_PORTAL, anchoMapa - LAYOUT_PORTAL.width - MARGEN_TEXTO_PORTAL));
        float y = zonaPortal.y + Math.min(zonaPortal.height, ALTURA_MAXIMA_REFERENCIA_TEXTO)
            + ALTURA_TEXTO_PORTAL;

        fontPortal.draw(Render.batch, mensaje, x, y);
    }

    private String obtenerMensajePortal() {
        if (esperandoPortal) {
            return MENSAJE_ESPERANDO;
        }
        if (jugadorLocal.getBounds().overlaps(zonaPortal)) {
            return portalEsAvance ? MENSAJE_AVANZAR : MENSAJE_VOLVER_HUB;
        }
        return null;
    }

    public void dibujarHitboxes() {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }

        shapeRenderer.setProjectionMatrix(camara.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Enemigo enemigo : ENEMIGOS) {
            enemigo.dibujarHitbox(shapeRenderer);
        }

        for (Proyectil proyectil : PROYECTILES) {
            proyectil.dibujarHitbox(shapeRenderer);
        }

        shapeRenderer.end();
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
        }

    }

    public boolean sincronizar(Cliente cliente) {
        if (cliente.hayReliquiaNueva()) {
            crearReliquia(cliente.getReliquiaX(), cliente.getReliquiaY());
        }

        if (cliente.fueRecogidaLaReliquia()) {
            eliminarReliquia();
        }

        if (reliquia != null) {
            reliquiaX = cliente.getReliquiaX();
            reliquiaY = cliente.getReliquiaY();
        }

        float[] portal = cliente.consumirPortalNuevo();
        if (portal != null && !portalEsAvance) {
            zonaPortal = new Rectangle(portal[0], portal[1], portal[2], portal[3]);
        }
        esperandoPortal = cliente.isEsperandoPortal();

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

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }

        if (fontPortal != null) {
            fontPortal.dispose();
            fontPortal = null;
        }

        jefe = null;
        ENEMIGOS.clear();
        PROYECTILES.clear();
        TUMBAS.clear();
        reliquia = null;
        zonaPortal = null;
    }
}
