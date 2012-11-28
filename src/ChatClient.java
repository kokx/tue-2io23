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

    ConcurrentLinkedQueue<String> buffer;

    InputReaderRunnable(InputStream in)
    {
        this.in = new BufferedReader(new InputStreamReader(in));
        buffer = new ConcurrentLinkedQueue<String>();
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

class ChatClient {

    public final static int PORT = 25665;

    long tokenCount = 0L;
    long starttime = 0L;

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

            // fix the token stuff
            tokenCount++;
            if ((tokenCount % 1001L) == 0) {
                int time = (int) ((System.currentTimeMillis() - starttime) / 1000L);
                if (time != 0) {
                    System.out.println("T/S: " + tokenCount / time);
                }
            }
        }
    }

    void run(String serverIp) throws IOException, InterruptedException
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

        starttime = System.currentTimeMillis();
        while (true) {
            chat.chat();
        }
    }

    /**
     * Try to obtain the current IP address.
     */
    public static InetAddress getLocalIp()
    {
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();

            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();

                Enumeration<InetAddress> adrs = nif.getInetAddresses();

                if (!nif.isLoopback() && nif.isUp() && !nif.isVirtual()) {
                    while (adrs.hasMoreElements()) {
                        InetAddress adr = adrs.nextElement();

                        if (adr != null && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress())) {
                            return adr;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("No IP found");
            System.exit(-1);
        }
        return null;
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        //System.err.println("IP: " + getLocalIp().toString());
        new ChatClient().run(args[0]);
    }
}
