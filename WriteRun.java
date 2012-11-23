import java.io.*;
import java.util.*;
import java.net.*;
import java.math.*;

class WriteRun implements Runnable {
    List<OutputStream> outStreams = new LinkedList<OutputStream>();
    BufferedReader in;

    public WriteRun(BufferedReader in)
    {
        this.in = in;
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

    void writeToOutputStream(ChatProto.Token.Message message)
    {
        try {
            int len = message.toByteArray().length;
            byte[] length = intToByteArray(len);

            for (OutputStream out : outStreams) {
                out.write(length);
                message.writeTo(out);
            }
        } catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(-1);
        }
    }

    void addOutputStream(OutputStream out)
    {
        outStreams.add(out);
    }

    public void run()
    {
        String input = "";

        try {
            while ((input = in.readLine()) != null) {
                ChatProto.Token.Message message = ChatProto.Token.Message.newBuilder()
                    .setMessage(input)
                    .build();
                writeToOutputStream(message);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
