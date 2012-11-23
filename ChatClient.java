import java.io.*;
import java.util.*;
import java.net.*;

class ChatClient {

    public final static int PORT = 25665;

    static Scanner sc;

    void run(String ip) throws IOException
    {
        Socket s = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            s = new Socket(InetAddress.getByName(ip), PORT);
            out = s.getOutputStream();
            in = s.getInputStream();
        } catch (UnknownHostException e) {
            // error
            System.err.println("CANNOT CONNECT");
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
        new ChatClient().run(args[0]);
    }
}
