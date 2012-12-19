package ogo.spec.game.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LobbyGui implements ActionListener{
    protected JFrame frame;
    
    protected JList serverList;
    protected JScrollPane scrollList;
    
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
        
        scrollList = new JScrollPane();
        serverList = new JList();
        
        scrollList.add(serverList);
        
        scrollList.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()-100));
        
        startButtonPanel = new JPanel(new GridLayout(1,3));
        startButtonPanel.add(startServer);
        startButtonPanel.add(searchServers);
        startButtonPanel.add(joinGame);
        
        startPanel.add(scrollList, BorderLayout.NORTH);
        startPanel.add(startButtonPanel, BorderLayout.SOUTH);
        
        frame.getContentPane().add(startPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    
    public void startLobby(){
        startServer.addActionListener(this);
        joinGame.addActionListener(this);
        DefaultListModel list = new DefaultListModel();
        list.addElement("No Servers Found");
        serverList.setModel(list);
    }
    
    
    public void actionPerformed(ActionEvent e){
        
    }
    
    /*public static void main(String[] args){
        new LobbyGui();//.startLobby();
    }*/
}
