package ogo.spec.game.model;

import java.util.Timer;
import java.util.Arrays;
import java.util.Iterator;

public class Game implements Iterable<Player>
{
    public static void main(String[] args) throws InterruptedException
    {
        Creature c = new LandCreature();
        Tile t1 = new Tile(TileType.LAND, 2,2);
        t1.setInhabitant(c);
         
        Creature c2 = new LandCreature();
        Tile t2 = new Tile(TileType.LAND, 1, 3);
        t2.setInhabitant(c2);
        
        c.attackingCreature = c2;
        while(true)
        {
            c.tick();
            Thread.sleep(10);
            System.out.println("C: " + c.getLife());
            System.out.println("C2: "  + c2.getLife());
        }
    }

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