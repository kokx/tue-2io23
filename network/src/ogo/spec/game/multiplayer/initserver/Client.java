package ogo.spec.game.multiplayer.initserver;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import com.google.protobuf.ByteString;

import ogo.spec.game.multiplayer.*;


// represents a connection to a client
class Client extends Peer
{

    // reader
    ReadRun read;

    public Client(ServerSocket server) throws Exception
    {
        if ((sock = server.accept()) == null) {
            throw new Exception("Connection failed.");
        }

        initIO();
    }

    /**
     * Get the IP address from this client.
     */
    byte[] getIp()
    {
        return sock.getInetAddress().getAddress();
    }

    // expect a reply
    void expectReply()
    {
        read = new ReadRun(in);
        new Thread(read).start();
    }

    boolean hasReply()
    {
        return read.wasRead;
    }
}
