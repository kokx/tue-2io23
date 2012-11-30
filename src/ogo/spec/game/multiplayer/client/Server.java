package ogo.spec.game.multiplayer.client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;


/**
 * Server for p2p communication.
 */
public class Server extends Peer
{
    ServerSocket serverSock = null;
    boolean connected = false;

    public Server(int port) throws IOException
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
}
