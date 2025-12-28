// ========================= Fruit.java =========================
package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.Color;
import java.util.Random;

/**
 * Represents a collectible fruit in the game world.
 * <p>
 * A fruit is rendered as a colored circle and can be collected by the avatar.
 * Upon collection, the fruit disappears temporarily and respawns after a
 * fixed amount of time corresponding to a full day cycle.
 */
public class Fruit extends GameObject {

    // --- Constants ---
    private static final float DAY_CYCLE_LENGTH = 30f;
    private boolean eaten;

    private static final Color[] FRUIT_COLORS = {
            new Color(200, 50, 50),    // red
            new Color(230, 200, 50),   // yellow
            new Color(230, 140, 50),   // orange
            new Color(160, 70, 200)    // purple
    };

    private static final float COLOR_SEED_X_MULTIPLIER = 53f;
    private static final float COLOR_SEED_Y_MULTIPLIER = 97f;

    private final Vector2 size;

    /** Tag assigned to all fruit objects */
    public static final String TAG = "fruit";

    /**
     * Constructs a new {@code Fruit} object.
     *
     * @param topLeft the top-left position of the fruit
     * @param size the dimensions of the fruit
     */
    public Fruit(Vector2 topLeft, Vector2 size) {
        super(topLeft, size, new OvalRenderable(randomFruitColor(topLeft)));
        setTag(TAG);
        eaten = false;
        this.size = size;
    }


    /**
     * Makes the fruit disappear after being collected.
     * <p>
     * The fruit becomes invisible and non-collidable, and a scheduled task
     * is created to respawn it after {@link #DAY_CYCLE_LENGTH} seconds.
     */
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

    /**
     * Respawns the fruit if it was previously eaten.
     * <p>
     * Restores the fruit's original size and visibility.
     */
    public void respawn() {
        if (eaten) {
            eaten = false;
            setDimensions(size);
            renderer().setOpaqueness(1f);
        }
    }

    /**
     * Chooses a deterministic fruit color based on the fruit's position.
     * <p>
     * This ensures visual consistency between runs while still providing
     * variation across different fruit locations.
     *
     * @param pos the position used to seed the random generator
     * @return a color selected from {@link #FRUIT_COLORS}
     */
    private static Color randomFruitColor(Vector2 pos) {
        Random random = new Random((long)(
                pos.x()*COLOR_SEED_X_MULTIPLIER + pos.y() * COLOR_SEED_Y_MULTIPLIER));
        return FRUIT_COLORS[random.nextInt(FRUIT_COLORS.length)];
    }
}
