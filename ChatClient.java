import java.io.*;
import java.util.*;
import java.net.*;

class ChatClient {

    public final static int PORT = 25665;

    static Scanner sc;

    void run(String ip) throws IOException
    {
        Socket s = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            s = new Socket(InetAddress.getByName(ip), PORT);
            out = new PrintWriter(s.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (UnknownHostException e) {
            // error
            System.err.println("CANNOT CONNECT");
        } catch (IOException e) {
            System.err.println("NO I/O");
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
        s.close();
    }

    public static void main(String args[]) throws IOException {
        new ChatClient().run(args[0]);
    }
}
