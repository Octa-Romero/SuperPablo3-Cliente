package com.prz.juego.hilos;

import com.prz.juego.entidades.enemigos.TipoEnemigo;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.red.EnemigoRed;
import com.prz.juego.red.JugadorEstadoRed;
import com.prz.juego.red.BolaFuegoRed;
import com.prz.juego.utilidades.Global;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class HiloCliente extends Thread{

    private DatagramSocket socket;
    private boolean fin = false;
    private volatile boolean conexionEstablecida = false;
    private volatile boolean seleccionarNivel = false;
    private volatile int votoLocalNivel = 0;
    private volatile int votoRemotoNivel = 0;
    private volatile int nivelParaIniciar;
    private volatile String personajeRemoto;
    private InetAddress ipServer;
    private int puerto = 9990;
    private JugadorEstadoRed estadoLocal = new JugadorEstadoRed();
    private JugadorEstadoRed estadoRemoto = new JugadorEstadoRed();
    private ArrayList<EnemigoRed> enemigosRecibidos = new ArrayList<>();
    private ArrayList<EnemigoRed> estadoEnemigos = new ArrayList<>();
    private ArrayList<Integer> enemigosEliminados = new ArrayList<>();
    private ArrayList<BolaFuegoRed> orbesNuevos = new ArrayList<>();
    private ArrayList<BolaFuegoRed> estadoOrbes = new ArrayList<>();
    private ArrayList<Integer> orbesEliminados = new ArrayList<>();

    public HiloCliente()
    {
        try {
            socket = new DatagramSocket();
            ipServer = InetAddress.getByName("255.255.255.255");
            enviarMensaje("Conectar");
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run()
    {
        while(!fin)
        {
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try {
                socket.receive(dp);
                procesarMensaje(dp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msj = new String(dp.getData()).trim();
        if(msj.equals("Conexion recibida"))
        {
            ipServer = dp.getAddress();
        } else if(msj.equals("Conexion establecida"))
        {
            conexionEstablecida = true;
        }else if(msj.equals("Empieza juego"))
        {
            Global.empieza = true;
        }else if(msj.equals("PABLO") || msj.equals("WALTER")) {
            personajeRemoto = msj;
        } else if (msj.startsWith("POS")) {
            procesarPosicion(msj);
        } else if (msj.startsWith("ATAQUE"))
        {
            procesarEstadoAtaque(msj);
        } else if (msj.startsWith("VIDA"))
        {
            procesarEstadoVida(msj);
        } else if (msj.startsWith("CREAR_ENEMIGO"))
        {
            procesarCreacionEnemigos(msj);
        } else if (msj.startsWith("ESTADO_ENEMIGO"))
        {
            procesarEstadoEnemigos(msj);
        } else if (msj.startsWith("ELIMINAR_ENEMIGO"))
        {
            procesarEliminacionEnemigos(msj);
        } else if (msj.startsWith("CREAR_ORBE"))
        {
            procesarCreacionOrbes(msj);
        } else if (msj.startsWith("ESTADO_ORBE"))
        {
            procesarEstadoOrbes(msj);
        } else if (msj.startsWith("ELIMINAR_ORBE"))
        {
            procesarEliminacionOrbes(msj);
        } else if (msj.startsWith("CENTRO_HUB"))
        {
            procesarEstaEnCentroHub(msj);
        } else if (msj.startsWith("ESTADO_SELECCION"))
        {
            procesarEstadoSeleccion(msj);
        } else if (msj.equals("ABRIR_SELECCION_NIVEL"))
        {
            seleccionarNivel = true;
        } else if (msj.startsWith("VOTOS_NIVEL;")) {
            procesarVotosNivel(msj);
        } else if (msj.startsWith("INICIAR_NIVEL"))
        {
            procesarInicioNivel(msj);
        }
    }

    private void procesarPosicion(String msj) {
        String[] mensajeParametrizado = msj.split(";");

        try {
            String personaje = mensajeParametrizado[1];
            float x = Float.parseFloat(mensajeParametrizado[2]);
            float y = Float.parseFloat(mensajeParametrizado[3]);
            int direccion = Integer.parseInt(mensajeParametrizado[4]);
            boolean estaEnSuelo = Boolean.parseBoolean(mensajeParametrizado[5]);
            boolean estaTrepando = Boolean.parseBoolean(mensajeParametrizado[6]);

            JugadorEstadoRed estado = obtenerEstadoJugador(personaje);

            estado.actualizarPosicion(x, y, direccion, estaEnSuelo, estaTrepando);
        } catch (NumberFormatException e) {
            System.out.println("Posición recibida inválida");
        }
    }

    private void procesarEstadoAtaque(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            String personaje = mensajeParametrizado[1];
            boolean estadoAtaque = Boolean.parseBoolean(mensajeParametrizado[2]);

            JugadorEstadoRed estado = obtenerEstadoJugador(personaje);

            estado.actualizarAtaque(estadoAtaque);
        } catch (NumberFormatException e) {
            System.out.println("Estado del ataque recibido invalido");
        }
    }

    private void procesarEstadoVida(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            String personaje = mensajeParametrizado[1];
            double vidaRecibida = Double.parseDouble(mensajeParametrizado[2]);

            JugadorEstadoRed estado = obtenerEstadoJugador(personaje);

            estado.actualizarVida(vidaRecibida);
        } catch (NumberFormatException e) {
            System.out.println("Valor de vida invalido");
        }
    }

    private synchronized void procesarEstadoEnemigos(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            float x = Float.parseFloat(mensajeParametrizado[2]);
            float y = Float.parseFloat(mensajeParametrizado[3]);
            int direccion = Integer.parseInt(mensajeParametrizado[4]);
            boolean estaAtacando = Boolean.parseBoolean(mensajeParametrizado[5]);
            double vida = Double.parseDouble(mensajeParametrizado[6]);

            EnemigoRed encontrado = buscarEnemigo(id);

            if (encontrado != null)
            {
                encontrado.actualizarEstado(x, y, direccion, estaAtacando, vida);
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del enemigo invalido");
        }
    }

    private synchronized void procesarCreacionEnemigos(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            TipoEnemigo tipo = TipoEnemigo.valueOf(mensajeParametrizado[2]);
            float x = Float.parseFloat(mensajeParametrizado[3]);
            float y = Float.parseFloat(mensajeParametrizado[4]);
            double vida = Double.parseDouble(mensajeParametrizado[5]);

            EnemigoRed nuevoEnemigo = new EnemigoRed(id, tipo, x , y, vida);

            enemigosRecibidos.add(nuevoEnemigo);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Parametros de creacion del enemigo invalido");
        }
    }

    private synchronized void procesarEliminacionEnemigos(String msj) {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            enemigosEliminados.add(id);

            estadoEnemigos.removeIf(e -> e.getId() == id);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("ID de enemigo eliminado inválido");
        }
    }

    private synchronized void procesarCreacionOrbes(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            float x = Float.parseFloat(mensajeParametrizado[2]);
            float y = Float.parseFloat(mensajeParametrizado[3]);
            int direccion = Integer.parseInt(mensajeParametrizado[4]);

            BolaFuegoRed nuevoOrbe = new BolaFuegoRed(id, x, y, direccion);

            orbesNuevos.add(nuevoOrbe);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Parametros de creacion del orbe invalido.");
        }
    }

    private synchronized void procesarEstadoOrbes(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            float x = Float.parseFloat(mensajeParametrizado[2]);
            float y = Float.parseFloat(mensajeParametrizado[3]);

            BolaFuegoRed encontrado = buscarOrbe(id);

            if (encontrado != null)
            {
                encontrado.actualizarEstado(x, y);
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del orbe invalido");
        }
    }

    private synchronized void procesarEliminacionOrbes(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            orbesEliminados.add(id);

            estadoOrbes.removeIf(e -> e.getId() == id);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("ID de orbe eliminado inválido");
        }
    }

    public void procesarEstaEnCentroHub(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            boolean estaEnCentro = Boolean.parseBoolean(mensajeParametrizado[1]);

            estadoLocal.actualizarCentroHub(estaEnCentro);
        } catch (NumberFormatException e) {
            System.out.println("Estado del centro Hub invalido");
        }
    }

    public void procesarEstadoSeleccion(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            boolean estadoSeleccion = Boolean.parseBoolean(mensajeParametrizado[1]);

            estadoLocal.actualizarEstadoSeleccion(estadoSeleccion);
        } catch (NumberFormatException e) {
            System.out.println("Estado del centro Hub invalido");
        }
    }

    private void procesarVotosNivel(String msj) {
        String[] datos = msj.split(";");

        try {
            votoLocalNivel = Integer.parseInt(datos[1]);
            votoRemotoNivel = Integer.parseInt(datos[2]);
        } catch (NumberFormatException e) {
            System.out.println("Votos de nivel inválidos");
        }
    }

    private void procesarInicioNivel(String msj)
    {
        String[] datos = msj.split(";");

        try {
            nivelParaIniciar = Integer.parseInt(datos[1]);
        } catch (NumberFormatException e) {
            System.out.println("Votos de nivel inválidos");
        }
    }

    private EnemigoRed buscarEnemigo(int id)
    {
        for(EnemigoRed e : estadoEnemigos)
        {
            if(e.getId() == id)
            {
                return e;
            }
        }
        return null;
    }

    private BolaFuegoRed buscarOrbe(int id)
    {
        for(BolaFuegoRed o : estadoOrbes)
        {
            if(o.getId() == id)
            {
                return o;
            }
        }
        return null;
    }

    private JugadorEstadoRed obtenerEstadoJugador(String personaje) {
        if(personaje.equals(personajeRemoto))
        {
            return estadoRemoto;
        } else
        {
            return estadoLocal;
        }
    }

    public void enviarMensaje(String msj)
    {
        byte[] data = msj.getBytes();
        try {
            DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, puerto);
            socket.send(dp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cerrar() {
        fin = true;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public boolean isConexionEstablecida() {
        return conexionEstablecida;
    }

    public synchronized boolean sePuedeSeleccionarNivel()
    {
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

    public Personajes getPersonajeRemoto()
    {
        return Personajes.valueOf(personajeRemoto);
    }

    public JugadorEstadoRed getEstadoLocal() {
        return estadoLocal;
    }

    public JugadorEstadoRed getEstadoRemoto() {
        return estadoRemoto;
    }

    public synchronized ArrayList<EnemigoRed> getEnemigosRecibidos() {
        ArrayList<EnemigoRed> copia = new ArrayList<>(enemigosRecibidos);
        enemigosRecibidos.clear();
        return copia;
    }

    public synchronized ArrayList<Integer> getEnemigosEliminados() {
        ArrayList<Integer> copia = new ArrayList<>(enemigosEliminados);
        enemigosEliminados.clear();
        return copia;
    }

    public synchronized ArrayList<EnemigoRed> getEstadoEnemigos() {
        return new ArrayList<>(estadoEnemigos);
    }

    public synchronized ArrayList<BolaFuegoRed> getOrbesRecibidos() {
        ArrayList<BolaFuegoRed> copia = new ArrayList<>(orbesNuevos);
        orbesNuevos.clear();
        return copia;
    }

    public synchronized ArrayList<Integer> getOrbesEliminados() {
        ArrayList<Integer> copia = new ArrayList<>(orbesEliminados);
        orbesEliminados.clear();
        return copia;
    }

    public synchronized ArrayList<BolaFuegoRed> getEstadoOrbes() {
        return new ArrayList<>(estadoOrbes);
    }


    public synchronized void registrarEstadoEnemigo(EnemigoRed enemigo) {
        estadoEnemigos.add(enemigo);
    }

    public synchronized void registrarEstadoOrbe(BolaFuegoRed orbe) {
        if (buscarOrbe(orbe.getId()) == null) {
            estadoOrbes.add(orbe);
        }
    }
}
