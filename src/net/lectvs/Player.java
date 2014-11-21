package net.lectvs;

import org.lwjgl.opengl.GL11;

/**
 * Created by Hayden on 9/30/2014.
 */
public class Player extends Entity {

    public float dashForce, jumpForce;
    public boolean hasDoubleJumped;

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
        friction = 0.8f;
        acceleration = 0.8f;
        deceleration = 1.6f;

        dashForce = 20;
        jumpForce = 10;

        hasDoubleJumped = false;
    }

    public void update() {

        // Handle input and translate it into velocity, animations, etc
        if (Main.keyLeftDown) {
            if (vx > 0) {
                vx -= deceleration;
            }
            else if (vx > -maxSpeed) {
                vx -= acceleration;
            }
            else {
                vx += friction;
            }

            if (Stats.canDash && Main.keySelectDown && !Main.keySelectLast) {
                addForce(-dashForce, 0);
            }

            sprite.play("run");
            sprite.sx = -1;
        } else if (Main.keyRightDown) {
            if (vx < 0) {
                vx += deceleration;
            }
            else if (vx < maxSpeed) {
                vx += acceleration;
            }
            else {
                vx -= friction;
            }

            if (Stats.canDash && Main.keySelectDown && !Main.keySelectLast) {
                addForce(dashForce, 0);
            }

            sprite.play("run");
            sprite.sx = 1;
        } else {
            vx -= Math.min(Math.abs(vx), friction) * Math.signum(vx);
            sprite.play("idle");
        }

        // Vertical movement
        ay = 0.4f;
        if (Main.keyJumpDown && !Main.keyJumpLast) {
            if (onGround) {
                vy = -jumpForce;
                onGround = false;
            } else if (Stats.canDoubleJump && !hasDoubleJumped) {
                vy = -jumpForce;
                hasDoubleJumped = true;
            }
            if (vy < 0) {
                ay = 0.2f;
            }
        }

        if (onGround) {
            hasDoubleJumped = false;
        }

        if (vy < 0) {
            sprite.play("jump");
        } else if (!onGround) {
            sprite.play("fall");
        }

        vx += ax;
        vy += ay;

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
