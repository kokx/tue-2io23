package ogo.spec.game.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI implements ActionListener, ListSelectionListener{
    protected Player player;
    
    protected JFrame frame;
    
    protected JList lobbyList;
    protected JScrollPane lobbyScrollList;
    
    protected JList clientList;
    protected JScrollPane clientScrollList;
    
    protected JPanel startPanel;
    protected JPanel inGamePanel;
    
    protected JPanel startButtonPanel;
    protected JPanel inGameButtonPanel;
    
    protected JButton startLobby;
    protected JButton searchLobbys;
    protected JButton joinLobby;
    
    protected JButton leaveLobby;
    protected JButton startGame;
    protected JButton sendMessage;
    
    protected JTextField chatInput;
    protected JTextArea chatOutput;
    protected JScrollPane chatScroll;
    
    protected Timer updateLobbyTimer;
    
    protected int lobbyAmount;
    protected int selectedLobby;
    
    protected String[] noLobbys = {"No Lobbys Were Found"};
    
    public GUI(Player p) throws Exception{
        player = p;
        
        frame = new JFrame("Play This Awesome Game!");
        
        frame.setSize(600,600);
        
        startPanel = new JPanel();
        inGamePanel = new JPanel();
        
        startLobby = new JButton("Start Server");
        searchLobbys = new JButton("Search for Servers");
        joinLobby = new JButton("Join Game");
        
        startLobby.setEnabled(true);
        searchLobbys.setEnabled(true);
        joinLobby.setEnabled(false);
        
        lobbyList = new JList();
        lobbyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        lobbyScrollList = new JScrollPane(lobbyList);
        lobbyScrollList.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()-100));
        
        startButtonPanel = new JPanel(new GridLayout(1,3));
        
        startButtonPanel.add(startLobby);
        startButtonPanel.add(searchLobbys);
        startButtonPanel.add(joinLobby);
        
        startPanel.add(lobbyScrollList, BorderLayout.NORTH);
        startPanel.add(startButtonPanel, BorderLayout.SOUTH);
        
        leaveLobby = new JButton("Back");
        startGame = new JButton("Start Game");
        sendMessage = new JButton("Send Chat Message");
        
        leaveLobby.setEnabled(true);
        startGame.setEnabled(false);
        sendMessage.setEnabled(true);
        
        chatInput = new JTextField(30);
        chatOutput = new JTextArea(10,30);
        chatScroll = new JScrollPane(chatOutput);
        
        clientList = new JList();
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        clientScrollList = new JScrollPane(clientList);
        
        inGameButtonPanel = new JPanel(new GridLayout(1,3));
        inGameButtonPanel.add(leaveLobby);
        inGameButtonPanel.add(sendMessage);
        inGameButtonPanel.add(startGame);
        
        inGamePanel.add(clientScrollList, BorderLayout.EAST);
        inGamePanel.add(chatOutput, BorderLayout.NORTH);
        inGamePanel.add(chatInput, BorderLayout.CENTER);
        inGamePanel.add(inGameButtonPanel, BorderLayout.SOUTH);
        
        frame.getContentPane().add(startPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        lobbyAmount = 0;
        selectedLobby = -1;
    }
    
    public void init() throws Exception{
        startLobby.addActionListener(this);
        searchLobbys.addActionListener(this);
        joinLobby.addActionListener(this);
        
        leaveLobby.addActionListener(this);
        startGame.addActionListener(this);
        sendMessage.addActionListener(this);
        
        lobbyList.addListSelectionListener(this);
        clientList.addListSelectionListener(this);
    }
    
    private DefaultListModel getListModel(String[] names){
        DefaultListModel list = new DefaultListModel();
        for(String s : names){
            list.addElement(s);
        }
        return list;
    }
    
    private void findServers() throws Exception{
        String[] lobbys = player.getServerNames();
        lobbyAmount = lobbys.length;
        if(lobbyAmount > 0){
            lobbyList.setModel(getListModel(lobbys));
        }else{
            lobbyList.setModel(getListModel(noLobbys));
        }
    }
    
    private void startLobby() throws Exception{
        player.openLobby();
        
        switchPanels(true);
    }
    
    private void joinLobby() throws Exception{
        player.joinLobby(selectedLobby);
        
        switchPanels(true);
        System.out.println("Done");
    }
    
    private void closeLobby() throws Exception{
        player.closeLobby();
        
        switchPanels(false);
    }
    
    private void startGame(boolean toggle) throws Exception{
        if(player.isHost){
            player.startGame();
        }else{
            player.setReady(toggle);
        }
    }
    
    private void sendMessage() throws Exception{
        String message = chatInput.getText();
        if(!message.isEmpty()){
            player.sendChatMessage(message);
        }
        chatInput.setText("");
    }
    
    private void switchPanels(boolean forward){
        if(forward){
            frame.getContentPane().remove(startPanel);
            frame.getContentPane().add(inGamePanel);
        }else{
            frame.getContentPane().remove(inGamePanel);
            frame.getContentPane().add(startPanel);
        }
        frame.getContentPane().invalidate();
        frame.getContentPane().validate();
        frame.repaint();
    }
    
    public void actionPerformed(ActionEvent e){
        try{
            if(e.getSource() == searchLobbys){
                findServers();
            }else if(e.getSource() == startLobby){
                startLobby();
            }else if(e.getSource() == joinLobby){
                joinLobby();
            }else if(e.getSource() == leaveLobby){
                closeLobby();
            }else if(e.getSource() == startGame){
                startGame(true);
            }else if(e.getSource() == sendMessage){
                sendMessage();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void valueChanged(ListSelectionEvent e) {
        if(e.getSource() == lobbyList){
            if (e.getValueIsAdjusting() == false) {
                selectedLobby = lobbyList.getSelectedIndex();
                joinLobby.setEnabled(selectedLobby!= -1 && selectedLobby < lobbyAmount);
            }
        }else if(e.getSource() == clientList){
            
        }
    }
}