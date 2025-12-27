// ========================= Leaf.java =========================
package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.Color;
import java.util.Random;

/**
 * Represents a single leaf in the tree canopy.
 * <p>
 * Each leaf is responsible for:
 * <ul>
 *   <li>Choosing its own green color with slight RGB noise</li>
 *   <li>Running a wind animation (rotation and size scaling)</li>
 * </ul>
 * The leaf's appearance and animation are deterministic based on its position,
 * ensuring visual consistency between runs.
 * <p>
 * <b>Note:</b> The color palette here is exclusively green and is unrelated
 * to the fruit color palette.
 */
public class Leaf extends GameObject {

    private final Random random;
    private static final long RANDOM_SEED_X_MULTIPLIER = 73L;
    private static final long RANDOM_SEED_Y_MULTIPLIER = 37L;

    // ---- Leaf color (green) ----
    private static final Color LEAF_BASE_COLOR = new Color(50, 200, 30);
    private static final int LEAF_COLOR_NOISE = 25;
    private static final int HIGHEST_CHANNEL_VAL = 255;

    // ---- Animation constants ----
    private static final int LEAF_ANGLE_MIN_DEG = 15;
    private static final int LEAF_ANGLE_MAX_DEG = 60;

    private static final float LEAF_START_DELAY_MAX = 0.6f;

    private static final float LEAF_ANGLE_DUR_MIN = 1.0f;
    private static final float LEAF_ANGLE_DUR_RANGE = 1.5f;

    private static final float LEAF_SCALE_MIN_BASE = 0.90f;
    private static final float LEAF_SCALE_MIN_RANGE = 0.05f;
    private static final float LEAF_SCALE_MAX_BASE = 1.05f;
    private static final float LEAF_SCALE_MAX_RANGE = 0.07f;

    private static final float LEAF_SIZE_DUR_MIN = 1.2f;
    private static final float LEAF_SIZE_DUR_RANGE = 2.0f;

    /**
     * Constructs a new {@code Leaf} object.
     *
     * @param topLeft the top-left position of the leaf
     * @param size the dimensions of the leaf
     */
    public Leaf(Vector2 topLeft, Vector2 size) {
        super(topLeft, size, null);
        long seed = (long) topLeft.x() * RANDOM_SEED_X_MULTIPLIER
                + (long) topLeft.y() * RANDOM_SEED_Y_MULTIPLIER;
        this.random = new Random(seed);
        renderer().setRenderable(new RectangleRenderable(addRgbNoise(LEAF_BASE_COLOR, LEAF_COLOR_NOISE)));
        animate(size);
    }

    private void animate(Vector2 baseSize) {
        final float startDelay = random.nextFloat() * LEAF_START_DELAY_MAX;

        final float maxAngle = randInt(LEAF_ANGLE_MIN_DEG, LEAF_ANGLE_MAX_DEG);
        final float angleDuration = LEAF_ANGLE_DUR_MIN + random.nextFloat() * LEAF_ANGLE_DUR_RANGE;

        final float minScale = LEAF_SCALE_MIN_BASE + random.nextFloat() * LEAF_SCALE_MIN_RANGE;
        final float maxScale = LEAF_SCALE_MAX_BASE + random.nextFloat() * LEAF_SCALE_MAX_RANGE;
        final float sizeDuration = LEAF_SIZE_DUR_MIN + random.nextFloat() * LEAF_SIZE_DUR_RANGE;

        new ScheduledTask(this, startDelay, false, () -> {
            new Transition<>(
                    this,
                    angle -> renderer().setRenderableAngle(angle),
                    -maxAngle, +maxAngle,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    angleDuration,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null
            );

            new Transition<>(
                    this,
                    scale -> setDimensions(baseSize.mult(scale)),
                    minScale, maxScale,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    sizeDuration,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null
            );
        });
    }

    private Color addRgbNoise(Color base, int noise) {
        int r = clamp255(base.getRed()   + randInt(-noise, noise));
        int g = clamp255(base.getGreen() + randInt(-noise, noise));
        int b = clamp255(base.getBlue()  + randInt(-noise, noise));
        return new Color(r, g, b);
    }

    private static int clamp255(int v) {
        return Math.max(0, Math.min(HIGHEST_CHANNEL_VAL, v));
    }

    // inclusive
    private int randInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
