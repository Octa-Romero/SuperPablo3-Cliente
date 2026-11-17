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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.red.Cliente;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Sonido;

public class MenuPausa {

    private static final float ALPHA_FONDO = 0.45f;
    private static final float ALPHA_PANEL = 0.65f;
    private static final float ANCHO_BOTON = 250;
    private static final float ALTO_BOTON = 50;
    private static final float SEPARACION_BOTONES = 20;
    private static final String TEXTO_VOLVER_HUB = "Volver al hub";

    private final Stage STAGE;
    private final Navegable NAV;
    private final PantallaJuego PANTALLA_JUEGO;
    private final Cliente CLIENTE;
    private final BitmapFont FONT_TITULO;
    private final BitmapFont FONT_BOTONES;
    private final Texture texturaBlanca;
    private TextButton botonVolverHub;
    private boolean activo = false;
    private boolean yoVoyAlHub = false;

    public MenuPausa(Navegable NAV, PantallaJuego anteriorPantalla, Cliente cliente) {
        this.NAV = NAV;
        this.PANTALLA_JUEGO = anteriorPantalla;
        this.CLIENTE = cliente;

        STAGE = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        STAGE.setKeyboardFocus(null);
        STAGE.setScrollFocus(null);

        FONT_TITULO = FuenteJuego.generar(42);
        FONT_BOTONES = FuenteJuego.generar(28);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texturaBlanca = new Texture(pixmap);
        pixmap.dispose();

        crearInterfaz();
        desactivar();
    }

    private void crearInterfaz() {
        TextureRegionDrawable base = new TextureRegionDrawable(new TextureRegion(texturaBlanca));
        Drawable fondoOscuro = base.tint(new Color(0, 0, 0, ALPHA_FONDO));
        Drawable panelOscuro = base.tint(new Color(0, 0, 0, ALPHA_PANEL));

        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.setBackground(fondoOscuro);
        raiz.setTouchable(Touchable.enabled);
        raiz.addListener(new ClickListener());

        Table panel = new Table();
        panel.setBackground(panelOscuro);
        panel.pad(40, 50, 40, 50);

        Label titulo = new Label("Pausa", new Label.LabelStyle(FONT_TITULO, Color.GOLD));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = FONT_BOTONES;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.RED;

        TextButton reanudar = new TextButton("Reanudar", style);
        TextButton opciones = new TextButton("Opciones", style);
        botonVolverHub = new TextButton(TEXTO_VOLVER_HUB, style);
        TextButton menu = new TextButton("Salir al menú", style);

        reanudar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                PANTALLA_JUEGO.setPausa(false);
                desactivar();
                cancelarVolverHub();
            }
        });

        opciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                PANTALLA_JUEGO.guardarPosicionCamara();
                NAV.cambiarPantalla(new PantallaOpciones(NAV, PANTALLA_JUEGO, true));
            }
        });

        botonVolverHub.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                if (!yoVoyAlHub) {
                    Sonido.detenerLoopPorNombre("FUEGO_SOSTENIDO");
                    yoVoyAlHub = true;
                    CLIENTE.enviarMensaje("VOLVER_HUB");
                    botonVolverHub.setText(TEXTO_VOLVER_HUB + " (1/2)");
                } else {
                    cancelarVolverHub();
                }
            }
        });

        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                NAV.volverAMenu();
            }
        });

        panel.add(titulo).padBottom(35).row();
        panel.add(reanudar).width(ANCHO_BOTON).height(ALTO_BOTON).padBottom(SEPARACION_BOTONES).row();
        panel.add(opciones).width(ANCHO_BOTON).height(ALTO_BOTON).padBottom(SEPARACION_BOTONES).row();
        if (!PANTALLA_JUEGO.estaEnHub()) {
            panel.add(botonVolverHub).width(ANCHO_BOTON).height(ALTO_BOTON).padBottom(SEPARACION_BOTONES).row();
        }
        panel.add(menu).width(ANCHO_BOTON).height(ALTO_BOTON);

        raiz.add(panel);
        STAGE.addActor(raiz);
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
        if (!yoVoyAlHub) {
            return;
        }
        yoVoyAlHub = false;
        CLIENTE.enviarMensaje("CANCELAR_HUB");
        CLIENTE.resetearVolverHub();
        botonVolverHub.setText(TEXTO_VOLVER_HUB);
    }

    public void actualizarBotonHub() {
        if (CLIENTE.ambosJugadoresVuelvenHub()) {
            if (!PANTALLA_JUEGO.estaEnHub()) {
                botonVolverHub.setText(TEXTO_VOLVER_HUB + " (2/2)");
                Sonido.CLICK.sonar();
                NAV.volverAHub();
            }
            CLIENTE.resetearVolverHub();
        } else if (CLIENTE.otroJugadorCanceloHub()) {
            botonVolverHub.setText(TEXTO_VOLVER_HUB);
            CLIENTE.resetearVolverHub();
        } else if (CLIENTE.otroJugadorVaAlHub() && !yoVoyAlHub) {
            botonVolverHub.setText(TEXTO_VOLVER_HUB + " (1/2)");
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
        texturaBlanca.dispose();
    }

    public Stage getStage() {
        return STAGE;
    }
}
