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
    public float vx, vy;
    public boolean onGround;
    public boolean collidingHoriz, collidingVert;
    public Sprite sprite;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
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