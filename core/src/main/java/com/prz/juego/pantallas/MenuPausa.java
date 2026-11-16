package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.red.Cliente;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class MenuPausa {

    private final Stage STAGE;
    private final Navegable NAV;
    private boolean activo = false;
    private final PantallaJuego PANTALLA_JUEGO;
    private final Cliente CLIENTE;
    private TextButton botonVolverHub;
    private boolean yoVoyAlHub = false;
    private boolean ambosVienen = false;
    private final BitmapFont FONT_TITULO;
    private final BitmapFont FONT_BOTONES;
    private Texture texturaFondoOscuro;
    private Texture texturaPanelOscuro;
    private Image fondoOscuro;
    private Image panelOscuro;

    public MenuPausa(Navegable NAV, PantallaJuego anteriorPantalla, Cliente cliente) {
        this.NAV = NAV;
        this.PANTALLA_JUEGO = anteriorPantalla;
        this.CLIENTE = cliente;

        STAGE = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));

        STAGE.setKeyboardFocus(null);
        STAGE.setScrollFocus(null);

        FONT_TITULO = crearFuente(42);
        FONT_BOTONES = crearFuente(28);

        mostrar();

        // El menu empieza desactivado
        desactivar();
    }

    private void mostrar() {
        crearFondos();

        Table contenedor = new Table();

        Label titulo = new Label("Pausa", new Label.LabelStyle(FONT_TITULO, Color.GOLD));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();

        style.font = FONT_BOTONES;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.RED;

        TextButton reanudar = new TextButton("Reanudar", style);
        TextButton opciones = new TextButton("Opciones", style);
        botonVolverHub = new TextButton("Volver al hub", style);
        TextButton menu = new TextButton("Salir al menú", style);

        reanudar.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Sonido.CLICK.sonar();
                        PANTALLA_JUEGO.setPausa(false);
                        desactivar();
                        // Al sacar la pausa, cancelar el volver al hub
                        cancelarVolverHub();
                    }
                });

        opciones.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Sonido.CLICK.sonar();
                        PANTALLA_JUEGO.guardarPosicionCamara();
                        NAV.cambiarPantalla(new PantallaOpciones(NAV, PANTALLA_JUEGO, true));
                    }
                });

        botonVolverHub.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Sonido.CLICK.sonar();
                        if (!yoVoyAlHub) {
                            // Detener el sonido del laser giratorio antes de volver al hub
                            Sonido.detenerLoopPorNombre("FUEGO_SOSTENIDO");
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

        menu.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Sonido.CLICK.sonar();
                        NAV.volverAMenu();
                    }
                });

        contenedor.add(titulo).padBottom(40).row();
        contenedor.add(reanudar).width(250).height(50).padBottom(20).row();
        contenedor.add(opciones).width(250).height(50).padBottom(20).row();
        if (!PANTALLA_JUEGO.estaEnHub()) {
            contenedor.add(botonVolverHub).width(250).height(50).padBottom(20).row();
        }
        contenedor.add(menu).width(250).height(50).padBottom(20).row();
        contenedor.pack();

        float anchoPanel = contenedor.getWidth() + 100;
        float altoPanel = contenedor.getHeight() + 80;

        panelOscuro.setSize(anchoPanel, altoPanel);

        panelOscuro.setPosition(
                (Config.ANCHO_BASE - anchoPanel) / 2f, (Config.ALTO_BASE - altoPanel) / 2f);

        contenedor.setPosition(Config.ANCHO_BASE / 2f, Config.ALTO_BASE / 2f, Align.center);

        STAGE.addActor(fondoOscuro);
        STAGE.addActor(panelOscuro);
        STAGE.addActor(contenedor);
    }

    private BitmapFont crearFuente(int tamano) {
        return FuenteJuego.generar(tamano);
    }

    private void crearFondos() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.45f));
        pixmap.fill();

        texturaFondoOscuro = new Texture(pixmap);
        fondoOscuro = new Image(new TextureRegionDrawable(new TextureRegion(texturaFondoOscuro)));
        fondoOscuro.setFillParent(true);

        pixmap.setColor(new Color(0, 0, 0, 0.65f));
        pixmap.fill();

        texturaPanelOscuro = new Texture(pixmap);
        panelOscuro = new Image(new TextureRegionDrawable(new TextureRegion(texturaPanelOscuro)));

        pixmap.dispose();
    }

    public void activar() {
        activo = true;
        STAGE.getRoot().setVisible(true);
        STAGE.getRoot().setTouchable(Touchable.enabled);
    }

    public void desactivar() {
        activo = false;
        STAGE.getRoot().setVisible(false);
        STAGE.getRoot().setTouchable(Touchable.disabled);
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
            // No volver al hub si ya estamos en el hub (evita mensajes residuales del servidor)
            if (!PANTALLA_JUEGO.estaEnHub()) {
                ambosVienen = true;
                if (botonVolverHub != null) {
                    botonVolverHub.setText("Volver al hub (2/2)");
                }
                Sonido.CLICK.sonar();
                NAV.volverAHub();
            }
            CLIENTE.resetearVolverHub();
        } else if (CLIENTE.otroJugadorCanceloHub()) {
            // El otro jugador cancelo, resetear
            if (botonVolverHub != null) {
                botonVolverHub.setText("Volver al hub");
            }
            CLIENTE.resetearVolverHub();
        } else if (CLIENTE.otroJugadorVaAlHub() && !yoVoyAlHub) {
            // El otro jugador quiere volver, mostrar (1/2) a ambos
            if (botonVolverHub != null) {
                botonVolverHub.setText("Volver al hub (1/2)");
            }
        }
    }

    public void update(float delta) {
        if (activo) {
            STAGE.act(delta);
            actualizarBotonHub();
        }
    }

    public void render() {
        if (activo) {
            STAGE.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            Render.batch.setProjectionMatrix(STAGE.getCamera().combined);
            STAGE.draw();
        }
    }

    public void resize(int width, int height) {
        STAGE.getViewport().update(width, height, true);
    }

    public void dispose() {
        STAGE.dispose();

        FONT_TITULO.dispose();
        FONT_BOTONES.dispose();

        if (texturaFondoOscuro != null) {
            texturaFondoOscuro.dispose();
        }

        if (texturaPanelOscuro != null) {
            texturaPanelOscuro.dispose();
        }
    }

    public Stage getStage() {
        return STAGE;
    }
}
