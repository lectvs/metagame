package net.lectvs;

/**
 * Created by Hayden on 11/10/2014.
 */
public class Enemy extends Entity {

    public float health, maxHealth;
    public float knockback;
    public float invincibilityTime;

    public Enemy(float x, float y) {
        super(x, y);
        health = maxHealth = 3;
        knockback = 5;
        invincibilityTime = 0;
    }

    public void update() {
        if (invincibilityTime > 0)
            invincibilityTime -= Main.delta;

        if (invincibilityTime <= 0 && collide(Game.player.attackbox, x, y)) {
            health -= 1;
            invincibilityTime = 0.5f;

            addForce(Math.signum(x + w / 2 - Game.player.x) * 10, -10);
            y -= vy;
        }
        if (health <= 0) {
            Game.removeObjects.add(this);
        }
    }

    public void render() {

    }
}
