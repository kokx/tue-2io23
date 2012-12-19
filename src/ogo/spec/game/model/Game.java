package ogo.spec.game.model;

import java.util.Timer;
import java.util.Arrays;
import java.util.Iterator;

public class Game implements Iterable<Player>
{

    public static final int TICK_TIME_IN_MS = 10;

    private Timer Timer;
    private Player[] players;
    private GameMap map;

    public Game(Player[] players, GameMap map) {
        this.players = players;
        this.map = map;
    }

    private void tick()
    {

    }

    public GameMap getMap() {
        return map;
    }

    @Override
    public Iterator<Player> iterator() {
        return Arrays.asList(players).iterator();
    }
}
