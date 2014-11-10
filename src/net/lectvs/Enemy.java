package net.lectvs;

/**
 * Created by Hayden on 11/10/2014.
 */
public class Enemy extends Entity {

    public float health, maxHealth;

    public Enemy(float x, float y) {
        super(x, y);
    }

    public void update() {
        if (health <= 0) {
            Game.removeObjects.add(this);
        }
    }

    public void render() {

    }
}
