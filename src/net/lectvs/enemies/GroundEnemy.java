package net.lectvs.enemies;

import net.lectvs.Enemy;
import net.lectvs.Game;
import net.lectvs.Maths;
import org.lwjgl.opengl.GL11;

/**
 * Created by Hayden on 11/10/2014.
 */
public class GroundEnemy extends Enemy {

    public int velocity;

    public GroundEnemy(float x, float y, int velocity) {
        super(x, y);
        this.velocity = velocity;
        setBounds(0, 0, 32, 32);

        maxHealth = 3;
        health = maxHealth;

        maxSpeed = Math.abs(velocity);
        //inertia = 1;
    }

    public void update() {

        vx = velocity;
        vy += 0.3f;
        move(true);

        if (vx == 0) {
            velocity = -velocity;
        }

        super.update();
    }

    public void render() {
        GL11.glColor4f(1, 0, 0, 1);
        Maths.drawRect(leftBound() - Game.camx, topBound() - Game.camy, w, h);
        super.render();
    }
}
