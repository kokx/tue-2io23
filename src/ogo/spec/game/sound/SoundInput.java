package ogo.spec.game.sound;

import javax.sound.sampled.*;

public class SoundInput {

    // TargetDataLine on the microphone.
    TargetDataLine targetDataLine;

    // To control the monitoring of the microphone.
    boolean stopCapture = false;
    boolean threadEnded = true;

    /**
     * Starts monitoring audio input from a microphone and calculates the sound level.
     */
    private void run() {
        AudioFormat audioFormat;
        try {
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

            // Get and display a list of available mixers.
            System.out.println("Available mixers:");
            for (Mixer.Info currentMixer : mixerInfo) {
                System.out.println(currentMixer.getName());
            }

            // Get and display a list of available mixers with record functionality.
            Mixer microphone = null;
            System.out.println("Available mixers with record functionality:");
            for (Mixer.Info currentMixer : mixerInfo) {
                Line.Info targetDataLineInfo = new Line.Info(TargetDataLine.class);
                if (AudioSystem.getMixer(currentMixer).isLineSupported(targetDataLineInfo)) {
                    System.out.println(currentMixer.getName());
                    if (microphone == null) {
                        // Set the first available mixer with record functionality to be the microphone.
                        microphone = AudioSystem.getMixer(currentMixer);
                    }
                }
            }

            // Get everything set up for capture.
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            // Get a TargetDataLine on the microphone.
            // Microphone == null implies no mixer with record functionality found.
            assert microphone != null;
            targetDataLine = (TargetDataLine) microphone.getLine(dataLineInfo);

            // Prepare the line for use.
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Create a thread to capture the microphone data and start it.
            Thread captureThread = new CaptureThread();
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    /**
     * In order to do a direct conversion for the audio bytes to calculate sound level we need
     * signed 8-bit big-endian linear PCM encoding.
     *
     * @return AudioFormat object for a given set of format parameters.
     */
    private AudioFormat getAudioFormat() {
        // 8000, 11025, 16000, 22050, 44100 Hz
        float sampleRate = 8000F;
        // 8, 16
        int sampleSizeInBits = 8;
        // 1 (monotone), 2 (stereo)
        int channels = 1;
        // true, false
        boolean signed = true;
        // true, false
        boolean bigEndian = true;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    /**
     * Calculates the magnitude of the signal using the Root Mean Square method.
     * http://en.wikipedia.org/wiki/Root_mean_square
     *
     * @param audioData ByteArray containing the current amplitudes of a signal.
     * @return The RMS of the signal.
     */
    private int calculateSoundLevel(byte[] audioData) {
        long sum = 0;
        for (byte amplitude : audioData) {
            sum += amplitude;
        }

        double average = sum / audioData.length;

        double sumMeanSquare = 0;
        for (byte amplitude : audioData) {
            sumMeanSquare += Math.pow(amplitude - average, 2);
        }

        double averageMeanSquare = sumMeanSquare / audioData.length;

        int rms = (int) (Math.pow(averageMeanSquare, 0.5));

        System.out.println("RMS: " + rms);
        return rms;
    }

    /**
     * A threat to capture data from the microphone.
     */
    class CaptureThread extends Thread {

        // Temporary buffer of arbitrary sample size.
        byte tempBuffer[] = new byte[500];

        public void run() {
            System.out.println("SoundInput - Starting CaptureThread");
            threadEnded = false;
            stopCapture = false;
            try {
                while (!stopCapture) {
                    // Read data from the microphone's data line.
                    int bufferSize = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (bufferSize > 0) {
                        calculateSoundLevel(tempBuffer);
                    }
                }
                targetDataLine.close();
                threadEnded = true;
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    public static void main(String args[]) {
        new SoundInput().run();
    }
}
