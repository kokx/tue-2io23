import java.io.*;
import java.util.*;
import java.net.*;
import java.math.*;

class ReadRun implements Runnable {
    PrintStream out;
    InputStream in;
    String prefix;

    public ReadRun(InputStream in, PrintStream out, String prefix)
    {
        this.in = in;
        this.out = out;
        this.prefix = prefix;
    }

    // assuming MSB is first (Big Endian)
    static int byteArrayToInt(byte[] array)
    {
        if (array.length > 4) {
            return -1;
        }

        int value = 0;
        for (int i = 0; i < array.length; i++) {
            value += (value << 8) + (array[i] & 0xFF);
        }
        return value;
    }

    public void run()
    {
        byte[] input = new byte[4];

        try {
            while (in.read(input, 0, 4) == 4) {
                int len = byteArrayToInt(input);

                byte data[] = new byte[len];
                in.read(data, 0, len);
                ChatProto.Message m = ChatProto.Message.parseFrom(data);

                out.println(prefix + m.getMessage());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
