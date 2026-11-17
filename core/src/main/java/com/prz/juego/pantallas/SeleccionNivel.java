package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.principal.Navegable;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.red.Cliente;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class SeleccionNivel implements Screen {

    private static final int CANTIDAD_NIVELES = 7;
    private static final int PORTALES_GRILLA = CANTIDAD_NIVELES - 1;
    private static final int COLUMNAS = 3;
    private static final int COLUMNAS_TOTALES = COLUMNAS + 2;

    private static final float TAMANO_PORTAL = 170;
    private static final float TAMANO_PORTAL_EXTRA = 100;
    private static final float PADDING_PORTAL = 5;
    private static final float ANCHO_LATERAL = TAMANO_PORTAL_EXTRA + PADDING_PORTAL * 2;

    private static final float RETRASO_INICIAL = 0.15f;
    private static final float PASO_ENTRE_PORTALES = 0.07f;
    private static final float DURACION_FADE = 0.25f;

    private final Cliente cliente;
    private final Navegable NAV;
    private final PantallaJuego PANTALLA_HUB;

    private Stage stage;
    private BitmapFont fontTitulo;
    private BitmapFont fontCancelar;
    private Imagen fondo;

    private final ImageButton[] portales = new ImageButton[CANTIDAD_NIVELES];

    public SeleccionNivel(Navegable NAV, Cliente cliente, PantallaJuego PANTALLA_HUB) {
        this.NAV = NAV;
        this.cliente = cliente;
        this.PANTALLA_HUB = PANTALLA_HUB;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        Gdx.input.setInputProcessor(stage);

        inicializarFuentes();
        inicializarFondo();
        crearInterfaz();
    }

    private void inicializarFuentes() {
        fontTitulo = FuenteJuego.generar(48);
        fontCancelar = FuenteJuego.generar(31);
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/seleccionNivel.png");
        fondo.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        fondo.setPosition(0, 0);
    }

    private ImageButton crearPortal(final int numeroNivel) {
        Texture textura = GestorRecursos.obtenerTextura("Menu/Portales/portal" + numeroNivel + ".png");

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(textura));

        ImageButton portal = new ImageButton(style);
        portal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                cliente.enviarMensaje("VOTAR_NIVEL;" + numeroNivel);
            }
        });

        animarAparicion(
            portal,
            RETRASO_INICIAL + (numeroNivel - 1) * PASO_ENTRE_PORTALES,
            DURACION_FADE);

        return portal;
    }

    private void animarAparicion(Actor actor, float retraso, float duracion) {
        actor.getColor().a = 0f;
        actor.addAction(Actions.sequence(Actions.delay(retraso), Actions.fadeIn(duracion)));
    }

    private TextButton crearBotonCancelar() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontCancelar;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;

        TextButton boton = new TextButton("Cancelar", style);
        boton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Sonido.CLICK.sonar();
                cliente.enviarMensaje("CANCELAR_SELECCION_NIVEL");
            }
        });

        return boton;
    }

    private void crearInterfaz() {
        Table principal = new Table();
        principal.setFillParent(true);

        Label titulo = new Label("Selección de nivel", new Label.LabelStyle(fontTitulo, Color.WHITE));
        principal.add(titulo).colspan(COLUMNAS_TOTALES).padBottom(20).row();

        for (int i = 0; i < PORTALES_GRILLA; i++) {
            if (i % COLUMNAS == 0) {
                principal.add().width(ANCHO_LATERAL);
            }

            portales[i] = crearPortal(i + 1);
            principal.add(portales[i]).size(TAMANO_PORTAL).pad(PADDING_PORTAL);

            if ((i + 1) % COLUMNAS == 0) {
                if (i + 1 == PORTALES_GRILLA) {
                    portales[CANTIDAD_NIVELES - 1] = crearPortal(CANTIDAD_NIVELES);
                    principal
                        .add(portales[CANTIDAD_NIVELES - 1])
                        .size(TAMANO_PORTAL_EXTRA)
                        .pad(PADDING_PORTAL);
                }
                principal.row();
            }
        }

        principal.add(crearBotonCancelar()).colspan(COLUMNAS_TOTALES).width(200).height(48).padTop(12);

        stage.addActor(principal);
    }

    private void iniciarNivel(int numeroNivel) {
        if (numeroNivel < 1 || numeroNivel > CANTIDAD_NIVELES) {
            return;
        }

        NAV.iniciarJuego(Nivel.getRuta(numeroNivel));
    }

    private void volverAlHub() {
        PANTALLA_HUB.reanudarDespuesDeSeleccion();
        NAV.cambiarPantalla(PANTALLA_HUB);
    }

    private void actualizarColoresPortales() {
        int miVoto = cliente.getVotoLocal();
        int votoRemoto = cliente.getVotoRemoto();

        for (int i = 0; i < portales.length; i++) {
            int nivel = i + 1;
            Color color = Color.WHITE;

            if (nivel == miVoto && nivel == votoRemoto) {
                color = Color.GREEN;
            } else if (nivel == miVoto) {
                color = Color.CYAN;
            } else if (nivel == votoRemoto) {
                color = Color.GOLD;
            }

            portales[i].getImage().setColor(color);
        }
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        if (cliente.consumirSeleccionCancelada()) {
            volverAlHub();
            return;
        }

        int nivel = cliente.getNivelIniciado();
        if (nivel != 0) {
            iniciarNivel(nivel);
            return;
        }

        Render.begin(stage.getCamera());
        fondo.dibujar(Render.batch);
        Render.end();

        stage.act(delta);
        actualizarColoresPortales();
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
        if (fontTitulo != null) {
            fontTitulo.dispose();
            fontCancelar.dispose();
        }
        if (fondo != null) {
            fondo.dispose();
        }
    }
}
