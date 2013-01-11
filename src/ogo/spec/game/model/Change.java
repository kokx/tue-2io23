package ogo.spec.game.model;

import ogo.spec.game.multiplayer.GameProto.Token;

public class Change
{

    public Change()
    {
    }

    public Change(Token.Change change)
    {
        // TODO: import from change
    }

    public enum ChangeType {
        MOVE_CREATURE, HEALTH, ENERGY, ATTACKING_CREATURE
    }

    public long tick;
    public Player player;

    public ChangeType type;

    public Creature creature;

    // for MOVE_CREATURE
    public int x;
    public int y;

    // for HEALTH / ENERGY
    public int newValue;

    // for ATTACKING_CREATURE
    public int otherCreatureId;
}
