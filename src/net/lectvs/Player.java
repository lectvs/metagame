package net.lectvs;

import org.lwjgl.opengl.GL11;

/**
 * Created by Hayden on 9/30/2014.
 */
public class Player extends Entity {

    public float dashForce, jumpForce;
    public boolean hasDoubleJumped, isDashing, isAttacking;
    public Wall attackbox;

    public Player(int x, int y) {
        super(x, y);
        setBounds(-9, -16, 18, 47);

        sprite = new Sprite("player_white.png", 32, 32, 0, 1, 1);
        sprite.setDimensions(64, 64);
        sprite.addAnim("idle", true, 0.1f, new int[]{0, 1, 2, 3, 4, 5});
        sprite.addAnim("run", true, 0.1f, new int[]{6, 7, 8, 9, 10, 11, 12, 13});
        sprite.addAnim("jump",true, 1f, new int[]{14});
        sprite.addAnim("fall", true, 1f, new int[]{15});
        sprite.addAnim("attack", false, 0.08f, new int[]{17, 18, 19, 20}, new Runnable() { public void run() { isAttacking = false; } });
        sprite.addAnim("dash", true, 1f, new int[]{9});

        maxSpeed = 5;
        friction = 0.8f;
        acceleration = 0.8f;
        deceleration = 1.6f;

        dashForce = 20;
        jumpForce = 10;

        hasDoubleJumped = false;
        isDashing = false;
        isAttacking = false;
    }

    public void update() {

        // Handle input and translate it into velocity, animations, etc

        // Horizontal Movement
        if (Main.keyLeftDown) {
            if (vx > 0) {
                vx -= deceleration;
            }
            else if (vx >= -maxSpeed) {
                vx -= Math.min(acceleration, maxSpeed + vx);
            }
            else {
                vx += friction;
            }

            if (Stats.canDash && Main.keyDashDown && !Main.keyDashLast) {
                addForce(-dashForce, 0);
                isDashing = true;
                isAttacking = false;
                sprite.play("dash");
            }

            if (!isDashing && !isAttacking)
                sprite.play("run");

            sprite.sx = -1;
        } else if (Main.keyRightDown) {
            if (vx < 0) {
                vx += deceleration;
            }
            else if (vx <= maxSpeed) {
                vx += Math.min(acceleration, maxSpeed - vx);
            }
            else {
                vx -= friction;
            }

            if (Stats.canDash && Main.keyDashDown && !Main.keyDashLast) {
                addForce(dashForce, 0);
                isDashing = true;
                isAttacking = false;
                sprite.play("dash");
            }

            if (!isDashing && !isAttacking)
                sprite.play("run");

            sprite.sx = 1;
        } else {
            vx -= Math.min(Math.abs(vx), friction) * Math.signum(vx);

            if (!isDashing && !isAttacking)
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

        if (isDashing) {
            if (Math.abs(vx) < 1.5f * maxSpeed) {
                isDashing = false;
            }
            onGround = false;
            vy = 0;
            ay = 0;
        }

        if (onGround) {
            hasDoubleJumped = false;
        }

        if (!isDashing && !isAttacking) {
            if (vy < 0) {
                sprite.play("jump");
            } else if (!onGround) {
                sprite.play("fall");
            }
        }

        if (Main.keyAttackDown && !Main.keyAttackLast) {
            isAttacking = true;
            sprite.playOverride("attack");
        }

        if (isAttacking && sprite.frame == 1)
            attackbox = new Wall((int)leftBound(), (int)topBound(), (int)w, (int)h);
        else
            attackbox = null;

        vx += ax;
        vy += ay;

        // Move the character
        move(true);
    }

    public void render() {
        super.render();

        // Draws collision box over player
        GL11.glColor4f(0.2f, 1, 0.2f, 1);
        //Lectvs.drawOutlineRect(leftBound() - Game.camx, topBound() - Game.camy, w, h, 1);
        if (attackbox != null) attackbox.render();
    }


}
