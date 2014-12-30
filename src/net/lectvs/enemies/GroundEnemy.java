package net.lectvs.enemies;

import net.lectvs.Enemy;
import net.lectvs.Game;
import net.lectvs.Lectvs;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

/**
 * Created by Hayden on 11/10/2014.
 */
public class GroundEnemy extends Enemy {

    public float velocity;

    public GroundEnemy(float x, float y, int velocity) {
        super(x, y);
        this.velocity = velocity;
        setBounds(0, 0, 32, 32);

        health = maxHealth = 10;
        vx = 1;

        maxSpeed = Math.abs(velocity);
        acceleration = 0.4f;
        friction = 0.4f;
    }

    public void update() {
        if (velocity < 0) {
            if (vx >= -maxSpeed) {
                vx -= Math.min(acceleration, maxSpeed + vx);
            } else {
                vx += friction;
            }
        }
        else {
            if (vx <= maxSpeed) {
                vx += Math.min(acceleration, maxSpeed - vx);
            } else {
                vx -= friction;
            }
        }

        vy += 0.3f;

        move(true);

        if (collidingHoriz) {
            velocity = -velocity;
            vx = velocity;
        }

        super.update();
    }

    public void render() {
        GL11.glColor4f(1, 0, 0, 1);
        Lectvs.drawRect(leftBound() - Game.camx, topBound() - Game.camy, w, h);
        super.render();
    }
}
