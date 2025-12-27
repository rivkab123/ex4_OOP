package pepse.world.avatar;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Supplier;

/**
 * A UI element responsible for displaying the avatar's current energy level.
 * <p>
 * The display observes an external energy supplier and updates its text
 * and color dynamically based on the energy value.
 * The element is rendered in camera coordinates so it remains fixed
 * on the screen regardless of world movement.
 */
public class EnergyDisplay extends GameObject {

    // --- Constants ---
    private static final String ENERGY_FORMAT = "%.0f%%";
    private static final float CRITICAL_ENERGY_THRESHOLD = 20f;
    private static final Color NORMAL_COLOR = Color.BLACK;
    private static final Color CRITICAL_COLOR = Color.RED;

    // --- Members ---
    private final TextRenderable textRenderable;
    private final Supplier<Float> energySupplier;
    private float lastEnergy;

    /**
     * Constructs a new {@code EnergyDisplay}.
     *
     * @param topLeftCorner the top-left position of the display on the screen
     * @param dimensions the size of the display area
     * @param energySupplier a supplier providing the current energy value
     */
    public EnergyDisplay(Vector2 topLeftCorner,
                         Vector2 dimensions,
                         Supplier<Float> energySupplier) {

        // Initialize with default percentage
        super(topLeftCorner, dimensions, new TextRenderable("100%"));

        this.textRenderable = (TextRenderable) this.renderer().getRenderable();
        this.energySupplier = energySupplier;
        this.lastEnergy = 100f;

        // Ensure the UI stays fixed on the screen regardless of camera movement
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * Updates the energy display each frame.
     * <p>
     * The displayed text and color are updated only if the energy value
     * has changed since the last frame, in order to improve performance.
     *
     * @param deltaTime time elapsed since the last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        float currentEnergy = energySupplier.get();

        // Optimization: Only update the string if the value actually changed
        if (currentEnergy != lastEnergy) {
            updateDisplayText(currentEnergy);
            updateDisplayColor(currentEnergy);
            lastEnergy = currentEnergy;
        }
    }

    /**
     * Updates the textual representation of the energy value.
     *
     * @param energy the current energy value
     */
    private void updateDisplayText(float energy) {
        textRenderable.setString(String.format(ENERGY_FORMAT, energy));
    }

    /**
     * Changes the text color based on the energy level (e.g., Red for low energy).
     */
    private void updateDisplayColor(float energy) {
        if (energy < CRITICAL_ENERGY_THRESHOLD) {
            textRenderable.setColor(CRITICAL_COLOR);
        } else {
            textRenderable.setColor(NORMAL_COLOR);
        }
    }
}
