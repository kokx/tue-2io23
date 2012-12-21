package ogo.spec.game.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.List;
import javax.swing.*;
import ogo.spec.game.multiplayer.PeerInfo;

import ogo.spec.game.multiplayer.initserver.ChatServer;
import ogo.spec.game.multiplayer.client.Client;

public class LobbyGui implements ActionListener{
    
    protected String nickname;
    
    protected JFrame frame;
    
    protected JList serverList;
    protected JScrollPane serverScrollList;
    
    protected JList clientList;
    protected JScrollPane clientScrollList;
    
    protected JPanel startPanel;
    protected JPanel inGamePanel;
    
    protected JPanel startButtonPanel;
    protected JPanel inGameButtonPanel;
    
    protected JButton startServer;
    protected JButton searchServers;
    protected JButton joinGame;
    
    public LobbyGui(){
        frame = new JFrame("Play This Awesome Game!");
        
        frame.setSize(600,600);
        
        startPanel = new JPanel();
        inGamePanel = new JPanel();
        
        startServer = new JButton("Start Server");
        startServer.setEnabled(true);
        //startServer.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        
        searchServers = new JButton("Search for Servers");
        searchServers.setEnabled(true);
        //searchServers.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        
        joinGame = new JButton("Join Game");
        joinGame.setEnabled(false);
        //joinGame.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        serverList = new JList();
        
        DefaultListModel list = new DefaultListModel();
        list.addElement("No Attempt To Find Servers Yet");
        serverList.setModel(list);
        
        serverScrollList = new JScrollPane(serverList);
        serverScrollList.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()-100));
        
        startButtonPanel = new JPanel(new GridLayout(1,3));
        
        startButtonPanel.add(startServer);
        startButtonPanel.add(searchServers);
        startButtonPanel.add(joinGame);
        
        startPanel.add(serverScrollList, BorderLayout.NORTH);
        startPanel.add(startButtonPanel, BorderLayout.SOUTH);
        
        //inGamePanel.add(clientScrollList, BorderLayout.NORTH);
        
        frame.getContentPane().add(startPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    
    private void setListModel() throws Exception{
         Client client = new Client();

         List<PeerInfo> servers = client.findServers();
         
         DefaultListModel list = new DefaultListModel();
         if(servers.isEmpty()){
             list.addElement("No Servers Found LOL");
         }else{
            for(PeerInfo p : servers){
                list.addElement(p.ip.toString());
            } 
         }
         serverList.setModel(list);
         //frame.repaint();
    }
    
    public void init() throws Exception{
        nickname = JOptionPane.showInputDialog(null, "Enter Your Nickname: ", "", 1);
        frame.setTitle("Welcome " + nickname + ". Enjoy Playing This Awesome Game!!!");
        setListModel();
        startServer.addActionListener(this);
        searchServers.addActionListener(this);
        joinGame.addActionListener(this);
    }
    
    ChatServer initServer = null;
    public void startLobby() throws Exception{
        initServer = new ChatServer();
        initServer.run();
        
        switchPanels(true);
    }
    
    private void switchPanels(boolean forward){
        if(forward){
            frame.getContentPane().remove(startPanel);
            frame.getContentPane().add(inGamePanel);
        }else{
            frame.getContentPane().remove(inGamePanel);
            frame.getContentPane().add(startPanel);
        }
    }
    
    public void actionPerformed(ActionEvent e){
        try{
            if(e.getSource() == searchServers){
                setListModel();
            }else if(e.getSource() == startServer){
                startLobby();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
        //frame.repaint();
    }
    
    public static void main(String[] args) throws Exception{
        new LobbyGui().init();
    }
}