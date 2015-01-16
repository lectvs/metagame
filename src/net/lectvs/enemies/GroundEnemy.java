package net.lectvs.enemies;

import net.lectvs.Enemy;
import net.lectvs.Game;
import net.lectvs.Lectvs;
import net.lectvs.Sprite;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

/**
 * Created by Hayden on 11/10/2014.
 */
public class GroundEnemy extends Enemy {

    public float velocity;
    public boolean isAttacking;

    public GroundEnemy(float x, float y, int velocity) {
        super(x, y);
        sprite = new Sprite("enemy.png", 32, 32, 0, 1, 1);
        sprite.setDimensions(64, 64);
        sprite.addAnim("run", true, Math.abs(velocity / 10f), new int[]{6, 7, 8, 9, 10, 11, 12, 13});
        sprite.addAnim("fall", true, 1f, new int[]{15});
        sprite.addAnim("attack", false, 0.08f, new int[]{17, 18, 19, 20}, new Runnable() { public void run() { isAttacking = false; } });

        setBounds(-9, -16, 18, 47);

        health = maxHealth = 10;
        vx = 1;

        this.velocity = velocity;
        maxSpeed = Math.abs(velocity);
        acceleration = 0.8f;
        friction = 0.4f;
    }

    public void update() {
        if (onGround) {
            if (velocity < 0) {
                if (vx >= -maxSpeed) {
                    vx -= Math.min(acceleration, maxSpeed + vx);
                } else {
                    vx += friction;
                }
            } else {
                if (vx <= maxSpeed) {
                    vx += Math.min(acceleration, maxSpeed - vx);
                } else {
                    vx -= friction;
                }
            }
        }

        vy += 0.3f;

        move(true);

        if (collidingHoriz) {
            velocity = -velocity;
            vx = velocity;
        }

        sprite.sx = Math.signum(velocity);
        if (sprite.sx == 0) sprite.sx = 1;
        sprite.play("run");

        if (!onGround)
            sprite.play("fall");

        super.update();
    }

    public void render() {
        GL11.glColor4f(1, 0, 0, 1);
        if (!(Math.floor(flashTime * 4) % 4 % 2 == 1)) {
            sprite.render(x - Game.camx, y - Game.camy);
        }
        super.render();
    }
}
