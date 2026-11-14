package com.prz.juego.hilos;

import com.prz.juego.entidades.enemigos.TipoEnemigo;
import com.prz.juego.entidades.jefes.Mano;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.red.*;
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
    private ArrayList<ProyectilRed> proyectilesNuevo = new ArrayList<>();
    private ArrayList<ProyectilRed> estadoProyectiles = new ArrayList<>();
    private ArrayList<Integer> proyectilesEliminados = new ArrayList<>();
    private JefeRed jefeNuevo;
    private JefeRed estadoJefe;
    private boolean jefeEliminado = false;
    private ManoRed manoIzquierda = new ManoRed(Mano.Lado.IZQUIERDA);
    private ManoRed manoDerecha = new ManoRed(Mano.Lado.DERECHA);
    private FuegoSueloRed fuegoSuelo = new FuegoSueloRed();

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
        } else if (msj.startsWith("CREAR_PROYECTIL"))
        {
            procesarCreacionProyectiles(msj);
        } else if (msj.startsWith("ESTADO_PROYECTIL"))
        {
            procesarEstadoProyectiles(msj);
        } else if (msj.startsWith("ELIMINAR_PROYECTIL"))
        {
            procesarEliminacionProyectiles(msj);
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
        } else if (msj.startsWith("CREAR_JEFE"))
        {
            procesarCreacionJefe(msj);
        } else if (msj.startsWith("ESTADO_JEFE"))
        {
            procesarEstadoJefe(msj);
        } else if (msj.startsWith("ELIMINAR_JEFE"))
        {
            procesarEliminacionJefe();
        } else if (msj.startsWith("ESTADO_MANO"))
        {
            procesarEstadoMano(msj);
        } else if (msj.startsWith("ESTADO_FUEGO_SUELO"))
        {
            procesarEstadoFuegoSuelo(msj);
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

    private synchronized void procesarCreacionProyectiles(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            String origen = mensajeParametrizado[2];
            float x = Float.parseFloat(mensajeParametrizado[3]);
            float y = Float.parseFloat(mensajeParametrizado[4]);
            float direccion = Float.parseFloat(mensajeParametrizado[5]);

            ProyectilRed nuevoProyectil = new ProyectilRed(id, origen, x, y, direccion);

            proyectilesNuevo.add(nuevoProyectil);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Parametros de creacion del proyectil invalido.");
        }
    }

    private synchronized void procesarEstadoProyectiles(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            float x = Float.parseFloat(mensajeParametrizado[2]);
            float y = Float.parseFloat(mensajeParametrizado[3]);

            ProyectilRed encontrado = buscarProyectil(id);

            if (encontrado != null)
            {
                encontrado.actualizarEstado(x, y);
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del proyectil invalido");
        }
    }

    private synchronized void procesarEliminacionProyectiles(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            int id = Integer.parseInt(mensajeParametrizado[1]);
            proyectilesEliminados.add(id);

            estadoProyectiles.removeIf(e -> e.getId() == id);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("ID de proyectil eliminado inválido");
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
        String[] mensajeParametrizado = msj.split(";");

        try {
            votoLocalNivel = Integer.parseInt(mensajeParametrizado[1]);
            votoRemotoNivel = Integer.parseInt(mensajeParametrizado[2]);
        } catch (NumberFormatException e) {
            System.out.println("Votos de nivel inválidos");
        }
    }

    private void procesarInicioNivel(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            nivelParaIniciar = Integer.parseInt(mensajeParametrizado[1]);
        } catch (NumberFormatException e) {
            System.out.println("Votos de nivel inválidos");
        }
    }

    private synchronized void procesarCreacionJefe(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            float x = Float.parseFloat(mensajeParametrizado[1]);
            float y = Float.parseFloat(mensajeParametrizado[2]);
            double vida = Double.parseDouble(mensajeParametrizado[3]);

            jefeNuevo = new JefeRed(x, y ,vida);
            estadoJefe = jefeNuevo;
            jefeEliminado = false;
        } catch (NumberFormatException e) {
            System.out.println("Parametros para la creacion del jefe invalidos");
        }
    }

    private synchronized void procesarEstadoJefe(String msj) {
        String[] mensajeParametrizado = msj.split(";");

        try {
            float x = Float.parseFloat(mensajeParametrizado[1]);
            float y = Float.parseFloat(mensajeParametrizado[2]);
            double vida = Double.parseDouble(mensajeParametrizado[3]);
            boolean visible = Boolean.parseBoolean(mensajeParametrizado[4]);
            float escala = Float.parseFloat(mensajeParametrizado[5]);

            if (estadoJefe != null) {
                estadoJefe.actualizarEstado(x, y, vida, visible, escala);
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado del jefe inválido");
        }
    }

    private synchronized void procesarEliminacionJefe() {
        jefeEliminado = true;
        estadoJefe = null;
        jefeNuevo = null;
    }

    private synchronized void procesarEstadoMano(String msj) {
        String[] mensajeParametrizado = msj.split(";");

        try {
            Mano.Lado lado = Mano.Lado.valueOf(mensajeParametrizado[1]);
            float x = Float.parseFloat(mensajeParametrizado[2]);
            float y = Float.parseFloat(mensajeParametrizado[3]);
            double vida = Double.parseDouble(mensajeParametrizado[4]);
            boolean visible = Boolean.parseBoolean(mensajeParametrizado[5]);
            float escala = Float.parseFloat(mensajeParametrizado[6]);

            Mano.EstadoAtaque estadoAtaque = Mano.EstadoAtaque.valueOf(mensajeParametrizado[7]);

            Mano.EstiloAtaque estiloAtaque = Mano.EstiloAtaque.valueOf(mensajeParametrizado[8]);

            boolean mostrarFuegoGolpe = Boolean.parseBoolean(mensajeParametrizado[9]);

            ManoRed mano = obtenerMano(lado);

            mano.actualizarEstado(x, y, vida, visible, escala, estadoAtaque, estiloAtaque, mostrarFuegoGolpe);

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Estado de mano inválido");
        }
    }

    private synchronized void procesarEstadoFuegoSuelo(String msj) {
        String[] mensajeParametrizado = msj.split(";");

        try {
            float x = Float.parseFloat(mensajeParametrizado[1]);
            float y = Float.parseFloat(mensajeParametrizado[2]);
            float alto = Float.parseFloat(mensajeParametrizado[3]);
            boolean activo = Boolean.parseBoolean(mensajeParametrizado[4]);

            fuegoSuelo.actualizarEstado(x, y, alto, activo);

        } catch (NumberFormatException e) {
            System.out.println("Estado de FuegoSuelo inválido");
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

    private ProyectilRed buscarProyectil(int id)
    {
        for(ProyectilRed o : estadoProyectiles)
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

    private ManoRed obtenerMano(Mano.Lado lado) {
        if (lado == Mano.Lado.IZQUIERDA) {
            return manoIzquierda;
        }

        return manoDerecha;
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

    public synchronized ArrayList<ProyectilRed> getProyectilesNuevos() {
        ArrayList<ProyectilRed> copia = new ArrayList<>(proyectilesNuevo);
        proyectilesNuevo.clear();
        return copia;
    }

    public synchronized ArrayList<ProyectilRed> getEstadoProyectiles() {
        return new ArrayList<>(estadoProyectiles);
    }

    public synchronized ArrayList<Integer> getProyectilesEliminados() {
        ArrayList<Integer> copia = new ArrayList<>(proyectilesEliminados);
        proyectilesEliminados.clear();
        return copia;
    }


    public synchronized void registrarEstadoEnemigo(EnemigoRed enemigo) {
        estadoEnemigos.add(enemigo);
    }

    public synchronized void registrarEstadoProyectil(ProyectilRed orbe) {
        if (buscarProyectil(orbe.getId()) == null) {
            estadoProyectiles.add(orbe);
        }
    }
    public synchronized JefeRed getJefeNuevo() {
        JefeRed nuevo = jefeNuevo;
        jefeNuevo = null;
        return nuevo;
    }

    public synchronized JefeRed getEstadoJefe() {
        return estadoJefe;
    }

    public synchronized boolean getJefeEliminado() {
        boolean eliminado = jefeEliminado;
        jefeEliminado = false;
        return eliminado;
    }

    public synchronized ManoRed getEstadoMano(Mano.Lado lado) {
        return obtenerMano(lado);
    }

    public synchronized FuegoSueloRed getEstadoFuegoSuelo()
    {
        return this.fuegoSuelo;
    }
}
