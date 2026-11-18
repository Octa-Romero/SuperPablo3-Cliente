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
import com.prz.juego.principal.Navegable;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class Controles implements Screen {

    private static final String[][] CONTROLES = {
        {"Movimiento", "A/D o Flecha Izquierda/Flecha Derecha"},
        {"Salto", "W, Flecha Arriba, Espacio o Z"},
        {"Ataque", "X o J"},
        {"Pausa", "Esc"},
        {"Interactuar", "E"}
    };

    private static final float ALPHA_PANEL = 0.6f;

    private final Navegable NAV;
    private Stage stage;
    private Imagen fondo;
    private BitmapFont fontTitulo;
    private BitmapFont fontAccion;
    private BitmapFont fontTeclas;
    private BitmapFont fontBoton;
    private Texture texturaBlanca;

    public Controles(Navegable NAV) {
        this.NAV = NAV;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        inicializarFuentes();
        inicializarFondo();
        crearInterfaz();
        Gdx.input.setInputProcessor(stage);
    }

    private void inicializarFuentes() {
        fontTitulo = FuenteJuego.generar(52);
        fontAccion = FuenteJuego.generar(34);
        fontTeclas = FuenteJuego.generar(28);
        fontBoton = FuenteJuego.generar(32);
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/controles.png");
        fondo.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        fondo.setPosition(0, 0);
    }

    private Drawable crearFondoPanel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texturaBlanca = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texturaBlanca))
            .tint(new Color(0, 0, 0, ALPHA_PANEL));
    }

    private TextButton crearBotonVolver() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontBoton;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;

        TextButton volver = new TextButton("Volver", style);
        volver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                NAV.volverAMenu();
            }
        });
        return volver;
    }

    private void crearInterfaz() {
        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.center();

        Table panel = new Table();
        panel.setBackground(crearFondoPanel());
        panel.pad(30, 60, 30, 60);

        Label titulo = new Label("Controles", new Label.LabelStyle(fontTitulo, Color.GOLD));
        panel.add(titulo).colspan(2).padBottom(30).row();

        Label.LabelStyle estiloAccion = new Label.LabelStyle(fontAccion, Color.GOLD);
        Label.LabelStyle estiloTeclas = new Label.LabelStyle(fontTeclas, Color.WHITE);

        for (String[] control : CONTROLES) {
            panel.add(new Label(control[0], estiloAccion)).right().padRight(40).padBottom(18);
            panel.add(new Label(control[1], estiloTeclas)).left().padBottom(18).row();
        }

        panel.add(crearBotonVolver()).colspan(2).width(250).height(50).padTop(20);

        raiz.add(panel);
        stage.addActor(raiz);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        Render.begin(stage.getCamera());
        fondo.dibujar(Render.batch);
        Render.end();

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
        if (stage != null) {
            stage.dispose();
        }
        if (fondo != null) {
            fondo.dispose();
        }
        if (fontTitulo != null) {
            fontTitulo.dispose();
            fontAccion.dispose();
            fontTeclas.dispose();
            fontBoton.dispose();
        }
        if (texturaBlanca != null) {
            texturaBlanca.dispose();
        }
    }
}
