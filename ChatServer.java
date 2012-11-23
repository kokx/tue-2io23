import java.io.*;
import java.util.*;
import java.net.*;

class ChatServer {

    public final static int PORT = 25665;

    static Scanner sc;

    void run() throws IOException
    {
        ServerSocket server = null;
        Socket client = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            server = new ServerSocket(PORT);
            client = server.accept();

            System.out.println("Client connected");

            out = client.getOutputStream();
            in = client.getInputStream();
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        new Thread(new WriteRun(stdIn, out)).start();
        new Thread(new ReadRun(in, System.out, "echo: ")).start();

        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String args[]) throws IOException {
        new ChatServer().run();
    }
}
