package net.lectvs;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Hayden
 * Date: 6/25/13
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Entity {
    public float x, y;
    public float ox, oy;
    public float bx, by, w, h;
    public float ax, ay, vx, vy;
    public float maxSpeed, acceleration, deceleration, friction;
    public boolean onGround;
    public boolean collidingHoriz, collidingVert;
    public Sprite sprite;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
        ax = ay = vx = vy = 0;
        ox = oy = 0;
        bx = by = 0;
        onGround = false;
        collidingHoriz = collidingVert = false;
    }
    public void update() {

    }
    public void render() {
        sprite.render((int)x - Game.camx, (int)y - Game.camy);
    }

    public void addForce(float ax, float ay) {
        this.vx += ax;
        this.vy += ay;
    }

    // Moves the entity
    public void move(boolean collideWithWalls) {
        x += vx;
        if (collideWithWalls) {
            collideWithWallsX();
        }

        y += vy;
        if (onGround)
            y += Math.abs(vx);     // Moves the player down extra pixels to ensure it "sticks" to slopes when walking down them
        if (collideWithWalls) {
            collideWithWallsY();
        }
    }

    public void collideWithWallsX() {

        // If running up/down a slope, reduce movement speed accordingly
        Slope slope = collideSlopes(x, y);
        if (slope != null && onGround) {
            x = x - vx + slope.w / (float) Lectvs.distance(0, 0, slope.w, slope.h) * vx;
        }

        // Collide with slopes
        slope = collideSlopes(x, y);
        if (slope != null) {
            if (slope.dir == 2 || slope.dir == 3) {
                if (vx > 0 && leftBound() < slope.leftBound()) {
                    x = slope.x + ox - w - bx;
                    vx = 0;
                } else if ((slope.dir == 2 && bottomBound() > slope.bottomBound()) || (slope.dir == 3 && topBound() < slope.topBound())) {
                    x = slope.x + slope.w + ox - bx;
                    vx = 0;
                } else if (slope.dir == 2) {
                    if (leftBound() > slope.leftBound()) {
                        y = (slope.h / slope.w) * (leftBound() - slope.leftBound()) + slope.topBound() - h + oy - by;
                    } else {
                        y = slope.topBound() - h + oy - by;
                    }
                } else if (slope.dir == 3) {
                    if (leftBound() > slope.leftBound()) {
                        y = -(slope.h / slope.w) * (leftBound() - slope.leftBound()) + slope.bottomBound() + oy - by;
                    } else {
                        y = slope.bottomBound() + oy - by;
                    }
                }
            }
            if (slope.dir == 1 || slope.dir == 4) {
                if (vx < 0 && rightBound() > slope.rightBound()){
                    x = slope.x + slope.w + ox - bx;
                    vx = 0;
                } else if ((slope.dir == 1 && bottomBound() > slope.bottomBound()) || (slope.dir == 4 && topBound() < slope.topBound())) {
                    x = slope.x + ox - w - bx;
                    vx = 0;
                } else if (slope.dir == 1) {
                    if (rightBound() < slope.rightBound()) {
                        y = -(slope.h / slope.w) * (rightBound() - slope.leftBound()) + slope.bottomBound() - h + oy - by;
                    } else {
                        y = slope.topBound() - h + oy - by;
                    }
                } else if (slope.dir == 4) {
                    if (rightBound() < slope.rightBound()) {
                        y = (slope.h / slope.w) * (rightBound() - slope.leftBound()) + slope.topBound() + oy - by;
                    } else {
                        y = slope.bottomBound() + oy - by;
                    }
                }
            }
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
    }
    public void collideWithWallsY() {

        onGround = false;

        // Collide with walls
        Wall wall = collideWalls(x, y);
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

    // Moves the entity according to its velocity and handles player-wall collisions
    public void moveByWalls() {
        collidingHoriz = false;
        x += vx;
        Wall wall = collideWalls(x, y);
        if (wall != null) {
            if (vx > 0) {
                x = wall.x + ox - w - bx;
            } else {
                x = wall.x + wall.w + ox - bx;
            }
            collidingHoriz = true;
        }
        collidingVert = false;
        onGround = false;
        y += vy;
        wall = collideWalls(x, y);
        if (wall != null) {
            if (vy > 0) {
                y = wall.y + oy - h - by;
                onGround = true;
            } else {
                y = wall.y + wall.h + oy - by;
            }
            collidingVert = true;
        }
    }

    // Returns the wall the entity is colliding with, or null if it is not colliding with any
    public Wall collideWalls(float xx, float yy) {
        Wall wal = null;
        ArrayList<Wall> walls = new ArrayList<Wall>();
        walls.addAll(Game.walls);
        for (Wall wall : walls) {
            if (!(wall instanceof Slope) && collide(wall, xx, yy)) {
                wal = wall;
            }
        }
        return wal;
    }

    // Returns the slope the entity is colliding with, or null if it is not colliding with any
    public Slope collideSlopes(float xx, float yy) {
        Slope sl = null;
        ArrayList<Wall> walls = new ArrayList<Wall>();
        walls.addAll(Game.walls);
        boolean collide = false;
        for (Wall wall : walls) {
            if (wall instanceof Slope && collide(wall, x, y)) {
                sl = (Slope) wall;
                if ((sl.dir == 1 && bottomBound() - sl.bottomBound() > -(sl.h / sl.w) * (rightBound() - sl.leftBound())) ||
                    (sl.dir == 2 && bottomBound() - sl.topBound() > (sl.h / sl.w) * (leftBound() - sl.leftBound())) ||
                    (sl.dir == 3 && topBound() - sl.bottomBound() < -(sl.h / sl.w) * (leftBound() - sl.leftBound())) ||
                    (sl.dir == 4 && topBound() - sl.topBound() < (sl.h / sl.w) * (rightBound() - sl.leftBound()))) {

                    return sl;
                }
            }
        }

        return null;
    }

    // Sets the collision box, centered around the origin
    public void setBounds(float x, float y, float w, float h) {
        this.w = w;
        this.h = h;
        bx = x;
        by = y;
    }

    public void remove() {
        Game.removeObjects.add(this);
    }

    // Checks collision with another entity
    public boolean collide(Entity e, float x, float y) {
        return (rightBound() > e.leftBound() && leftBound() < e.rightBound() &&
                bottomBound() > e.topBound() && topBound() < e.bottomBound());
    }

    // Shorthand for the x and y values of the boundaries of the collision box
    public float leftBound() {
        return x - ox + bx;
    }
    public float topBound() {
        return y - oy + by;
    }
    public float rightBound() {
        return x - ox + bx + w;
    }
    public float bottomBound() {
        return y - oy + by + h;
    }
}