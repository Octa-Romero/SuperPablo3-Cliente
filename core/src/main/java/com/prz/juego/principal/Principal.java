package com.prz.juego.principal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.pantallas.PantallaJuego;
import com.prz.juego.pantallas.PantallaMenu;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.red.Cliente;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class Principal extends Game implements Navegable {

    private Cliente cliente;
    private PantallaJuego pantallaJuego;

    @Override
    public void create() {
        Render.batch = new SpriteBatch();
        cliente = new Cliente();
        this.setScreen(new PantallaMenu(this, cliente));
    }

    @Override
    public void dispose() {

        Screen pantallaActual = getScreen();
        if (pantallaActual != null) {
            pantallaActual.dispose();
        }

        if (Render.batch != null) {
            Render.batch.dispose();
            Render.batch = null;
        }

        if(cliente != null)
        {
            cliente.cerrar();
        }

        Musica.dispose();
        Sonido.dispose();

        GestorRecursos.dispose();
    }

    @Override
    public void cambiarPantalla(Screen screen) {
        if (screen instanceof PantallaJuego juego) {
            pantallaJuego = juego;
        }
        setScreen(screen);
    }

    @Override
    public void iniciarJuego(String rutaNivel) {
        pantallaJuego = new PantallaJuego(this, cliente, rutaNivel);
        setScreen(pantallaJuego);
    }

    @Override
    public void volverAMenu() {
        pantallaJuego = null;
        cliente.desconectar();
        setScreen(new PantallaMenu(this, cliente));
    }

    @Override
    public void volverAHub() {
        if (pantallaJuego != null) {
            pantallaJuego.cargarHub();
            setScreen(pantallaJuego);
        }
    }

}
