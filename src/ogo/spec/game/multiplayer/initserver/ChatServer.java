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

class ChatServer {

    public final static int PORT = 25665;
    public final static int INIT_PORT = 25344; // this is a UDP port
    public final static int INIT_LISTEN_PORT = 25345; // this is a UDP port
    public final static int MAX_CLIENTS = 2;
    public final static int TIME_POLL = 100;

    // real stuff
    ServerSocket server = null;
    Socket clients[] = new Socket[MAX_CLIENTS];
    int lastClient = 0;

    OutputStream[] clientOut;
    InputStream[] clientIn;

    void run() throws Exception, IOException, InterruptedException
    {
        DatagramSocket sock = new DatagramSocket(INIT_LISTEN_PORT);

        BroadcastReceiverRunnable run = new BroadcastReceiverRunnable(sock);

        new Thread(run).start();

        // create a server
        Server server = new Server(PORT);

        // wait for MAX_CLIENTS clients to connect
        for (int i = 0; i < MAX_CLIENTS; i++) {
            server.connectClient();
        }

        // initialize the token ring and terminate
        server.init(PORT + 1);
    }

    public static void main(String args[]) throws Exception, IOException, InterruptedException {
        new ChatServer().run();
    }
}
