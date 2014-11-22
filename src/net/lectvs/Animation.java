package net.lectvs;

/**
 * Created by Hayden on 11/21/2014.
 */
public class Animation {

    public boolean repeat;
    public float delay;
    public int[] frames;
    public Runnable callback;

    public Animation(boolean repeat, float delay, int[] frames) {
        this.repeat = repeat;
        this.delay = delay;
        this.frames = frames;
    }

    public Animation(boolean repeat, float delay, int[] frames, Runnable callback) {
        this.repeat = repeat;
        this.delay = delay;
        this.frames = frames;
        this.callback = callback;
    }
}
