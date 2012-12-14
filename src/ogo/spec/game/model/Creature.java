package ogo.spec.game.model;

public class Creature
{
    private int life;
    
    protected int moveCooldown;
    protected int attaackCooldown;
    protected int lifeCooldown;
    
    public void tick()
    {
        
    }
    
    private void moveTick()
    {
        
    }
    
    private void attackTick()
    {
        
    }
    
    private void lifeTick()
    {
        
    }
    
    protected void die()
    {
        
    }
    
    public void select(Tile tile)
    {
            
    }
    
    private void doMove(Tile tile)
    {
        
    }
    
    private void doEat(Food food)
    {
        
    }
    
    private void doAttack(Creature creature)
    {
        
    }
    
    public boolean dealDamage(int damage)
    {
        return false;
    }
    
    private void eatCreature(Creature creature)
    {
    }
    
    protected abstract int getMoveSpeed(TileType tileType);
    
    protected abstract int getEatValue(Creature creature);
    
    protected boolean canMove()
    { 
        return true;
    }
}
