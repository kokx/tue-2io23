package ogo.spec.game.multiplayer.client;

import java.io.*;
import java.util.*;
import java.net.*;

import ogo.spec.game.multiplayer.*;

/**
 * Information of a peer.
 */
public class PeerInfo
{
    int port;
    InetAddress ip;
    boolean init;

    public PeerInfo(int port, InetAddress ip, boolean init)
    {
        this.port = port;
        this.ip = ip;
        this.init = init;
    }
}
