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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.red.Cliente;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Global;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

import java.util.ArrayList;
import java.util.List;

public class PantallaMenu implements Screen {

    private static final long TIEMPO_MOSTRAR_BUSCANDO = 1000;
    private static final float ANCHO_BOTON = 250;
    private static final float ALTO_BOTON = 50;
    private static final float SEPARACION_BOTONES = 25;
    private static final float ANCHO_TEXTO_ESPERA = 560;
    private static final float ALPHA_FONDO_ESPERA = 0.45f;
    private static final float ALPHA_PANEL_ESPERA = 0.65f;
    private static final String TEXTO_BUSCANDO_SERVER = "Buscando servidor...";
    private static final String TEXTO_BUSCANDO_JUGADOR = "Buscando otro jugador...";
    private static final String TEXTO_CONEXION_RECHAZADA = "Ya hay un jugador conectado con esta IP";

    private final Navegable NAV;
    private final Cliente cliente;

    private Stage stage;
    private Stage stageEspera;
    private Imagen fondo;
    private BitmapFont fontTitulo;
    private BitmapFont fontSubtitulo;
    private BitmapFont fontBoton;
    private TextButton.TextButtonStyle estiloBoton;
    private Texture texturaBlanca;
    private Label lblEspera;

    private boolean esperandoConexion = false;
    private boolean buscandoServidor = false;
    private long tiempoInicioBusqueda = 0;

    public PantallaMenu(Navegable NAV, Cliente cliente) {
        this.NAV = NAV;
        this.cliente = cliente;
        Musica.MENU.sonar();
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        inicializarFondo();
        inicializarFuentes();
        crearTexturaBlanca();
        crearInterfaz();
        Gdx.input.setInputProcessor(stage);
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/menuSP3.png");
        actualizarFondo();
    }

    private void actualizarFondo() {
        fondo.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        fondo.setPosition(0, 0);
    }

    private void inicializarFuentes() {
        fontTitulo = FuenteJuego.generar(64);
        fontSubtitulo = FuenteJuego.generar(36);
        fontBoton = FuenteJuego.generar(28);

        estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.font = fontBoton;
        estiloBoton.fontColor = Color.WHITE;
        estiloBoton.overFontColor = Color.GOLD;
        estiloBoton.downFontColor = Color.RED;
    }

