package net.lectvs;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.*;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hayden
 * Date: 6/25/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class FBO {
    public int framebufferID;
    public int colorTextureID;
    public int depthRenderBufferID;

    public int width, height;

    // Create new FBO
    public FBO(int width, int height) {
        framebufferID = glGenFramebuffersEXT();
        colorTextureID = glGenTextures();
        depthRenderBufferID = glGenRenderbuffersEXT();

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);
        glBindTexture(GL_TEXTURE_2D, colorTextureID);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT, (ByteBuffer) null);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, colorTextureID, 0);
        glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);
        glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

        this.width = width;
        this.height = height;
    }

    // Binds the FBO to the renderer like a texture
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, colorTextureID);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    // Sets the current render mode to render to this FBO instead of the game
    public void setFramebuffer() {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);
        GL11.glViewport(0,0,width,height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    // Sets the renderer to render to the main window
    public static void unset() {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        GL11.glViewport(0, 0, Main.windowWidth, Main.windowHeight);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Main.windowWidth, Main.windowHeight, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    // Gets a ByteBuffer, for analyzing the individual pixels
    public ByteBuffer getBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
        glReadBuffer(GL_FRONT);
        ByteBuffer b = BufferUtils.createByteBuffer(width * height * 4);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, b);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return b;
    }

    public void render(double x, double y, double w, double h) {
        bind();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2d(x, y);
        glTexCoord2f(1, 1);
        glVertex2d(x + w, y);
        glTexCoord2f(1, 0);
        glVertex2d(x + w, y + h);
        glTexCoord2f(0, 0);
        glVertex2d(x, y + h);
        glEnd();
    }
}
