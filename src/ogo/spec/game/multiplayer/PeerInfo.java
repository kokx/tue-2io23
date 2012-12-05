package ogo.spec.game.multiplayer;

import java.io.*;
import java.util.*;
import java.net.*;

import ogo.spec.game.multiplayer.*;

/**
 * Information of a peer.
 */
public class PeerInfo
{
    public int port;
    public InetAddress ip;
    public boolean init;

    public PeerInfo(int port, InetAddress ip, boolean init)
    {
        this.port = port;
        this.ip = ip;
        this.init = init;
    }
}
