package net.lectvs;

/**
 * Created with IntelliJ IDEA.
 * User: Hayden
 * Date: 6/22/13
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Wall extends Entity {
    public Wall(int x, int y, int width, int height) {
        super(x, y);
        this.w = width;
        this.h = height;
    }

    public void render() {
        Lectvs.drawRect(x - Game.camx, y - Game.camy, w, h);
    }
}
