package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Render;

public class PantallaFinal implements Screen {

    private static final float MARGEN_LATERAL = 40f;
    private static final float SEPARACION_TITULOS = 5f;
    private static final float SEPARACION_CREDITOS = 10f;

    private static final float FADE_IN_ESCENA = 2f;
    private static final float ESPERA_ESCENA = 4f;
    private static final float FADE_OUT_ESCENA = 2f;

    private static final float FADE_IN_FIN = 3f;
    private static final float ESPERA_FIN = 5f;
    private static final float FADE_OUT_FIN = 2f;

    private static final float FADE_IN_CREDITOS = 3f;
    private static final float ESPERA_CREDITOS = 18f;
    private static final float FADE_OUT_CREDITOS = 3f;

    private static final String[] ESCENAS = {
        "La batalla había llegado a su fin.",
        "El sacrificio de Pablo salvó al reino y selló para siempre la maldición de El Retumbar.",
        "El Retumbar fue derrotado, y con él desapareció la oscuridad que durante tanto tiempo había amenazado estas tierras.",
        "Sin embargo, la victoria tuvo un precio. Pablo entregó todo para que los demás pudieran vivir en paz.",
        "Su nombre jamás sería olvidado, porque incluso cuando los héroes desaparecen, sus leyendas permanecen."
    };

    private static final String CREDITOS =
        "[GOLD]Desarrollo[]\n\n"
            + "Octavio Romero\n[LIGHT_GRAY]Interfaz, sistema multijugador[]\n\n"
            + "Santiago Papale\n[LIGHT_GRAY]Arte, sprites, mapas e interfaz[]\n\n"
            + "Matias Rodrigo Zurita\n[LIGHT_GRAY]Enemigos, música y sonido[]\n\n\n"
            + "[GOLD]Agradecimientos especiales[]\n\n"
            + "Leandro Zelaya\n[LIGHT_GRAY]Por sus diseños de los juegos anteriores,\n"
            + "reutilizados en esta entrega.[]";

    private final Navegable NAV;
    private Stage stage;
    private Imagen fondo;

    private BitmapFont fontTitulo;
    private BitmapFont fontSubtitulo;
    private BitmapFont fontRelato;
    private BitmapFont fontFin;
    private BitmapFont fontCreditos;

    private Label.LabelStyle estiloRelato;
    private Label.LabelStyle estiloFin;
    private Label.LabelStyle estiloCreditos;

    private Label titulo;
    private Label subtitulo;
    private Label texto;

    private int escenaActual = 0;
    private boolean enCreditos = false;

    public PantallaFinal(Navegable NAV) {
        this.NAV = NAV;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        inicializarFuentes();
        inicializarFondo();
        crearInterfaz();

        Gdx.input.setInputProcessor(null);
        mostrarSiguienteEscena();
    }

    private void inicializarFuentes() {
        fontTitulo = FuenteJuego.generar(48);
        fontSubtitulo = FuenteJuego.generar(36);
        fontRelato = FuenteJuego.generar(40);
        fontFin = FuenteJuego.generar(64);
        fontCreditos = FuenteJuego.generar(26);
        fontCreditos.getData().markupEnabled = true;
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/fondoFinal.png");
        actualizarFondo();
    }

    private void actualizarFondo() {
        fondo.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        fondo.setPosition(0, 0);
    }

    private void crearInterfaz() {
        estiloRelato = new Label.LabelStyle(fontRelato, Color.WHITE);
        estiloFin = new Label.LabelStyle(fontFin, Color.WHITE);
        estiloCreditos = new Label.LabelStyle(fontCreditos, Color.WHITE);

        titulo = new Label("Super Pablo 3", new Label.LabelStyle(fontTitulo, Color.GOLD));
        subtitulo = new Label("~ La Última Batalla ~", new Label.LabelStyle(fontSubtitulo, Color.RED));

        texto = new Label("", estiloRelato);
        texto.setAlignment(Align.center);
        texto.setWrap(true);

        titulo.getColor().a = 0f;
        subtitulo.getColor().a = 0f;
        texto.getColor().a = 0f;

        stage.addActor(titulo);
        stage.addActor(subtitulo);
        stage.addActor(texto);
    }

    private void acomodar() {
        if (enCreditos) {
            acomodarCreditos();
        } else {
            acomodarEscena();
        }
    }

    private void ajustarTexto() {
        float anchoTexto = stage.getViewport().getWorldWidth() - MARGEN_LATERAL * 2f;
        texto.setWidth(anchoTexto);
        texto.setHeight(texto.getPrefHeight());
    }

    private void acomodarEscena() {
        float alto = stage.getViewport().getWorldHeight();

        ajustarTexto();
        texto.setPosition(MARGEN_LATERAL, (alto - texto.getHeight()) / 2f);
    }

    private void acomodarCreditos() {
        float ancho = stage.getViewport().getWorldWidth();
        float alto = stage.getViewport().getWorldHeight();

        titulo.pack();
        subtitulo.pack();
        ajustarTexto();

        float altoTotal = titulo.getHeight()
            + SEPARACION_TITULOS
            + subtitulo.getHeight()
            + SEPARACION_CREDITOS
            + texto.getHeight();

        float y = (alto + altoTotal) / 2f;

        y -= titulo.getHeight();
        titulo.setPosition((ancho - titulo.getWidth()) / 2f, y);

        y -= SEPARACION_TITULOS + subtitulo.getHeight();
        subtitulo.setPosition((ancho - subtitulo.getWidth()) / 2f, y);

        y -= SEPARACION_CREDITOS + texto.getHeight();
        texto.setPosition(MARGEN_LATERAL, y);
    }

    private void mostrarTexto(
        String contenido,
        Label.LabelStyle estilo,
        float fadeIn,
        float espera,
        float fadeOut,
        Runnable alTerminar) {

        texto.setStyle(estilo);
        texto.setText(contenido);
        acomodar();

        texto.getColor().a = 0f;
        texto.addAction(
            Actions.sequence(
                Actions.fadeIn(fadeIn),
                Actions.delay(espera),
                Actions.fadeOut(fadeOut),
                Actions.run(alTerminar)));
    }

    private void mostrarSiguienteEscena() {
        if (escenaActual >= ESCENAS.length) {
            mostrarFin();
            return;
        }

        mostrarTexto(
            ESCENAS[escenaActual],
            estiloRelato,
            FADE_IN_ESCENA,
            ESPERA_ESCENA,
            FADE_OUT_ESCENA,
            () -> {
                escenaActual++;
                mostrarSiguienteEscena();
            });
    }

    private void mostrarFin() {
        mostrarTexto("Fin.", estiloFin, FADE_IN_FIN, ESPERA_FIN, FADE_OUT_FIN, this::mostrarCreditos);
    }

    private void mostrarCreditos() {
        enCreditos = true;

        titulo.getColor().a = 0f;
        subtitulo.getColor().a = 0f;

        for (Label encabezado : new Label[] {titulo, subtitulo}) {
            encabezado.addAction(
                Actions.sequence(
                    Actions.fadeIn(FADE_IN_CREDITOS),
                    Actions.delay(ESPERA_CREDITOS),
                    Actions.fadeOut(FADE_OUT_CREDITOS)));
        }

        mostrarTexto(
            CREDITOS,
            estiloCreditos,
            FADE_IN_CREDITOS,
            ESPERA_CREDITOS,
            FADE_OUT_CREDITOS,
            NAV::volverAMenu);
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
        acomodar();
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
            fontSubtitulo.dispose();
            fontRelato.dispose();
            fontFin.dispose();
            fontCreditos.dispose();
        }
    }
}
