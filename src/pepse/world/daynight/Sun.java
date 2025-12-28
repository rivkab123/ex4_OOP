package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

import static danogl.components.Transition.TransitionType.TRANSITION_LOOP;

/**
 * A utility class responsible for creating the sun game object.
 * <p>
 * The sun is rendered as a yellow circle that moves along a circular
 * trajectory across the sky, completing a full rotation over a
 * configurable day-night cycle length.
 */
public class Sun {

    // --- Constants ---
    private static final float SUN_HEIGHT_RATIO = 0.2f;
    private static final float SUN_X_LOCATION_RATIO = 2f;
    private static final float SUN_INITIAL_Y_RATIO  = 2f / 3f;
    private static final float SUN_INITIAL_X_RATIO = 0.5f;
    private static final float SUN_SIZE_RATIO = 0.15f;
    private static final float HALF_RATIO = 0.5f;
    private static final float FULL_CIRCLE_DEGREES = 360f;
    private static final float INITIAL_DEGREES = 0f;
    private static final String SUN_TAG = "sun";

    /**
     * Creates and returns a sun game object.
     * <p>
     * The sun is rendered in camera coordinates so it remains fixed
     * relative to the screen and moves along a circular path that
     * represents its movement across the sky.
     *
     * @param windowDimensions the dimensions of the game window
     * @param cycleLength the duration (in seconds) of a full day-night cycle
     * @return a {@link GameObject} representing the sun
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {

        OvalRenderable renderable = new OvalRenderable(Color.YELLOW);

        float sunSize = windowDimensions.y() * SUN_SIZE_RATIO;
        Vector2 sunDimensions = new Vector2(sunSize, sunSize);

        Vector2 initialSunCenter = new Vector2(
                windowDimensions.x() / SUN_X_LOCATION_RATIO,
                windowDimensions.y() * SUN_HEIGHT_RATIO);

        GameObject sun = new GameObject(
                initialSunCenter.subtract(sunDimensions.mult(HALF_RATIO)),
                sunDimensions,
                renderable);

        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        float cycleCenterY = windowDimensions.y() * SUN_INITIAL_Y_RATIO;
        float cycleCenterX = windowDimensions.x() * SUN_INITIAL_X_RATIO;
        Vector2 cycleCenter = new Vector2(cycleCenterX, cycleCenterY);

        new Transition<Float>(
                sun,
                (Float angle) -> sun.setCenter(initialSunCenter.subtract(cycleCenter)
                        .rotated(angle)
                        .add(cycleCenter)),
                INITIAL_DEGREES,
                FULL_CIRCLE_DEGREES,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                TRANSITION_LOOP,
                null);
        return sun;
    }


}
