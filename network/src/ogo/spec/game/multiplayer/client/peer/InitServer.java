package ogo.spec.game.multiplayer.client.peer;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;


public class InitServer extends Peer
{

    public InitServer(InetAddress ip, int port) throws UnknownHostException, IOException
    {
        sock = new Socket(ip, port);
        initIO();
    }

    /**
     * Send a reply.
     */
    public void reply(boolean reply)
    {
        ChatProto.Reply rep = ChatProto.Reply.newBuilder()
            .setDone(reply)
            .build();
        write(rep);
    }

    /**
     * Get the port on which we need to setup a server.
     */
    public int getPort() throws IOException
    {
        return ChatProto.Init.parseFrom(read()).getPort();
    }

    /**
     * Get the info of the peer we need to connect to.
     */
    public PeerInfo getConnectTo() throws IOException
    {
        ChatProto.ConnectTo connectTo = ChatProto.ConnectTo.parseFrom(read());
        PeerInfo peer = new PeerInfo(connectTo.getPort(), InetAddress.getByAddress(connectTo.getIp().toByteArray()), connectTo.getInit());
        return peer;
    }
    
    public void close() throws IOException
    {
        in.close();
        out.close();
        sock.close();
    }
}
