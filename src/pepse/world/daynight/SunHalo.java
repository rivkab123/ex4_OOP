package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Responsible for creating a visual halo effect around the sun.
 * The halo follows the sun's position to provide a glowing atmosphere.
 */
public class SunHalo {

    // --- Constants ---
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final String HALO_TAG = "sunHalo";
    private static final float HALO_SIZE_MULTIPLICAND = 2f;

    /**
     * Creates a SunHalo GameObject that represents a glowing circle around the sun.
     * * @param sun The sun GameObject that this halo will surround.
     * @return A GameObject representing the sun's halo.
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
