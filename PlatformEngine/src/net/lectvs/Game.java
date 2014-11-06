package net.lectvs;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hayden
 * Date: 8/23/13
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game extends Screen{

    public static ArrayList<Wall> walls = new ArrayList<Wall>();
    public static ArrayList<Entity> gameObjects = new ArrayList<Entity>();
    public static ArrayList<Entity> removeObjects = new ArrayList<Entity>();
    public static ArrayList<Entity> addObjects = new ArrayList<Entity>();

    public static int camx, camy;

    public static Player player;

    public void init() {
        // Remove all entities
        gameObjects.clear();
        removeObjects.clear();
        addObjects.clear();

        player = new Player(100, 200);
        camx = 0;
        camy = 0;

        loadLevel(0);
    }

    public void update() {
        player.update();

        // Set camera to place the player in the center of the screen
        camx = (int)player.leftBound() + (int)player.w / 2 - 250;
        camy = (int)player.topBound() + (int)player.h / 2 - 150;

        for (Entity e : gameObjects) {
            e.update();

            // Do NOT call gameObjects.add() or gameObjects.remove() anywhere in here! Use addObjects.add() and removeObjects.add() instead
        }

        // Add and remove all queued entities
        for (Entity e : removeObjects) gameObjects.remove(e);
        for (Entity e : addObjects) gameObjects.add(e);

        // Clear the queue after objects have been added
        removeObjects.clear();
        addObjects.clear();
    }

    public void render() {

        // Clears the background with the color white
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glColor4f(1, 1, 1, 1);
        for (Entity e : gameObjects) {
            e.render();
        }
        player.render();

        glColor4f(0, 0, 0, 1);
        glBindTexture(GL_TEXTURE_2D, 0);
        for (Wall w : walls) {
            w.render();
        }
    }

    // Load the level. This will become a file interpreter once we start using a level editor
    public void loadLevel(int id) {
        walls.add(new Wall(0, 0, 40, 720));
        walls.add(new Wall(40, 0, 960, 40));
        walls.add(new Wall(960, 40, 40, 680));
        walls.add(new Wall(40, 680, 920, 40));

        walls.add(new Wall(40, 40, 40, 80));
        walls.add(new Wall(80, 40, 40, 40));
        walls.add(new Slope(120, 40, 80, 40, 3));
        walls.add(new Slope(80, 80, 40, 40, 3));
        walls.add(new Slope(40, 120, 40, 80, 3));

        walls.add(new Wall(920, 40, 40, 80));
        walls.add(new Wall(880, 40, 40, 40));
        walls.add(new Slope(800, 40, 80, 40, 4));
        walls.add(new Slope(880, 80, 40, 40, 4));
        walls.add(new Slope(920, 120, 40, 80, 4));

        walls.add(new Wall(40, 600, 40, 80));
        walls.add(new Wall(80, 640, 40, 40));
        walls.add(new Slope(40, 520, 40, 80, 2));
        walls.add(new Slope(80, 600, 40, 40, 2));
        walls.add(new Slope(120, 640, 80, 40, 2));

        walls.add(new Wall(920, 600, 40, 80));
        walls.add(new Wall(880, 640, 40, 40));
        walls.add(new Slope(920, 520, 40, 80, 1));
        walls.add(new Slope(880, 600, 40, 40, 1));
        walls.add(new Slope(800, 640, 80, 40, 1));

        walls.add(new Slope(320, 600, 80, 80, 1));
        walls.add(new Slope(400, 560, 80, 40, 1));
        walls.add(new Slope(480, 480, 80, 80, 1));
        walls.add(new Wall(400, 600, 80, 80));
        walls.add(new Wall(480, 560, 80, 120));
        walls.add(new Wall(560, 480, 160, 200));
        walls.add(new Wall(720, 580, 40, 100));

        walls.add(new Wall(320, 440, 120, 40));

        walls.add(new Wall(160, 320, 120, 40));

        walls.add(new Slope(360, 160, 160, 80, 1));
        walls.add(new Slope(440, 240, 80, 40, 3));
        walls.add(new Wall(360, 240, 80, 40));
        walls.add(new Wall(520, 160, 80, 80));
        walls.add(new Slope(600, 160, 40, 40, 2));
        walls.add(new Slope(520, 240, 80, 80, 4));
        walls.add(new Slope(640, 200, 40, 80, 2));
        walls.add(new Slope(640, 280, 40, 40, 3));
        walls.add(new Wall(600, 200, 40, 120));
    }
}
