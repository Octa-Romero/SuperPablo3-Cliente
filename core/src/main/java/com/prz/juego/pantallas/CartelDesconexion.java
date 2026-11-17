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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.FuenteJuego;

public class CartelDesconexion {

    private static final float ANCHO_PANEL = 480;
    private static final float ALTO_PANEL = 280;
    private static final float ANCHO_BOTON = 250;
    private static final float ALTO_BOTON = 50;
    private static final String TEXTO_ESPERANDO_JUGADOR = "Esperando al otro jugador...";

    private final Stage STAGE;
    private final Navegable NAV;
    private final BitmapFont FONT_TITULO;
    private final BitmapFont FONT_SUBTITULO;
    private final BitmapFont FONT_BOTONES;
    private final Texture texturaBlanca;
    private final Drawable fondoOscuro;
    private final Drawable panelOscuro;
    private Label lblSubtitulo;
    private boolean activo = false;
    private boolean esperandoJugador = false;

    public CartelDesconexion(Navegable NAV) {
        this.NAV = NAV;

        STAGE = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        STAGE.setKeyboardFocus(null);
        STAGE.setScrollFocus(null);

        FONT_TITULO = FuenteJuego.generar(36);
        FONT_SUBTITULO = FuenteJuego.generar(24);
        FONT_BOTONES = FuenteJuego.generar(28);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texturaBlanca = new Texture(pixmap);
        pixmap.dispose();

        TextureRegionDrawable base = new TextureRegionDrawable(new TextureRegion(texturaBlanca));
        fondoOscuro = base.tint(new Color(0, 0, 0, 0.45f));
        panelOscuro = base.tint(new Color(0, 0, 0, 0.75f));

        desactivar();
    }

    public void mostrarServidorPerdido() {
        esperandoJugador = false;
        mostrar("Conexión perdida", "Se perdió la conexión con el servidor");
    }

    public void mostrarJugadorDesconectado() {
        esperandoJugador = true;
        mostrar("Jugador desconectado", TEXTO_ESPERANDO_JUGADOR);
    }

    public void actualizarTiempoReconexion(int segundos) {
        if (activo && esperandoJugador && lblSubtitulo != null && segundos > 0) {
            lblSubtitulo.setText(TEXTO_ESPERANDO_JUGADOR + " (" + segundos + " s)");
        }
    }

    private void mostrar(String titulo, String subtitulo) {
        STAGE.clear();

        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.setBackground(fondoOscuro);
        raiz.setTouchable(Touchable.enabled);
        raiz.addListener(new ClickListener());

        Table panel = new Table();
        panel.setBackground(panelOscuro);
        panel.pad(20);

        Label lblTitulo = new Label(titulo, new Label.LabelStyle(FONT_TITULO, Color.GOLD));
        lblTitulo.setAlignment(Align.center);

        lblSubtitulo = new Label(subtitulo, new Label.LabelStyle(FONT_SUBTITULO, Color.WHITE));
        lblSubtitulo.setWrap(true);
        lblSubtitulo.setAlignment(Align.center);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = FONT_BOTONES;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.RED;

        TextButton salir = new TextButton("Salir al menú", style);
        salir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NAV.volverAMenu();
            }
        });

        panel.add(lblTitulo).padBottom(15).row();
        panel.add(lblSubtitulo).width(ANCHO_PANEL - 60).padBottom(40).row();
        panel.add(salir).width(ANCHO_BOTON).height(ALTO_BOTON);

        raiz.add(panel).size(ANCHO_PANEL, ALTO_PANEL);

        STAGE.addActor(raiz);
        activar();
    }

    public void activar() {
        activo = true;
        STAGE.getRoot().setVisible(true);
        STAGE.getRoot().setTouchable(Touchable.enabled);
    }

    public void desactivar() {
        activo = false;
        esperandoJugador = false;
        STAGE.getRoot().setVisible(false);
        STAGE.getRoot().setTouchable(Touchable.disabled);
    }

    public boolean estaActivo() {
        return activo;
    }

    public void update(float delta) {
        if (activo) {
            STAGE.act(delta);
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
        FONT_SUBTITULO.dispose();
        FONT_BOTONES.dispose();
        texturaBlanca.dispose();
    }

    public Stage getStage() {
        return STAGE;
    }
}
