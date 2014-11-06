package net.lectvs;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Hayden on 10/1/2014.
 */
public class Slope extends Wall {

    public int dir; // _| = 1, |_ = 2, F = 3, 7 = 4

    public Slope(int x, int y, int w, int h, int dir) {
        super(x, y, w, h);
        this.dir = dir;
    }

    public void render() {
        glBegin(GL_TRIANGLES);
        if (dir == 1) {
            glVertex2d(x + w - Game.camx, y - Game.camy);
            glVertex2d(x + w - Game.camx, y + h - Game.camy);
            glVertex2d(x - Game.camx, y + h - Game.camy);
        }
        else if (dir == 2) {
            glVertex2d(x - Game.camx, y - Game.camy);
            glVertex2d(x - Game.camx, y + h - Game.camy);
            glVertex2d(x + w - Game.camx, y + h - Game.camy);
        }
        else if (dir == 3) {
            glVertex2d(x - Game.camx, y - Game.camy);
            glVertex2d(x + w - Game.camx, y - Game.camy);
            glVertex2d(x - Game.camx, y + h - Game.camy);
        }
        else {
            glVertex2d(x - Game.camx, y - Game.camy);
            glVertex2d(x + w - Game.camx, y - Game.camy);
            glVertex2d(x + w - Game.camx, y + h - Game.camy);
        }
        glEnd();
    }

}
