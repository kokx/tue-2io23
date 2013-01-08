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

public class LobbyGui extends Lobby implements ActionListener, ListSelectionListener{
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
    
    protected int lobbyAmount = 0;
    protected int selectedLobby = -1;
    
    protected String[] noLobbys = {"No Lobbys Were Found"};
    
    public LobbyGui() throws Exception{
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
        sendMessage.setEnabled(false);
        
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
    }
    
    public void init() throws Exception{
        nickname = JOptionPane.showInputDialog(null, "Enter Your Nickname: ", "", 1);
        frame.setTitle("Welcome " + nickname + ". Enjoy Playing This Awesome Game!!!");
        
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
        String[] lobbys = getServerNames();
        lobbyAmount = lobbys.length;
        if(lobbyAmount > 0){
            lobbyList.setModel(getListModel(lobbys));
        }else{
            lobbyList.setModel(getListModel(noLobbys));
        }
    }
    
    private void openLobby() throws Exception{
        startLobby();
        
        switchPanels(true);
    }
    
    private void joinServer() throws Exception{
        joinLobby(selectedLobby);
        
        switchPanels(false);
    }
    
    private void leaveLobby() throws Exception{
        switchPanels(false);
        
        closeLobby();
    }
    
    private void startLobby() throws Exception{
        startServer();
        
        switchPanels(true);
    }
    
    private void sendMessage() throws Exception{
        String message = chatInput.getText();
        
        sendChatMessage(message);
        
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
                joinServer();
            }else if(e.getSource() == leaveLobby){
                leaveLobby();
            }else if(e.getSource() == startGame){
                startLobby();
            }else if(e.getSource() == sendMessage){
                sendMessage();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            System.err.println(ex.getMessage());
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
    
    public static void main(String[] args) throws Exception{
        new LobbyGui().init();
    }
}