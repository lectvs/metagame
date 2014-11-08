package net.lectvs;

import org.lwjgl.opengl.GL11;

/**
 * Created by Hayden on 9/30/2014.
 */
public class Player extends Entity {

    public float ax, ay;
    public boolean onGround;

    public float speed = 3;
    public float jumpForce = 14;

    public Player(int x, int y) {
        super(x, y);
        setBounds(-9, -16, 18, 32);

        sprite = new Sprite("player.png", 16, 16, 0, 1, 1);
        sprite.setDimensions(32, 32);
        sprite.addAnim("idle", new float[]{1, 0.125f, 0, 1, 2, 3});
        sprite.addAnim("run", new float[]{1, 0.125f, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14});
        sprite.addAnim("jump", new float[]{1, 1f, 9});
        sprite.addAnim("fall", new float[]{1, 1f, 11});
    }

    public void update() {

        // Handle input and translate it into velocity, animations, etc
        vx = 0;
        if (Main.keyLeftDown) {
            ax = -(4 + vx) / 3;
            vx = -speed;

            sprite.play("run");
            sprite.sx = -1;
        }
        else if (Main.keyRightDown) {
            ax = (4 - vx) / 3;
            vx = speed;

            sprite.play("run");
            sprite.sx = 1;
        }
        else {
            if (onGround)
            {
                ax = -vx / 2;
            } else {
                ax = -vx / 8;
            }
            sprite.play("idle");
        }

        // Vertical movement
        ay = 0.4f;
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

        //vx += ax;
        vy += ay;
        vy *= 0.95;

        // Move the character
        move(true);
    }

    public void render() {
        super.render();

        // Draws collision box over player
        GL11.glColor4f(1, 0, 0, 1);
        Maths.drawOutlineRect(leftBound() - Game.camx, topBound() - Game.camy, w, h, 1);
    }

    // Moves the player
    public void move(boolean collideWithWalls) {
        x += vx;
        if (collideWithWalls) {
            collideWithWallsX();
        }

        y += vy;
        if (onGround)
            y += 2;     // Moves the player down extra pixels to ensure it "sticks" to slopes when walking down them
        if (collideWithWalls) {
            collideWithWallsY();
        }
    }

    public void collideWithWallsX() {

        // If running up/down a slope, reduce movement speed accordingly
        Slope slope = collideSlopes(x, y);
        if (slope != null && onGround) {
            x = x - vx + slope.w / (float)Maths.distance(0, 0, slope.w, slope.h) * vx;
        }

        // Collide with walls
        Wall wall = collideWalls(x, y);
        if (wall != null) {
            if (vx > 0) {
                x = wall.x + ox - w - bx;
                vx = 0;
            } else {
                x = wall.x + wall.w + ox - bx;
                vx = 0;
            }
        }

        // Collide with slopes
        slope = collideSlopes(x, y);
        if (slope != null) {
            if (slope.dir == 2 || slope.dir == 3) {
                if (vx > 0 && leftBound() < slope.leftBound()) {
                    x = slope.x + ox - w - bx;
                    vx = 0;
                } else if (slope.dir == 2 && bottomBound() > slope.bottomBound() || (slope.dir == 3 && topBound() < slope.topBound())) {
                    x = slope.x + slope.w + ox - bx;
                    vx = 0;
                } else if (slope.dir == 2 && leftBound() > slope.leftBound()) {
                    y = (slope.h / slope.w) * (leftBound() - slope.leftBound()) + slope.topBound() - h + oy - by;
                } else if (slope.dir == 3 && leftBound() > slope.leftBound()) {
                    y = -(slope.h / slope.w) * (leftBound() - slope.leftBound()) + slope.bottomBound() + oy - by;
                }
            }
            if (slope.dir == 1 || slope.dir == 4) {
                if (vx < 0 && rightBound() > slope.rightBound()){
                    x = slope.x + slope.w + ox - bx;
                    vx = 0;
                } else if ((slope.dir == 1 && bottomBound() > slope.bottomBound() || (slope.dir == 4 && topBound() < slope.topBound()))) {
                    x = slope.x + ox - w - bx;
                    vx = 0;
                }  else if (slope.dir == 1 && rightBound() < slope.rightBound()) {
                    y = -(slope.h / slope.w) * (rightBound() - slope.leftBound()) + slope.bottomBound() - h + oy - by;
                } else if (slope.dir == 4 && rightBound() < slope.rightBound()) {
                    y = (slope.h / slope.w) * (rightBound() - slope.leftBound()) + slope.topBound() + oy - by;
                }
            }
        }
    }
    public void collideWithWallsY() {

        // Collide with walls
        Wall wall = collideWalls(x, y);
        onGround = false;
        if (wall != null) {
            if (vy > 0) {
                y = wall.y + oy - h - by;
                vy = 0;
                onGround = true;
            } else {
                y = wall.y + wall.h + oy - by;
                vy = 0;
            }
        }

        // Collide with slopes
        Slope slope = collideSlopes(x, y);
        if (slope != null) {
            if (slope.dir == 3 || slope.dir == 4) {
                if (vy > 0 && topBound() < slope.topBound()) {
                    y = slope.y + oy - h - by;
                    vy = 0;
                    onGround = true;
                } else if (slope.dir == 3) {
                    if (leftBound() > slope.leftBound()) {
                        y = -(slope.h / slope.w) * (leftBound() - slope.leftBound()) + slope.bottomBound() + oy - by;
                        vy = 0;
                    } else {
                        y = slope.bottomBound() + oy - by;
                        vy = 0;
                    }
                } else if (slope.dir == 4) {
                    if (rightBound() < slope.rightBound()) {
                        y = (slope.h / slope.w) * (rightBound() - slope.leftBound()) + slope.topBound() + oy - by;
                        vy = 0;
                    } else {
                        y = slope.bottomBound() + oy - by;
                        vy = 0;
                    }
                }
            }
            if (slope.dir == 1 || slope.dir == 2) {
                if (vy < 0 && bottomBound() > slope.bottomBound()) {
                    y = slope.y + slope.h + oy - by;
                    vy = 0;
                } else if (slope.dir == 1) {
                    if (rightBound() < slope.rightBound()) {
                        y = -(slope.h / slope.w) * (rightBound() - slope.leftBound()) + slope.bottomBound() - h + oy - by;
                        vy = 0;
                        onGround = true;
                    } else {
                        y = slope.topBound() - h + oy - by;
                        vy = 0;
                        onGround = true;
                    }
                } else if (slope.dir == 2) {
                    if (leftBound() > slope.leftBound()) {
                        y = (slope.h / slope.w) * (leftBound() - slope.leftBound()) + slope.topBound() - h + oy - by;
                        vy = 0;
                        onGround = true;
                    } else {
                        y = slope.topBound() - h + oy - by;
                        vy = 0;
                        onGround = true;
                    }
                }
            }
        }
    }
}
