/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ogo.spec.game.gui;

import java.util.List;
import ogo.spec.game.multiplayer.PeerInfo;
import ogo.spec.game.multiplayer.initserver.ChatServer;
import ogo.spec.game.multiplayer.client.Client;

/**
 *
 * @author florian
 */
public class Lobby {
    
    Client client;
    ChatServer initServer;
    
    String nickname;
    
    public Lobby() throws Exception{
        client = new Client();
    }
    
    protected void startServer() throws Exception{
        initServer = new ChatServer();
        initServer.run();
    }
    
    protected void closeLobby() throws Exception{
        initServer.close();
        initServer = null;
    }
    
    List<PeerInfo> servers;
    protected String[] getServerNames() throws Exception{
        client = new Client();
        servers = client.findServers();
        String[] names = new String[servers.size()];
        
        /* Edit to make it more informative */
        for(int i = 0; i < servers.size(); i++){
            names[i] = servers.get(i).ip.toString();
        }
        /* Edit to make it more informative */
        
        return names;
    }
    
    protected void sendChatMessage(String message){
        
    }
    
    protected void joinLobby(int serverNum) throws Exception{
        client.connect(servers.get(serverNum));
    }
}
