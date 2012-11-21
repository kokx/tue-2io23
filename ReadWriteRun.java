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
