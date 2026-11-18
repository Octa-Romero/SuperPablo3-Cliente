package com.prz.juego.red;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.Proyectil;
import com.prz.juego.entidades.Tumba;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.enemigos.Enemigos;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.entidades.retumbar.*;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Global;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Sonido;

import com.prz.juego.niveles.Nivel;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class HiloCliente extends Thread {

    private static final long INTERVALO_BUSQUEDA = 5000;
    private static final long TIEMPO_LIMITE_PONG = 5000;
    private static final long INTERVALO_PING = 1000;
    private static final int PUERTO = 9990;
    private static final float DISTANCIA_MAXIMA_SONIDO = 1200f;
    private static final int CANTIDAD_RAYOS = 5;
    private static final int CANTIDAD_LASERS = 15;

    private final ArrayList<Meteorito> METEORITOS = new ArrayList<>();
    private final ArrayList<Integer> METEORITOS_ELIMINADOS = new ArrayList<>();
    private final ArrayList<Fueguito> FUEGUITOS = new ArrayList<>();
    private final ArrayList<Fueguito> FUEGUITOS_ELIMINADOS = new ArrayList<>();

    private DatagramSocket socket;
    private volatile boolean fin = false;
    private volatile boolean servidorEncontrado = false;
    private int idCliente;
    private volatile boolean conexionEstablecida = false;
    private volatile boolean conexionRechazada = false;
    private volatile boolean busquedaFallida = false;
    private long ultimoIntentoConexion;
    private volatile boolean seleccionarNivel = false;
    private volatile int votoLocalNivel = 0;
    private volatile int votoRemotoNivel = 0;
    private volatile int nivelParaIniciar;
    private volatile String personajeOcupado;
    private volatile String personajeRemoto;
    private volatile String personajeRemotoElegido;
    private volatile int contadorComenzar = 0;
    private volatile boolean listoParaComenzar = false;
    private InetAddress ipServer;
    private JugadorEstado estadoLocal = new JugadorEstado();
    private JugadorEstado estadoRemoto = new JugadorEstado();
    private ArrayList<Enemigo> enemigosRecibidos = new ArrayList<>();
    private ArrayList<Enemigo> estadoEnemigos = new ArrayList<>();
    private ArrayList<Integer> enemigosEliminados = new ArrayList<>();
    private ArrayList<Proyectil> proyectilesNuevo = new ArrayList<>();
    private ArrayList<Proyectil> estadoProyectiles = new ArrayList<>();
    private ArrayList<Integer> proyectilesEliminados = new ArrayList<>();
    private final HashMap<String, Tumba> TUMBAS_RECIBIDAS = new HashMap<>();
    private Retumbar jefeNuevo;
    private Retumbar estadoJefe;

    private volatile float camaraX = Config.ANCHO_BASE / 2f;
    private volatile float camaraY = Config.ALTO_BASE / 2f;

    private volatile long ultimoPong = System.currentTimeMillis();
    private volatile boolean servidorPerdido = false;
    private volatile boolean jugadorRemotoDesconectado = false;

    private volatile boolean otroJugadorVaAlHub = false;
    private volatile boolean ambosJugadoresVuelvenHub = false;
    private volatile boolean otroJugadorCanceloHub = false;
    private volatile boolean mostrarCreditos = false;
    private volatile boolean eliminarAtaquesJefe = false;
    private volatile String estadoReconexion;
    private volatile String nivelReconexion;
    private volatile boolean jugadorRemotoReconecto = false;
    private volatile boolean necesitaSnapshot = false;
    private volatile boolean snapshotPendiente = false;
    private volatile boolean expulsado = false;
    private volatile int segundosReconexion = 0;
    private volatile boolean seleccionCancelada = false;
    private volatile boolean reliquiaNueva = false;
    private volatile boolean reliquiaRecogida = false;
    private volatile float reliquiaX;
    private volatile float reliquiaY;
    private static final int CANTIDAD_RELIQUIAS = 5;
    private final boolean[] RELIQUIAS_RECOGIDAS = new boolean[CANTIDAD_RELIQUIAS];
    private volatile boolean nivel7Alcanzado = false;
    private volatile float[] portalNuevo;
    private volatile boolean esperandoPortal = false;

    public HiloCliente(int idCliente) {
        this.idCliente = idCliente;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setSoTimeout(500);
            ipServer = InetAddress.getByName("255.255.255.255");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        iniciarHiloPing();
    }

    private void iniciarHiloPing() {
        Thread hiloPing = new Thread(() -> {
            while (!fin) {
                try {
                    Thread.sleep(INTERVALO_PING);
                } catch (InterruptedException e) {
                    break;
                }
                try {
                    if (servidorEncontrado) {
                        enviarMensaje("PING");
                        if (snapshotPendiente) {
                            enviarMensaje("SOLICITAR_SNAPSHOT");
                        }
                        long ahora = System.currentTimeMillis();
                        if (ahora - ultimoPong > TIEMPO_LIMITE_PONG) {
                            servidorPerdido = true;
                            servidorEncontrado = false;
                            conexionEstablecida = false;
                        }
                    }
                } catch (Exception ignorada) {
                }
            }
        });
        hiloPing.setDaemon(true);
        hiloPing.start();
    }

    @Override
    public void run() {
        while (!fin) {
            actualizarBusquedaServidor();
            byte[] datos = new byte[1024];
            DatagramPacket dp = new DatagramPacket(datos, datos.length);
            try {
                socket.receive(dp);
                procesarMensaje(dp);
            } catch (SocketTimeoutException e) {
                actualizarBusquedaServidor();
            } catch (SocketException e) {
                if (!fin) {
                    System.out.println("Error en el socket del cliente: " + e.getMessage());
                }
                break;
            } catch (IOException e) {
                if (!fin) {
                    System.out.println("Error de recepción: " + e.getMessage());
                }
            } catch (RuntimeException e) {
                System.out.println("Error procesando mensaje: " + e);
                e.printStackTrace();
            }
        }
    }

    private void actualizarBusquedaServidor() {
        if (conexionEstablecida || servidorEncontrado) {
            return;
        }

        long ahora = System.currentTimeMillis();

        if (ultimoIntentoConexion == 0 || ahora - ultimoIntentoConexion >= INTERVALO_BUSQUEDA) {
            if (ultimoIntentoConexion != 0) {
                busquedaFallida = true;
            }

            ultimoIntentoConexion = ahora;
            if (Config.DEMO_MODE) {
                enviarMensaje("Conectar;DEMO;" + idCliente);
            } else {
                enviarMensaje("Conectar");
            }
        }
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msj = new String(dp.getData(), 0, dp.getLength()).trim();

        if (msj.equals("Conexion recibida")) {
            ipServer = dp.getAddress();
            servidorEncontrado = true;
            busquedaFallida = false;
            ultimoPong = System.currentTimeMillis();
            enviarMensaje("PING");
        } else if (msj.equals("Conexion establecida")) {
            conexionEstablecida = true;
            conexionRechazada = false;
        } else if (msj.equals("CONEXION_RECHAZADA")) {
            conexionRechazada = true;
        } else if (msj.equals("Empieza juego")) {
            Global.empieza = true;
        } else if (msj.equals("PABLO") || msj.equals("WALTER")) {
            personajeRemoto = msj;
        } else if (msj.startsWith("PERSONAJE_OCUPADO;")) {
            procesarPersonajeOcupado(msj);
        } else if (msj.startsWith("PERSONAJE_REMOTO;")) {
            procesarPersonajeRemoto(msj);
        } else if (msj.startsWith("CONTADOR_COMENZAR;")) {
            procesarContadorComenzar(msj);
        } else if (msj.startsWith("POS")) {
            procesarPosicion(msj);
        } else if (msj.startsWith("ATAQUE")) {
            procesarEstadoAtaque(msj);
        } else if (msj.startsWith("VIDA")) {
            procesarEstadoVida(msj);
        } else if (msj.startsWith("CREAR_ENEMIGO")) {
            procesarCreacionEnemigos(msj);
        } else if (msj.startsWith("ESTADO_ENEMIGO")) {
            procesarEstadoEnemigos(msj);
        } else if (msj.startsWith("ELIMINAR_ENEMIGO")) {
            procesarEliminacionEnemigos(msj);
        } else if (msj.startsWith("CREAR_PROYECTIL")) {
            procesarCreacionProyectiles(msj);
        } else if (msj.startsWith("ESTADO_PROYECTIL")) {
            procesarEstadoProyectiles(msj);
        } else if (msj.startsWith("ELIMINAR_PROYECTIL")) {
            procesarEliminacionProyectiles(msj);
        } else if (msj.startsWith("CENTRO_HUB")) {
            procesarEstaEnCentroHub(msj);
        } else if (msj.startsWith("ESTADO_SELECCION")) {
            procesarEstadoSeleccion(msj);
        } else if (msj.equals("ABRIR_SELECCION_NIVEL")) {
            seleccionarNivel = true;
        } else if (msj.startsWith("VOTOS_NIVEL;")) {
            procesarVotosNivel(msj);
        } else if (msj.startsWith("INICIAR_NIVEL")) {
            procesarInicioNivel(msj);
        } else if (msj.startsWith("CREAR_JEFE")) {
            procesarCreacionJefe(msj);
        } else if (msj.startsWith("ESTADO_JEFE")) {
            procesarEstadoJefe(msj);
        } else if (msj.startsWith("ELIMINAR_JEFE")) {
            procesarEliminacionJefe();
        } else if (msj.startsWith("ESTADO_MANO")) {
            procesarEstadoMano(msj);
        } else if (msj.startsWith("ESTADO_FUEGO_SUELO")) {
            procesarEstadoFuegoSuelo(msj);
        } else if (msj.startsWith("ESTADO_RAYOS")) {
            procesarEstadoRayos(msj);
        } else if (msj.startsWith("ESTADO_LASERES")) {
            procesarEstadoLasers(msj);
        } else if (msj.startsWith("CREAR_METEORITO")) {
            procesarCreacionMeteorito(msj);
        } else if (msj.startsWith("ESTADO_METEORITO")) {
            procesarEstadoMeteorito(msj);
        } else if (msj.startsWith("ELIMINAR_METEORITO")) {
            procesarEliminacionMeteorito(msj);
        } else if (msj.startsWith("ESTADO_LASER_JEFE;")) {
            procesarEstadoLaserJefe(msj);
        } else if (msj.startsWith("CREAR_FUEGUITO;")) {
            procesarCreacionFueguito(msj);
        } else if (msj.startsWith("ESTADO_FUEGUITO;")) {
            procesarEstadoFueguito(msj);
        } else if (msj.startsWith("ELIMINAR_FUEGUITO;")) {
            procesarEliminacionFueguito(msj);
        } else if (msj.startsWith("ESTADO_TUMBA;")) {
            procesarEstadoTumba(msj);
        } else if (msj.startsWith("PORTAL;")) {
            procesarPortal(msj);
        } else if (msj.startsWith("PROGRESO;")) {
            procesarProgreso(msj);
        } else if(msj.startsWith("RELIQUIA")){
            procesarEstadoReliquia(msj);
        } else if (msj.startsWith("SONIDO;")) {
            procesarSonido(msj);
        } else if (msj.startsWith("MUSICA;")) {
            procesarMusica(msj);
        } else if (msj.startsWith("INVENCIBILIDAD;")) {
            procesarEstadoInvencibilidad(msj);
        } else if (msj.equals("VOLVER_HUB;OTRO")) {
            otroJugadorVaAlHub = true;
        } else if (msj.equals("VOLVER_HUB;AMBOS")) {
            ambosJugadoresVuelvenHub = true;
        } else if (msj.equals("CANCELAR_HUB")) {
            otroJugadorCanceloHub = true;
        } else if (msj.equals("MOSTRAR_CREDITOS")) {
            mostrarCreditos = true;
        } else if (msj.equals("ELIMINAR_ATAQUES_JEFE")) {
            eliminarAtaquesJefe = true;
            limpiarAtaquesJefe();
        } else if (msj.equals("PONG")) {
            ultimoPong = System.currentTimeMillis();
            servidorPerdido = false;
        } else if (msj.equals("JUGADOR_DESCONECTADO")) {
            jugadorRemotoDesconectado = true;
        } else if (msj.startsWith("RECONEXION;")) {
            procesarReconexion(msj);
        } else if (msj.equals("JUGADOR_RECONECTADO")) {
            jugadorRemotoReconecto = true;
            segundosReconexion = 0;
        } else if (msj.equals("SNAPSHOT_REQUERIDO")) {
            necesitaSnapshot = true;
        } else if (msj.equals("SNAPSHOT_FIN")) {
            snapshotPendiente = false;
            enviarMensaje("SNAPSHOT_OK");
        } else if (msj.startsWith("TIMER_RECONEXION;")) {
            procesarTemporizadorReconexion(msj);
        } else if (msj.equals("EXPULSADO")) {
            expulsado = true;
        } else if (msj.equals("CANCELAR_SELECCION")) {
            seleccionarNivel = false;
            votoLocalNivel = 0;
            votoRemotoNivel = 0;
            estadoLocal.actualizarEstadoSeleccion(false);
            seleccionCancelada = true;
        }
    }

    private void procesarTemporizadorReconexion(String msj) {
        String[] partes = msj.split(";");

        try {
            segundosReconexion = Integer.parseInt(partes[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Temporizador de reconexión inválido");
        }
    }

    private void procesarReconexion(String msj) {
        String[] partes = msj.split(";");
        if (partes.length >= 3) {
            personajeRemoto = partes[2];
        }
        if (partes.length >= 4) {
            try {
                nivelReconexion = Nivel.getRuta(Integer.parseInt(partes[3]));
            } catch (IllegalArgumentException e) {
                System.out.println("Nivel de reconexión inválido");
            }
        }
        if (partes.length >= 2) {
            estadoReconexion = partes[1];
        }
    }

    private void procesarPersonajeOcupado(String msj) {
        String[] partes = msj.split(";");

        if (partes.length == 2) {
            personajeOcupado = partes[1];
        }
    }

    private void procesarPersonajeRemoto(String msj) {
        String[] partes = msj.split(";");

        if (partes.length == 2) {
            personajeRemotoElegido = partes[1];
        }
    }

    private void procesarContadorComenzar(String msj) {
        String[] partes = msj.split(";");

        if (partes.length == 2) {
            try {
                contadorComenzar = Integer.parseInt(partes[1]);
            } catch (NumberFormatException ignorada) {
            }
        }
    }

    private void procesarPosicion(String msj) {
        String[] partes = msj.split(";");

        try {
            String personaje = partes[1];
            float x = Float.parseFloat(partes[2]);
            float y = Float.parseFloat(partes[3]);
            int direccion = Integer.parseInt(partes[4]);
            boolean estaEnSuelo = Boolean.parseBoolean(partes[5]);
            boolean estaTrepando = Boolean.parseBoolean(partes[6]);

            obtenerEstadoJugador(personaje).actualizarPosicion(x, y, direccion, estaEnSuelo, estaTrepando);
        } catch (NumberFormatException e) {
            System.out.println("Posición recibida inválida");
        }
    }

    private void procesarEstadoAtaque(String msj) {
        String[] partes = msj.split(";");

        try {
            String personaje = partes[1];
            boolean estadoAtaque = Boolean.parseBoolean(partes[2]);
            int tipoAnimacion = 0;
            float tiempoAnimacion = 0f;

            if (partes.length > 3) {
                tipoAnimacion = Integer.parseInt(partes[3]);
            }
            if (partes.length > 4) {
                tiempoAnimacion = Float.parseFloat(partes[4]);
            }

            obtenerEstadoJugador(personaje).actualizarAtaque(estadoAtaque, tipoAnimacion, tiempoAnimacion);
        } catch (NumberFormatException e) {
            System.out.println("Estado del ataque recibido inválido");
        }
    }

    private void procesarEstadoVida(String msj) {
        String[] partes = msj.split(";");

        try {
            String personaje = partes[1];
            double vidaRecibida = Double.parseDouble(partes[2]);

            obtenerEstadoJugador(personaje).actualizarVida(vidaRecibida);
        } catch (NumberFormatException e) {
            System.out.println("Valor de vida inválido");
        }
    }

    private synchronized void procesarCreacionEnemigos(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            Enemigos tipo = Enemigos.valueOf(partes[2]);
            float x = Float.parseFloat(partes[3]);
            float y = Float.parseFloat(partes[4]);
            float ancho = Float.parseFloat(partes[6]);
            float alto = Float.parseFloat(partes[7]);

            enemigosRecibidos.add(new Enemigo(id, tipo, x, y, ancho, alto));
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Parámetros de creación del enemigo inválidos");
        }
    }

    private synchronized void procesarEstadoEnemigos(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            float x = Float.parseFloat(partes[2]);
            float y = Float.parseFloat(partes[3]);
            int direccion = Integer.parseInt(partes[4]);
            double vida = Double.parseDouble(partes[6]);
            boolean spriteVisible = Boolean.parseBoolean(partes[7]);
            int tipoAnimacion = Integer.parseInt(partes[8]);
            float tiempoAnimacion = Float.parseFloat(partes[9]);
            float anchoAtaque = Float.parseFloat(partes[12]);

            Rectangle ataque = null;
            if (anchoAtaque > 0f) {
                ataque = new Rectangle(
                    Float.parseFloat(partes[10]),
                    Float.parseFloat(partes[11]),
                    anchoAtaque,
                    Float.parseFloat(partes[13]));
            }

            Enemigo encontrado = buscarEnemigo(id);

            if (encontrado != null) {
                encontrado.actualizarEstado(
                    x, y, direccion, vida, spriteVisible, tipoAnimacion, tiempoAnimacion, ataque);
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del enemigo inválido");
        }
    }

    private synchronized void procesarEliminacionEnemigos(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            enemigosEliminados.add(id);
            estadoEnemigos.removeIf(e -> e.getId() == id);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("ID de enemigo eliminado inválido");
        }
    }

    private synchronized void procesarCreacionProyectiles(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            String origen = partes[2];
            float x = Float.parseFloat(partes[3]);
            float y = Float.parseFloat(partes[4]);
            float direccion = Float.parseFloat(partes[5]);
            float ancho = partes.length > 7 ? Float.parseFloat(partes[6]) : 0f;
            float alto = partes.length > 7 ? Float.parseFloat(partes[7]) : 0f;

            proyectilesNuevo.add(new Proyectil(id, origen, x, y, direccion, ancho, alto));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Parámetros de creación del proyectil inválidos");
        }
    }

    private synchronized void procesarEstadoProyectiles(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            float x = Float.parseFloat(partes[2]);
            float y = Float.parseFloat(partes[3]);

            Proyectil encontrado = buscarProyectil(id);

            if (encontrado != null) {
                encontrado.actualizarEstado(x, y);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del proyectil inválido");
        }
    }

    private synchronized void procesarEliminacionProyectiles(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            proyectilesEliminados.add(id);
            estadoProyectiles.removeIf(e -> e.getId() == id);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("ID de proyectil eliminado inválido");
        }
    }

    private void procesarEstaEnCentroHub(String msj) {
        String[] partes = msj.split(";");

        try {
            estadoLocal.actualizarCentroHub(Boolean.parseBoolean(partes[1]));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del centro del Hub inválido");
        }
    }

    private void procesarEstadoSeleccion(String msj) {
        String[] partes = msj.split(";");

        try {
            estadoLocal.actualizarEstadoSeleccion(Boolean.parseBoolean(partes[1]));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de selección inválido");
        }
    }

    private void procesarVotosNivel(String msj) {
        String[] partes = msj.split(";");

        try {
            votoLocalNivel = Integer.parseInt(partes[1]);
            votoRemotoNivel = Integer.parseInt(partes[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Votos de nivel inválidos");
        }
    }

    private void procesarInicioNivel(String msj) {
        String[] partes = msj.split(";");

        try {
            nivelParaIniciar = Integer.parseInt(partes[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Inicio de nivel inválido");
        }
    }

    private synchronized void procesarCreacionJefe(String msj) {
        String[] partes = msj.split(";");

        try {
            float x = Float.parseFloat(partes[1]);
            float y = Float.parseFloat(partes[2]);
            double vida = Double.parseDouble(partes[3]);

            jefeNuevo = new Retumbar(x, y, vida);
            estadoJefe = jefeNuevo;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Parámetros para la creación del jefe inválidos");
        }
    }

    private synchronized void procesarEstadoJefe(String msj) {
        String[] partes = msj.split(";");

        try {
            float x = Float.parseFloat(partes[1]);
            float y = Float.parseFloat(partes[2]);
            double vida = Double.parseDouble(partes[3]);
            boolean visible = Boolean.parseBoolean(partes[4]);
            float escala = Float.parseFloat(partes[5]);
            String faseEspecial = "NORMAL";
            if (partes.length > 6) {
                faseEspecial = partes[6];
            }

            if (estadoJefe != null) {
                estadoJefe.actualizarEstado(x, y, vida, visible, escala, faseEspecial);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del jefe inválido");
        }
    }

    private synchronized void procesarEliminacionJefe() {
        if (estadoJefe != null) {
            estadoJefe.getLaserJefe().reiniciar();
        }

        estadoJefe = null;
        jefeNuevo = null;
        limpiarFueguitos();
    }

    private synchronized void procesarEstadoReliquia(String msj) {
        String[] partes = msj.split(";");

        try {
            if (partes.length >= 2 && partes[1].equals("APARECER")) {
                reliquiaX = Float.parseFloat(partes[2]);
                reliquiaY = Float.parseFloat(partes[3]);

                reliquiaNueva = true;
                reliquiaRecogida = false;

            } else if (partes.length >= 2 && partes[1].equals("RECOGIDA")) {
                reliquiaNueva = false;
                reliquiaRecogida = true;
            } else if (partes.length >= 4 && partes[1].equals("POS")) {
                reliquiaX = Float.parseFloat(partes[2]);
                reliquiaY = Float.parseFloat(partes[3]);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de reliquia inválido");
        }
    }

    private synchronized void procesarEstadoMano(String msj) {
        String[] partes = msj.split(";");

        try {
            Mano.Lado lado = Mano.Lado.valueOf(partes[1]);
            float x = Float.parseFloat(partes[2]);
            float y = Float.parseFloat(partes[3]);
            double vida = Double.parseDouble(partes[4]);
            boolean visible = Boolean.parseBoolean(partes[5]);
            float escala = Float.parseFloat(partes[6]);
            Mano.EstadoAtaque estadoAtaque = Mano.EstadoAtaque.valueOf(partes[7]);
            Mano.EstiloAtaque estiloAtaque = Mano.EstiloAtaque.valueOf(partes[8]);
            boolean mostrarFuegoGolpe = Boolean.parseBoolean(partes[9]);

            if (estadoJefe != null) {
                estadoJefe.getMano(lado).actualizarEstado(
                    x, y, vida, visible, escala, estadoAtaque, estiloAtaque, mostrarFuegoGolpe);
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de mano inválido");
        }
    }

    private synchronized void procesarEstadoFuegoSuelo(String msj) {
        String[] partes = msj.split(";");

        try {
            float x = Float.parseFloat(partes[1]);
            float y = Float.parseFloat(partes[2]);
            float alto = Float.parseFloat(partes[3]);
            boolean activo = Boolean.parseBoolean(partes[4]);

            if (estadoJefe != null) {
                estadoJefe.getFuegoSuelo().actualizarEstado(x, y, alto, activo);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de FuegoSuelo inválido");
        }
    }

    private synchronized void procesarEstadoRayos(String msj) {
        String[] partes = msj.split(";");

        try {
            Rayo.Fase fase = null;
            if (!partes[1].equals("NINGUNO")) {
                fase = Rayo.Fase.valueOf(partes[1]);
            }
            boolean mostrarAviso = Boolean.parseBoolean(partes[2]);
            float[] posiciones = new float[CANTIDAD_RAYOS];

            for (int i = 0; i < posiciones.length; i++) {
                posiciones[i] = Float.parseFloat(partes[i + 3]);
            }

            if (estadoJefe != null) {
                estadoJefe.getRayos().actualizarEstado(fase, mostrarAviso, posiciones);
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de rayos inválido");
        }
    }

    private synchronized void procesarEstadoLasers(String msj) {
        String[] partes = msj.split(";");

        try {
            Laser.Fase fase = null;
            if (!partes[1].equals("NINGUNO")) {
                fase = Laser.Fase.valueOf(partes[1]);
            }
            boolean mostrarAviso = Boolean.parseBoolean(partes[2]);
            boolean[] activos = new boolean[CANTIDAD_LASERS];

            for (int i = 0; i < activos.length; i++) {
                activos[i] = Boolean.parseBoolean(partes[i + 3]);
            }

            if (estadoJefe != null) {
                estadoJefe.getLasers().actualizarEstado(fase, mostrarAviso, activos);
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de láseres inválido");
        }
    }

    private synchronized void procesarCreacionMeteorito(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            Meteorito.Tipo tipo = Meteorito.Tipo.valueOf(partes[2]);
            float x = Float.parseFloat(partes[3]);
            float y = Float.parseFloat(partes[4]);

            if (buscarMeteorito(id) == null) {
                METEORITOS.add(new Meteorito(id, tipo, x, y));
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Creación de meteorito inválida");
        }
    }

    private synchronized void procesarEstadoMeteorito(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            Meteorito.Tipo tipo = Meteorito.Tipo.valueOf(partes[2]);
            float x = Float.parseFloat(partes[3]);
            float y = Float.parseFloat(partes[4]);
            boolean impacto = Boolean.parseBoolean(partes[5]);
            float rotacion = Float.parseFloat(partes[6]);
            float tiempoEfecto = Float.parseFloat(partes[7]);

            Meteorito meteorito = buscarMeteorito(id);

            if (meteorito == null) {
                meteorito = new Meteorito(id, tipo, x, y);
                METEORITOS.add(meteorito);
            }

            meteorito.actualizarEstado(y, impacto, rotacion, tiempoEfecto);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de meteorito inválido");
        }
    }

    private synchronized void procesarEliminacionMeteorito(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);

            METEORITOS.removeIf(m -> m.getId() == id);
            METEORITOS_ELIMINADOS.add(id);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Eliminación de meteorito inválida");
        }
    }

    private synchronized void procesarEstadoLaserJefe(String msj) {
        String[] partes = msj.split(";");

        try {
            boolean visible = Boolean.parseBoolean(partes[1]);
            boolean aviso = Boolean.parseBoolean(partes[2]);
            float angulo = Float.parseFloat(partes[3]);
            float tiempoAnimacion = Float.parseFloat(partes[4]);

            if (estadoJefe != null) {
                estadoJefe.getLaserJefe().actualizarEstado(visible, aviso, angulo, tiempoAnimacion);
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del láser del jefe inválido");
        }
    }

    private synchronized void procesarCreacionFueguito(String msj) {
        actualizarOCrearFueguito(msj, "Creación de fueguito inválida");
    }

    private synchronized void procesarEstadoFueguito(String msj) {
        actualizarOCrearFueguito(msj, "Estado de fueguito inválido");
    }

    private void actualizarOCrearFueguito(String msj, String mensajeError) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            Fueguito.Tipo tipo = Fueguito.Tipo.valueOf(partes[2]);
            float x = Float.parseFloat(partes[3]);
            float y = Float.parseFloat(partes[4]);
            float tamano = Float.parseFloat(partes[5]);
            float rotacion = Float.parseFloat(partes[6]);

            Fueguito fueguito = buscarFueguito(id, tipo);

            if (fueguito == null) {
                FUEGUITOS.add(new Fueguito(id, tipo, x, y, tamano, rotacion));
            } else {
                fueguito.actualizarEstado(x, y, tamano, rotacion);
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println(mensajeError);
        }
    }

    private synchronized void procesarEliminacionFueguito(String msj) {
        String[] partes = msj.split(";");

        try {
            int id = Integer.parseInt(partes[1]);
            Fueguito.Tipo tipo = Fueguito.Tipo.valueOf(partes[2]);

            FUEGUITOS.removeIf(f -> f.getId() == id && f.getTipo() == tipo);
            FUEGUITOS_ELIMINADOS.add(new Fueguito(id, tipo));
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Eliminación de fueguito inválida");
        }
    }

    private synchronized void procesarEstadoTumba(String msj) {
        String[] partes = msj.split(";");

        try {
            TUMBAS_RECIBIDAS.put(
                partes[1],
                new Tumba(partes[1], Float.parseFloat(partes[2]), Float.parseFloat(partes[3])));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de tumba inválido");
        }
    }

    public synchronized boolean hayReliquiaNueva() {
        boolean nueva = reliquiaNueva;
        reliquiaNueva = false;
        return nueva;
    }

    public synchronized boolean fueRecogidaLaReliquia() {
        boolean recogida = reliquiaRecogida;
        reliquiaRecogida = false;
        return recogida;
    }

    public float getReliquiaX() {
        return reliquiaX;
    }

    public float getReliquiaY() {
        return reliquiaY;
    }

    public synchronized ArrayList<Tumba> getEstadoTumbas() {
        ArrayList<Tumba> estados = new ArrayList<>(TUMBAS_RECIBIDAS.values());
        TUMBAS_RECIBIDAS.clear();
        return estados;
    }

    private void procesarSonido(String msj) {
        Gdx.app.postRunnable(() -> {
            try {
                String[] partes = msj.split(";");
                if (partes.length < 2) {
                    return;
                }

                String nombre = partes[1];
                String accion = partes.length >= 3 ? partes[2] : "";

                if (accion.equals("LOOP")) {
                    if (partes.length >= 5 && !estaCerca(partes, 3)) {
                        Sonido.detenerLoopPorNombre(nombre);
                    } else {
                        Sonido.reproducirLoopPorNombre(nombre);
                    }
                    return;
                }

                if (accion.equals("DETENER_LOOP")) {
                    if (partes.length < 5 || estaCerca(partes, 3)) {
                        Sonido.detenerLoopPorNombre(nombre);
                    }
                    return;
                }

                if (partes.length >= 4 && !estaCerca(partes, 2)) {
                    return;
                }

                Sonido.reproducirPorNombre(nombre);
            } catch (Throwable ignorada) {
            }
        });
    }

    private boolean estaCerca(String[] partes, int indice) {
        try {
            float sonidoX = Float.parseFloat(partes[indice]);
            float sonidoY = Float.parseFloat(partes[indice + 1]);
            return Math.abs(camaraX - sonidoX) <= DISTANCIA_MAXIMA_SONIDO
                && Math.abs(camaraY - sonidoY) <= DISTANCIA_MAXIMA_SONIDO;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private void procesarMusica(String msj) {
        Gdx.app.postRunnable(() -> {
            try {
                String[] partes = msj.split(";");
                if (partes.length < 2) {
                    return;
                }

                if ("PARAR".equals(partes[1])) {
                    Musica.parar();
                    return;
                }

                Musica.reproducirPorNombre(partes[1]);
            } catch (Throwable ignorada) {
            }
        });
    }

    private void procesarEstadoInvencibilidad(String msj) {
        String[] partes = msj.split(";");

        try {
            String personaje = partes[1];
            boolean invencible = Boolean.parseBoolean(partes[2]);
            boolean spriteVisible = true;
            if (partes.length > 3) {
                spriteVisible = Boolean.parseBoolean(partes[3]);
            }

            obtenerEstadoJugador(personaje).actualizarInvencibilidad(invencible, spriteVisible);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de invencibilidad recibido inválido");
        }
    }

    private Enemigo buscarEnemigo(int id) {
        for (Enemigo e : estadoEnemigos) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    private Proyectil buscarProyectil(int id) {
        for (Proyectil p : estadoProyectiles) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    private Meteorito buscarMeteorito(int id) {
        for (Meteorito meteorito : METEORITOS) {
            if (meteorito.getId() == id) {
                return meteorito;
            }
        }
        return null;
    }

    private Fueguito buscarFueguito(int id, Fueguito.Tipo tipo) {
        for (Fueguito fueguito : FUEGUITOS) {
            if (fueguito.getId() == id && fueguito.getTipo() == tipo) {
                return fueguito;
            }
        }
        return null;
    }

    private JugadorEstado obtenerEstadoJugador(String personaje) {
        if (personaje.equals(personajeRemoto)) {
            return estadoRemoto;
        }
        return estadoLocal;
    }

    public void enviarMensaje(String msj) {
        String mensajeFinal;
        if (Config.DEMO_MODE && !msj.startsWith("DEMO;")) {
            mensajeFinal = "DEMO;" + idCliente + ";" + msj;
        } else {
            mensajeFinal = msj;
        }
        byte[] data = mensajeFinal.getBytes();
        try {
            DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, PUERTO);
            socket.send(dp);
        } catch (SocketException ignorada) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarCamara(float x, float y) {
        this.camaraX = x;
        this.camaraY = y;
    }

    public void cerrar() {
        fin = true;

        try {
            if (socket != null && !socket.isClosed() && ipServer != null) {
                enviarMensaje("DESCONECTAR");
            }
        } catch (Exception ignorada) {
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignorada) {
        }
    }

    public boolean isConexionEstablecida() {
        return conexionEstablecida;
    }

    public boolean busquedaFallida() {
        return busquedaFallida;
    }

    public boolean encontroServidor() {
        return servidorEncontrado;
    }

    public String getPersonajeOcupado() {
        return personajeOcupado;
    }

    public String getPersonajeRemotoElegido() {
        return personajeRemotoElegido;
    }

    public int getContadorComenzar() {
        return contadorComenzar;
    }

    public synchronized boolean sePuedeSeleccionarNivel() {
        if (seleccionarNivel) {
            seleccionarNivel = false;
            return true;
        }
        return false;
    }

    public int getMiVotoNivel() {
        return votoLocalNivel;
    }

    public int getVotoRemotoNivel() {
        return votoRemotoNivel;
    }

    public synchronized int getNivelParaIniciar() {
        int nivel = nivelParaIniciar;
        nivelParaIniciar = 0;
        return nivel;
    }

    public Personajes getPersonajeRemoto() {
        if (personajeRemoto == null) {
            return null;
        }
        return Personajes.valueOf(personajeRemoto);
    }

    public JugadorEstado getEstadoLocal() {
        return estadoLocal;
    }

    public JugadorEstado getEstadoRemoto() {
        return estadoRemoto;
    }

    public synchronized ArrayList<Enemigo> getEnemigosRecibidos() {
        ArrayList<Enemigo> copia = new ArrayList<>(enemigosRecibidos);
        enemigosRecibidos.clear();
        return copia;
    }

    public synchronized ArrayList<Integer> getEnemigosEliminados() {
        ArrayList<Integer> copia = new ArrayList<>(enemigosEliminados);
        enemigosEliminados.clear();
        return copia;
    }

    public synchronized ArrayList<Proyectil> getProyectilesNuevos() {
        ArrayList<Proyectil> copia = new ArrayList<>(proyectilesNuevo);
        proyectilesNuevo.clear();
        return copia;
    }

    public synchronized ArrayList<Proyectil> getEstadoProyectiles() {
        return new ArrayList<>(estadoProyectiles);
    }

    public synchronized ArrayList<Integer> getProyectilesEliminados() {
        ArrayList<Integer> copia = new ArrayList<>(proyectilesEliminados);
        proyectilesEliminados.clear();
        return copia;
    }

    public synchronized void registrarEstadoEnemigo(Enemigo enemigo) {
        if (buscarEnemigo(enemigo.getId()) == null) {
            estadoEnemigos.add(enemigo);
        }
    }

    public synchronized void registrarEstadoProyectil(Proyectil orbe) {
        if (buscarProyectil(orbe.getId()) == null) {
            estadoProyectiles.add(orbe);
        }
    }

    public synchronized Retumbar getJefeNuevo() {
        Retumbar nuevo = jefeNuevo;
        jefeNuevo = null;
        return nuevo;
    }

    public synchronized ArrayList<Meteorito> getEstadoMeteoritos() {
        return new ArrayList<>(METEORITOS);
    }

    public synchronized ArrayList<Integer> getMeteoritosEliminados() {
        ArrayList<Integer> eliminados = new ArrayList<>(METEORITOS_ELIMINADOS);
        METEORITOS_ELIMINADOS.clear();
        return eliminados;
    }

    public synchronized ArrayList<Fueguito> getEstadoFueguitos() {
        return new ArrayList<>(FUEGUITOS);
    }

    public synchronized ArrayList<Fueguito> getFueguitosEliminados() {
        ArrayList<Fueguito> eliminados = new ArrayList<>(FUEGUITOS_ELIMINADOS);
        FUEGUITOS_ELIMINADOS.clear();
        return eliminados;
    }

    private synchronized void limpiarFueguitos() {
        FUEGUITOS.clear();
        FUEGUITOS_ELIMINADOS.clear();
    }

    private synchronized void limpiarAtaquesJefe() {
        METEORITOS.clear();
        limpiarFueguitos();
    }

    private synchronized void procesarPortal(String msj) {
        String[] partes = msj.split(";");

        try {
            if (partes[1].equals("APARECER")) {
                portalNuevo = new float[] {
                    Float.parseFloat(partes[2]),
                    Float.parseFloat(partes[3]),
                    Float.parseFloat(partes[4]),
                    Float.parseFloat(partes[5])
                };
            } else if (partes[1].equals("VOTO")) {
                esperandoPortal = Boolean.parseBoolean(partes[2]);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de portal inválido");
        }
    }

    private void procesarProgreso(String msj) {
        String[] partes = msj.split(";");

        try {
            synchronized (RELIQUIAS_RECOGIDAS) {
                for (int i = 0; i < CANTIDAD_RELIQUIAS; i++) {
                    RELIQUIAS_RECOGIDAS[i] = Boolean.parseBoolean(partes[i + 1]);
                }
            }
            nivel7Alcanzado = Boolean.parseBoolean(partes[CANTIDAD_RELIQUIAS + 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Progreso recibido inválido");
        }
    }

    public boolean[] getReliquiasRecogidas() {
        synchronized (RELIQUIAS_RECOGIDAS) {
            return RELIQUIAS_RECOGIDAS.clone();
        }
    }

    public boolean isNivel7Alcanzado() {
        return nivel7Alcanzado;
    }

    public synchronized float[] consumirPortalNuevo() {
        float[] portal = portalNuevo;
        portalNuevo = null;
        return portal;
    }

    public boolean isEsperandoPortal() {
        return esperandoPortal;
    }

    public boolean getOtroJugadorVaAlHub() {
        return otroJugadorVaAlHub;
    }

    public boolean getAmbosJugadoresVuelvenHub() {
        return ambosJugadoresVuelvenHub;
    }

    public void solicitarSnapshotEntidades() {
        limpiarEntidades();
        necesitaSnapshot = false;
        snapshotPendiente = true;
        enviarMensaje("SOLICITAR_SNAPSHOT");
    }

    public boolean necesitaSnapshot() {
        return necesitaSnapshot;
    }

    public boolean isExpulsado() {
        return expulsado;
    }

    public synchronized boolean consumirSeleccionCancelada() {
        boolean cancelada = seleccionCancelada;
        seleccionCancelada = false;
        return cancelada;
    }

    public int getSegundosReconexion() {
        return segundosReconexion;
    }

    public synchronized void limpiarEntidades() {
        snapshotPendiente = false;
        enemigosRecibidos.clear();
        estadoEnemigos.clear();
        enemigosEliminados.clear();
        proyectilesNuevo.clear();
        estadoProyectiles.clear();
        proyectilesEliminados.clear();
        TUMBAS_RECIBIDAS.clear();
        jefeNuevo = null;
        estadoJefe = null;
        METEORITOS.clear();
        METEORITOS_ELIMINADOS.clear();
        reliquiaNueva = false;
        reliquiaRecogida = false;
        reliquiaX = 0f;
        reliquiaY = 0f;
        limpiarFueguitos();
        portalNuevo = null;
        esperandoPortal = false;
        mostrarCreditos = false;
    }

    public void resetearVolverHub() {
        otroJugadorVaAlHub = false;
        ambosJugadoresVuelvenHub = false;
        otroJugadorCanceloHub = false;
    }

    public boolean getOtroJugadorCanceloHub() {
        return otroJugadorCanceloHub;
    }

    public synchronized boolean getMostrarCreditos() {
        boolean mostrar = mostrarCreditos;
        mostrarCreditos = false;
        return mostrar;
    }

    public synchronized boolean getEliminarAtaquesJefe() {
        boolean eliminar = eliminarAtaquesJefe;
        eliminarAtaquesJefe = false;
        return eliminar;
    }

    public boolean isServidorPerdido() {
        return servidorPerdido;
    }

    public boolean isJugadorRemotoDesconectado() {
        return jugadorRemotoDesconectado;
    }

    public void resetearJugadorRemotoDesconectado() {
        jugadorRemotoDesconectado = false;
    }

    public boolean isListoParaComenzar() {
        return listoParaComenzar;
    }

    public void setListoParaComenzar(boolean listo) {
        this.listoParaComenzar = listo;
    }

    public String getEstadoReconexion() {
        String estado = estadoReconexion;
        estadoReconexion = null;
        return estado;
    }

    public boolean isJugadorRemotoReconecto() {
        boolean reconecto = jugadorRemotoReconecto;
        jugadorRemotoReconecto = false;
        return reconecto;
    }

    public String getNivelReconexion() {
        String estado = nivelReconexion;
        nivelReconexion = null;
        return estado;
    }

    public boolean isConexionRechazada() {
        return conexionRechazada;
    }
}
