package ogo.spec.game.multiplayer.initserver;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import com.google.protobuf.ByteString;

import ogo.spec.game.multiplayer.*;

/**
 * UDP thread
 */
class BroadcastReceiverRunnable implements Runnable
{
    DatagramSocket sock;

    ConcurrentLinkedQueue<DatagramPacket> buffer = new ConcurrentLinkedQueue<DatagramPacket>();
    
    boolean read;
    
    public void stop(){
        read = false;
    }

    BroadcastReceiverRunnable(DatagramSocket sock)
    {
        this.sock = sock;
        this.read = true;
    }

    public void run()
    {
        try {
            while (read) {
                DatagramPacket p = new DatagramPacket(new byte[1], 1);

                sock.receive(p);

                buffer.add(p);

                //System.err.println(p.getAddress().toString());

                // simply send a packet back
                byte[] data = new byte[1];
                data[0] = 1;
                DatagramPacket c = new DatagramPacket(data, 1, p.getAddress(), p.getPort());

                sock.send(c);
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

class ConnectClients implements Runnable{
    Server server = null;
    
    public boolean shouldConnect;
    
    public ConnectClients(int port) throws IOException{
        server = new Server(port);
        shouldConnect = true;
    }
    
    public void close() throws Exception{
        server.close();
        shouldConnect = false;
    }
    
    public void run(){
        for (int i = 0; i < ChatServer.MAX_CLIENTS && shouldConnect; i++) {
            try{
                server.connectClient();
            }catch (Exception e) {
                if(shouldConnect){
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    public List<Integer> createCreaturesFromData(int[][] data){
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < data.length; i++){
            for (int j = 0; j < data[i].length; j++) {
                list.add(data[i][j]);
            }
        }
        return list;
    }
    
    public List<String> createNamesFromData(String[] names){
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < names.length; i++){
            list.add(names[i]);
        }
        return list;
    }
    
    public void sendInitialGameState(int[][] data, String[] names){
        for(Client c : server.clients){
            GameProto.InitialGameState init = GameProto.InitialGameState.newBuilder()
                    .addAllData(createCreaturesFromData(data))
                    .addAllNames(createNamesFromData(names))
                    .build();
            c.sendInitialGameState(init);
        }
    }
}

class ClientsReadyRunnable implements Runnable{
    ConnectClients connect;
    ArrayList<Client> expectingClients;
    
    boolean isReady;
    boolean threadRunning;
    
    GameProto.IsReady[] data;
    public ClientsReadyRunnable(ConnectClients c){
        connect = c;
        expectingClients = new ArrayList<Client>();
        isReady = false;
        threadRunning = true;
        data = null;
    }
    
    public void run(){
        try{
            for(Client c : connect.server.clients){
                c.expectReply();
                expectingClients.add(c);
            }
            
            while(threadRunning){
                Thread.sleep(500);
                for(Client c : connect.server.clients){
                    if(!expectingClients.contains(c)){
                        c.expectReply();
                        expectingClients.add(c);
                    }
                }
                
                int count = 0;
                for(Client c : expectingClients){
                    if(c.hasReply()){
                        count++;
                    }
                }
                isReady = (expectingClients.size() == count+1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean isReady(){
        return isReady;
    }
    
    public void stop(){
        threadRunning = false;
    }
    
    private void getData() throws Exception{
        if(data == null){
            data = new GameProto.IsReady[expectingClients.size()];
            for(int i = 0; i < expectingClients.size(); i++){
                data[i] = GameProto.IsReady.parseFrom(expectingClients.get(i).getData());
            }
        }
    }
    
    public int[][] getCreatureTypes() throws Exception{
        int[][] result = new int[expectingClients.size()][3];
        getData();
        for(int i = 0; i < data.length; i++){
            result[i][0] = data[i].getCreature1();
            result[i][1] = data[i].getCreature2();
            result[i][2] = data[i].getCreature3();
        }
        return result;
    }
    
    public String[] getNames() throws Exception{
        String[] result = new String[expectingClients.size()];
        getData();
        for(int i = 0; i < data.length; i++){
            result[i] = data[i].getName();
        }
        return result;
    }
}

public class ChatServer {

    public final static int PORT = 25765;
    public final static int INIT_PORT = 25744; // this is a UDP port
    public final static int INIT_LISTEN_PORT = 25745; // this is a UDP port
    public final static int MAX_CLIENTS = 6;
    public final static int TIME_POLL = 100;

    // real stuff
    private ConnectClients connect = null;
    private ClientsReadyRunnable readyState = null;
    private DatagramSocket sock = null;
    private BroadcastReceiverRunnable run = null;
    
    public void sendInitialGameState(int[][] data, String[] names){
        connect.sendInitialGameState(data, names);
    }
    
    public void initConnection() throws InterruptedException{
        connect.server.init(PORT+1);
    }
    
    public int getClientCount(){
        return connect.server.getClientCount();
    }
    
    public boolean canStartGame(){
        return readyState.isReady();
    }
    
    public void stopReadyState(){
        readyState.stop();
    }
    
    public int[][] getCreatureTypes() throws Exception{
        return readyState.getCreatureTypes();
    }
    
    public String[] getNames() throws Exception{
        return readyState.getNames();
    }
    
    public void close() throws Exception{
        connect.close();
        run.stop();
        sock.close();
        readyState.stop();
    }
    
    public void runCLI() throws Exception{
        sock = new DatagramSocket(INIT_LISTEN_PORT);
        
        run = new BroadcastReceiverRunnable(sock);
        
        connect = new ConnectClients(PORT);
        
        new Thread(run).start();
        
        for(int i = 0; i < 2/*MAX_CLIENTS*/; i++){
            connect.server.connectClient();
        }
        
        run.stop();
        
        connect.server.init(PORT + 1);
    }

    public void run() throws Exception, IOException
    {
        sock = new DatagramSocket(INIT_LISTEN_PORT);

        run = new BroadcastReceiverRunnable(sock);

        new Thread(run).start();
        
        connect = new ConnectClients(PORT);

        new Thread(connect).start();
        
        readyState = new ClientsReadyRunnable(connect);
        
        new Thread(readyState).start();
    }

    public static void main(String args[]) throws Exception, IOException, InterruptedException {
        System.out.println("Enter which type of ChatServer you want; 0 is CLI Mode, Lobby Mode otherwise");
        Scanner sc = new Scanner(System.in);
        if(sc.nextInt() == 0){
            //System.out.println("CLI");
            new ChatServer().runCLI();
        }else{
            //System.out.println("ELSE");
            new ChatServer().run();
        }
    }
}
