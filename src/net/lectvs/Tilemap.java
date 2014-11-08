package net.lectvs;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Hayden
 * Date: 6/30/13
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tilemap {
    public Texture texture;
    public int width, height, tileWidth, tileHeight;
    public int[][][] tiles;

    // dir: directory of tilemap sprite sheet, width/height = width and height of the tilemap, tileWidth/tileHeight = pixel width and height of the tiles
    public Tilemap(String dir, int width, int height, int tileWidth, int tileHeight) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/" + dir));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        tiles = new int[(int)(width / tileWidth)][(int)(height / tileHeight)][2];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tiles[i][j] = new int[]{-1, -1};
            }
        }
    }

    // Sets the tilemap tile to a specific tile
    public void setTile(int x, int y, int tx, int ty) {
        tiles[x][y][0] = tx;
        tiles[x][y][1] = ty;
    }

    public void render(int x, int y) {
        //glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
        //int i = 3;
        //int j = 10;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j][0] > -1) {
                    glBegin(GL_QUADS);
                    glTexCoord2d(tiles[i][j][0] * 20 / 256f, tiles[i][j][1] * 20 / 256f);
                    glVertex2d(x + i * 20, y + j * 20);
                    glTexCoord2d((tiles[i][j][0] * 20 + 20) / 256f, tiles[i][j][1] * 20 / 256f);
                    glVertex2d(x + i * 20 + tileWidth, y + j * 20);
                    glTexCoord2d((tiles[i][j][0] * 20 + 20) / 256f, (tiles[i][j][1] * 20 + 20) / 256f);
                    glVertex2d(x + i * 20 + tileWidth, y + j * 20 + tileHeight);
                    glTexCoord2d(tiles[i][j][0] * 20 / 256f, (tiles[i][j][1] * 20 + 20) / 256f);
                    glVertex2d(x + i * 20, y + j * 20 + tileHeight);
                    glEnd();
                }
            }
        }

    }
}
