package ogo.spec.game.multiplayer;

import java.io.*;
import java.util.*;
import java.net.*;

public abstract class Peer
{
    protected Socket sock;

    protected DataInputStream in;
    protected OutputStream out;

    // assuming MSB is first (Big Endian)
    protected static byte[] intToByteArray(int value)
    {
        return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16 & 0xFF),
            (byte)(value >>> 8 & 0xFF),
            (byte)(value & 0xFF)
        };
    }

    // assuming MSB is first (Big Endian)
    protected static int byteArrayToInt(byte[] array)
    {
        return java.nio.ByteBuffer.wrap(array).getInt();
    }

    protected void initIO() throws IOException
    {
        in = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        out = sock.getOutputStream();
    }

    /**
     * Write a message.
     */
    public void write(com.google.protobuf.GeneratedMessage message)
    {
        try {
            byte[] data = message.toByteArray();
            System.out.print("Data: ");
            for(byte b : data){
                System.out.print(b + " ");
            }
            System.out.println("");
            int len = message.toByteArray().length;
            byte[] length = intToByteArray(len);

            out.write(length);
            message.writeTo(out);
        } catch (IOException e) {
            System.out.println("I/O Error");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Read a message.
     */
    public byte[] read() throws IOException
    {
        byte[] input = new byte[4];
        //in.readFully(input, 0, 4);
        in.read(input, 0, 4);
        int len = byteArrayToInt(input);
        
        System.out.println("Data Length: " + len);

        byte[] data = new byte[len];
        //in.readFully(data, 0, len);
        in.read(input, 0, len);
        
        for(byte b : data){
            System.out.print(b + " ");
        }
        System.out.println("");
        
        return data;
    }
}
