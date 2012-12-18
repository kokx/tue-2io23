package ogo.spec.game.graphics.view;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s102877
 */
public class Timer implements Runnable {
    private int t;
    private long sleepTime;

    public Timer() {
        t = 0;
        sleepTime = 1000;
    }

    public Timer(long sleepTime) {
        t = 0;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        while (true) {
            t++;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int getTime() {
        return t;
    }

    public long getSleepTime() {
        return sleepTime;
    }
}
