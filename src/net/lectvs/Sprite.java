package net.lectvs;

import java.io.IOException;
import java.util.*;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Sprite {
    public Texture texture;
    public double angle;
    public float ox, oy;
    public float sx, sy;
    public Color color;
    public float timer;
    public int frame;
    public int width, height;
    public Animation currentAnim;
    public String animName;
    public Map<String, Animation> animations;

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
        animations = new HashMap<String, Animation>();
        frame = 0;
        timer = 0;
        currentAnim = new Animation(true, 0, new int[]{0});
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
    public void addAnim(String name, boolean repeat, float delay, int[] frames) {
        animations.put(name, new Animation(repeat, delay, frames));
    }
    public void addAnim(String name, boolean repeat, float delay, int[] frames, Runnable callback) {
        animations.put(name, new Animation(repeat, delay, frames, callback));
    }

    // Plays an animation. Will not run if the same animation is already playing
    public void play(String name) {
        if (name != animName) {
            playOverride(name);
        }
    }

    // Overrides the current playing animation regardless of the animation state
    public void playOverride(String name) {
        Animation anim = animations.get(name);
        if (anim != null) {
            animName = name;
            currentAnim.repeat = anim.repeat;
            currentAnim.delay = anim.delay;
            currentAnim.frames = anim.frames;
            currentAnim.callback = anim.callback;
            frame = 0;
        }
    }

    // Renders the sprite and updates animation cycles
    public void render(float x, float y) {
        color.bind();

        if (!animations.isEmpty()) {
            timer += Main.delta;
            if (timer > currentAnim.delay) {
                timer = 0;
                frame++;
                if (frame > currentAnim.frames.length - 1) {
                    if (currentAnim.callback != null)
                        currentAnim.callback.run();
                    frame = currentAnim.frames.length - 1;
                    if (currentAnim.repeat) frame = 0;
                }
            }
        }
        renderWithoutAnimation(x, y);
    }

    // Renders the sprite without updating the animation
    public void renderWithoutAnimation(float x, float y) {
        x = (float)Math.floor(x);
        y = (float)Math.floor(y);
        try {
            if (frame > currentAnim.frames.length - 1) {
                frame = currentAnim.frames.length - 1;
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0.0f);
            GL11.glRotated(angle, 0, 0, 1.0f);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f((float)currentAnim.frames[frame] * width / texture.getTextureWidth(), 0);
            GL11.glVertex2f(-ox * sx, -oy * sy);
            GL11.glTexCoord2f((float)(currentAnim.frames[frame] + 1) * width / texture.getTextureWidth(), 0);
            GL11.glVertex2f((width - ox) * sx, -oy * sy);
            GL11.glTexCoord2f((float)(currentAnim.frames[frame] + 1) * width / texture.getTextureWidth(), 1);
            GL11.glVertex2f((width - ox) * sx, (height - oy) * sy);
            GL11.glTexCoord2f((float)currentAnim.frames[frame] * width / texture.getTextureWidth(), 1);
            GL11.glVertex2f(-ox * sx, (height - oy) * sy);
            GL11.glEnd();
            GL11.glPopMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
