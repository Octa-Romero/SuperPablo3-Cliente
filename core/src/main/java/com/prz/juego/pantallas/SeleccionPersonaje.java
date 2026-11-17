package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.principal.Navegable;
import com.prz.juego.red.Cliente;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Global;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class SeleccionPersonaje implements Screen {

    private Cliente cliente;
    private final Navegable NAV;
    private final PantallaMenu PANTALLA_MENU;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont fontBoton;
    private BitmapFont fontCheck;
    private Imagen fondo;
    private Personajes personajeSeleccionado;

    private TextButton btnPablo;
    private TextButton btnWalter;
    private TextButton btnComenzar;

    private Label lblCheckPablo;
    private Label lblCheckWalter;
    private boolean personajeEnviado = false;

    private CartelDesconexion cartelDesconexion;

    public SeleccionPersonaje(Navegable NAV, Cliente cliente, PantallaMenu PANTALLA_MENU) {
        this.NAV = NAV;
        this.cliente = cliente;
        this.PANTALLA_MENU = PANTALLA_MENU;
        this.cartelDesconexion = new CartelDesconexion(NAV);
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(cartelDesconexion.getStage());
        mux.addProcessor(stage);
        Gdx.input.setInputProcessor(mux);
        inicializarFuentes();
        inicializarFondo();
        crearInterfaz();
    }

    private void inicializarFuentes() {
        font = FuenteJuego.generar(48);
        fontBoton = FuenteJuego.generar(28);
        fontCheck = FuenteJuego.generar(36);
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/seleccionPersonaje.png");
        actualizarFondo();
    }

    private void actualizarFondo() {
        float ancho = stage.getViewport().getWorldWidth();
        float alto = stage.getViewport().getWorldHeight();
        fondo.setSize(ancho, alto);
        fondo.setPosition(0, 0);
    }

    private TextButton.TextButtonStyle crearEstiloBoton() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontBoton;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;
        style.disabledFontColor = Color.GRAY;
        return style;
    }

    private void aplicarColorBoton(TextButton boton, boolean seleccionado) {
        TextButton.TextButtonStyle style = boton.getStyle();
        style.fontColor = seleccionado ? Color.YELLOW : Color.WHITE;
        style.overFontColor = seleccionado ? Color.YELLOW : Color.GOLD;
    }

    private void actualizarColoresBotones() {
        aplicarColorBoton(btnPablo, personajeSeleccionado == Personajes.PABLO);
        aplicarColorBoton(btnWalter, personajeSeleccionado == Personajes.WALTER);
    }

    private void seleccionar(Personajes personaje) {
        Sonido.CLICK.sonar();
        personajeSeleccionado = personaje;
        personajeEnviado = false;
        cliente.setPersonsajeLocal(personaje);
        cliente.enviarMensaje(personaje.name());
        personajeEnviado = true;
        actualizarColoresBotones();
    }

    private void crearInterfaz() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label titulo = new Label("Selección de personaje", new Label.LabelStyle(font, Color.WHITE));

        lblCheckPablo = new Label("V", new Label.LabelStyle(fontCheck, Color.YELLOW));
        lblCheckWalter = new Label("V", new Label.LabelStyle(fontCheck, Color.YELLOW));
        lblCheckPablo.setAlignment(Align.center);
        lblCheckWalter.setAlignment(Align.center);
        lblCheckPablo.setVisible(false);
        lblCheckWalter.setVisible(false);

        btnPablo = new TextButton("Pablo", crearEstiloBoton());
        btnWalter = new TextButton("Walter", crearEstiloBoton());
        btnPablo.setVisible(false);
        btnWalter.setVisible(false);
        btnPablo.setDisabled(true);
        btnWalter.setDisabled(true);

        btnComenzar = new TextButton("Comenzar", crearEstiloBoton());
        btnComenzar.setDisabled(true);

        btnPablo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                seleccionar(Personajes.PABLO);
            }
        });

        btnWalter.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                seleccionar(Personajes.WALTER);
            }
        });

        btnComenzar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                if (cliente.isListoParaComenzar()) {
                    cliente.setListoParaComenzar(false);
                    cliente.enviarMensaje("CANCELAR_COMENZAR");
                } else {
                    cliente.setListoParaComenzar(true);
                    cliente.enviarMensaje("LISTO_COMENZAR");
                }
            }
        });

        float anchoCol = 250;
        float separacion = 500;
        float mitad = separacion / 2;

        table.add(titulo).colspan(2).center().padBottom(50).row();

        table.add(lblCheckPablo).width(anchoCol).height(25).padRight(mitad).padBottom(5);
        table.add(lblCheckWalter).width(anchoCol).height(25).padLeft(mitad).padBottom(5).row();

        table.add(btnPablo).width(anchoCol).height(35).padRight(mitad).padBottom(60);
        table.add(btnWalter).width(anchoCol).height(35).padLeft(mitad).padBottom(60).row();

        table.add(btnComenzar).colspan(2).center().width(300).height(70);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        Render.begin(stage.getCamera());
        fondo.dibujar(Render.batch);
        Render.end();
        stage.act(delta);
        stage.draw();

        if (cliente.isServidorPerdido() && !cartelDesconexion.estaActivo()) {
            cartelDesconexion.mostrarServidorPerdido();
        }

        if (cliente.isJugadorRemotoDesconectado() && !cartelDesconexion.estaActivo()) {
            cartelDesconexion.mostrarJugadorDesconectado();
            cliente.resetearJugadorRemotoDesconectado();
        }

        if (cliente.isJugadorRemotoReconecto() && cartelDesconexion.estaActivo()) {
            cartelDesconexion.desactivar();
        }

        if (cliente.isExpulsado()) {
            if (cartelDesconexion.estaActivo()) {
                cartelDesconexion.desactivar();
            }
            NAV.volverAMenu();
            return;
        }

        if (cartelDesconexion.estaActivo()) {
            cartelDesconexion.actualizarTiempoReconexion(cliente.getSegundosReconexion());
            cartelDesconexion.update(delta);
            cartelDesconexion.render();
            return;
        }

        if (Global.empieza) {
            Personajes nombreRemoto = cliente.getPersonajeRemoto();
            if (nombreRemoto != null) {
                Global.empieza = false;
                NAV.cambiarPantalla(new PantallaJuego(NAV, cliente, null));
                return;
            }
        }

        btnPablo.setVisible(true);
        btnWalter.setVisible(true);
        btnPablo.setDisabled(false);
        btnWalter.setDisabled(false);

        String remotoElegido = cliente.getPersonajeRemotoElegido();
        if (remotoElegido != null) {
            if (remotoElegido.equals("PABLO")) {
                lblCheckPablo.setVisible(true);
                lblCheckWalter.setVisible(false);
            } else if (remotoElegido.equals("WALTER")) {
                lblCheckWalter.setVisible(true);
                lblCheckPablo.setVisible(false);
            }
        }

        boolean ambosElegieronDistintos = personajeSeleccionado != null
            && remotoElegido != null
            && !personajeSeleccionado.name().equals(remotoElegido);

        if (!ambosElegieronDistintos && cliente.isListoParaComenzar()) {
            cliente.setListoParaComenzar(false);
            cliente.enviarMensaje("CANCELAR_COMENZAR");
        }

        if (ambosElegieronDistintos) {
            btnComenzar.setDisabled(false);
            int contador = cliente.getContadorComenzar();
            btnComenzar.setText(contador >= 1 ? "Comenzar (" + contador + "/2)" : "Comenzar");
        } else {
            btnComenzar.setDisabled(true);
            btnComenzar.setText("Comenzar");
        }
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
        stage.dispose();
        font.dispose();
        fontBoton.dispose();
        fontCheck.dispose();
        fondo.dispose();
        cartelDesconexion.dispose();
    }
}
