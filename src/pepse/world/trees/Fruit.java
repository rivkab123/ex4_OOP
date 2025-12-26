// ========================= Fruit.java =========================
package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.Color;
import java.util.Random;

/**
 * Fruit is a simple circle (OvalRenderable).
 * Constructor gets size + location, and chooses its color by itself.
 *
 * NOTE: These are the 4 colors you asked for (red, yellow, orange, purple).
 */

public class Fruit extends GameObject {

    private static final float DAY_CYCLE_LENGTH = 30f;
    private static final Random RANDOM = new Random();
    private boolean eaten;

    private static final Color[] FRUIT_COLORS = {
            new Color(200, 50, 50),    // red
            new Color(230, 200, 50),   // yellow
            new Color(230, 140, 50),   // orange
            new Color(160, 70, 200)    // purple
    };

    public static final String TAG = "fruit";
    private final Vector2 size;

    public Fruit(Vector2 topLeft, Vector2 size) {
        super(topLeft, size, new OvalRenderable(randomFruitColor(topLeft)));
        setTag(TAG);
        eaten = false;
        this.size = size;
    }


    // TODO change numbers to constant variables
    private static Color randomFruitColor(Vector2 pos) {
        Random random = new Random((long)(pos.x()*53 + pos.y() * 97));
        return FRUIT_COLORS[random.nextInt(FRUIT_COLORS.length)];
    }

    public void disappear() {
        eaten = true;
        setDimensions(Vector2.ZERO);        // no size -> effectively no collision
        renderer().setOpaqueness(0f);       // invisible (if your renderer supports this)
        new ScheduledTask(
                this,                 // owner that always exists
                DAY_CYCLE_LENGTH,     // 30 seconds
                false,                 // repeat forever
                this::respawn
        );
    }

    public void respawn() {
        if (eaten) {
            eaten = false;
            setDimensions(size);
            renderer().setOpaqueness(1f);
        }
    }
}