    private void crearTexturaBlanca() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texturaBlanca = new Texture(pixmap);
        pixmap.dispose();
    }

    private TextButton crearBoton(String texto, Runnable accion) {
        TextButton boton = new TextButton(texto, estiloBoton);
        boton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                accion.run();
            }
        });
        return boton;
    }

    private void crearInterfaz() {
        Label titulo = new Label("Super Pablo 3", new Label.LabelStyle(fontTitulo, Color.GOLD));
        Label subtitulo = new Label("~ La Última Batalla ~", new Label.LabelStyle(fontSubtitulo, Color.RED));

        List<TextButton> botones = new ArrayList<>();

        if (Config.DEMO_MODE) {
            botones.add(crearBoton("Jugar (Jugador 1)", () -> jugarComoCliente(1)));
            botones.add(crearBoton("Jugar (Jugador 2)", () -> jugarComoCliente(2)));
        } else {
            botones.add(crearBoton("Jugar", this::iniciarBusqueda));
        }

        botones.add(crearBoton("Opciones", () ->
            NAV.cambiarPantalla(new PantallaOpciones(NAV, PantallaMenu.this, false))));
        botones.add(crearBoton("Controles", () -> NAV.cambiarPantalla(new Controles(NAV))));
        botones.add(crearBoton("Salir", () -> Gdx.app.exit()));

        Table contenedor = new Table();
        contenedor.setFillParent(true);

        contenedor.add(titulo).padBottom(5).row();
        contenedor.add(subtitulo).padBottom(60).row();

        for (int i = 0; i < botones.size(); i++) {
            float separacion = i < botones.size() - 1 ? SEPARACION_BOTONES : 0;
            contenedor.add(botones.get(i)).width(ANCHO_BOTON).height(ALTO_BOTON).padBottom(separacion).row();
        }

        stage.addActor(contenedor);
    }

    private void jugarComoCliente(int id) {
        cliente.setClienteId(id);
        iniciarBusqueda();
    }

    private void iniciarBusqueda() {
        esperandoConexion = true;
        buscandoServidor = true;
        tiempoInicioBusqueda = System.currentTimeMillis();
        mostrarCartelEspera();
    }

    private void cancelarBusqueda() {
        esperandoConexion = false;
        buscandoServidor = false;
        cliente.desconectar();
        cerrarCartelEspera();
    }

    private void finalizarEspera() {
        esperandoConexion = false;
        cerrarCartelEspera();
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        Render.begin(stage.getCamera());
        fondo.dibujar(Render.batch);
        Render.end();

        stage.act(delta);
        stage.draw();

        if (esperandoConexion) {
            verificarConexion();
            if (stageEspera != null) {
                stageEspera.act(delta);
                stageEspera.draw();
            }
        }
    }

    private void verificarConexion() {
        if (buscandoServidor) {
            long transcurrido = System.currentTimeMillis() - tiempoInicioBusqueda;
            if (transcurrido < TIEMPO_MOSTRAR_BUSCANDO) {
                setTextoEspera(TEXTO_BUSCANDO_SERVER);
                return;
            }
            cliente.conectar();
            buscandoServidor = false;
        }

        String estadoReconexion = cliente.getEstadoReconexion();
        if (estadoReconexion != null) {
            finalizarEspera();
            navegarPorEstadoReconexion(estadoReconexion, cliente.getNivelReconexion());
            return;
        }

        if (cliente.isConexionEstablecida()) {
            finalizarEspera();
            NAV.cambiarPantalla(new SeleccionPersonaje(NAV, cliente, PantallaMenu.this));
        } else if (cliente.isConexionRechazada()) {
            setTextoEspera(TEXTO_CONEXION_RECHAZADA);
        } else if (cliente.encontroServidor()) {
            setTextoEspera(TEXTO_BUSCANDO_JUGADOR);
        } else {
            setTextoEspera(TEXTO_BUSCANDO_SERVER);
        }
    }

    private void navegarPorEstadoReconexion(String estado, String nivelReconexion) {
        switch (estado) {
            case "HUB":
            case "SELECCION_NIVEL":
            case "CREDITOS":
                Global.empieza = true;
                NAV.cambiarPantalla(new PantallaJuego(NAV, cliente, null));
                break;
            case "JUGANDO":
                Global.empieza = true;
                NAV.cambiarPantalla(new PantallaJuego(NAV, cliente, nivelReconexion));
                break;
            case "SELECCION_PERSONAJE":
            default:
                NAV.cambiarPantalla(new SeleccionPersonaje(NAV, cliente, PantallaMenu.this));
                break;
        }
    }

    private void setTextoEspera(String texto) {
        if (lblEspera != null) {
            lblEspera.setText(texto);
        }
    }

    private void mostrarCartelEspera() {
        liberarCartelEspera();

        stageEspera = new Stage(new ExtendViewport(Config.ANCHO_BASE, Config.ALTO_BASE));

        TextureRegionDrawable base = new TextureRegionDrawable(new TextureRegion(texturaBlanca));

        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.setBackground(base.tint(new Color(0, 0, 0, ALPHA_FONDO_ESPERA)));

        Table panel = new Table();
        panel.setBackground(base.tint(new Color(0, 0, 0, ALPHA_PANEL_ESPERA)));
        panel.pad(40, 100, 40, 100);

        lblEspera = new Label(TEXTO_BUSCANDO_SERVER, new Label.LabelStyle(fontSubtitulo, Color.WHITE));
        lblEspera.setAlignment(Align.center);

        TextButton btnCancelar = crearBoton("Cancelar", this::cancelarBusqueda);

        panel.add(lblEspera).width(ANCHO_TEXTO_ESPERA).padBottom(30).row();
        panel.add(btnCancelar).width(ANCHO_BOTON).height(ALTO_BOTON);

        raiz.add(panel);
        stageEspera.addActor(raiz);

        Gdx.input.setInputProcessor(stageEspera);
    }

    private void liberarCartelEspera() {
        if (stageEspera != null) {
            stageEspera.dispose();
            stageEspera = null;
        }
        lblEspera = null;
    }

    private void cerrarCartelEspera() {
        liberarCartelEspera();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (stageEspera != null) {
            stageEspera.getViewport().update(width, height, true);
        }
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
        liberarCartelEspera();

        if (stage != null) {
            stage.dispose();
        }
        if (fondo != null) {
            fondo.dispose();
        }
        if (fontTitulo != null) {
            fontTitulo.dispose();
            fontSubtitulo.dispose();
            fontBoton.dispose();
        }
        if (texturaBlanca != null) {
            texturaBlanca.dispose();
        }
    }
}
