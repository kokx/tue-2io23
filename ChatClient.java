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
    
    void run(String serverip) throws IOException
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
        
        ReadRun read = new ReadRun(in);
        new Thread(read).start();
        
        while(!read.wasRead){
            Thread.sleep(100);
        }
        
        int serverPort = ChatProto.Init.parseFrom(read.data).getPort();
        ServerSocket server;
        
        try {
            server = new ServerSocket(serverPort);
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }
        
        ChatProto.Reply reply = ChatProto.Reply.newBuilder().setDone(true).build();
        
        writeToOutputStream(reply, out);

        /*BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        WriteRun writeRunner = new WriteRun(stdIn);
        writeRunner.addOutputStream(out);
        new Thread(writeRunner).start();
        new Thread(new ReadRun(in, System.out, "echo: ")).start();

        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }*/
    }

    public static void main(String args[]) throws IOException {
        new ChatClient().run(args[0]);
    }
}
