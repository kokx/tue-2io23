import java.io.*;
import java.util.*;
import java.net.*;
import java.math.*;

class WriteRun implements Runnable {
    OutputStream out;
    BufferedReader in;

    public WriteRun(BufferedReader in, OutputStream out)
    {
        this.in = in;
        this.out = out;
    }

    // assuming MSB is first (Big Endian)
    static byte[] intToByteArray(int value)
    {
        return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value
        };
    }

    void writeToOutputStream(ChatProto.Message message)
    {
        try {
            int len = message.toByteArray().length;
            byte[] length = intToByteArray(len);

            out.write(length);

            message.writeTo(out);
        } catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(-1);
        }
    }

    public void run()
    {
        String input = "";

        try {
            while ((input = in.readLine()) != null) {
                ChatProto.Message message = ChatProto.Message.newBuilder()
                    .setMessage(input)
                    .build();
                writeToOutputStream(message);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
