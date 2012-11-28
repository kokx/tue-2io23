import java.io.*;
import java.util.*;
import java.net.*;
import java.math.*;

public class ReadRun implements Runnable {
    InputStream in;

    public boolean wasRead;

    // Hij slaat niet meer de message op, maar de data. Zo kan deze class
    // gebruikt worden voor alle messages. Je moet alleen zelf de .parseFrom()
    // method aanroepen. Modularity FTW!
    byte[] data;

    public ReadRun(InputStream in)
    {
        this.in = in;
        this.wasRead = false;
    }

    // assuming MSB is first (Big Endian)
    static int byteArrayToInt(byte[] array)
    {
        return java.nio.ByteBuffer.wrap(array).getInt();
    }

    public void run()
    {
        byte[] input = new byte[4];

        try {
            if (in.read(input, 0, 4) == 4) {
                int len = byteArrayToInt(input);

                data = new byte[len];
                in.read(data, 0, len);
                wasRead = true;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
