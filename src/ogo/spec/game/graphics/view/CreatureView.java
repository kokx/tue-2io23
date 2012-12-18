package ogo.spec.game.graphics.view;

import ogo.spec.game.model.*;

/**
 *
 * @author s102877
 */
public class CreatureView {

    Creature creature;
    Tile previousLocation;
    Timer timer;
    int t0;
    double unit;

    public CreatureView(Creature creature, Timer timer) {
        this.creature = creature;
        this.timer = timer;
    }

    public void move(double animationLength) {
        previousLocation = creature.getPath().getPreviousTile();
        t0 = timer.getTime();
        unit = timer.getSleepTime() / animationLength;
    }

    public Vector getCurrentLocation() {
        if (previousLocation == null) {
            return new Vector(creature.getPath().getCurrentTile().getX()+0.5,creature.getPath().getCurrentTile().getY()+0.5,0);
        } else {
            double x = (creature.getPath().getCurrentTile().getX() - previousLocation.getX() + 0.5) / (unit * (timer.getTime() - t0));

            double y = (creature.getPath().getCurrentTile().getY() - previousLocation.getY() + 0.5) / (unit * (timer.getTime() - t0));
            double z = 0;
            Vector V = new Vector(x, y, z); //vector to move over

            x = previousLocation.getX();
            y = previousLocation.getY();
            z = 0;
            Vector P = new Vector(x, y, z); //previous location

            return P.add(V);
        }
    }
}
