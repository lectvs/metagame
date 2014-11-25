package net.lectvs;

import org.lwjgl.Sys;
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
    public int textureWidth, textureHeight;
    public int[][][] tiles;

    // dir: directory of tilemap sprite sheet, width/height = width and height of the tilemap, tileWidth/tileHeight = pixel width and height of the tiles
    public Tilemap(String dir, int width, int height, int tileWidth, int tileHeight) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("net/lectvs/res/" + dir));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        textureWidth = texture.getTextureWidth();
        textureHeight = texture.getTextureHeight();

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

    public void render(float x, float y) {
        x = (float)Math.floor(x);
        y = (float)Math.floor(y);
        //glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
        //int i = 3;
        //int j = 10;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j][0] > -1) {
                    glBegin(GL_QUADS);
                    glTexCoord2d(tiles[i][j][0] * tileWidth / (float)textureWidth, tiles[i][j][1] * tileHeight / (float)textureHeight);
                    glVertex2d(x + i * tileWidth, y + j * tileHeight);
                    glTexCoord2d((tiles[i][j][0] * tileWidth + tileWidth) / (float)textureWidth, tiles[i][j][1] * tileHeight / (float)textureHeight);
                    glVertex2d(x + i * tileWidth + tileWidth, y + j * tileHeight);
                    glTexCoord2d((tiles[i][j][0] * tileWidth + tileWidth) / (float)textureWidth, (tiles[i][j][1] * tileHeight + tileHeight) / (float)textureHeight);
                    glVertex2d(x + i * tileWidth + tileWidth, y + j * tileHeight + tileHeight);
                    glTexCoord2d(tiles[i][j][0] * tileWidth / (float)textureWidth, (tiles[i][j][1] * tileHeight + tileHeight) / (float)textureHeight);
                    glVertex2d(x + i * tileWidth, y + j * tileHeight + tileHeight);
                    glEnd();
                }
            }
        }

    }
}
