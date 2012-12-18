package ogo.spec.game.multiplayer.client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;
import ogo.spec.game.multiplayer.PeerInfo;
import ogo.spec.game.multiplayer.client.peer.*;
import ogo.spec.game.multiplayer.ChatProto.*;

/**
 * Client for network communication.
 */
class Client {

    public final static int PORT = 25665;
    public final static int INIT_PORT = 25345; // this is a UDP port
    public final static int INIT_LISTEN_PORT = 25344; // this is a UDP port
    public final static String BROADCAST_IP = "192.168.1.255";

    public final static int WAIT_SERVER_TIMEOUT = 1000;

    /**
     * Runnable for receiving UDP packets.
     */
    class DatagramReceiverRunnable implements Runnable
    {
        DatagramSocket sock;

        ConcurrentLinkedQueue<DatagramPacket> buffer = new ConcurrentLinkedQueue<DatagramPacket>();

        DatagramReceiverRunnable(DatagramSocket sock)
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

    /**
     * Thread to wait for a server connection.
     */
    class ServerConnectionListenRunnable implements Runnable
    {
        PeerServer server;

        ServerConnectionListenRunnable(PeerServer server)
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


    // API methods
    protected DatagramSocket udpSock;
    protected TokenChangeListener tokenChangeListener;
    protected PeerServer server;
    protected PeerClient client;

    /**
     * Constructor.
     */
    public Client() throws SocketException
    {
        udpSock = new DatagramSocket(INIT_LISTEN_PORT);
    }

    /**
     * Find servers on the network.
     */
    public List<PeerInfo> findServers() throws UnknownHostException, IOException, InterruptedException
    {
        sendBroadcast();

        return getServerList();
    }

    /**
     * Set the token change listener.
     */
    public void setTokenChangeListener(TokenChangeListener listener)
    {
        tokenChangeListener = listener;
    }

    /**
     * Connect to the given server, and wait for it to start the token ring.
     *
     * Please note that this method will only terminate when the connection is closes.
     * Thus, you should only call this from a thread that doesn't do anything else.
     */
    public void connect(PeerInfo serv) throws IOException, UnknownHostException, InterruptedException
    {
        InitServer init = new InitServer(serv.ip, serv.port);

        // find initialization port
        int port = init.getPort();

        // create a local server with the given port
        server = new PeerServer(port);

        // tell the server we got the message and started a server
        init.reply(true);

        // create a connection to the given peer
        PeerInfo info = init.getConnectTo();

        new Thread(new ServerConnectionListenRunnable(server)).start();

        client = new PeerClient(info);

        // wait until we are connected
        while (!server.isConnected()) {
            Thread.sleep(100);
        }

        init.reply(true);

        if (info.init) {
            init();
        }

        while (true) {
            // get the next token and give it to the TokenChangeListener
            Token token = tokenChangeListener.tokenChanged(getToken());

            sendToken(token);
        }
    }

    // protected methods
    /**
     * Broadcast to find servers.
     */
    protected void sendBroadcast() throws UnknownHostException, IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[]{1}, 1, InetAddress.getByName(BROADCAST_IP), INIT_PORT);
        udpSock.send(packet);
    }

    /**
     * Get the server list.
     */
    protected List<PeerInfo> getServerList() throws InterruptedException
    {
        DatagramReceiverRunnable run = new DatagramReceiverRunnable(udpSock);
        new Thread(run).start();

        Thread.sleep(WAIT_SERVER_TIMEOUT);
        DatagramPacket packet = new DatagramPacket(new byte[]{1}, 1);

        ArrayList<PeerInfo> peers = new ArrayList<PeerInfo>();

        while ((packet = run.buffer.poll()) != null) {
            peers.add(new PeerInfo(PORT, packet.getAddress(), false));
        }

        return peers;
    }

    /**
     * Get the token.
     */
    protected Token getToken() throws IOException
    {
        try {
            return Token.parseFrom(server.read());
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            return null;
        }
    }

    /**
     * Send the token.
     */
    protected void sendToken(Token token)
    {
        client.write(token);
    }

    /**
     * Send the token.
     */
    protected void init()
    {
        Token token = Token.newBuilder()
            .setLastId(0)
            .build();
        sendToken(token);
    }
}
