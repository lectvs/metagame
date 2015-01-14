package net.lectvs;

import net.lectvs.enemies.GroundEnemy;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
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

        truecamx = Math.max(Main.gameWidth / 2, Math.min(levelWidth - Main.gameWidth / 2 - 1, truecamx));
        truecamy = Math.max(Main.gameHeight / 2, Math.min(levelHeight - Main.gameHeight / 2 - 1, truecamy));

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
        if (backgroundPanel != null) {
            for (int i = 0; i < levelWidth; i += 1280) {
                backgroundPanel.renderWholeImage(i - camx, -camy);
            }
        }
        if (background != null)
            //background.render(-camx, -camy);

        for (Entity e : gameObjects) {
            e.render();
        }
        player.render();

        glColor4f(1, 1, 0, 1);
        glBindTexture(GL_TEXTURE_2D, 0);
        for (Wall w : walls) {
           // w.render();
        }

        glColor4f(1, 1, 1, 1);
        if (foreground != null)
            foreground.render(-camx, -camy);
        if (foregroundPanel != null) {
            for (int i = 0; i < levelWidth; i += 1280) {
                foregroundPanel.renderWholeImage(i - camx, -camy);
            }
        }
    }

    // Load the level
    public void loadLevel(String name) {
        try {
            InputStream in = getClass().getResourceAsStream("/net/lectvs/res/" + name + ".dam");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            doc.getDocumentElement().normalize();

            background = null;
            backgroundPanel = null;
            foreground = null;
            foregroundPanel = null;

            NodeList nodes = doc.getElementsByTagName("imagelayer");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element node = (Element) nodes.item(i);
                if (node.getAttribute("name").startsWith("background")) {
                    backgroundPanel = new Sprite(node.getAttribute("file"));
                }
                if (node.getAttribute("name").startsWith("foreground")) {
                    foregroundPanel = new Sprite(node.getAttribute("file"));
                }
            }

            nodes = doc.getElementsByTagName("maplayer");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element node = (Element) nodes.item(i);
                int width = Integer.parseInt(node.getAttribute("width")) * 32;
                int height = Integer.parseInt(node.getAttribute("height")) * 32;

                if (width > levelWidth)
                    levelWidth = width;
                if (height > levelHeight)
                    levelHeight = height;
            }

            for (int i = 0; i < nodes.getLength(); i++) {
                Element node = (Element) nodes.item(i);
                if (node.getAttribute("name").startsWith("collision")) {
                    NodeList rows = node.getElementsByTagName("row");
                    for (int j = 0; j < rows.getLength(); j++) {
                        String[] data = rows.item(j).getTextContent().split(",");
                        int wallStart = -1;
                        for (int k = 0; k < data.length; k++) {
                            int d  = Integer.parseInt(data[k]);
                            int d2 = -1;
                            if (k < data.length - 1)
                                d2 = Integer.parseInt(data[k + 1]);

                            if (wallStart <= -1 && (d == 1 || d == 9)) {
                                wallStart = k;
                            }
                            if (wallStart > -1 && !(d == 1 || d == 9)) {
                                walls.add(new Wall(wallStart * 32, j * 32, (k - wallStart) * 32, 32));
                                wallStart = -1;
                            }
                            if (wallStart > -1 && k == data.length - 1) {
                                walls.add(new Wall(wallStart * 32, j * 32, (k - wallStart + 1) * 32, 32));
                                wallStart = -1;
                            }

                            if ((d == 2 && d2 == 3) || (d == 10 && d2 == 11)) {
                                walls.add(new Slope(k * 32, j * 32, 64, 32, 1));
                            }
                            if ((d == 4 && d2 == 5) || (d == 12 && d2 == 13)) {
                                walls.add(new Slope(k * 32, j * 32, 64, 32, 2));
                            }
                        }
                    }
                }
                if (node.getAttribute("name").startsWith("background")) {
                    background = new Tilemap(node.getAttribute("tileset"), levelWidth, levelHeight, 32, 32);

                    NodeList rows = node.getElementsByTagName("row");
                    for (int j = 0; j < rows.getLength(); j++) {
                        String[] data = rows.item(j).getTextContent().split(",");
                        for (int k = 0; k < data.length; k++) {
                            background.setTile(k, j, Integer.parseInt(data[k]) % (background.textureWidth / background.tileWidth), (int)Math.floor(Integer.parseInt(data[k]) / (double)(background.textureWidth / background.tileWidth)));
                        }
                    }
                }
                if (node.getAttribute("name").startsWith("foreground")) {
                    foreground = new Tilemap(node.getAttribute("tileset"), levelWidth, levelHeight, 32, 32);

                    NodeList rows = node.getElementsByTagName("row");
                    for (int j = 0; j < rows.getLength(); j++) {
                        String[] data = rows.item(j).getTextContent().split(",");
                        for (int k = 0; k < data.length; k++) {
                            foreground.setTile(k, j, Integer.parseInt(data[k]) % (foreground.textureWidth / foreground.tileWidth), (int)Math.floor(Integer.parseInt(data[k]) / (double)(foreground.textureWidth / foreground.tileWidth)));
                        }
                    }
                }
            }

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
