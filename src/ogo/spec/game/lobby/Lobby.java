/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ogo.spec.game.lobby;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import ogo.spec.game.multiplayer.PeerInfo;
import ogo.spec.game.multiplayer.client.Client;
import ogo.spec.game.multiplayer.initserver.ChatServer;
import ogo.spec.game.multiplayer.GameProto;

/**
 *
 * @author florian
 */
public class Lobby {
    Game game;

    GUI theGui;

    Client client;
    ChatServer initServer;

    List<PeerInfo> serverList;

    boolean isHost;

    public Lobby(){
        isHost = false;
    }

    private void initGame(){
        game = new Game();
        client.setTokenChangeListener(game);
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
    
    class DatagramReceiverRunnable implements Runnable
    {
        DatagramSocket sock;

        ConcurrentLinkedQueue<DatagramPacket> buffer = new ConcurrentLinkedQueue<DatagramPacket>();

        boolean read;

        DatagramReceiverRunnable(DatagramSocket sock)
        {
            this.sock = sock;
            this.read = true;
        }

        public void stop(){
            read = false;
        }

        public void run()
        {
            try {
                while (read) {
                    DatagramPacket p = new DatagramPacket(new byte[1], 1);

                    sock.receive(p);

                    buffer.add(p);
                }
            } catch (IOException e) {
                if(read){
                    System.err.println("I/O Error");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    public final static int INIT_PORT = 25945; // this is a UDP port
    public final static int INIT_LISTEN_PORT = 4444; // this is a UDP port
    public final static String BROADCAST_IP = "192.168.1.255";

    public void openLobby() throws Exception{
        isHost = true;

        /* Start new Server to connect to */
        initServer = new ChatServer();
        initServer.run();

        boolean done = false;

        DatagramPacket packet;
        DatagramSocket sendSock;
        DatagramSocket receiveSock = new DatagramSocket(INIT_PORT);

        while(!done){
            packet = new DatagramPacket(new byte[]{2}, 1, InetAddress.getByName(BROADCAST_IP), INIT_PORT);
            sendSock = new DatagramSocket();
            sendSock.send(packet);

            DatagramReceiverRunnable run = new DatagramReceiverRunnable(receiveSock);
            new Thread(run).start();

            Thread.sleep(100);

            while((packet = run.buffer.poll()) != null){
                //System.out.println("Data: " + packet.getData()[0]);
                if(packet.getData()[0] == 2){
                    break;
                }

            }
            if(packet != null){
                //System.err.println("Found Packet!");
                getServerNames();

                PeerInfo ownServer = null;
                for(PeerInfo p : serverList){
                    if(packet.getAddress().toString().equals(p.ip.toString())){
                        ownServer = p;
                    }
                }

                client.connectToInitServer(ownServer);

                done = true;
            }else{
                System.err.println("LOBBY: Could not find own server; unable to connect self to lobby");
            }
            run.stop();
        }
    }

    public void joinLobby(int serverNum) throws Exception{
        isHost = false;

        client.connectToInitServer(serverList.get(serverNum));
    }
    
    private GameProto.IsReady parseReadyInfo(){
        int[] creatures = theGui.getCreatureInfo();
        return  GameProto.IsReady.newBuilder()
                .setCreature1(creatures[0])
                .setCreature2(creatures[1])
                .setCreature3(creatures[2])
                .build();
    }
    
    class TokenRingRunnable implements Runnable
    {
        Client client;
        public TokenRingRunnable(Client c){
            client = c;
        }
        
        public void run(){
            try{
                client.startTokenRing();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void finishConnection() throws Exception
    {
        System.out.println("Finish");
        client.connectToPeer();

        System.out.println("Stop Gui");
        theGui.stop();
        initGame();

        new Thread(new TokenRingRunnable(client)).start();
    }
    
    public void setReady() throws Exception{
        GameProto.IsReady ready = parseReadyInfo();
        System.out.println("Parse Ready Done");
        client.setReady(ready);
    }

    class InitConnectionRunnable implements Runnable{
        ChatServer init;

        public InitConnectionRunnable(ChatServer initServer){
            init = initServer;
        }

        public void run(){
            try{
                init.initConnection();
            } catch (Exception e){
                System.err.println("Problem with Init Server:\n" + e.getMessage());
            }
        }
    }

    public void startGame() throws Exception{
        assert(isHost);
        
        setReady();
        
        initServer.stopReadyState();
        
        if(canStartGame()){
            new Thread(new InitConnectionRunnable(initServer)).start();

            finishConnection();
        }else{
            System.out.println("Playing on your own? You pathetic loser!!!!");
        }
    }

    public int getClientCount(){
        return initServer.getClientCount();
    }
    
    public boolean canStartGame(){
        return initServer.canStartGame();
    }
    
    public static void main(String[] args) throws Exception{
        new Lobby().runGUI();
    }
}
