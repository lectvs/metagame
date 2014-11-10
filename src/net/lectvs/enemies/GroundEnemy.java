package net.lectvs.enemies;

import net.lectvs.Enemy;
import net.lectvs.Game;
import net.lectvs.Maths;
import org.lwjgl.opengl.GL11;

/**
 * Created by Hayden on 11/10/2014.
 */
public class GroundEnemy extends Enemy {

    public GroundEnemy(float x, float y) {
        super(x, y);
        setBounds(0, 0, 32, 32);

        maxHealth = 3;
        health = maxHealth;
    }

    public void update() {
        super.update();
    }

    public void render() {
        GL11.glColor4f(1, 0, 0, 1);
        Maths.drawRect(leftBound() - Game.camx, topBound() - Game.camy, w, h);
        super.render();
    }
}
