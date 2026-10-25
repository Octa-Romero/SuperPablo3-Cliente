package com.prz.juego.hilos;

import com.prz.juego.red.Cliente;
import com.prz.juego.utilidades.Global;

import java.io.IOException;
import java.net.*;

public class HiloCliente extends Thread{

    private Cliente cliente;
    private DatagramSocket socket;
    private boolean fin = false;
    private volatile boolean conexionEstablecida = false;
    private volatile String personajeRemoto;
    private InetAddress ipServer;
    private int puerto = 9990;
    private float xRecibida, yRecibida;
    private boolean posicionRecibida = false;

    public HiloCliente(Cliente cliente)
    {
        try {
            this.cliente = cliente;
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
        System.out.println(msj);
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
        }
    }

    private void procesarPosicion(String msj)
    {
        String[] mensajeParametrizado = msj.split(";");

        try {
            posicionRecibida = true;
            xRecibida = Float.parseFloat(mensajeParametrizado[1]);
            yRecibida = Float.parseFloat(mensajeParametrizado[2]);
        } catch (NumberFormatException e) {
            System.out.println("Posición recibida inválida");
        }
    }

    public void enviarMensaje(String msj)
    {
        //System.out.println("Enviado: " + msj);
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

    public String getPersonajeRemoto()
    {
        return this.personajeRemoto;
    }

    public float getXRecibida() {
        return xRecibida;
    }

    public float getYRecibida() {
        return yRecibida;
    }

    public boolean isPosicionRecibida()
    {
        return this.posicionRecibida;
    }
}
