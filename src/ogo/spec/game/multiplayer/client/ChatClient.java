package ogo.spec.game.multiplayer.client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;
import ogo.spec.game.multiplayer.client.peer.*;


class ChatClient {

    void run() throws IOException, InterruptedException, UnknownHostException
    {
        Client client = new Client();

        List<PeerInfo> servers = client.findServers();

        Scanner sc = new Scanner(System.in);

        System.out.println("The following servers are available:");

        for (int i = 0; i < servers.size(); i++) {
            System.out.println("IP (" + (i + 1) + "): " + servers.get(i).ip.toString());
        }

        System.out.println("Please type in the server number to which you want to connect:");

        int num = sc.nextInt() - 1;

        client.connect(servers.get(num));
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        new ChatClient().run();
    }
}
