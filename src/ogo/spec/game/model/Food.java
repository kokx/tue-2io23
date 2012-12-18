package ogo.spec.game.model;

public class Food extends Inhabitant
{
    public static final int HEALTH_REWARDED = 1;
    
    public void eat()
    {
        super.currentTile.setInhabitant(null);
    }
}
