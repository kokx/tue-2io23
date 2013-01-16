package ogo.spec.game.model;

public class Change implements Comparable<Change>
{

    public enum ChangeType {
        MOVE_CREATURE, HEALTH, ENERGY, ATTACKING_CREATURE, PING
    }

    public long id = -1;
    public long tick;
    public Player player;
    public int playerId;

    public ChangeType type;

    public Creature creature;
    public int creatureId;

    // for MOVE_CREATURE
    public int x;
    public int y;

    // for HEALTH / ENERGY
    public int newValue;

    // for ATTACKING_CREATURE
    public int otherCreatureId;

    /**
     * Compare to a change.
     */
    public int compareTo(Change ch)
    {
        if (ch.tick > tick) {
            return 1;
        } else if (ch.tick == tick) {
            return 0;
        } else {
            return -1;
        }
    }
}
