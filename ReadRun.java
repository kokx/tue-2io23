import java.io.*;
import java.util.*;
import java.net.*;
import java.math.*;

class ReadRun implements Runnable {
    InputStream in;
    
    public boolean reply = false;

    public ReadRun(InputStream in)
    {
        this.in = in;
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
            if (in.read(input, 0, 4) == 4) {
                int len = byteArrayToInt(input);

                byte data[] = new byte[len];
                in.read(data, 0, len);
                ChatProto.Reply m = ChatProto.Reply.parseFrom(data);
                reply = true;
                
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
