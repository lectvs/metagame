package net.lectvs;

/**
 * Created by Hayden on 11/10/2014.
 */
public class Enemy extends Entity {

    public float health, maxHealth;
    public float knockback;
    public float invincibilityTime, flashTime;
    public float constInvincibilityTime, constFlashTime;
    public boolean flashing;

    public Enemy(float x, float y) {
        super(x, y);
        health = maxHealth = 3;
        knockback = 5;
        constInvincibilityTime = 0.5f;
        constFlashTime = 0.5f;

        invincibilityTime = 0;
        flashTime = 0;
        flashing = false;
    }

    public void update() {
        if (invincibilityTime > 0) {
            invincibilityTime -= Main.delta;
        }
        else if (collide(Game.player.attackbox, x, y)) {
            health -= 1;
            invincibilityTime = constInvincibilityTime;
            flashTime = constFlashTime;

            addForce(Game.player.direction() * 5, -4);
        }
        if (flashTime > 0) {
            flashTime -= Main.delta;
            flashing = Math.floor(flashTime * 4) % 4 % 2 == 1;
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
