package net.lectvs;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

/**
 * Created with IntelliJ IDEA.
 * User: Hayden
 * Date: 6/22/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Maths {

    // The distance formula
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    // Draws an arc (pizza slice) with the given center, radius, and angles. Higher num_segments = higher quality but lower performance
    public static void drawArc(double cx, double cy, double r, double start_angle, double arc_angle, int num_segments)
    {
        start_angle *= Math.PI / 180;
        arc_angle *= Math.PI / 180;

        double theta = arc_angle / (double)(num_segments - 1);//theta is now calculated from the arc angle instead, the - 1 bit comes from the fact that the arc is open
        double tangential_factor = Math.tan(theta);
        double radial_factor = Math.cos(theta);


        double x = r * Math.cos(start_angle);//we now start at the start angle
        double y = r * Math.sin(start_angle);

        GL11.glBegin(GL11.GL_POLYGON);//since the arc is not a closed curve, this is a strip now
        GL11.glVertex2d(cx, cy);
        for(int ii = 0; ii < num_segments; ii++)
        {
            GL11.glVertex2d(x + cx, y + cy);

            double tx = -y;
            double ty = x;

            x += tx * tangential_factor;
            y += ty * tangential_factor;

            x *= radial_factor;
            y *= radial_factor;
        }
        GL11.glEnd();
    }

    // Draws a rectangle
    public static void drawRect(float x, float y, float w, float h) {
        glBindTexture(GL_TEXTURE_2D, 0);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d((int)x, (int)y);
        GL11.glVertex2d((int)x + (int)w, (int)y);
        GL11.glVertex2d((int)x + (int)w, (int)y + (int)h);
        GL11.glVertex2d((int)x, (int)y + (int)h);
        GL11.glEnd();
    }

    //Draws a hollow rectangle
    public static void drawOutlineRect(float x, float y, float w, float h, int borderWidth) {
        glBindTexture(GL_TEXTURE_2D, 0);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d((int) x, (int) y);
        GL11.glVertex2d((int) x + borderWidth, (int) y);
        GL11.glVertex2d((int) x + borderWidth, (int) y + (int) h);
        GL11.glVertex2d((int) x, (int) y + (int) h);
        GL11.glVertex2d((int) x + (int) w - borderWidth, (int) y);
        GL11.glVertex2d((int) x + (int) w, (int) y);
        GL11.glVertex2d((int) x + (int) w, (int) y + (int) h);
        GL11.glVertex2d((int) x + (int) w - borderWidth, (int) y + (int) h);
        GL11.glVertex2d((int) x, (int) y);
        GL11.glVertex2d((int) x + (int) w, (int) y);
        GL11.glVertex2d((int) x + (int) w, (int) y + borderWidth);
        GL11.glVertex2d((int) x, (int) y + borderWidth);
        GL11.glVertex2d((int) x, (int) y + (int) h - borderWidth);
        GL11.glVertex2d((int) x + (int) w, (int) y + (int) h - borderWidth);
        GL11.glVertex2d((int) x + (int) w, (int) y + (int) h);
        GL11.glVertex2d((int) x, (int) y + (int) h);
        GL11.glEnd();
    }
}

