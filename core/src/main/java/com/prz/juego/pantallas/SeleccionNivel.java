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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
import com.badlogic.gdx.Input;

public class SeleccionNivel implements Screen {

    private static final int CANTIDAD_NIVELES = 7;
    private static final int CANTIDAD_RELIQUIAS = 5;
    private static final int NIVEL_CON_REQUISITOS = 6;
    private static final int NIVEL_SECRETO = 7;
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

    private static final int TAMANO_FUENTE_ESTADO = 22;
    private static final String TEXTO_COMPLETADO = "Completado";
    private static final String TEXTO_BLOQUEADO = "Bloqueado";
    private static final Color COLOR_BLOQUEADO = new Color(0.25f, 0.25f, 0.25f, 1f);
    private static final Color COLOR_PENDIENTE = new Color(0.72f, 0.72f, 0.72f, 1f);

    private final Cliente cliente;
    private final Navegable NAV;
    private final PantallaJuego PANTALLA_HUB;

    private Stage stage;
    private BitmapFont fontTitulo;
    private BitmapFont fontCancelar;
    private BitmapFont fontEstado;
    private Imagen fondo;

    private final ImageButton[] portales = new ImageButton[CANTIDAD_NIVELES];
    private final Stack[] contenedores = new Stack[CANTIDAD_NIVELES];
    private final Label[] etiquetas = new Label[CANTIDAD_NIVELES];

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
        fontEstado = FuenteJuego.generar(TAMANO_FUENTE_ESTADO);
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/seleccionNivel.png");
        fondo.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        fondo.setPosition(0, 0);
    }

    private boolean todasLasReliquias(boolean[] reliquias) {
        for (boolean recogida : reliquias) {
            if (!recogida) {
                return false;
            }
        }
        return true;
    }

    private boolean estaDisponible(int numeroNivel) {
        if (numeroNivel == NIVEL_CON_REQUISITOS) {
            return todasLasReliquias(cliente.getReliquiasRecogidas());
        }
        if (numeroNivel == NIVEL_SECRETO) {
            return cliente.isNivel7Alcanzado();
        }
        return true;
    }

    private ImageButton crearPortal(final int numeroNivel) {
        Texture textura = GestorRecursos.obtenerTextura("Menu/Portales/portal" + numeroNivel + ".png");

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(textura));

        ImageButton portal = new ImageButton(style);
        portal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!estaDisponible(numeroNivel)) {
                    return;
                }
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

    private Stack crearContenedor(int indice) {
        ImageButton portal = crearPortal(indice + 1);

        Label etiqueta = new Label("", new Label.LabelStyle(fontEstado, Color.WHITE));
        etiqueta.setAlignment(Align.bottom);
        etiqueta.setTouchable(Touchable.disabled);

        Stack contenedor = new Stack();
        contenedor.add(portal);
        contenedor.add(etiqueta);

        portales[indice] = portal;
        etiquetas[indice] = etiqueta;
        contenedores[indice] = contenedor;

        return contenedor;
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

            principal.add(crearContenedor(i)).size(TAMANO_PORTAL).pad(PADDING_PORTAL);

            if ((i + 1) % COLUMNAS == 0) {
                if (i + 1 == PORTALES_GRILLA) {
                    principal
                        .add(crearContenedor(CANTIDAD_NIVELES - 1))
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

    private Color colorPortal(
        int nivel, boolean completado, boolean bloqueado, int miVoto, int votoRemoto) {
        if (bloqueado) {
            return COLOR_BLOQUEADO;
        }
        if (nivel == miVoto && nivel == votoRemoto) {
            return Color.GREEN;
        }
        if (nivel == miVoto) {
            return Color.CYAN;
        }
        if (nivel == votoRemoto) {
            return Color.GOLD;
        }
        return completado ? Color.WHITE : COLOR_PENDIENTE;
    }

    private void actualizarPortales() {
        boolean[] reliquias = cliente.getReliquiasRecogidas();
        boolean nivel7Alcanzado = cliente.isNivel7Alcanzado();
        boolean todasRecogidas = todasLasReliquias(reliquias);
        int miVoto = cliente.getVotoLocal();
        int votoRemoto = cliente.getVotoRemoto();

        for (int i = 0; i < CANTIDAD_NIVELES; i++) {
            int nivel = i + 1;
            boolean visible = nivel != NIVEL_SECRETO || nivel7Alcanzado;
            boolean bloqueado = nivel == NIVEL_CON_REQUISITOS && !todasRecogidas;
            boolean completado = nivel <= CANTIDAD_RELIQUIAS
                ? reliquias[i]
                : nivel == NIVEL_CON_REQUISITOS && nivel7Alcanzado;

            contenedores[i].setVisible(visible);
            portales[i].setTouchable(visible && !bloqueado ? Touchable.enabled : Touchable.disabled);
            portales[i].getImage().setColor(colorPortal(nivel, completado, bloqueado, miVoto, votoRemoto));

            if (completado) {
                etiquetas[i].setText(TEXTO_COMPLETADO);
                etiquetas[i].setColor(Color.GREEN);
            } else if (bloqueado) {
                etiquetas[i].setText(TEXTO_BLOQUEADO);
                etiquetas[i].setColor(Color.LIGHT_GRAY);
            } else {
                etiquetas[i].setText("");
            }
        }
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        // DEBUG
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            cliente.enviarMensaje("DEBUG_DESBLOQUEAR");
        }

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
        actualizarPortales();
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
            fontEstado.dispose();
        }
        if (fondo != null) {
            fondo.dispose();
        }
    }
}
