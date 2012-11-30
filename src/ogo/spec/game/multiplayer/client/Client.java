package ogo.spec.game.multiplayer.client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;

/**
 * Client for p2p communication.
 */
class Client extends Peer
{
    PeerInfo peer;

    Client(PeerInfo peer) throws UnknownHostException, IOException
    {
        this.peer = peer;
        sock = new Socket(peer.ip, peer.port);
        initIO();
    }
}
