package net.lectvs;

/**
 * Created with IntelliJ IDEA.
 * User: Hayden
 * Date: 7/6/13
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceTimer {

    public static int sequence = 0;
    public static boolean isActive = false;
    public static float timer = 0, lastTime = 0;

    public static void update() {
        if (isActive) {
            timer += Main.delta;
            if (sequence == 0) { // For running different timed sequences
                if (t(1)) { } // What to do when the timer reaches 1 second, etc
            }
        }
        lastTime = timer;
    }

    // Starts a predefined sequence. Only one sequence can be running at a time!
    public static void startSequence(int num) {
        sequence = num;
        isActive = true;
        timer = 0;
        lastTime = 0;
    }

    // Stops the current sequence
    public static void stop() {
        isActive = false;
    }

    // Checks whether the current time is EXACTLY t seconds
    public static boolean t(float time) {
        return timer >= time && lastTime < time;
    }
}
