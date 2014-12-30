package net.lectvs;

/**
 * Created by hayden on 12/3/14.
 */
public class Hitbox extends Entity {

    public boolean active;

    public Hitbox(float x, float y, float w, float h) {
        super(x, y);
        this.w = w;
        this.h = h;
        bx = by = 0;
        ox = oy = 0;

        active = true;
    }
}
