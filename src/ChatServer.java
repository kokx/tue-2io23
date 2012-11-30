import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import com.google.protobuf.ByteString;

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

                System.out.println("IP: " + p.getAddress().toString());

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

    // assuming MSB is first (Big Endian)
    static byte[] intToByteArray(int value)
    {
        return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16 & 0xFF),
            (byte)(value >>> 8 & 0xFF),
            (byte)(value & 0xFF)
        };
    }

    // represents a connection to a client
    class Client
    {
        Socket sock;

        OutputStream out;
        InputStream in;

        // reader
        ReadRun read;

        public Client(ServerSocket server) throws Exception
        {
            if ((sock = server.accept()) == null) {
                throw new Exception("Connection failed.");
            }

            out = sock.getOutputStream();
            in = sock.getInputStream();
        }

        /**
         * Get the IP address from this client.
         */
        byte[] getIp()
        {
            return sock.getInetAddress().getAddress();
        }

        // expect a reply
        void expectReply()
        {
            read = new ReadRun(in);
            new Thread(read).start();
        }

        boolean hasReply()
        {
            return read.wasRead;
        }

        void write(com.google.protobuf.GeneratedMessage message)
        {
            try {
                int len = message.toByteArray().length;
                byte[] length = intToByteArray(len);

                out.write(length);
                message.writeTo(out);
            } catch (IOException e) {
                System.err.println("I/O Error");
                System.exit(-1);
            }
        }
    }

    // represents the server
    class Server {

        ServerSocket sock = null;

        ArrayList<Client> clients = new ArrayList<Client>();

        /**
         * Constructor.
         */
        public Server(int port) throws IOException
        {
            sock = new ServerSocket(port);
        }

        /**
         * Wait for a client to connect.
         */
        public void connectClient() throws Exception
        {
            clients.add(new Client(sock));
        }

        public void waitClientReply() throws InterruptedException
        {
            // wait for all clients to have a reply
            boolean readAll = false;
            while (!readAll) {
                Thread.sleep(ChatServer.TIME_POLL);

                readAll = true;
                for (Client c : clients) {
                    readAll = readAll && c.hasReply();
                }
            }
        }

        /**
         * Initialize the token ring.
         */
        public void init(int initialPort) throws InterruptedException
        {
            // give everyone the proper port number
            for (int i = 0; i < clients.size(); i++) {
                // create the message
                ChatProto.Init init = ChatProto.Init.newBuilder()
                    .setPort(initialPort + i)
                    .build();
                clients.get(i).write(init);

                clients.get(i).expectReply();
            }

            waitClientReply();

            // link all clients
            for (int i = 0; i < clients.size(); i++) {
                ByteString ip = ByteString.copyFrom(clients.get((i + 1) % clients.size()).getIp());
                ChatProto.ConnectTo message = ChatProto.ConnectTo.newBuilder()
                    .setIp(ip)
                    .setPort((i + 1) % clients.size() + initialPort)
                    .setInit(i == 0)
                    .build();
                clients.get(i).write(message);

                clients.get(i).expectReply();
            }

            waitClientReply();
        }
    }

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
