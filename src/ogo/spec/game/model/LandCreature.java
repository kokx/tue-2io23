package ogo.spec.game.model;

public class LandCreature extends Creature
{

    @Override
    protected int getMoveSpeed(TileType tileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected int getEatValue(Creature creature) {
        assert !(creature instanceof LandCreature);
        if(creature instanceof AirCreature)
        {
            return 4;
        }
        if(creature instanceof SeaCreature)
        {   
            return 6;
        }
        return 0;
    }
    
}