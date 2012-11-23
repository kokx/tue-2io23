import java.io.*;
import java.util.*;
import java.net.*;

class ChatServer {

    public final static int PORT = 25665;
    public final static int MAX_CLIENTS = 2;
    public final static int TIME_POLL = 100;
    
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
    
    void run() throws IOException
    {
        ServerSocket server = null;
        Socket client[] = new Socket[MAX_CLIENTS];
        int clients = 0;
        
        OutputStream[] outputs = new OutputStream[MAX_CLIENTS];
        InputStream[] inputs = new InputStream[MAX_CLIENTS];
        
        // open connection with all clients
        try {
            server = new ServerSocket(PORT);
            while ((clients < MAX_CLIENTS) && (client[clients] = server.accept()) != null) {
                System.out.println("Client connected");
                outputs[clients] = client[clients].getOutputStream();
                inputs[clients] = client[clients].getInputStream();
                clients++;
            }
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }
        
        // write ports to open to all clients 
        for(int i = 0; i < clients; i++){
            // port = PORT + i;
            ChatProto.Init init = ChatProto.Init.newBuilder().setPort(PORT+i).build();
            writeToOutputStream(init, outputs[i]);
        }
        
        // read from all clients
        ReadRun[] reads = new ReadRun[clients];
        for(int i = 0; i < clients; i++){
            reads[i] = new ReadRun(inputs[i]);
            new Thread(reads[i]).start();
        }
        
        // check for all messages to be received
        boolean readAll = false;
        while(!readAll){
            Thread.sleep(TIME_POLL);
            
            readAll = true;
            for(int i = 0; i < clients; i++){
                readAll = readAll && reads[i].wasRead;
            }
        }
        
        // write ports and ip's to listen to all clients
        for(int i = 0; i < clients; i++){
            byte[] ip = client[(i + 1) % clients].getInetAddress().getAddress();
            ChatProto.ConnectTo message = ChatProto.ConnectTo.newBuilder()
                    .setIp(com.google.protobuf.ByteString.copyFrom(ip))
                    .setPort((i+1)%clients+PORT)
                    .setInit(i==0).build();
            writeToOutputStream(message, outputs[i]);
        }
        
        // check for all messages to be received
        for(int i = 0; i < clients; i++){
            reads[i] = new ReadRun(inputs[i]);
            new Thread(reads[i]).start();
            
        }
        
        readAll = false;
        while(!readAll){
            Thread.sleep(TIME_POLL);
            
            readAll = true;
            for(int i = 0; i < clients; i++){
                readAll = readAll && reads[i].wasRead;
            }
        }
        
        
    }

    public static void main(String args[]) throws IOException {
        new ChatServer().run();
    }
}
