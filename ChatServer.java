import java.io.*;
import java.util.*;
import java.net.*;

class ChatServer {

    public final static int PORT = 25665;

    static Scanner sc;

    BufferedReader stdIn;

    WriteRun writeRunner;

    void addClient(Socket client) throws IOException
    {
        OutputStream out = client.getOutputStream();
        InputStream in = client.getInputStream();

        new Thread(new ReadRun(in, System.out, "echo: ")).start();
        writeRunner.addOutputStream(out);
    }

    void run() throws IOException
    {
        ServerSocket server = null;
        Socket client = null;
        stdIn = new BufferedReader(new InputStreamReader(System.in));

        writeRunner = new WriteRun(stdIn);
        new Thread(writeRunner).start();

        try {
            server = new ServerSocket(PORT);

            while ((client = server.accept()) != null) {
                addClient(client);
                System.out.println("Client connected");
            }
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }
    }

    public static void main(String args[]) throws IOException {
        new ChatServer().run();
    }
}
