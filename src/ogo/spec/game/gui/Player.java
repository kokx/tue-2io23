/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ogo.spec.game.gui;

import java.util.List;
import javax.swing.JOptionPane;
import ogo.spec.game.multiplayer.PeerInfo;
import ogo.spec.game.multiplayer.client.Client;
import ogo.spec.game.multiplayer.initserver.ChatServer;

/**
 *
 * @author florian
 */
public class Player {
    String nickname;
    
    GUI theGui;
    
    Client client;
    ChatServer initServer;
    
    List<PeerInfo> serverList;
    
    boolean isHost;
    boolean isReady;
    
    public Player(){
        nickname = JOptionPane.showInputDialog(null, "Enter Your Nickname: ", "", 1);
        isHost = false;
    }
    
    public void runGUI() throws Exception{
        theGui = new GUI(this);
        theGui.init();
    }
    
    private String[] convertServerList(List<PeerInfo> l){
        /* Moet nog wat descriptiever worden.. */
        String[] names = new String[l.size()];
        for(int i = 0; i < l.size(); i++){
            names[i] = l.get(i).ip.toString();
        }
        return names;
    }
    
    public String[] getServerNames() throws Exception{
        client = new Client();
        serverList = client.findServers();
        return convertServerList(serverList);
    }
    
    public void openLobby() throws Exception{
        isHost = true;
        isReady = true;
        
        /* Start new Server to connect to */
        initServer = new ChatServer();
        initServer.run();
        
        getServerNames();
        
        
    }
    
    public void joinLobby(int serverNum) throws Exception{
        isHost = false;
        isReady = false;
        
        client.connectToInitServer(serverList.get(serverNum));
    }
    
    public void closeLobby() throws Exception{
        if(isHost){
            initServer.close();
        }
        
        client.close();
        
        isHost = false;
        isReady = false;
    }
    
    public void startGame() throws Exception{
        assert(isHost);
        
        // 
    }
    
    public void setReady(boolean flag) throws Exception{
        assert(!isHost);
        isReady = flag;
        
        client.connectToPeer();
    }
    
    public void sendChatMessage(String message) throws Exception{
        
    }
    
    public static void main(String[] args) throws Exception{
        new Player().runGUI();
    }
}
