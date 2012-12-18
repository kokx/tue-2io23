package ogo.spec.game.model;

public abstract class Creature extends Inhabitant {

    public static final int ATTACK_COOLDOWN_TICKS = 200;
    protected Creature attackingCreature;
    private int life;
    private CreaturePath path;
    protected int moveCooldown;
    protected int attackCooldown;
    protected int lifeCooldown;

    public Creature() {
        this.moveCooldown = -1;
        this.attackCooldown = 0;
        this.lifeCooldown = 0;
        this.life = 15;
        path = new CreaturePath(null, super.currentTile);
    }

    public int getLife() {
        return life;
    }

    public void tick() {
        this.lifeTick();
        this.attackTick();
        this.moveTick();
    }

    private void moveTick() {
    }

    private void attackTick() {
        if (this.attackCooldown == 0 && this.attackingCreature != null) {
            Tile attackCreatureTile = this.attackingCreature.getCurrentTile();
            if (super.currentTile.isAdjacent(attackCreatureTile)) {
                this.strike();
                this.attackCooldown = Creature.ATTACK_COOLDOWN_TICKS;
            } else {
                this.attackingCreature = null;
            }
        }
        if ((--this.attackCooldown) < 0) {
            this.attackCooldown = 0;
        }
        System.out.println(this.attackCooldown);
    }

    private void strike() {
        int damage = 3;
        //TODO: listen to mic for damage
        if (this.attackingCreature.dealDamage(damage)) {
            //he dead
            this.eatCreature(this.attackingCreature);
            this.attackingCreature = null;
        }
    }

    private void lifeTick() {
        if (this.lifeCooldown == 0) {
            this.dealDamage(1);
            //informal specs say life should decrease with 1 every 5seconds.
            this.lifeCooldown = 5000 / Game.TICK_TIME_IN_MS;
        }
        if((--this.lifeCooldown) < 0)
            this.lifeCooldown = 0;
    }

    protected void die() {
    }

    public void select(Tile tile) {
    }

    private void doMove(Tile tile) {
    }

    private void doEat(Food food) {
    }

    private void doAttack(Creature creature) {
    }

    public boolean dealDamage(int damage) {
        this.life -= damage;
        if(this.life <= 0)
        {
            this.die();
            return true;
        }
        else
        {
            return false;
        }
    }

    private void eatCreature(Creature creature) {
        this.addLife(this.getEatValue(creature));
    }

    protected void addLife(int life)
    {
        this.life += life;
        if(this.life > 20)
        {
            this.life = 20;
        }
    }
    
    protected abstract int getMoveSpeed(TileType tileType);

    protected abstract int getEatValue(Creature creature);

    protected boolean canMove() {
        return true;
    }

    /**
     * Get the path.
     */
    public CreaturePath getPath() {
        return path;
    }
}
