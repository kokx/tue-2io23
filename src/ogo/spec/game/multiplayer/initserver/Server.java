package ogo.spec.game.multiplayer.initserver;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import com.google.protobuf.ByteString;

import ogo.spec.game.multiplayer.*;

// represents the server
class Server
{

    ServerSocket sock = null;

    ArrayList<Client> clients = new ArrayList<Client>();

    /**
     * Constructor.
     */
    public Server(int port) throws IOException
    {
        sock = new ServerSocket(port);
    }

    /**
     * Wait for a client to connect.
     */
    public void connectClient() throws Exception
    {
        clients.add(new Client(sock));
    }

    public void waitClientReply() throws InterruptedException
    {
        // wait for all clients to have a reply
        boolean readAll = false;
        while (!readAll) {
            Thread.sleep(ChatServer.TIME_POLL);

            readAll = true;
            for (Client c : clients) {
                readAll = readAll && c.hasReply();
            }
        }
    }

    /**
     * Initialize the token ring.
     */
    public void init(int initialPort) throws InterruptedException
    {
        // give everyone the proper port number
        for (int i = 0; i < clients.size(); i++) {
            // create the message
            ChatProto.Init init = ChatProto.Init.newBuilder()
                .setPort(initialPort + i)
                .build();
            clients.get(i).write(init);

            clients.get(i).expectReply();
        }

        waitClientReply();

        // link all clients
        for (int i = 0; i < clients.size(); i++) {
            ByteString ip = ByteString.copyFrom(clients.get((i + 1) % clients.size()).getIp());
            ChatProto.ConnectTo message = ChatProto.ConnectTo.newBuilder()
                .setIp(ip)
                .setPort((i + 1) % clients.size() + initialPort)
                .setInit(i == 0)
                .build();
            clients.get(i).write(message);

            clients.get(i).expectReply();
        }

        waitClientReply();
    }
}
