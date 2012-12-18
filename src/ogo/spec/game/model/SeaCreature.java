package ogo.spec.game.model;

public class SeaCreature extends Creature
{

    @Override
    protected int getMoveSpeed(TileType tileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected int getEatValue(Creature creature) {
        assert !(creature instanceof SeaCreature);
        if(creature instanceof AirCreature)
        {
            return 4;
        }
        if(creature instanceof LandCreature)
        {   
            return 6;
        }
        return 0;
    }
    
}