package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

import java.util.function.Consumer;

public class PantallaOpciones implements Screen {

    private static final int[][] RESOLUCIONES = {
        {800, 450},
        {1280, 720},
        {1600, 900},
        {1920, 1080}
    };

    private static final float ALPHA_PANEL = 0.6f;
    private static final float ANCHO_CONTROL = 250;
    private static final float ALTO_BOTON = 50;
    private static final float ALTO_BOTON_RESOLUCION = 40;
    private static final float ALTO_SLIDER = 30;
    private static final float ALTO_PISTA = 10;
    private static final int DIAMETRO_KNOB = 20;
    private static final float ANCHO_PORCENTAJE = 70;

    private final Navegable NAV;
    private final Screen PANTALLA_ANTERIOR;
    private final boolean VENIA_DE_JUEGO;

    private Stage stage;
    private Imagen fondo;
    private BitmapFont fontTitulo;
    private BitmapFont fontBoton;
    private BitmapFont fontVolumen;
    private Texture texturaBlanca;
    private Texture texturaKnob;

    private Table panel;
    private Table listaResoluciones;
    private Table filaMusica;
    private Table filaSonido;

    private Label titulo;
    private Label tituloMusica;
    private Label tituloEfectos;
    private Label labelMusica;
    private Label labelSonido;
    private TextButton btnResolucion;
    private TextButton btnToggleFullscreen;
    private TextButton btnVolver;
    private Slider sliderMusica;
    private Slider sliderSonido;

    private boolean mostrarResoluciones = false;

    public PantallaOpciones(Navegable NAV, Screen PANTALLA_ANTERIOR, boolean VENIA_DE_JUEGO) {
        this.NAV = NAV;
        this.PANTALLA_ANTERIOR = PANTALLA_ANTERIOR;
        this.VENIA_DE_JUEGO = VENIA_DE_JUEGO;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        inicializarFuentes();
        inicializarFondo();
        crearTexturas();
        crearUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void inicializarFuentes() {
        fontTitulo = FuenteJuego.generar(56);
        fontBoton = FuenteJuego.generar(32);
        fontVolumen = FuenteJuego.generar(24);
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/fondoAjustes.png");
        actualizarFondo();
    }

    private void actualizarFondo() {
        fondo.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        fondo.setPosition(0, 0);
    }

    private void crearTexturas() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texturaBlanca = new Texture(pixmap);
        pixmap.dispose();

        pixmap = new Pixmap(DIAMETRO_KNOB, DIAMETRO_KNOB, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(DIAMETRO_KNOB / 2, DIAMETRO_KNOB / 2, DIAMETRO_KNOB / 2);
        texturaKnob = new Texture(pixmap);
        pixmap.dispose();
    }

    private Drawable crearFondoPanel() {
        return new TextureRegionDrawable(new TextureRegion(texturaBlanca))
            .tint(new Color(0, 0, 0, ALPHA_PANEL));
    }

    private TextButton.TextButtonStyle crearEstiloBoton() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontBoton;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;
        return style;
    }

    private Slider.SliderStyle crearEstiloSlider() {
        TextureRegionDrawable base = new TextureRegionDrawable(new TextureRegion(texturaBlanca));

        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = base.tint(Color.DARK_GRAY);
        style.background.setMinHeight(ALTO_PISTA);
        style.knobBefore = base.tint(Color.GOLD);
        style.knobBefore.setMinHeight(ALTO_PISTA);
        style.knob = new TextureRegionDrawable(new TextureRegion(texturaKnob));
        return style;
    }

