package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

import static danogl.components.Transition.TransitionType.TRANSITION_BACK_AND_FORTH;

/**
 * A utility class responsible for creating the night overlay effect.
 * <p>
 * The night effect is implemented as a full-screen black rectangle whose
 * opacity smoothly transitions between day (noon) and night (midnight)
 * using a back-and-forth animation cycle.
 */
public class Night {


    // --- Constants ---
    private static final String NIGHT_TAG = "night";
    private static final Float NOON_OPACITY = 0f;
    private static final Float MIDNIGHT_OPACITY = 0.5f;

    /**
     * Creates and returns a night overlay game object.
     * <p>
     * The overlay covers the entire window and smoothly transitions its
     * opacity between {@link #NOON_OPACITY} and {@link #MIDNIGHT_OPACITY}
     * over the given cycle length. The object is rendered in camera
     * coordinates so it remains fixed relative to the screen.
     *
     * @param windowDimensions the dimensions of the game window
     * @param cycleLength the duration (in seconds) of a full day-night cycle
     * @return a {@link GameObject} representing the night overlay
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        RectangleRenderable renderable = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO,windowDimensions ,renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        new Transition<Float>(
                night,
                night.renderer()::setOpaqueness,
                NOON_OPACITY,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength,
                TRANSITION_BACK_AND_FORTH,
                null);
        return  night;
    }

}
