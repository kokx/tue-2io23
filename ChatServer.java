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

        while (true) {
            String userInput = stdIn.readLine();
            String sInput = stdIn.readLine();

            if (userInput != null || sInput != null) {
                if (userInput != null) {
                    out.println(userInput);
                    System.out.println(userInput);
                }
                if (sInput != null) {
                    System.out.println(sInput);
                }
            } else {
                break;
            }
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
