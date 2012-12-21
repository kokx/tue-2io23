package ogo.spec.game.multiplayer.initserver;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import com.google.protobuf.ByteString;

import ogo.spec.game.multiplayer.*;

/**
 * UDP thread
 */
class BroadcastReceiverRunnable implements Runnable
{
    DatagramSocket sock;

    ConcurrentLinkedQueue<DatagramPacket> buffer = new ConcurrentLinkedQueue<DatagramPacket>();

    BroadcastReceiverRunnable(DatagramSocket sock)
    {
        this.sock = sock;
    }

    public void run()
    {
        try {
            while (true) {
                DatagramPacket p = new DatagramPacket(new byte[1], 1);

                sock.receive(p);

                buffer.add(p);

                System.err.println(p.getAddress().toString());

                // simply send a packet back
                byte[] data = new byte[1];
                data[0] = 1;
                DatagramPacket c = new DatagramPacket(data, 1, p.getAddress(), p.getPort());

                sock.send(c);
            }
        } catch (IOException e) {
            System.err.println("I/O Error");
            System.exit(1);
        }
    }
}

class ConnectClients implements Runnable{
    Server server = null;
    
    public boolean shouldConnect;
    
    public ConnectClients(int port) throws IOException{
        server = new Server(port);
        shouldConnect = true;
    }
    public void run(){
        for (int i = 0; i < ChatServer.MAX_CLIENTS && shouldConnect; i++) {
            try{
                server.connectClient();
            }catch (Exception e) {
                // nothing
            }
        }
    }
}

public class ChatServer {

    public final static int PORT = 25665;
    public final static int INIT_PORT = 25344; // this is a UDP port
    public final static int INIT_LISTEN_PORT = 25345; // this is a UDP port
    public final static int MAX_CLIENTS = 2;
    public final static int TIME_POLL = 100;

    // real stuff
    private ConnectClients connect = null;
    
    public void initConnection() throws InterruptedException{
        connect.server.init(PORT+1);
    }

    public void run() throws Exception, IOException
    {
        DatagramSocket sock = new DatagramSocket(INIT_LISTEN_PORT);

        BroadcastReceiverRunnable run = new BroadcastReceiverRunnable(sock);

        new Thread(run).start();
        
        connect = new ConnectClients(PORT);

        new Thread(connect).start();
    }

    public static void main(String args[]) throws Exception, IOException, InterruptedException {
        new ChatServer().run();
    }
}
