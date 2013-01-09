/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 //
package ogo.spec.game.gui;

import java.util.LinkedList;
import java.util.List;
import ogo.spec.game.multiplayer.ChatProto;
import ogo.spec.game.multiplayer.PeerInfo;
import ogo.spec.game.multiplayer.initserver.ChatServer;
import ogo.spec.game.multiplayer.client.Client;
import ogo.spec.game.multiplayer.client.InputReaderRunnable;
import ogo.spec.game.multiplayer.client.TokenChangeListener;

/**
 *
 * @author florian
 //
public class Lobby implements TokenChangeListener{
    
    String nickname;
    
    int nextId;
    InputReaderRunnable reader;
    
    List<PeerInfo> servers;
    LinkedList<String> newMessages;
    LinkedList<ChatProto.Token.Message> receivedMessages;
    
    int joinedServer;
    boolean isReady;
    
    public Lobby() throws Exception{
        client = new Client();
        nextId = 0;
        joinedServer = -1;
    }
    
    protected void startServer() throws Exception{
        initServer = new ChatServer();
        initServer.run();
    }
    
    protected void closeLobby() throws Exception{
        initServer.close();
    }
    
    protected String[] getServerNames() throws Exception{
        client = new Client();
        servers = client.findServers();
        String[] names = new String[servers.size()];
        
        // Edit to make it more informative 
        for(int i = 0; i < servers.size(); i++){
            names[i] = servers.get(i).ip.toString();
        }
        // Edit to make it more informative 
        
        return names;
    }
    
    protected void joinLobby(int serverNum) throws Exception{
        joinedServer = serverNum;
    }
    
    protected void setReady(boolean flag){
        isReady = flag;
        if(flag){
            client.connect(joinedServer);
        }
    }
    
    protected void sendChatMessage(String message){
        newMessages.add(message);
    }
    
    protected void receiveMessages(List<ChatProto.Token.Message> l){
        receivedMessages.clear();
        for(ChatProto.Token.Message m : l){
            receivedMessages.add(m);
        }
    }

    ChatProto.Token.Builder copyToken(ChatProto.Token token){
        ChatProto.Token.Builder builder = ChatProto.Token.newBuilder();

        builder.mergeFrom(token);

        return builder;
    }
    /**
     * Get messages from the input reader.
     *
    public Iterable<ChatProto.Token.Message> getMessages(){
        LinkedList<ChatProto.Token.Message> messages = new LinkedList<ChatProto.Token.Message>();

        for(String s : newMessages){
            messages.add(ChatProto.Token.Message.newBuilder()
                    .setId(nextId)
                    .setName(nickname)
                    .setMessage(s)
                    .build());
            nextId++;
        }
        
        newMessages.clear();

        return messages;
    }

    public ChatProto.Token tokenChanged(ChatProto.Token token){
        receiveMessages(token.getMessageList());

        ChatProto.Token.Builder builder = copyToken(token);

        builder.addAllMessage(getMessages());

        builder.setLastId(nextId);

        return builder.build();
    }
}*/
