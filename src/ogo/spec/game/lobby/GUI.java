package ogo.spec.game.lobby;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI implements ActionListener, ListSelectionListener{
    protected Lobby lobby;

    protected JFrame frame;

    protected JList lobbyList;
    protected JScrollPane lobbyScrollList;

    protected JPanel startPanel;
    protected JPanel inGamePanel;

    protected JPanel startButtonPanel;

    protected JButton startLobby;
    protected JButton searchLobbys;
    protected JButton joinLobby;

    protected JButton startGame;

    protected JTextArea lobbyOutput;

    protected Timer lobbyChecker;

    protected int lobbyAmount;
    protected int selectedLobby;
    protected JRadioButton[][] creatureButtons;

    protected String[] noLobbys = {"No Lobbys Were Found"};
    
    String nickname;

    public GUI(Lobby l) throws Exception{
        lobby = l;

        nickname = JOptionPane.showInputDialog(null, "Enter your Nickname", null, 1);
        while(nickname.isEmpty()){
            nickname = JOptionPane.showInputDialog(null, "Enter your Nickname, and this time, don't leave it blank!", null, 1);
        }

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

        startGame = new JButton("Start Game");

        startGame.setEnabled(false);

        lobbyOutput = new JTextArea(1,30);
        lobbyOutput.setEditable(false);
        
        ButtonGroup[] groups = new ButtonGroup[3];
        creatureButtons = new JRadioButton[3][3];
        JPanel radioPanel = new JPanel(new GridLayout(3,3));
        String[] creatures = {"Land Creature", "Sea Creature", "Air Creature"};
        for(int i = 0; i < 3; i++){
            groups[i] = new ButtonGroup();
            for(int j = 0; j < 3; j++){
                creatureButtons[i][j] = new JRadioButton(creatures[j]);
                creatureButtons[i][j].setSelected(j == i);
                groups[i].add(creatureButtons[i][j]);
                
                radioPanel.add(creatureButtons[i][j]);
            }
        }
        

        inGamePanel.add(lobbyOutput, BorderLayout.NORTH);
        inGamePanel.add(startGame);
        inGamePanel.add(radioPanel);

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

        startGame.addActionListener(this);

        lobbyList.addListSelectionListener(this);
        
        for(int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++) {
                creatureButtons[i][j].addActionListener(this);
            }
        }
    }

    private DefaultListModel getListModel(String[] names){
        DefaultListModel list = new DefaultListModel();
        for(String s : names){
            list.addElement(s);
        }
        return list;
    }

    String[] serverNames;
    private void findServers() throws Exception{
        serverNames = lobby.getServerNames();
        lobbyAmount = serverNames.length;
        if(lobbyAmount > 0){
            lobbyList.setModel(getListModel(serverNames));
        }else{
            lobbyList.setModel(getListModel(noLobbys));
        }
    }

    public void stop(){
        frame.setVisible(false);
        frame.dispose();
        if(lobbyChecker != null){
            lobbyChecker.stop();
        }
    }

    private void startLobby() throws Exception{
        if(lobby.openLobby()){
            switchPanels(true);

            startLobbyChecker();
        }
    }

    public void updateLobbyOutput(){
        int clients = lobby.getClientCount();
        lobbyOutput.setText("There are now " + clients + " clients in the Lobby");
        if(lobby.canStartGame() && clients > 1){
            startGame.setEnabled(true);
        }else{
            startGame.setEnabled(false);
        }
    }

    private void startLobbyChecker(){
        lobbyChecker = new Timer(1001, this);
        lobbyChecker.start();
    }

    class JoinLobbyRunnable implements Runnable{
        Lobby lobby;
        int selectedLobby;
        public JoinLobbyRunnable(Lobby l, int selected){
            lobby = l;
            selectedLobby = selected;
        }

        public void run(){
            try{
                lobby.joinLobby(selectedLobby);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public int[] getCreatureInfo(){
        int[] result = new int[3];
        for(int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++) {
                if(creatureButtons[i][j].isSelected()){
                    result[i] = j;
                }
            }
        }
        return result;
    }

    private void joinLobby() throws Exception{
        startGame.setText("Ready!");
        startGame.setEnabled(true);

        new Thread(new JoinLobbyRunnable(lobby, selectedLobby)).start();

        switchPanels(true);

        frame.repaint();

        lobbyOutput.setText("You Joined a Lobby with IP: " + serverNames[selectedLobby]);
    }

    private void startGame() throws Exception{
        startGame.setEnabled(false);
        if(lobby.isHost){
            lobby.startGame();
        }else{
            lobby.setReady();
            lobby.finishConnection();
        }
    }

    private void switchPanels(boolean forward){
        if(forward){
            startPanel.setVisible(false);
            inGamePanel.setVisible(true);
            frame.getContentPane().remove(startPanel);
            frame.getContentPane().add(inGamePanel);
        }else{
            startPanel.setVisible(true);
            inGamePanel.setVisible(false);
            frame.getContentPane().remove(inGamePanel);
            frame.getContentPane().add(startPanel);
        }
        frame.getContentPane().invalidate();
        frame.getContentPane().validate();
        frame.repaint();
    }
    
    public void checkRadioButtons(){
        int[] count = new int[3];
        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++){
                if(creatureButtons[i][j].isSelected()){
                    count[j]++;
                }
            }
        }
        int over = -1;
        for(int i = 0; i < 3; i++){
            if(count[i] >= 2){
                over = i;
            }
        }
        if(over != -1){
            for(int i = 0; i < 3; i++){
                if(!creatureButtons[i][over].isSelected()){
                    creatureButtons[i][over].setEnabled(false);
                }
            }
        }else{
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    creatureButtons[i][j].setEnabled(true);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e){
        try{
            if(e.getSource() == searchLobbys){
                findServers();
            }else if(e.getSource() == startLobby){
                startLobby();
            }else if(e.getSource() == joinLobby){
                joinLobby();
            }else if(e.getSource() == startGame){
                startGame();
            }else if(e.getSource() == lobbyChecker){
                updateLobbyOutput();
            }else{
                checkRadioButtons();
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
        }
    }
}
