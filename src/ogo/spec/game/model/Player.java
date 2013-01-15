package ogo.spec.game.model;

import java.util.Arrays;
import java.util.Iterator;

public class Player implements Iterable<Creature>
{
    private String name;
    
    private int id;

    private Creature[] creatures = new Creature[0];

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void setCreatures(Creature[] creatures) {
        this.creatures = creatures;
    }
    
    public int getId(){
        return id;
    }

    @Override
    public Iterator<Creature> iterator() {
        return Arrays.asList(creatures).iterator();
    }
    
    public Creature[] getCreatures()
    {
        return this.creatures;
    }
    
    public boolean isAttacking() {
        boolean result = false;
        for(Creature creature : creatures) {
            if (creature.attackingCreature != null) {
                result = true;
                break;
            }
        }
        return result;
    }
}
