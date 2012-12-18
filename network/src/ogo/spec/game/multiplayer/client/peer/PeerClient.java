package ogo.spec.game.multiplayer.client.peer;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.Peer;
import ogo.spec.game.multiplayer.PeerInfo;

/**
 * Client for p2p communication.
 */
public class PeerClient extends Peer
{
    PeerInfo peer;

    public PeerClient(PeerInfo peer) throws UnknownHostException, IOException
    {
        this.peer = peer;
        sock = new Socket(peer.ip, peer.port);
        initIO();
    }
}
