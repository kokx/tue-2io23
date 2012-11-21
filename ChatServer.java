import java.io.*;
import java.util.*;
import java.net.*;

class ReadWriteRun implements Runnable {
    PrintStream out;
    BufferedReader in;
    String prefix;

    public ReadWriteRun(BufferedReader in, PrintStream out, String prefix)
    {
        this.in = in;
        this.out = out;
        this.prefix = prefix;
    }

    public void run()
    {
        String input = "";

        try {
            while ((input = in.readLine()) != null) {
                out.println(prefix + input);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

class ChatServer {

    public final static int PORT = 25665;

    static Scanner sc;

    void run() throws IOException
    {
        ServerSocket server = null;
        Socket client = null;
        PrintStream out = null;
        BufferedReader in = null;
        try {
            server = new ServerSocket(PORT);
            client = server.accept();

            System.out.println("Client connected");

            out = new PrintStream(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        ReadWriteRun write = new ReadWriteRun(stdIn, out, "");
        ReadWriteRun read = new ReadWriteRun(in, System.out, "echo: ");
        new Thread(write).start();
        new Thread(read).start();

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
