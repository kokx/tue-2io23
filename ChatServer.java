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
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            server = new ServerSocket(PORT);
            client = server.accept();

            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            System.err.println("NO I/O");
            System.exit(-1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        String userInput;

        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            System.out.println("echo: " + in.readLine());
        }

        out.close();
        in.close();
        stdIn.close();
        client.close();
        server.close();
    }

    public static void main(String args[]) throws IOException {
        new ChatServer().run();
    }
}
