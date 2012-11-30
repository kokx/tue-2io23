import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

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

    // assuming MSB is first (Big Endian)
    static int byteArrayToInt(byte[] array)
    {
        return java.nio.ByteBuffer.wrap(array).getInt();
    }

    /**
     * Try to obtain the current IP address(es).
     */
    public static List<InetAddress> getLocalIps()
    {
        ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();

            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();

                Enumeration<InetAddress> adrs = nif.getInetAddresses();

                // FIXME: we should not broadcast on loopback!
                if (/*!nif.isLoopback() &&*/ nif.isUp() && !nif.isVirtual()) {
                    while (adrs.hasMoreElements()) {
                        InetAddress adr = adrs.nextElement();

                        // FIXME: we should not broadcast on loopback!
                        if (adr != null/* && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress())*/) {
                            ips.add(adr);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("No IP found");
            System.exit(-1);
        }

        return ips;
    }

    class PeerInfo
    {
        int port;
        InetAddress ip;
        boolean init;
    }

    abstract class Peer
    {
        Socket sock;

        DataInputStream in;
        OutputStream out;

        protected void initIO() throws IOException
        {
            in = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
            out = sock.getOutputStream();
        }

        /**
         * Write a message.
         */
        void write(com.google.protobuf.GeneratedMessage message)
        {
            try {
                int len = message.toByteArray().length;
                byte[] length = intToByteArray(len);

                out.write(length);
                message.writeTo(out);
            } catch (IOException e) {
                System.out.println("I/O Error");
                System.exit(-1);
            }
        }

        /**
         * Read a message.
         */
        byte[] read() throws IOException
        {
            byte[] input = new byte[4];
            in.readFully(input, 0, 4);

            int len = byteArrayToInt(input);

            byte[] data = new byte[len];
            in.readFully(data, 0, len);
            return data;
        }
    }

    class InitServer extends Peer
    {

        InitServer(String ip, int port) throws UnknownHostException, IOException
        {
            sock = new Socket(InetAddress.getByName(ip), port);
            initIO();
        }

        /**
         * Send a reply.
         */
        void reply(boolean reply)
        {
            ChatProto.Reply rep = ChatProto.Reply.newBuilder()
                .setDone(reply)
                .build();
            write(rep);
        }

        /**
         * Get the port on which we need to setup a server.
         */
        int getPort() throws IOException
        {
            return ChatProto.Init.parseFrom(read()).getPort();
        }

        /**
         * Get the info of the peer we need to connect to.
         */
        PeerInfo getConnectTo() throws IOException
        {
            ChatProto.ConnectTo connectTo = ChatProto.ConnectTo.parseFrom(read());
            PeerInfo peer = new PeerInfo();
            peer.port = connectTo.getPort();
            peer.init = connectTo.getInit();
            peer.ip = InetAddress.getByAddress(connectTo.getIp().toByteArray());
            return peer;
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
     * Server for p2p communication.
     */
    class Server extends Peer
    {
        ServerSocket serverSock = null;
        boolean connected = false;

        Server(int port) throws IOException
        {
            serverSock = new ServerSocket(port);
        }

        boolean accept() throws IOException
        {
            boolean ret = (sock = serverSock.accept()) != null;
            initIO();
            connected = true;
            return ret;
        }
    }

    /**
     * Client for p2p communication.
     */
    class Client extends Peer
    {
        PeerInfo peer;

        Client(PeerInfo peer) throws UnknownHostException, IOException
        {
            this.peer = peer;
            sock = new Socket(peer.ip, peer.port);
            initIO();
        }
    }

    /**
     * Chat.
     */
    class Chat
    {
        Peer prev;
        Peer next;

        InputReaderRunnable inputReader;

        int nextId = 0;

        Chat(Peer prev, Peer next)
        {
            this.prev = prev;
            this.next = next;
            inputReader = new InputReaderRunnable(System.in);
            new Thread(inputReader).start();
        }

        ChatProto.Token getToken() throws IOException
        {
            return ChatProto.Token.parseFrom(prev.read());
        }

        /**
         * Copy the token.
         */
        ChatProto.Token.Builder copyToken(ChatProto.Token token)
        {
            ChatProto.Token.Builder builder = ChatProto.Token.newBuilder();

            builder.mergeFrom(token);

            return builder;
        }

        public void init()
        {
            ChatProto.Token token = ChatProto.Token.newBuilder()
                .setLastId(0)
                .build();
            next.write(token);
        }

        /**
         * Get messages from the input reader.
         */
        public Iterable<ChatProto.Token.Message> getMessages()
        {
            LinkedList<ChatProto.Token.Message> messages = new LinkedList<ChatProto.Token.Message>();

            String line;
            while ((line = inputReader.buffer.poll()) != null) {
                ChatProto.Token.Message message = ChatProto.Token.Message.newBuilder()
                    .setId(nextId)
                    .setName("kokx")
                    .setMessage(line)
                    .build();
                messages.add(message);
                nextId++;
            }

            return messages;
        }

        public void chat() throws IOException
        {
            ChatProto.Token token = getToken();

            // get all messages
            List<ChatProto.Token.Message> messages = token.getMessageList();

            for (ChatProto.Token.Message message : messages) {
                if (message.getId() >= nextId) {
                    System.out.println(message.getName() + ": " + message.getMessage());
                    nextId = message.getId() + 1;
                }
            }

            // build a new token, and send it
            ChatProto.Token.Builder builder = copyToken(token);

            builder.addAllMessage(getMessages());

            builder.setLastId(nextId);

            // write the message
            next.write(builder.build());
        }
    }

    void connect(String serverIp) throws IOException, InterruptedException
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
        // we will simply broadcast to 255.255.255.255
        // this might not be the best thing, find that out later

        InetAddress broadcast = InetAddress.getByName("192.168.1.255");

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

        while ((packet = packetReceiver.buffer.poll()) != null) {
            System.out.println("IP: " + packet.getAddress().toString());
        }
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        new ChatClient().run();
        //new ChatClient().connect(args[0]);
    }
}
