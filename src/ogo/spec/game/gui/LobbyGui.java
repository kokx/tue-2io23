package ogo.spec.game.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import ogo.spec.game.multiplayer.initserver.*;

public class LobbyGui implements ActionListener{
    
    protected String nickname;
    
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
        serverList = new JList();
        
        DefaultListModel list = new DefaultListModel();
        list.addElement("No Servers Found");
        serverList.setModel(list);
        
        scrollList = new JScrollPane(serverList);
        
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
    boolean c = true;
    private void setListModel(){
         c = !c;
         DefaultListModel list = new DefaultListModel();
         list.addElement(nickname);
         if(c){
             list.addElement("JE MOEDER");
         }else{
             list.addElement("JOOD!");
         }
         
         serverList.setModel(list);
         //frame.repaint();
    }
    
    public void init(){
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
        if(e.getSource() == searchServers){
            setListModel();
        }
        //frame.repaint();
    }
    
    public static void main(String[] args){
        new LobbyGui().init();
    }
}
