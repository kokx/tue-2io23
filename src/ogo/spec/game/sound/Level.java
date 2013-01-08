package ogo.spec.game.sound;

public class Level
{

    public final static long TIMEOUT = 100;

    /**
     * Runnable that checks the sound level.
     */
    private class SoundLevelRunnable implements Runnable
    {

        long timeout;
        double soundLevel;
        boolean stop = false;

        SoundLevelRunnable(long timeout)
        {
            this.timeout = timeout;
        }

        /**
         * Stop the runnable.
         */
        void stop()
        {
            stop = true;
        }

        /**
         * Get the sound level.
         */
        double getSoundLevel()
        {
            return soundLevel;
        }

        /**
         * Determine the sound level.
         *
         * @todo Implement this method
         */
        private double determineSoundLevel()
        {
            return 0;
        }

        /**
         * Let the thread sleep.
         */
        private void sleep()
        {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                // just continue execution
            }
        }

        /**
         * Run method.
         */
        public void run()
        {
            while (!stop) {
                soundLevel = determineSoundLevel();
                sleep();
            }
        }
    }

    /**
     * Sound level runnable.
     */
    private SoundLevelRunnable run;

    /**
     * Constructor.
     *
     * Starts a thread that will keep checking the sound level.
     */
    public Level()
    {
        run = new SoundLevelRunnable(TIMEOUT);

        new Thread(run).start();
    }

    /**
     * Get the sound level.
     *
     * @return Current sound level
     */
    public double getLevel()
    {
        return run.getSoundLevel();
    }
}
