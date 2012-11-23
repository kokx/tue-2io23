import java.io.*;
import java.util.*;
import java.net.*;

class ChatClient {

    public final static int PORT = 25665;
    
    static byte[] intToByteArray(int value)
    {
        return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value
        };
    }
    
    void writeToOutputStream(com.google.protobuf.GeneratedMessage message, OutputStream out)
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
    
    void run(String serverip) throws IOException, InterruptedException
    {
        Socket s = null;
        OutputStream out = null;
        InputStream in = null;
        
        try {
            s = new Socket(InetAddress.getByName(serverip), PORT);
            out = s.getOutputStream();
            in = s.getInputStream();
        } catch (UnknownHostException e) {
            // error
            System.err.println("CANNOT CONNECT");
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }
        
        System.out.println("Server connected");
        
        System.out.println("Reading Port Info from Server now");
        ReadRun read = new ReadRun(in);
        new Thread(read).start();
        
        while(!read.wasRead){
            Thread.sleep(100);
        }
        
        int serverPort = ChatProto.Init.parseFrom(read.data).getPort();
        ServerSocket server = null;
        
        System.out.println("Setting up Server for Peer-to-Peer now");
        try {
            server = new ServerSocket(serverPort);
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }
        
        ChatProto.Reply reply = ChatProto.Reply.newBuilder().setDone(true).build();
        
        System.out.println("Writing Acknowledge now");
        writeToOutputStream(reply, out);
        
        System.out.println("Reading Connection info now");
        read = new ReadRun(in);
        new Thread(read).start();
        
        while(!read.wasRead){
            Thread.sleep(100);
        }
        
        ChatProto.ConnectTo info = ChatProto.ConnectTo.parseFrom(read.data);
        
        byte[] ip = info.getIp().toByteArray();
        int port = info.getPort();
        boolean init = info.getInit();
        
        System.out.println("Received Connection Info Now");
        
        /* Potentieel Probleem: Als new Socket() en server.accept() allebei 
         *              Blocking Calls zijn, dan krijg je nooit een verbinding.
         *              Ik weet alleen niet of dat allebei zo is.
         */
        
        // The next person. You only send to him (and maybe receive aknowledge)
        Socket next;
        InputStream next_in;
        OutputStream next_out;
        
        try {
            next = new Socket(InetAddress.getByAddress(ip), port);
            next_out = s.getOutputStream();
            next_in = s.getInputStream();
        } catch (UnknownHostException e) {
            // error
            System.err.println("CANNOT CONNECT TO NEXT");
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }
        
        // the previous person: You only receive from him (and maybe acknowledge)
        Socket prev;
        InputStream prev_in;
        OutputStream prev_out;
        try {
            if((prev = server.accept()) != null) {
                System.out.println("Client connected");
                prev_out = prev.getOutputStream();
                prev_in = prev.getInputStream();
            }
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }
        
        reply = ChatProto.Reply.newBuilder().setDone(true).build();
        
        writeToOutputStream(reply, out);
        System.out.println("Peer-to-Peer connection set up");
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        new ChatClient().run(args[0]);
    }
}
