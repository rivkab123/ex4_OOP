package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A utility class responsible for creating a visual halo effect around the sun.
 * <p>
 * The halo is rendered as a semi-transparent circle that surrounds the sun
 * in order to create a glowing atmospheric effect. The halo is positioned
 * in camera coordinates so it remains visually aligned with the sun.
 */
public class SunHalo {

    // --- Constants ---
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final String HALO_TAG = "sunHalo";
    private static final float HALO_SIZE_MULTIPLICAND = 2f;

    /**
     * Creates and returns a {@link GameObject} representing a glowing halo
     * around the given sun object.
     * <p>
     * The halo is created larger than the sun itself and is initially centered
     * at the sun's position. It is rendered in camera coordinates to ensure
     * consistent alignment with the sun on screen.
     *
     * @param sun the sun {@link GameObject} that the halo surrounds
     * @return a {@link GameObject} representing the sun's halo
     */

    public static GameObject create(GameObject sun){
        // Prepare the visual representation
        OvalRenderable renderable = new OvalRenderable(SUN_HALO_COLOR);

        // Calculate dimensions: the halo is larger than the sun itself
        Vector2 sunDimensions = sun.getDimensions();
        Vector2 haloDimensions = sunDimensions.mult(HALO_SIZE_MULTIPLICAND);

        // Create the halo object centered at the sun's current position
        GameObject sunHalo = new GameObject(
                Vector2.ZERO, // Initial position, will be updated by center synchronization
                haloDimensions,
                renderable);

        // Ensure the halo is correctly centered initially
        sunHalo.setCenter(sun.getCenter());

        // UI/Camera Settings
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(HALO_TAG);

        return sunHalo;
    }
}
