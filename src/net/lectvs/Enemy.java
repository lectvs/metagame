package net.lectvs;

import org.lwjgl.Sys;

/**
 * Created by Hayden on 11/10/2014.
 */
public class Enemy extends Entity {

    public float health, maxHealth;
    public float knockback;
    public float invincibilityTime;
    public boolean flashing;

    public Enemy(float x, float y) {
        super(x, y);
        health = maxHealth = 3;
        knockback = 5;
        invincibilityTime = 0;
        flashing = false;
    }

    public void update() {
        if (invincibilityTime > 0) {
            flashing = Math.floor(invincibilityTime * 4) % 4 % 2 == 1;
            invincibilityTime -= Main.delta;
        }
        else if (collide(Game.player.attackbox, x, y)) {
            health -= 1;
            invincibilityTime = 0.5f;
            flashing = true;

            addForce(Game.player.direction() * 10, -4);
        }
        else {
            flashing = false;
        }
        if (health <= 0) {
            die();
        }
    }

    public void die() {
        Game.removeObjects.add(this);
    }

    public void render() {

    }
}
