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
    
    void chat(boolean init, 
              InputStream prev_in, OutputStream prev_out, 
              InputStream next_in, OutputStream next_out)
              throws InterruptedException, IOException{
        
        if(init){
            ChatProto.Token token = ChatProto.Token
                                    .newBuilder()
                                    .setLastId(0)
                                    .build();
            writeToOutputStream(token, next_out);
            //System.out.println("Sent First Token");
        }
        System.out.println("in Chat now");
        ReadRun read;
        
        int i = 0;
        final int RUNS = 4;
        
        while(i < RUNS){
            read = new ReadRun(prev_in);
            new Thread(read).start();
            System.out.println("Reading");
            while(!read.wasRead){
                Thread.sleep(10);
            }
            //System.out.println("Received Token");
            ChatProto.Token token = ChatProto.Token.parseFrom(read.data);
            
            List<ChatProto.Token.Message> list = token.getMessageList();
            int lastId = token.getLastId();
            System.out.println("Received LastId: " + lastId);
            
            token = ChatProto.Token.newBuilder().setLastId(lastId+1).build();
            
            writeToOutputStream(token, next_out);
            
            i++;
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
        
        System.out.println("Received Connection Info Now: " + init);
        
        /* Potentieel Probleem: Als new Socket() en server.accept() allebei 
         *              Blocking Calls zijn, dan krijg je nooit een verbinding.
         *              Ik weet alleen niet of dat allebei zo is.
         */
        
        // The next person. You only send to him (and maybe receive aknowledge)
        Socket next;
        InputStream next_in = null;
        OutputStream next_out = null;
        
        try {
            next = new Socket(InetAddress.getByAddress(ip), port);
            next_out = next.getOutputStream();
            next_in = next.getInputStream();
        } catch (UnknownHostException e) {
            // error
            System.err.println("CANNOT CONNECT TO NEXT");
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }
        
        // the previous person: You only receive from him (and maybe acknowledge)
        Socket prev;
        InputStream prev_in = null;
        OutputStream prev_out = null;
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
        
        
        chat(init, prev_in, prev_out, next_in, next_out);
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        new ChatClient().run(args[0]);
    }
}
