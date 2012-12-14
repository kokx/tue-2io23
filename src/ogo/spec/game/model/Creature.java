package ogo.spec.game.model;

public abstract class Creature extends Inhabitant
{
    private Creature attackingCreature;
    private int life;
    
    protected int moveCooldown;
    protected int attackCooldown;
    protected int lifeCooldown;
    
    public Creature()
    {
        this.moveCooldown = -1;
        this.attackCooldown = 0;
        this.lifeCooldown = 0;
    }
    
    public void tick()
    {
        this.lifeTick();
        this.attackTick();
        this.moveTick();
    }
    
    private void moveTick()
    {
        
    }
    
    private void attackTick()
    {
        
    }
    
    private void lifeTick()
    {
        if(this.lifeCooldown == 0)
        {
            this.dealDamage(1);
            //informal specs say life should decrease with 1 every 5seconds.
            this.lifeCooldown = 5000 / Game.TICK_TIME_IN_MS;
        }
        this.lifeCooldown--;
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