    private Slider crearSlider(
        Slider.SliderStyle style,
        float valorInicial,
        Label porcentaje,
        Consumer<Float> alCambiar) {

        Slider slider = new Slider(0f, 1f, 0.01f, false, style);
        slider.setValue(valorInicial);
        porcentaje.setText(obtenerPorcentaje(valorInicial));

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volumen = slider.getValue();
                alCambiar.accept(volumen);
                porcentaje.setText(obtenerPorcentaje(volumen));
            }
        });

        return slider;
    }

    private Table crearFilaVolumen(Slider slider, Label porcentaje) {
        Table fila = new Table();
        fila.add(slider).width(ANCHO_CONTROL).height(ALTO_SLIDER);
        fila.add(porcentaje).width(ANCHO_PORCENTAJE).padLeft(10);
        return fila;
    }

    private void crearUI() {
        TextButton.TextButtonStyle estiloBoton = crearEstiloBoton();
        Label.LabelStyle estiloVolumen = new Label.LabelStyle(fontVolumen, Color.WHITE);

        titulo = new Label("Opciones", new Label.LabelStyle(fontTitulo, Color.GOLD));
        tituloMusica = new Label("Volumen música", estiloVolumen);
        tituloEfectos = new Label("Volumen efectos", estiloVolumen);
        labelMusica = new Label("", estiloVolumen);
        labelSonido = new Label("", estiloVolumen);

        Slider.SliderStyle estiloSlider = crearEstiloSlider();
        sliderMusica = crearSlider(estiloSlider, Musica.getVolumen(), labelMusica, v -> Musica.setVolumen(v));
        sliderSonido = crearSlider(estiloSlider, Sonido.getVolumen(), labelSonido, v -> Sonido.setVolumen(v));
        filaMusica = crearFilaVolumen(sliderMusica, labelMusica);
        filaSonido = crearFilaVolumen(sliderSonido, labelSonido);

        btnToggleFullscreen = new TextButton(textoFullscreen(), estiloBoton);
        btnResolucion = new TextButton(textoResolucion(Config.getAncho(), Config.getAlto()), estiloBoton);
        btnVolver = new TextButton("Volver", estiloBoton);

        listaResoluciones = new Table();
        for (int[] resolucion : RESOLUCIONES) {
            agregarResolucion(resolucion[0], resolucion[1], estiloBoton);
        }

        btnResolucion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                mostrarResoluciones = !mostrarResoluciones;
                actualizarInterfaz();
            }
        });

        btnToggleFullscreen.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                Config.toggleFullscreen();
                mostrarResoluciones = false;
                btnToggleFullscreen.setText(textoFullscreen());
                btnResolucion.setText(textoResolucion(Config.getAncho(), Config.getAlto()));
                refrescarVista();
            }
        });

        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                if (VENIA_DE_JUEGO) {
                    NAV.cambiarPantalla(PANTALLA_ANTERIOR);
                    PantallaJuego pantallaJuego = (PantallaJuego) PANTALLA_ANTERIOR;
                    pantallaJuego.restaurarPosicionCamara();
                    pantallaJuego.setPausa(true);
                } else {
                    NAV.volverAMenu();
                }
            }
        });

        panel = new Table();
        panel.setBackground(crearFondoPanel());
        panel.pad(30, 50, 30, 50);

        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.center();
        raiz.add(panel);

        actualizarInterfaz();
        stage.addActor(raiz);
    }

    private void agregarResolucion(int ancho, int alto, TextButton.TextButtonStyle style) {
        TextButton boton = new TextButton(ancho + "x" + alto, style);

        boton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                Config.setWindowed(ancho, alto);
                mostrarResoluciones = false;
                btnResolucion.setText(textoResolucion(ancho, alto));
                btnToggleFullscreen.setText(textoFullscreen());
                refrescarVista();
            }
        });

        listaResoluciones.add(boton).size(ANCHO_CONTROL, ALTO_BOTON_RESOLUCION).row();
    }

    private void actualizarInterfaz() {
        panel.clearChildren();

        panel.add(titulo).padBottom(25).row();

        panel.add(tituloMusica).padBottom(5).row();
        panel.add(filaMusica).padBottom(15).row();

        panel.add(tituloEfectos).padBottom(5).row();
        panel.add(filaSonido).padBottom(20).row();

        panel.add(btnToggleFullscreen).size(ANCHO_CONTROL, ALTO_BOTON).padBottom(10).row();

        if (!Config.fullscreen) {
            panel.add(btnResolucion).size(ANCHO_CONTROL, ALTO_BOTON).padBottom(10).row();
            if (mostrarResoluciones) {
                panel.add(listaResoluciones).padBottom(10).row();
            }
        }

        panel.add(btnVolver).size(ANCHO_CONTROL, ALTO_BOTON);
    }

    private void refrescarVista() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        actualizarFondo();
        actualizarInterfaz();
    }

    private String textoFullscreen() {
        return Config.fullscreen ? "Modo ventana" : "Pantalla completa";
    }

    private String textoResolucion(int ancho, int alto) {
        return "Resolución: " + ancho + "x" + alto;
    }

    private String obtenerPorcentaje(float volumen) {
        return Math.round(volumen * 100) + "%";
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
        actualizarFondo();
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
            fontBoton.dispose();
            fontVolumen.dispose();
        }
        if (texturaBlanca != null) {
            texturaBlanca.dispose();
        }
        if (texturaKnob != null) {
            texturaKnob.dispose();
        }
    }
}
