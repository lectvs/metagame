package net.lectvs;

import net.lectvs.enemies.GroundEnemy;

import java.io.*;
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

    public Sprite foregroundPanel, backgroundPanel;
    public Tilemap foreground, background;
    public int levelWidth, levelHeight;

    public static float camx, camy;
    public static float truecamx, truecamy;

    public static Player player;

    public void init() {
        // Remove all entities
        gameObjects.clear();
        removeObjects.clear();
        addObjects.clear();

        player = new Player(100, 200);
        camx = camy = 0;
        truecamx = truecamy = 0;

        loadLevel("level1");
    }

    public void update() {
        player.update();

        // Set camera to place the player in the center of the screen
        truecamx += (camFocusX() - truecamx) / 4f;
        truecamy += (camFocusY() - truecamy) / 4f;

        if (Math.abs(camFocusX() - truecamx) < 5) {
            truecamx = camFocusX();
        }
        if (Math.abs(camFocusY() - truecamy) < 5) {
            truecamy = camFocusY();
        }

        truecamx = Math.max(Main.gameWidth / 2, Math.min(levelWidth - Main.gameWidth / 2, truecamx));
        truecamy = Math.max(Main.gameHeight / 2, Math.min(levelHeight - Main.gameHeight / 2, truecamy));

        camx = truecamx - Main.gameWidth / 2;
        camy = truecamy - Main.gameHeight / 2;

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
        glClearColor(0f, 0.13f, 0.29f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glColor4f(1, 1, 1, 1);
        if (backgroundPanel != null)
            backgroundPanel.render(-camx, -camy);
        if (background != null)
            background.render(-camx, -camy);

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
        if (foreground != null)
            foreground.render(-camx, -camy);
        if (foregroundPanel != null)
            foregroundPanel.render(-camx, -camy);
    }

    // Load the level. This will become a file interpreter once we start using a level editor
    public void loadLevel(String name) {
        try {
            InputStream in = getClass().getResourceAsStream("/net/lectvs/res/" + name + ".oel");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            String[] data;
            int i;

            line = br.readLine(); // Level w/h data
            data = line.split("\"");
            levelWidth = Integer.parseInt(data[1]);
            levelHeight = Integer.parseInt(data[3]);

            line = br.readLine(); // BG tileset + First line
            data = line.split(">");
            background = new Tilemap(data[0].split("\"")[1] + ".png", levelWidth, levelHeight, 32, 32);
            backgroundPanel = new Sprite("forest_backgroundpanel.png");
            data = line.split(">")[1].split(",");
            i = 0;
            do { // BG tile data
                for (int j = 0; j < data.length; j++) {
                    background.setTile(j, i, Integer.parseInt(data[j]) % (background.textureWidth / background.tileWidth), (int)Math.floor(Integer.parseInt(data[j]) / (double)(background.textureWidth / background.tileWidth)));
                }
                i++;
                line = br.readLine();
                data = line.split("<")[0].split(",");
            } while (!line.startsWith("  <"));

            while (!(line = br.readLine()).startsWith("  <")) { // Wall data
                data = line.split("\"");
                walls.add(new Wall(Integer.parseInt(data[1]), Integer.parseInt(data[3]), Integer.parseInt(data[5]), Integer.parseInt(data[7])));
            }

            line = br.readLine(); // FG tileset + First line
            data = line.split(">");
            foreground = new Tilemap(data[0].split("\"")[1] + ".png", levelWidth, levelHeight, 32, 32);
            foregroundPanel = new Sprite("forest_foregroundpanel.png");
            data = data[1].split(",");
            i = 0;
            do { // FG tile data
                for (int j = 0; j < data.length; j++) {
                    foreground.setTile(j, i, Integer.parseInt(data[j]) % (foreground.textureWidth / foreground.tileWidth), (int)Math.floor(Integer.parseInt(data[j]) / (double)(foreground.textureWidth / foreground.tileWidth)));
                    if (j < data.length - 1) {
                        //add slope data to slope tiles
                        if ((data[j].equals("24") && data[j + 1].equals("25")) || (data[j].equals("28") && data[j + 1].equals("29"))) {
                            walls.add(new Slope(32 * j, 32 * i, 64, 32, 1));
                        } else if ((data[j].equals("26") && data[j + 1].equals("27")) || (data[j].equals("30") && data[j + 1].equals("31"))) {
                            walls.add(new Slope(32 * j, 32 * i, 64, 32, 2));
                        }
                    }
                }
                i++;
                line = br.readLine();
                data = line.split("<")[0].split(",");
            } while (!line.startsWith("</level>"));

        } catch (Exception e) {
            // Failed to load level
            e.printStackTrace();
        }

        walls.add(new Wall(-100, 0, 100, levelHeight));
        walls.add(new Wall(0, -100, levelWidth, 100));
        walls.add(new Wall(levelWidth, 0, 100, levelHeight));
        walls.add(new Wall(0, levelHeight, levelWidth, 100));

        gameObjects.add(new GroundEnemy(200, 200, -1));
    }

    public float camFocusX() {
        float camfocusx = player.leftBound() + player.w / 2 + player.vx * 2;
        //if (player.isDashing)
        //    camfocusx += 100 * Math.signum(player.vx);
        return camfocusx;
    }
    public float camFocusY() {
        return player.topBound() + player.h / 2;
    }
}
