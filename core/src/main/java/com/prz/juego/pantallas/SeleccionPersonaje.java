package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.red.Cliente;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Global;
import com.prz.juego.utilidades.Render;
import com.prz.juego.entidades.jugadores.Personajes;

public class SeleccionPersonaje implements Screen {

    private Cliente cliente;
    private final Navegable nav;
    private Stage stage;
    private BitmapFont font;

    private Personajes personajeSeleccionado;

    private TextButton btnPablo;
    private TextButton btnWalter;
    private TextButton btnComenzar;

    private Label lblEstado;
    private boolean conexionLista = false;

    public SeleccionPersonaje(Navegable nav, Cliente cliente) {
        this.nav = nav;
        this.cliente = cliente;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();

        crearInterfaz();
    }

    private void crearInterfaz() {

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.defaults().pad(10);

        Label titulo = new Label("SELECCIÓN DE PERSONAJE", new Label.LabelStyle(font, Color.WHITE));
        titulo.setFontScale(2f);

        lblEstado = new Label("Elegí tu personaje", new Label.LabelStyle(font, Color.WHITE));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;

        btnPablo = new TextButton("PABLO", style);
        btnWalter = new TextButton("WALTER", style);

        btnComenzar = new TextButton("COMENZAR", style);

        btnPablo.setVisible(false);
        btnWalter.setVisible(false);
        btnComenzar.setVisible(false);

        btnPablo.setDisabled(true);
        btnWalter.setDisabled(true);
        btnComenzar.setDisabled(true);

        btnPablo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                personajeSeleccionado = Personajes.PABLO;
                btnComenzar.setVisible(true);
                btnComenzar.setDisabled(false);
            }
        });

        btnWalter.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                personajeSeleccionado = Personajes.WALTER;
                btnComenzar.setVisible(true);
                btnComenzar.setDisabled(false);
            }
        });

        btnComenzar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (personajeSeleccionado == null) {
                    return;
                }

                conexionLista = true;

                btnComenzar.setDisabled(true);
                btnPablo.setDisabled(true);
                btnWalter.setDisabled(true);

                lblEstado.setText("Esperando al otro jugador...");

                cliente.enviarMensaje(personajeSeleccionado.name());
            }
        });

        table.add(titulo).padBottom(40).row();
        table.add(lblEstado).padTop(20).row();
        table.add(btnPablo).width(250).height(60).padBottom(20).row();
        table.add(btnWalter).width(250).height(60).padBottom(40).row();

        table.add(btnComenzar).width(300).height(70);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        stage.act(delta);
        stage.draw();

        if (Global.empieza) {
            String nombreRemoto = cliente.getPersonajeRemoto();

            if (nombreRemoto == null) {
                return;
            }

            Global.empieza = false;

            Personajes personajeRemoto = Personajes.valueOf(nombreRemoto);

            nav.cambiarPantalla(
                new PantallaJuego(nav, personajeSeleccionado, personajeRemoto, cliente)
            );

            return;
        }

        if (cliente.isConexionEstablecida() && !conexionLista) {

            btnPablo.setVisible(true);
            btnWalter.setVisible(true);

            btnPablo.setDisabled(false);
            btnWalter.setDisabled(false);

            lblEstado.setText("Elegí tu personaje");
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
