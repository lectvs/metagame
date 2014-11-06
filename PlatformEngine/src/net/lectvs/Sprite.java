package net.lectvs;

import java.io.IOException;
import java.util.*;

import com.sun.deploy.util.ArrayUtil;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Sprite {
    public Texture texture;
    public double angle;
    public float ox, oy;
    public float sx, sy;
    public Color color;
    public float delay, timer;
    public boolean repeat;
    public int frame;
    public int width, height;
    public int[] currentAnim;
    public String animName;
    public Map<String, float[]> animations;

    // dir = directory, ox/oy = origin point, angle = angle, sx/sy = scale, rgba = color tint
    public Sprite(String dir) {
        this(dir, 0, 0, 0, 1, 1);
    }
    public Sprite(String dir, float ox, float oy, double angle, float sx, float sy, float r, float g, float b, float a) {
        this(dir, ox, oy, angle, sx, sy);
        this.color = new Color(r, g, b, a);
    }
    public Sprite(String dir, float ox, float oy, double angle, float sx, float sy) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("net/lectvs/res/" + dir));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        this.angle = angle;
        this.ox = ox;
        this.oy = oy;
        this.sx = sx;
        this.sy = sy;
        this.color = new Color(1f, 1f, 1f, 1f);
        animations = new HashMap<String, float[]>();
        frame = 0;
        delay = 0;
        timer = 0;
        repeat = true;
        currentAnim = new int[]{0};
        width = texture.getTextureWidth();
        height = texture.getTextureHeight();
        animName = "";
    }

    // Sets the frame dimensions
    public void setDimensions(int w, int h) {
        width = w;
        height = h;
    }

    // Adds an animation to the sprite. frames = {repeat 1/0, delay, [frame numbers]}
    public void addAnim(String name, float[] frames) {
        animations.put(name, frames);
    }

    // Plays an animation. Will not run if the same animation is already playing
    public void play(String name) {
        if (name != animName) {
            playOverride(name);
        }
    }

    // Overrides the current playing animation regardless of the animation state
    public void playOverride(String name) {
        animName = name;
        currentAnim = new int[animations.get(name).length - 2];
        for (int i = 2; i < currentAnim.length + 2; i++) {
            currentAnim[i - 2] = (int)animations.get(name)[i];
        }
        delay = animations.get(name)[1];
        repeat = animations.get(name)[0] == 1;
        frame = 0;
    }

    // Renders the sprite and updates animation cycles
    public void render(int x, int y) {
        color.bind();

        if (!animations.isEmpty()) {
            timer += Main.delta;
            if (timer > delay) {
                timer = 0;
                frame++;
                if (frame > currentAnim.length - 1) {
                    frame = currentAnim.length - 1;
                    if (repeat) frame = 0;
                }
            }
        }
        renderWithoutAnimation(x, y);
    }

    // Renders the sprite without updating the animation
    public void renderWithoutAnimation(int x, int y) {
        try {
            if (frame > currentAnim.length - 1) {
                frame = currentAnim.length - 1;
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0.0f);
            GL11.glRotated(angle, 0, 0, 1.0f);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f((float)currentAnim[frame] * width / texture.getTextureWidth(), 0);
            GL11.glVertex2f(-ox * sx, -oy * sy);
            GL11.glTexCoord2f((float)(currentAnim[frame] + 1) * width / texture.getTextureWidth(), 0);
            GL11.glVertex2f((width - ox) * sx, -oy * sy);
            GL11.glTexCoord2f((float)(currentAnim[frame] + 1) * width / texture.getTextureWidth(), 1);
            GL11.glVertex2f((width - ox) * sx, (height - oy) * sy);
            GL11.glTexCoord2f((float)currentAnim[frame] * width / texture.getTextureWidth(), 1);
            GL11.glVertex2f(-ox * sx, (height - oy) * sy);
            GL11.glEnd();
            GL11.glPopMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
