package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.entidades.jefes.Mano;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.principal.Navegable;
import com.prz.juego.red.Cliente;
import com.prz.juego.red.EnemigoRed;
import com.prz.juego.red.FueguitoRed;
import com.prz.juego.red.FuegoSueloRed;
import com.prz.juego.red.JefeRed;
import com.prz.juego.red.LaserJefeRed;
import com.prz.juego.red.LasersRed;
import com.prz.juego.red.ManoRed;
import com.prz.juego.red.MeteoritoRed;
import com.prz.juego.red.ProyectilRed;
import com.prz.juego.red.RayosRed;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;

public class GameOver implements Screen {

    private final Navegable NAV;
    private final Nivel NIVEL;
    private final Cliente CLIENTE;
    private Stage stage;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private TextButton botonVolverHub;
    private boolean yoVoyAlHub = false;
    private boolean ambosVienen = false;
    private boolean volviendoAlHub = false;

    public GameOver(Navegable NAV, Nivel nivel, Cliente cliente) {
        this.NAV = NAV;
        this.NIVEL = nivel;
        this.CLIENTE = cliente;
    }

    @Override
    public void show() {
        inicializarStage();
        inicializarFuentes();
        shapeRenderer = new ShapeRenderer();
        crearInterfaz();

        Gdx.input.setInputProcessor(stage);
    }

    private void inicializarStage() {
        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
    }

    private void inicializarFuentes() {
        font = FuenteJuego.generar(48);
    }

