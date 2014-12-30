package net.lectvs;

/**
 * Created by hayden on 12/3/14.
 */
public class AttackBox extends Entity {

    public float tx, ty;
    public float damage, knockback;
    public boolean active;
    public Entity parent;

    public AttackBox(Entity parent, float x, float y, float w, float h, float damage, float knockback) {
        super(parent.x + x, parent.y + y);
        tx = x;
        ty = y;
        this.parent = parent;

        //setBounds(0, 0, w, h);

        active = true;
    }
    public void update() {
        x = parent.x + tx;
        y = parent.y + ty;
    }
}
