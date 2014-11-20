package net.lectvs;

import net.lectvs.enemies.GroundEnemy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;

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

    public Tilemap foreground, background;
    public int levelw, levelh;

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

        loadLevel("level1");
    }

    public void update() {
        player.update();

        // Set camera to place the player in the center of the screen
        camx = (int)player.leftBound() + (int)player.w / 2 - Main.gameWidth / 2;
        camy = (int)player.topBound() + (int)player.h / 2 - Main.gameHeight / 2;

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
        glClearColor(0.14f, 0.14f, 0.08f, 1);
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

        glColor4f(1, 1, 1, 1);
        foreground.render(-camx, -camy);
    }

    // Load the level. This will become a file interpreter once we start using a level editor
    public void loadLevel(String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("net/lectvs/res/" + name + ".oel"));
            String line;
            String[] data;
            int i;

            line = br.readLine(); // Level w/h data
            data = line.split("\"");
            levelw = Integer.parseInt(data[1]);
            levelh = Integer.parseInt(data[3]);
            foreground = new Tilemap("tiles.png", levelw, levelh, 64, 64);

            line = br.readLine(); // BG tileset + First line
            while (!(line = br.readLine()).startsWith("  <")) { // BG tile data

            }
            while (!(line = br.readLine()).startsWith("  <")) { // Wall data
                data = line.split("\"");
                walls.add(new Wall(Integer.parseInt(data[1]), Integer.parseInt(data[3]), Integer.parseInt(data[5]), Integer.parseInt(data[7])));
            }
            line = br.readLine(); // FG tileset + First line
            data = line.split(">")[1].split(",");
            for (int j = 0; j < data.length; j++) {
                foreground.setTile(j, 0, Integer.parseInt(data[j]), 0);
            }

            i = 1;
            while (!(line = br.readLine()).startsWith("</level>")) { // FG tile data
                data = line.split("<")[0].split(",");
                for (int j = 0; j < data.length; j++) {
                    foreground.setTile(j, i, Integer.parseInt(data[j]), 0);
                }
                i++;
            }

        } catch (Exception e) {
            // Failed to load level
            e.printStackTrace();
        }
        walls.add(new Slope(256, 448, 128, 64, 1));
        walls.add(new Slope(576, 384, 128, 64, 1));

        walls.add(new Wall(0, 0, 0, levelh));
        walls.add(new Wall(0, 0, levelw, 0));
        walls.add(new Wall(levelw, 0, 0, levelh));
        walls.add(new Wall(0, levelh, levelw, 0));

        gameObjects.add(new GroundEnemy(200, 200, -1));
    }
}
