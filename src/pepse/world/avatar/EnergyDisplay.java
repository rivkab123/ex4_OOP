package pepse.world.avatar;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Supplier;

/**
 * A UI element responsible for displaying the current energy level of the avatar.
 * It observes an energy supplier and updates the displayed text and color accordingly.
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
     * Constructs a new EnergyDisplay.
     * @param topLeftCorner The position of the display on the screen.
     * @param dimensions The size of the text display area.
     * @param energySupplier A functional interface to fetch the current energy level.
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
     * Updates the display text and color whenever the energy level changes.
     * @param deltaTime The time elapsed since the last update.
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
     * Updates the string representation of the energy.
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
