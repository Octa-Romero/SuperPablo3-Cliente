package com.prz.juego.hilos;

import com.prz.juego.red.Cliente;

import java.io.IOException;
import java.net.*;

public class HiloCliente extends Thread {

    private Cliente cliente;
    private DatagramSocket socket;
    private volatile boolean fin = false;

    private InetAddress ipServer;
    private int puerto = 9993;

    private volatile boolean conectado = false;
    private volatile boolean partidaIniciada = false;
    private volatile long ultimoPong = 0;

    private static final long INTERVALO_PING = 1000;
    private static final long TIEMPO_LIMITE = 5000;

    public HiloCliente(Cliente cliente) {
        try {
            this.cliente = cliente;
            socket = new DatagramSocket();
            socket.setSoTimeout(100);

            ipServer = InetAddress.getByName("192.168.1.34");

        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        long ultimoPingEnviado = 0;
        long ultimoIntentoConexion = 0;

        while (!fin) {
            long ahora = System.currentTimeMillis();

            // Intentar conectar o reconectar.
            if (!conectado && ahora - ultimoIntentoConexion >= 2000) {
                enviarMensaje("Conectar");
                ultimoIntentoConexion = ahora;
            }

            // Enviar PING para mantener la conexión.
            if (conectado && ahora - ultimoPingEnviado >= INTERVALO_PING) {
                enviarMensaje("PING");
                ultimoPingEnviado = ahora;
            }

            // Detectar si el servidor dejó de responder.
            if (conectado && ahora - ultimoPong >= TIEMPO_LIMITE) {
                conectado = false;
                partidaIniciada = false;

                System.out.println("Se perdió la conexión con el servidor");
            }

            byte[] datos = new byte[1024];
            DatagramPacket dp = new DatagramPacket(datos, datos.length);

            try {
                socket.receive(dp);
                procesarMensaje(dp);
            } catch (SocketTimeoutException e) {
                // No llegó ningún mensaje; continúa el bucle.
            } catch (IOException e) {
                if (!fin) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msj = new String(
            dp.getData(),
            dp.getOffset(),
            dp.getLength()
        );

        // Aceptar mensajes solamente del servidor configurado.
        if (!dp.getAddress().equals(ipServer)
            || dp.getPort() != puerto) {
            return;
        }

        System.out.println("Mensaje del servidor: " + msj);

        if (msj.equals("Conexion recibida")) {
            conectado = true;
            ultimoPong = System.currentTimeMillis();

        } else if (msj.equals("PONG")) {
            ultimoPong = System.currentTimeMillis();

        } else if (msj.equals("Empezarchat")) {
            conectado = true;
            partidaIniciada = true;
            ultimoPong = System.currentTimeMillis();

            cliente.empezarChat(true);

        } else if (msj.equals("Sala llena")) {
            conectado = false;
            System.out.println("La sala está llena");

        } else if (msj.contains("desconectado")) {
            System.out.println(msj);

        } else {
            System.out.println(msj);
        }
    }

    public void enviarMensaje(String msj) {
        if (socket == null || socket.isClosed()) {
            return;
        }

        byte[] datos = msj.getBytes();

        try {
            DatagramPacket dp = new DatagramPacket(
                datos, datos.length, ipServer, puerto
            );

            socket.send(dp);

        } catch (IOException e) {
            if (!fin) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConectado() {
        return conectado;
    }

    public boolean isPartidaIniciada() {
        return partidaIniciada;
    }

    public void detener() {
        fin = true;
        socket.close();
    }
}
