package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.principal.Navegable;
import com.prz.juego.red.Cliente;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class GameOver implements Screen {

    private static final float ALPHA_OVERLAY = 0.5f;
    private static final String TEXTO_VOLVER_HUB = "Volver al hub";

    private final Navegable NAV;
    private final Nivel NIVEL;
    private final Cliente CLIENTE;
    private Stage stage;
    private BitmapFont fontTitulo;
    private BitmapFont fontMensaje;
    private BitmapFont fontBoton;
    private Texture texturaBlanca;
    private TextButton botonVolverHub;
    private boolean yoVoyAlHub = false;
    private boolean volviendoAlHub = false;

    public GameOver(Navegable NAV, Nivel nivel, Cliente cliente) {
        this.NAV = NAV;
        this.NIVEL = nivel;
        this.CLIENTE = cliente;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        inicializarFuentes();
        crearInterfaz();
        Gdx.input.setInputProcessor(stage);
    }

    private void inicializarFuentes() {
        fontTitulo = FuenteJuego.generar(96);
        fontMensaje = FuenteJuego.generar(32);
        fontBoton = FuenteJuego.generar(36);
    }

    private Drawable crearOverlay() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texturaBlanca = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texturaBlanca))
            .tint(new Color(0, 0, 0, ALPHA_OVERLAY));
    }

    private TextButton.TextButtonStyle crearEstiloBoton() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontBoton;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;
        return style;
    }

    private void crearInterfaz() {
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(crearOverlay());
        table.center();

        Label titulo = new Label("Game Over", new Label.LabelStyle(fontTitulo, Color.RED));
        Label mensaje = new Label("Has perdido la partida", new Label.LabelStyle(fontMensaje, Color.LIGHT_GRAY));

        TextButton.TextButtonStyle style = crearEstiloBoton();
        botonVolverHub = new TextButton(TEXTO_VOLVER_HUB, style);
        TextButton volverMenu = new TextButton("Salir al menú", style);

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

        volverMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                NAV.volverAMenu();
            }
        });

        table.add(titulo).padBottom(15).row();
        table.add(mensaje).padBottom(50).row();
        table.add(botonVolverHub).width(380).height(60).padBottom(15).row();
        table.add(volverMenu).width(380).height(60);

        stage.addActor(table);
    }

    public void cancelarVolverHub() {
        yoVoyAlHub = false;
        CLIENTE.enviarMensaje("CANCELAR_HUB");
        if (botonVolverHub != null) {
            botonVolverHub.setText(TEXTO_VOLVER_HUB);
        }
        CLIENTE.resetearVolverHub();
    }

    public void actualizarBotonHub() {
        if (volviendoAlHub) {
            return;
        }

        if (CLIENTE.ambosJugadoresVuelvenHub()) {
            volviendoAlHub = true;
            botonVolverHub.setText(TEXTO_VOLVER_HUB + " (2/2)");
            Musica.MENU.sonar();
            NAV.volverAHub();
        } else if (CLIENTE.otroJugadorCanceloHub()) {
            botonVolverHub.setText(TEXTO_VOLVER_HUB);
            CLIENTE.resetearVolverHub();
        } else if (CLIENTE.otroJugadorVaAlHub() && !yoVoyAlHub) {
            botonVolverHub.setText(TEXTO_VOLVER_HUB + " (1/2)");
        }
    }

    @Override
    public void render(float delta) {
        actualizarBotonHub();

        if (volviendoAlHub) {
            return;
        }

        if (NIVEL != null) {
            NIVEL.update(delta);
            if (NIVEL.sincronizar(CLIENTE)) {
                NAV.cambiarPantalla(new PantallaFinal(NAV));
                return;
            }
        }

        Render.limpiarPantalla();
        dibujarNivel();

        stage.act(delta);
        stage.draw();
    }

    private void dibujarNivel() {
        if (NIVEL == null) {
            return;
        }

        NIVEL.renderMapa();
        Render.begin(NIVEL.getCamara());
        NIVEL.render();
        Render.end();
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
        if (stage != null) {
            stage.dispose();
        }
        if (fontTitulo != null) {
            fontTitulo.dispose();
            fontMensaje.dispose();
            fontBoton.dispose();
        }
        if (texturaBlanca != null) {
            texturaBlanca.dispose();
        }
    }
}
