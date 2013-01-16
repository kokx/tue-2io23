/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ogo.spec.game.lobby;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.imageio.ImageIO;
import ogo.spec.game.model.*;
import ogo.spec.game.multiplayer.PeerInfo;
import ogo.spec.game.multiplayer.client.Client;
import ogo.spec.game.multiplayer.initserver.ChatServer;
import ogo.spec.game.multiplayer.GameProto;

/**
 *
 * @author florian
 */
public class Lobby {
    GameRun game;

    GUI theGui;

    Client client;
    ChatServer initServer;

    List<PeerInfo> serverList;

    boolean isHost;
    
    public final static String mapImagePath = "src/ogo/spec/game/lobby/Map.bmp";

    public Lobby(){
        isHost = false;
    }
    
    private int[] loadMapImage(){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(mapImagePath));
        } catch (IOException e) {
            
        }
        
        byte[] data = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
        int[] newData = new int[data.length];
        for(int i = 0; i < data.length; i++){
            newData[i] = (data[i]+512)%256;
        }
        return newData;
    }
    
    private GameMap generateMap(){
        int[] data = loadMapImage();
        TileType[][] types = new TileType[50][50];
        for(int i = 0; i < 50; i++){
            for(int j = 0; j < 50; j++){
                if(data[i*150 + j*3 + 0] == 0 && data[i*150 + j*3 + 1] == 0 && data[i*150 + j*3 + 2] == 0){
                    types[i][j] = TileType.LAND;
                }else if(data[i*150 + j*3 + 0] == 255 && data[i*150 + j*3 + 1] == 255 && data[i*150 + j*3 + 2] == 255){
                    types[i][j] = TileType.DEEP_WATER;
                }else{
                    types[i][j] = TileType.SHALLOW_WATER;
                }
            }
        }
        
        return new GameMap(types);
    }

    private void initGame(int[][] data, String[] names, int id){
        Player[] players = new Player[names.length];
        for (int i = 0; i < names.length; i++) {
            players[i] = new Player(names[i], i);
        }
        GameMap map = generateMap();
        
        for(int i = 0; i < data.length; i++){
            Creature[] creatures = new Creature[3];
            for(int j = 0; j < data[i].length; j++){
                Creature inh;
                if(data[i][j] == 0){
                    inh = new LandCreature(map.getTile(i*6, j*6), map, i*3 + j);
                }else if(data[i][j] == 1){
                    inh = new SeaCreature(map.getTile(i*6, j*6), map, i*3 + j);
                }else{
                    inh = new AirCreature(map.getTile(i*6, j*6), map, i*3 + j);
                }
                map.getTile(i*6, j*6).setInhabitant(inh);
                
                creatures[j] = inh;
            }
            players[i].setCreatures(creatures);
        }
        
        Game game2 = new Game(players, map);
        game = new GameRun(game2, id);
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
            names[i] = l.get(i).ip.toString().substring(1);
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
        
        int length;

        DatagramReceiverRunnable(DatagramSocket sock, int length)
        {
            this.sock = sock;
            this.read = true;
            this.length = length;
        }

        public void stop(){
            read = false;
        }

        public void run()
        {
            try {
                while (read) {
                    DatagramPacket p = new DatagramPacket(new byte[length], 1);

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
    
    public final static int MAX_CONNECTION_TRIES = 20;

    public boolean openLobby() throws Exception{
        isHost = true;

        /* Start new Server to connect to */
        initServer = new ChatServer();
        initServer.run();

        boolean done = false;

        DatagramPacket packet;
        DatagramSocket sendSock;
        DatagramSocket receiveSock = new DatagramSocket(INIT_PORT);

        int count = 0;
        while(!done && count < MAX_CONNECTION_TRIES){
            packet = new DatagramPacket(new byte[]{2}, 1, InetAddress.getByName(BROADCAST_IP), INIT_PORT);
            sendSock = new DatagramSocket();
            sendSock.send(packet);

            DatagramReceiverRunnable run = new DatagramReceiverRunnable(receiveSock, 1);
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
            count ++;
        }
        receiveSock.close();
        if(!done){
            initServer.close();
        }
        return done;
    }

    public void joinLobby(int serverNum) throws Exception{
        isHost = false;
        client.connectToInitServer(serverList.get(serverNum));
    }

    private GameProto.IsReady.Builder parseReadyInfo(){
        int[] creatures = theGui.getCreatureInfo();
        return  GameProto.IsReady.newBuilder()
                .setCreature1(creatures[0])
                .setCreature2(creatures[1])
                .setCreature3(creatures[2]);
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
                if(e instanceof EOFException){
                    try{
                        client.close();
                        System.out.println("Closed Connection To Next Person");
                        System.exit(0);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }else{
                    e.printStackTrace();
                }
            }
        }
    }

    public void finishConnection() throws Exception
    {
        GameProto.InitialGameState data = client.receiveInitialGameState();
        
        int players = data.getDataCount()/3;
        int[][] creatureData = new int[players][3];
        String[] names = new String[players];
        for(int i = 0; i < players; i++){
            for (int j = 0; j < 3; j++) {
                creatureData[i][j] = data.getData(i*3 + j);
            }
            names[i] = data.getNames(i);
        }
        
        int id = data.getId();
        
        client.connectToPeer();
        
        theGui.stop();
        
        initGame(creatureData, names, id);

        new Thread(new TokenRingRunnable(client)).start();
    }

    public void setReady() throws Exception{
        GameProto.IsReady.Builder ready = parseReadyInfo();
        client.setReady(ready.setName(theGui.nickname).build());
    }

    class InitConnectionRunnable implements Runnable{
        ChatServer init;
        int[][] data;
        String[] names;
        public InitConnectionRunnable(ChatServer initServer, int[][] creatureData, String[] playerNames){
            init = initServer;
            data = creatureData;
            names = playerNames;
        }

        public void run(){
            try{
                init.sendInitialGameState(data, names);
                
                init.initConnection();
            } catch (Exception e){
                System.err.println("Problem with Init Server:\n" + e.getMessage());
            }
        }
    }

    public void startGame() throws Exception{
        assert(isHost && canStartGame());
        
        setReady();

        initServer.stopReadyState();
        
        int[][] creatureData = initServer.getCreatureTypes();
        
        String[] names = initServer.getNames();
        for(int i = 0; i < names.length; i++){
            names[i] = i + "-" + names[i];
        }
        
        new Thread(new InitConnectionRunnable(initServer, creatureData, names)).start();

        finishConnection();
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
