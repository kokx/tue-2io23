package ogo.spec.game.multiplayer.client.peer;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;


/**
 * Server for p2p communication.
 */
public class PeerServer extends Peer
{
    protected ServerSocket serverSock = null;
    protected boolean connected = false;

    public PeerServer(int port) throws IOException
    {
        serverSock = new ServerSocket(port);
    }

    public boolean accept() throws IOException
    {
        boolean ret = (sock = serverSock.accept()) != null;
        initIO();
        connected = true;
        return ret;
    }

    public boolean isConnected()
    {
        return connected;
    }
}
