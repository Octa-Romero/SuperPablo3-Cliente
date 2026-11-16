package com.prz.juego.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.prz.juego.entidades.jefes.Mano;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.entidades.jugadores.Pablo;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.principal.Navegable;
import com.prz.juego.niveles.Hub;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.red.*;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class PantallaJuego implements Screen {

    private Cliente cliente;
    private final Navegable NAV;
    private Jugador jugadorLocal;
    private JugadorEstadoRed estadoRedLocal;
    private Jugador jugadorRemoto;

    private Nivel NIVEL;
    private final Entrada ENTRADA;
    private MenuPausa MENU_PAUSA;
    private boolean pausado = false;
    private final Hud HUD;
    private Hub hub;
    private boolean cambiarASeleccionNivel = false;
    private boolean envioEntradaActivo = true;

    private float tiempoMensaje = 0f;
    private static final float INTERVALO_MENSAJE = 0.25f;

    private String ultimoEstadoIzquierda = "";
    private String ultimoEstadoDerecha = "";
    private String ultimoEstadoArriba = "";
    private String ultimoEstadoAbajo = "";
    private String ultimoEstadoAtaque = "";
    private String ultimoEstadoE = "";

    public PantallaJuego(Navegable NAV, Cliente cliente, String rutaNivel) {
        this.NAV = NAV;
        this.ENTRADA = new Entrada();
        // Limpiar entidades del nivel anterior antes de cargar uno nuevo
        cliente.limpiarEntidades();
        this.NIVEL = rutaNivel == null ? null : Nivel.crear(rutaNivel);
        this.MENU_PAUSA = new MenuPausa(NAV, this, cliente);
        this.cliente = cliente;

        Personajes personajeElegido = cliente.getPersonajeLocal();
        Personajes personajeRemoto = cliente.getPersonajeRemoto();

        jugadorLocal = personajeElegido.crear(50, 150);
        jugadorLocal.setEntrada(ENTRADA);
        jugadorLocal.setEstadoRed(cliente.getEstadoLocal());

        this.estadoRedLocal = jugadorLocal.getEstadoRed();

        jugadorRemoto = personajeRemoto.crear(50, 150);
        jugadorRemoto.setEstadoRed(cliente.getEstadoRemoto());

        if (rutaNivel == null) {
            hub = new Hub();
            hub.cargar("Niveles/Niveles/Hub.tmx", jugadorLocal, jugadorRemoto);
        } else {
            hub = null;
            NIVEL.cargar(jugadorLocal, jugadorRemoto);
        }

        HUD = new Hud(jugadorLocal, Render.batch);
    }

    @Override
    public void show() {
        //Gdx.input.setCursorCatched(!pausado);

        InputMultiplexer mux = new InputMultiplexer();

        mux.addProcessor(MENU_PAUSA.getStage());
        mux.addProcessor(ENTRADA);

        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        // Detectar Escape
        if (ENTRADA.aprietaEscape()) {
            setPausa(!pausado);

            if (pausado) {
                MENU_PAUSA.activar();
                // Enviar entrada nula al servidor para detener el movimiento
                cliente.enviarMensaje("INPUT;NOIZQUIERDA");
                cliente.enviarMensaje("INPUT;NODERECHA");
                cliente.enviarMensaje("INPUT;NOARRIBA");
                cliente.enviarMensaje("INPUT;NOABAJO");
                cliente.enviarMensaje("INPUT;NOATACA");
                cliente.enviarMensaje("INPUT;NOE");
            } else {
                MENU_PAUSA.desactivar();
                // Al cerrar el menú de pausa, cancelar el estado de volver al hub
                MENU_PAUSA.cancelarVolverHub();
            }
        }

        if (!pausado && envioEntradaActivo) {
            actualizarEnvioEntrada(delta);
        } else if (pausado) {
            // Cuando está pausado, limpiar la entrada para que el jugador se detenga
            ENTRADA.limpiar();
        }

        // Actualizar entidades siempre
        if (hub != null) {
            hub.update(delta, ENTRADA);

            if (!pausado && ENTRADA.aprietaE()) {

                if (estadoRedLocal.estaEsperandoSeleccion()) {

                    cliente.enviarMensaje("SELECCION_NIVEL;false");
                    // Reactivar el envío de entrada al cancelar la selección
                    envioEntradaActivo = true;
                    // Limpiar entrada para evitar movimientos residuales que causen teletransporte
                    ENTRADA.limpiar();

                } else if (hub.puedeInteractuarPancrasio()) {

                    hub.interactuarPancrasio();

                } else if (estadoRedLocal.estaEnCentroHub()) {

                    cliente.enviarMensaje("SELECCION_NIVEL;true");
                    // Detener el envío de entrada para que el servidor procese los mensajes nulos
                    envioEntradaActivo = false;
                    // Limpiar entrada y forzar idle para que la animación no se congele
                    ENTRADA.limpiar();
                    cliente.enviarMensaje("INPUT;NOIZQUIERDA");
                    cliente.enviarMensaje("INPUT;NODERECHA");
                    cliente.enviarMensaje("INPUT;NOARRIBA");
                    cliente.enviarMensaje("INPUT;NOABAJO");
                    cliente.enviarMensaje("INPUT;NOATACA");
                    cliente.enviarMensaje("INPUT;NOE");
                    jugadorLocal.forzarIdle();
                    estadoRedLocal.resetearAnimacionRecibida();
                }
            }

            // Forzar idle mientras se espera al otro jugador (cada frame, no solo al presionar E)
            if (estadoRedLocal.estaEsperandoSeleccion()) {
                jugadorLocal.forzarIdle();
            }

            if (!pausado && cliente.sePuedeSeleccionarNivel()) {

                guardarPosicionCamara();
                ENTRADA.limpiar();
                // Enviar entrada nula al servidor para que el jugador vuelva al idle
                cliente.enviarMensaje("INPUT;NOIZQUIERDA");
                cliente.enviarMensaje("INPUT;NODERECHA");
                cliente.enviarMensaje("INPUT;NOARRIBA");
                cliente.enviarMensaje("INPUT;NOABAJO");
                cliente.enviarMensaje("INPUT;NOATACA");
                cliente.enviarMensaje("INPUT;NOE");
                // Forzar la animacion de idle localmente para que se renderice en este frame
                jugadorLocal.forzarIdle();
                jugadorRemoto.forzarIdle();
                // Cambiar pantalla al siguiente frame para que el idle se vea
                cambiarASeleccionNivel = true;
            }
        } else {
            NIVEL.update(delta, ENTRADA);
        }

        // Actualizaciones de estado de red que siguen ejecutándose aunque esté pausado
        if (hub == null) {
            for (EnemigoRed e : cliente.getEnemigosRecibidos()) {
                NIVEL.agregarEnemigos(e);
                cliente.registrarEstadoEnemigo(e);
            }

            for (EnemigoRed e : cliente.getEstadoEnemigos()) {
                NIVEL.actualizarEstadoEnemigos(e);
            }

            for (Integer id : cliente.getEnemigosEliminados()) {
                NIVEL.eliminarEnemigo(id);
            }

            for (ProyectilRed p : cliente.getProyectilesNuevos()) {
                NIVEL.agregarProyectil(p);
                cliente.registrarEstadoProyectil(p);
            }

            for (ProyectilRed p : cliente.getEstadoProyectiles()) {
                NIVEL.actualizarEstadoProyectil(p);
            }

            for (Integer id : cliente.getProyectilesEliminados()) {
                NIVEL.eliminarProyectil(id);
            }

            JefeRed jefeRecibido = cliente.getJefeRecibido();

            if(jefeRecibido != null)
            {
                NIVEL.crearJefe(jefeRecibido);
                HUD.setJefe(NIVEL.getJefe());
            }

            JefeRed estadoJefe = cliente.getEstadoJefe();

            if(estadoJefe != null)
            {
                NIVEL.actualizarEstadoJefe(estadoJefe);
            }

            ManoRed izquierda = cliente.getEstadoMano(Mano.Lado.IZQUIERDA);
            ManoRed derecha = cliente.getEstadoMano(Mano.Lado.DERECHA);

            if (izquierda != null && izquierda.recibioEstado()) {
                NIVEL.actualizarEstadoMano(izquierda);
            }

            if (derecha != null && derecha.recibioEstado()) {
                NIVEL.actualizarEstadoMano(derecha);
            }

            FuegoSueloRed estadoFuegoSuelo = cliente.getEstadoFuegoSuelo();

            if(estadoFuegoSuelo != null && estadoFuegoSuelo.tieneEstadoRecibido())
            {
                NIVEL.actualizarEstadoFuegoSuelo(estadoFuegoSuelo);
            }

            RayosRed rayos = cliente.getEstadoRayos();

            if (rayos != null && rayos.tieneEstadoRecibido()) {
                NIVEL.actualizarEstadoRayos(rayos);
            }

            LasersRed lasers = cliente.getEstadoLasers();

            if (lasers != null && lasers.tieneEstadoRecibido()) {
                NIVEL.actualizarEstadoLasers(lasers);
            }

            for (MeteoritoRed meteorito : cliente.getMeteoritosNuevos()) {
                NIVEL.crearMeteorito(meteorito);
            }

            for (MeteoritoRed meteorito : cliente.getEstadoMeteoritos()) {
                if (meteorito.tieneEstadoRecibido()) {
                    NIVEL.actualizarEstadoMeteorito(meteorito);
                }
            }

            for (int id : cliente.getMeteoritosEliminados()) {
                NIVEL.eliminarMeteorito(id);
            }

            LaserJefeRed estadoLaserJefe = cliente.getEstadoLaserJefe();

            if (estadoLaserJefe.tieneEstadoRecibido()) {
                NIVEL.actualizarEstadoLaserJefe(estadoLaserJefe);
            }

            for (FueguitoRed fueguito : cliente.getFueguitosNuevos()) {
                NIVEL.crearFueguito(fueguito);
            }

            for (FueguitoRed fueguito : cliente.getEstadoFueguitos()) {
                NIVEL.actualizarEstadoFueguito(fueguito);
            }

            for (FueguitoRed fueguito : cliente.getFueguitosEliminados()) {
                NIVEL.eliminarFueguito(
                    fueguito.getId(),
                    fueguito.getTipo()
                );
            }

            // No eliminar el jefe inmediatamente para que siga visible durante los 2 segundos
            // antes de los créditos. El jefe se eliminará implícitamente al cambiar a PantallaFinal.

            // Eliminar todos los ataques del jefe cuando el servidor lo indica
            if (cliente.getEliminarAtaquesJefe()) {
                NIVEL.eliminarAtaquesJefe();
            }

            if (cliente.getMostrarCreditos()) {
                NAV.cambiarPantalla(new PantallaFinal(NAV));
                return;
            }
        }

        // Actualizar la posición de la cámara para el rango de escucha de sonidos
        if (hub != null) {
            com.badlogic.gdx.graphics.Camera cam = hub.getCamara();
            cliente.actualizarCamara(cam.position.x, cam.position.y);
        } else {
            com.badlogic.gdx.graphics.Camera cam = NIVEL.getCamara();
            cliente.actualizarCamara(cam.position.x, cam.position.y);
        }

        // Dibujar el Hub o el NIVEL seleccionado
        if (hub != null) {
            hub.renderMapa();

            Render.begin(hub.getCamara());
            hub.render();
            hub.dibujarAlertaCentro(estadoRedLocal.estaEnCentroHub(), estadoRedLocal.estaEsperandoSeleccion());
            Render.end();

        } else {
            NIVEL.renderMapa();

            Render.begin(NIVEL.getCamara());
            NIVEL.render();
            Render.end();

            NIVEL.renderDebug();

            if (NIVEL.isGameOver()) {
                NAV.cambiarPantalla(new GameOver(NAV, NIVEL, cliente));
                return;
            }
        }

        if (hub == null) {
            HUD.setTieneReliquia(NIVEL.tieneReliquia());
            HUD.update(delta);

            boolean localMuerto = jugadorLocal.estaMuerto();
            boolean remotoMuerto = jugadorRemoto.estaMuerto();
            String nombreVivo = localMuerto
                ? (jugadorRemoto instanceof Pablo ? "Pablo" : "Walter")
                : (jugadorLocal instanceof Pablo ? "Pablo" : "Walter");
            String nombreMuerto = remotoMuerto
                ? (jugadorRemoto instanceof Pablo ? "Pablo" : "Walter")
                : "";
            HUD.setEstadoMuerte(localMuerto, remotoMuerto, nombreVivo, nombreMuerto);

            HUD.dibujar();
        }

        if (pausado) {
            MENU_PAUSA.update(delta);
            MENU_PAUSA.render();
        }

        // Cambiar a SeleccionNivel después de renderizar el frame con el idle
        if (cambiarASeleccionNivel) {
            cambiarASeleccionNivel = false;
            NAV.cambiarPantalla(new SeleccionNivel(NAV, cliente, this));
        }
    }

    private void actualizarEnvioEntrada(float delta) {
        tiempoMensaje += delta;

        String estadoIzquierda = obtenerEstadoIzquierda();
        String estadoDerecha = obtenerEstadoDerecha();
        String estadoArriba = obtenerEstadoArriba();
        String estadoAtaque = obtenerEstadoAtaque();
        String estadoAbajo = obtenerEstadoAbajo();
        String estadoE = obtenerEstadoE();

        if (!estadoIzquierda.equals(ultimoEstadoIzquierda)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoIzquierda);
            ultimoEstadoIzquierda = estadoIzquierda;
        }

        if (!estadoDerecha.equals(ultimoEstadoDerecha)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoDerecha);
            ultimoEstadoDerecha = estadoDerecha;
        }

        if (!estadoArriba.equals(ultimoEstadoArriba)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoArriba);
            ultimoEstadoArriba = estadoArriba;
        }

        if (!estadoAbajo.equals(ultimoEstadoAbajo)
            || tiempoMensaje >= INTERVALO_MENSAJE) {

            cliente.enviarMensaje(estadoAbajo);
            ultimoEstadoAbajo = estadoAbajo;
        }

        if (!estadoAtaque.equals(ultimoEstadoAtaque)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoAtaque);
            ultimoEstadoAtaque = estadoAtaque;
        }

        if (!estadoE.equals(ultimoEstadoE)
            || tiempoMensaje >= INTERVALO_MENSAJE) {
            cliente.enviarMensaje(estadoE);
            ultimoEstadoE = estadoE;
        }

        if (tiempoMensaje >= INTERVALO_MENSAJE) {
            tiempoMensaje = 0f;
        }
    }


    private String obtenerEstadoIzquierda() {
        if (ENTRADA.mueveIzquierda()) {
            return "INPUT;IZQUIERDA";
        }
        return "INPUT;NOIZQUIERDA";
    }

    private String obtenerEstadoDerecha() {
        if (ENTRADA.mueveDerecha()) {
            return "INPUT;DERECHA";
        }
        return "INPUT;NODERECHA";
    }

    private String obtenerEstadoArriba() {
        if (ENTRADA.mueveArriba()) {
            return "INPUT;ARRIBA";
        }
        return "INPUT;NOARRIBA";
    }

    private String obtenerEstadoAbajo() {
        if (ENTRADA.mueveAbajo()) {
            return "INPUT;ABAJO";
        }
        return "INPUT;NOABAJO";
    }

    private String obtenerEstadoAtaque() {
        if (ENTRADA.ataca()) {
            return "INPUT;ATACA";
        }
        return "INPUT;NOATACA";
    }

    private String obtenerEstadoE() {
        if (ENTRADA.ataca()) {
            return "INPUT;E";
        }
        return "INPUT;NOE";
    }

    @Override
    public void resize(int width, int height) {
        if (hub != null) {
            hub.resize(width, height);
        } else {
            NIVEL.resize(width, height);
        }

        MENU_PAUSA.resize(width, height);
        HUD.actualizarTamano(width, height);
    }


    public void setPausa(boolean valor) {
        this.pausado = valor;
        Gdx.input.setCursorCatched(!valor);
    }

    public void guardarPosicionCamara() {
        if (hub != null) {
            hub.guardarPosicionCamara();
        } else {
            NIVEL.guardarPosicionCamara();
        }
    }

    public boolean estaEnHub() {
        return NIVEL == null;
    }

    public void cargarHub() {
        // Desactivar pausa si estaba activa
        if (pausado) {
            pausado = false;
            MENU_PAUSA.desactivar();
        }
        // Limpiar el nivel actual si existe
        if (NIVEL != null) {
            NIVEL.dispose();
            NIVEL = null;
        }
        // Limpiar el hub actual si existe
        if (hub != null) {
            hub.dispose();
        }
        // Dispose del MenuPausa anterior
        MENU_PAUSA.dispose();
        // Reiniciar los jugadores a las posiciones iniciales del hub
        jugadorLocal.reiniciar(50, 150);
        jugadorRemoto.reiniciar(50, 150);
        // Crear el hub
        hub = new Hub();
        hub.cargar("Niveles/Niveles/Hub.tmx", jugadorLocal, jugadorRemoto);
        // Crear nuevo MenuPausa
        MENU_PAUSA = new MenuPausa(NAV, this, cliente);
        HUD.setJefe(null);
        ENTRADA.limpiar();
        // Limpiar cualquier mensaje residual de volver al hub
        cliente.resetearVolverHub();
        // Reconfigurar el InputMultiplexer con el nuevo MenuPausa
        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(MENU_PAUSA.getStage());
        mux.addProcessor(ENTRADA);
        Gdx.input.setInputProcessor(mux);
        // Reactivar el envío de entrada al volver al hub
        envioEntradaActivo = true;
    }

    public void restaurarPosicionCamara() {
        if (hub != null) {
            hub.restaurarPosicionCamara();
        } else {
            NIVEL.restaurarPosicionCamara();
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setCursorCatched(false);
        Sonido.FUEGO_SOSTENIDO.detenerLoop();
    }

    @Override
    public void dispose() {
        if (hub != null) {
            hub.dispose();
        }
        if (NIVEL != null) {
            NIVEL.dispose();
        }
        MENU_PAUSA.dispose();
        HUD.dispose();
    }
}
