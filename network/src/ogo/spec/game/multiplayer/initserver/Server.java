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

    public final static int TIME_POLL = 100;

    ServerSocket sock = null;

    ArrayList<Client> clients = new ArrayList<Client>();
    
    boolean connectClients;

    /**
     * Constructor.
     */
    public Server(int port) throws IOException
    {
        sock = new ServerSocket(port);
        connectClients = true;
        
    }
    
    public void close() throws Exception{
        for(Client c : clients){
            c.close();
        }
        sock.close();
    }

    /**
     * Wait for a client to connect.
     */
    public void connectClient() throws Exception
    {
        Client c = new Client(sock);
        if(connectClients){
            clients.add(c);
        }else{
            c.close();
        }
    }

    public void waitClientReply() throws InterruptedException
    {
        // wait for all clients to have a reply
        boolean readAll = false;
        while (!readAll) {
            Thread.sleep(TIME_POLL);

            readAll = true;
            for (Client c : clients) {
                readAll = readAll && c.hasReply();
            }
        }
    }
    
    public int getClientCount(){
        return clients.size();
    }

    /**
     * Initialize the token ring.
     */
    public void init(int initialPort) throws InterruptedException
    {
        connectClients = false;
        // give everyone the proper port number to setup a server on;
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
