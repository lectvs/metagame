package net.lectvs;

import org.lwjgl.opengl.GL11;

/**
 * Created by Hayden on 9/30/2014.
 */
public class Player extends Entity {

    public float jumpForce;

    public Player(int x, int y) {
        super(x, y);
        setBounds(-9, -16, 18, 47);

        sprite = new Sprite("player_white.png", 32, 32, 0, 1, 1);
        sprite.setDimensions(64, 64);
        sprite.addAnim("idle", new float[]{1, 0.1f, 0, 1, 2, 3, 4, 5});
        sprite.addAnim("run", new float[]{1, 0.1f, 6, 7, 8, 9, 10, 11, 12, 13});
        sprite.addAnim("jump", new float[]{1, 1f, 14});
        sprite.addAnim("fall", new float[]{1, 1f, 15});
        sprite.addAnim("attack", new float[]{1, .08f, 17, 18, 19, 20});

        maxSpeed = 5;
        inertia = 3;
        jumpForce = 14;
    }

    public void update() {

        // Handle input and translate it into velocity, animations, etc
        // vx = 0;
        if (Main.keyLeftDown) {
            ax = Math.min(-(maxSpeed + vx) / inertia, 0);

            sprite.play("run");
            sprite.sx = -1;
        }
        else if (Main.keyRightDown) {
            ax = Math.max((maxSpeed - vx) / inertia, 0);

            sprite.play("run");
            sprite.sx = 1;
        }
        else {
            if (onGround)
            {
                ax = -vx / inertia * 2f;
            } else {
                ax = -vx / inertia / 3f;
            }
            sprite.play("idle");
        }

        // Vertical movement
        ay = 0.4f;
        if (Main.keySelectDown && !Main.keySelectLast) {
            addForce(50, -10);
        }
        if (Main.keyJumpDown) {
            if (onGround)
            {
                vy = -jumpForce;
                onGround = false;
            }
            if (vy < 0)
            {
                ay = 0.2f;
            }
        }
        if (vy < 0) {
            sprite.play("jump");
        } else if (!onGround) {
            sprite.play("fall");
        }

        vx += ax;
        vy += ay;
        vy *= 0.95;

        // Move the character
        move(true);
    }

    public void render() {
        super.render();

        // Draws collision box over player
        GL11.glColor4f(0.2f, 1, 0.2f, 1);
        //Maths.drawOutlineRect(leftBound() - Game.camx, topBound() - Game.camy, w, h, 1);
    }


}
