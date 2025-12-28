package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the static sky background in the game world.
 * <p>
 * The sky is a rectangle covering the entire window, rendered behind all other objects.
 * It is fixed to the camera coordinates, so it moves with the camera.
 */
public class Sky {

    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final String SKY_TAG = "sky";

    /**
     * Creates a {@link GameObject} representing the sky.
     *
     * @param windowDimensions the dimensions of the game window
     * @return a GameObject representing the sky
     */
    public static GameObject create(Vector2 windowDimensions) {
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));

        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        sky.setTag(SKY_TAG);

        return sky;

    }
}
