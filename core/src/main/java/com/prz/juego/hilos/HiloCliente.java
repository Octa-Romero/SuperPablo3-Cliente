package com.prz.juego.hilos;

import java.io.IOException;
import java.net.*;

public class HiloCliente extends Thread{

    private DatagramSocket socket;
    private boolean fin = false;
    private InetAddress ipServer;
    private int puerto =  9992;

    public HiloCliente()
    {
        try {
            socket = new DatagramSocket();
            ipServer = InetAddress.getByName("192.168.0.15");
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run()
    {
        while(!fin){
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try {
                socket.receive(dp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void procesarMensaje(DatagramPacket dp)
    {
        String msj = new String(dp.getData()).trim();
        System.out.println(msj);
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
}
