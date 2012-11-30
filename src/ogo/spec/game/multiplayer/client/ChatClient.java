package ogo.spec.game.multiplayer.client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;

/**
 * Read input into a buffer.
 */
class InputReaderRunnable implements Runnable
{
    BufferedReader in;

    ConcurrentLinkedQueue<String> buffer = new ConcurrentLinkedQueue<String>();

    InputReaderRunnable(InputStream in)
    {
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    public void run()
    {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                buffer.add(line);
            }
        } catch (IOException e) {
            System.err.println("I/O Error");
            System.exit(1);
        }
    }
}

/**
 * Thread to wait for a server connection.
 */
class ServerConnectionListenRunnable implements Runnable
{
    Server server;

    ServerConnectionListenRunnable(Server server)
    {
        this.server = server;
    }

    public void run()
    {
        try {
            server.accept();
        } catch (IOException e) {
            System.err.println("I/O not working");
            System.exit(-1);
        }
    }
}

/**
 * Runnable for receiving UDP packets.
 */
class PacketReceiverRunnable implements Runnable
{
    DatagramSocket sock;

    ConcurrentLinkedQueue<DatagramPacket> buffer = new ConcurrentLinkedQueue<DatagramPacket>();

    PacketReceiverRunnable(DatagramSocket sock)
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
            }
        } catch (IOException e) {
            System.err.println("I/O Error");
            System.exit(1);
        }
    }
}

class ChatClient {

    public final static int PORT = 25665;
    public final static int INIT_PORT = 25345; // this is a UDP port
    public final static int INIT_LISTEN_PORT = 25344; // this is a UDP port
    public final static String BROADCAST_IP = "192.168.1.255";

    void connect(InetAddress serverIp) throws IOException, InterruptedException
    {
        InitServer init = null;
        try {
            init = new InitServer(serverIp, PORT);
        } catch (UnknownHostException e) {
            // error
            System.err.println("CANNOT CONNECT");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }

        int port = init.getPort();

        // setup a server with the given port
        Server server = new Server(port);
        Client client;

        // tell the server we got the message and started a server
        init.reply(true);

        // create a connection to the given peer
        PeerInfo info = init.getConnectTo();

        //System.err.println("Port: " + port);
        System.err.println("Connected to: ");
        System.err.println("IP: " + info.ip.toString() + " Port: " + info.port);

        // get the thread
        new Thread(new ServerConnectionListenRunnable(server)).start();

        client = new Client(info);

        // wait until connected
        while (!server.connected) {
            Thread.sleep(100);
        }

        // tell the init server we're done
        init.reply(true);

        // start the chat
        Chat chat = new Chat(server, client);

        if (info.init) {
            chat.init();
        }

        while (true) {
            chat.chat();
        }
    }

    void run() throws IOException, InterruptedException
    {
        Scanner sc = new Scanner(System.in);

        InetAddress broadcast = InetAddress.getByName(BROADCAST_IP);

        DatagramSocket sock = new DatagramSocket(INIT_LISTEN_PORT);

        // just broadcast a 1
        byte[] data = new byte[1];
        data[0] = 1;

        DatagramPacket packet = new DatagramPacket(data, 1, broadcast, INIT_PORT);
        sock.send(packet);

        // check if there is a reply
        PacketReceiverRunnable packetReceiver = new PacketReceiverRunnable(sock);
        new Thread(packetReceiver).start();

        Thread.sleep(1000);

        ArrayList<InetAddress> ips = new ArrayList<InetAddress>();

        System.out.println("The following servers are available:");

        while ((packet = packetReceiver.buffer.poll()) != null) {
            ips.add(packet.getAddress());
            System.out.println("IP: (" + ips.size() + ") " + packet.getAddress().toString());
        }

        System.out.println("Please type in the server number to which you want to connect:");

        int num = sc.nextInt();

        connect(ips.get(num - 1));
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        new ChatClient().run();
    }
}