    private void crearInterfaz() {

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.defaults().pad(8);

        Label titulo = new Label("Game Over", new Label.LabelStyle(font, Color.RED));
        titulo.setFontScale(3f);

        Label mensaje =
                new Label("Has perdido la partida", new Label.LabelStyle(font, Color.LIGHT_GRAY));
        mensaje.setFontScale(1.2f);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;

        botonVolverHub = new TextButton("Volver al hub", style);
        TextButton volverMenu = new TextButton("Salir al menú", style);

        botonVolverHub.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!yoVoyAlHub) {
                            // Detener el sonido del laser giratorio antes de volver al hub
                            com.prz.juego.utilidades.Sonido.detenerLoopPorNombre("FUEGO_SOSTENIDO");
                            // Confirmar volver al hub
                            yoVoyAlHub = true;
                            CLIENTE.enviarMensaje("VOLVER_HUB");
                            botonVolverHub.setText("Volver al hub (1/2)");
                        } else {
                            // Cancelar (toggle)
                            cancelarVolverHub();
                        }
                    }
                });

        volverMenu.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        NAV.volverAMenu();
                    }
                });

        table.add(titulo).padBottom(20).row();
        table.add(mensaje).padBottom(30).row();
        table.add(botonVolverHub).width(300).height(60).row();
        table.add(volverMenu).width(300).height(60);

        stage.addActor(table);
    }

    public void cancelarVolverHub() {
        yoVoyAlHub = false;
        ambosVienen = false;
        CLIENTE.enviarMensaje("CANCELAR_HUB");
        if (botonVolverHub != null) {
            botonVolverHub.setText("Volver al hub");
        }
        CLIENTE.resetearVolverHub();
    }

    public void actualizarBotonHub() {
        if (CLIENTE.ambosJugadoresVuelvenHub()) {
            ambosVienen = true;
            volviendoAlHub = true;
            botonVolverHub.setText("Volver al hub (2/2)");
            Musica.MENU.sonar();
            NAV.volverAHub();
        } else if (CLIENTE.otroJugadorCanceloHub()) {
            // El otro jugador cancelo, resetear
            botonVolverHub.setText("Volver al hub");
            CLIENTE.resetearVolverHub();
        } else if (CLIENTE.otroJugadorVaAlHub() && !yoVoyAlHub) {
            // El otro jugador quiere volver, mostrar (1/2) a ambos
            botonVolverHub.setText("Volver al hub (1/2)");
        }
    }

    @Override
    public void render(float delta) {
        // Verificar respuesta del servidor
        actualizarBotonHub();

        // Si ya estamos volviendo al hub, no dibujar el nivel (ya fue disposed)
        if (volviendoAlHub) {
            return;
        }

        // Actualizar el nivel con los datos del servidor para que el jefe y ataques extra sigan mostrándose
        if (NIVEL != null) {
            NIVEL.update(delta, null);

            // Procesar enemigos
            for (EnemigoRed e : CLIENTE.getEnemigosRecibidos()) {
                NIVEL.agregarEnemigos(e);
                CLIENTE.registrarEstadoEnemigo(e);
            }
            for (EnemigoRed e : CLIENTE.getEstadoEnemigos()) {
                NIVEL.actualizarEstadoEnemigos(e);
            }
            for (Integer id : CLIENTE.getEnemigosEliminados()) {
                NIVEL.eliminarEnemigo(id);
            }

            // Procesar proyectiles
            for (ProyectilRed p : CLIENTE.getProyectilesNuevos()) {
                NIVEL.agregarProyectil(p);
                CLIENTE.registrarEstadoProyectil(p);
            }
            for (ProyectilRed p : CLIENTE.getEstadoProyectiles()) {
                NIVEL.actualizarEstadoProyectil(p);
            }
            for (Integer id : CLIENTE.getProyectilesEliminados()) {
                NIVEL.eliminarProyectil(id);
            }

            // Procesar jefe
            JefeRed jefeRecibido = CLIENTE.getJefeRecibido();
            if (jefeRecibido != null) {
                NIVEL.crearJefe(jefeRecibido);
            }
            JefeRed estadoJefe = CLIENTE.getEstadoJefe();
            if (estadoJefe != null) {
                NIVEL.actualizarEstadoJefe(estadoJefe);
            }

            // Procesar manos
            ManoRed izquierda = CLIENTE.getEstadoMano(Mano.Lado.IZQUIERDA);
            ManoRed derecha = CLIENTE.getEstadoMano(Mano.Lado.DERECHA);
            if (izquierda != null && izquierda.recibioEstado()) {
                NIVEL.actualizarEstadoMano(izquierda);
            }
            if (derecha != null && derecha.recibioEstado()) {
                NIVEL.actualizarEstadoMano(derecha);
            }

            // Procesar fuego de suelo
            FuegoSueloRed estadoFuegoSuelo = CLIENTE.getEstadoFuegoSuelo();
            if (estadoFuegoSuelo != null && estadoFuegoSuelo.tieneEstadoRecibido()) {
                NIVEL.actualizarEstadoFuegoSuelo(estadoFuegoSuelo);
            }

            // Procesar ataques extra
            RayosRed rayos = CLIENTE.getEstadoRayos();
            if (rayos != null && rayos.tieneEstadoRecibido()) {
                NIVEL.actualizarEstadoRayos(rayos);
            }
            LasersRed lasers = CLIENTE.getEstadoLasers();
            if (lasers != null && lasers.tieneEstadoRecibido()) {
                NIVEL.actualizarEstadoLasers(lasers);
            }

            for (MeteoritoRed meteorito : CLIENTE.getMeteoritosNuevos()) {
                NIVEL.crearMeteorito(meteorito);
            }
            for (MeteoritoRed meteorito : CLIENTE.getEstadoMeteoritos()) {
                if (meteorito.tieneEstadoRecibido()) {
                    NIVEL.actualizarEstadoMeteorito(meteorito);
                }
            }
            for (int id : CLIENTE.getMeteoritosEliminados()) {
                NIVEL.eliminarMeteorito(id);
            }

            LaserJefeRed estadoLaserJefe = CLIENTE.getEstadoLaserJefe();
            if (estadoLaserJefe.tieneEstadoRecibido()) {
                NIVEL.actualizarEstadoLaserJefe(estadoLaserJefe);
            }

            for (FueguitoRed fueguito : CLIENTE.getFueguitosNuevos()) {
                NIVEL.crearFueguito(fueguito);
            }
            for (FueguitoRed fueguito : CLIENTE.getEstadoFueguitos()) {
                NIVEL.actualizarEstadoFueguito(fueguito);
            }
            for (FueguitoRed fueguito : CLIENTE.getFueguitosEliminados()) {
                NIVEL.eliminarFueguito(fueguito.getId(), fueguito.getTipo());
            }

            // No eliminar el jefe inmediatamente para que siga visible durante los 2 segundos
            // antes de los créditos.

            // Eliminar todos los ataques del jefe cuando el servidor lo indica
            if (CLIENTE.getEliminarAtaquesJefe()) {
                NIVEL.eliminarAtaquesJefe();
            }

            // Verificar si hay que mostrar créditos
            if (CLIENTE.getMostrarCreditos()) {
                NAV.cambiarPantalla(new PantallaFinal(NAV));
                return;
            }
        }

        // Limpiar pantalla
        Render.limpiarPantalla();

        // Dibujar el nivel de fondo
        if (NIVEL != null) {
            NIVEL.renderMapa();
            Render.begin(NIVEL.getCamara());
            NIVEL.render();
            Render.end();
        }

        // Dibujar overlay oscuro semi-transparente
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(0, 0, Config.ANCHO_BASE, Config.ALTO_BASE);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Dibujar la interfaz de game over
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
