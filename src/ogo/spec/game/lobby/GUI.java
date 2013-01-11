package ogo.spec.game.lobby;

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
    protected Lobby player;

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

    protected String[] noLobbys = {"No Lobbys Were Found"};

    public GUI(Lobby p) throws Exception{
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

        startGame = new JButton("Start Game");

        startGame.setEnabled(false);

        lobbyOutput = new JTextArea(1,30);
        lobbyOutput.setEditable(false);

        inGamePanel.add(lobbyOutput, BorderLayout.NORTH);
        //inGamePanel.add(startGame, BorderLayout.SOUTH);

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

    public void stop(){
        frame.setVisible(false);
        frame.dispose();
    }

    private void startLobby() throws Exception{
        inGamePanel.add(startGame, BorderLayout.SOUTH);

        switchPanels(true);

        frame.repaint();

        player.openLobby();

        startLobbyChecker();
    }

    public void updateLobbyOutput(){
        int clients = player.getClientCount();
        lobbyOutput.setText("There are now " + clients + " clients in the Lobby");
        if(player.isHost && clients > 1){
            startGame.setEnabled(true);
        }else{
            startGame.setEnabled(false);
        }
    }

    private void startLobbyChecker(){
        lobbyChecker = new Timer(1000, this);
        lobbyChecker.start();
    }

    class JoinLobbyRunnable implements Runnable{
        Lobby p;
        int lobby;
        public JoinLobbyRunnable(Lobby player, int selectedLobby){
            p = player;
            lobby = selectedLobby;
        }

        public void run(){
            try{
                p.joinLobby(selectedLobby);
                p.connectToLobby();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void joinLobby() throws Exception{

        new Thread(new JoinLobbyRunnable(player, selectedLobby)).start();

        switchPanels(true);

        frame.repaint();

        lobbyOutput.setText("You Joined a Lobby with IP: ");
    }

    private void startGame() throws Exception{
        if(player.isHost){
            player.startGame();
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
