package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.utils.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;

/**
 * Responsible for procedural terrain generation (ground blocks) in the game world.
 * <p>
 * The Terrain class creates blocks forming the ground and uses Perlin-like noise
 * to vary the ground height naturally. It can generate blocks in a horizontal range
 * without adding them to the game world directly.
 */
public class Terrain {

    private static final float GROUND_INITIAL_Y_RATIO  = 2f / 3f;
    private static final double NOISE_FACTOR = 210;
    private static final String GROUND_SURFACE_TAG = "surface";
    private final Vector2 windowDimensions;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final float groundHeightAtX0;
    private static final int TERRAIN_DEPTH = 20;
    private final NoiseGenerator noiseGenerator;

    /**
     * Constructs a Terrain generator.
     *
     * @param windowDimensions the dimensions of the game window
     * @param seed seed for deterministic terrain noise
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = windowDimensions.y() * GROUND_INITIAL_Y_RATIO ;
        this.noiseGenerator = new NoiseGenerator(seed, (int)groundHeightAtX0);
    }

    /**
     * Returns the ground height at a given x-coordinate, including noise variation.
     *
     * @param x the horizontal coordinate
     * @return the vertical coordinate of the ground at x
     */
    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, NOISE_FACTOR);
        return groundHeightAtX0 + noise;
    }

    /**
     * Creates blocks representing the terrain in the horizontal range [minX, maxX].
     * The top block of each column is tagged as {@link #GROUND_SURFACE_TAG}.
     *
     * @param minX minimum X-coordinate (inclusive)
     * @param maxX maximum X-coordinate (exclusive)
     * @return a list of blocks forming the terrain in the given range
     */
    public ArrayList<Block> createInRange(int minX, int maxX) {
        RectangleRenderable rectangleRenderable =
                new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));

        ArrayList<Block> blocks = new ArrayList<>();

        int firstX = (int) Math.floor((float) minX / Block.SIZE) * Block.SIZE;
        int yEnd = (int) windowDimensions.y() + TERRAIN_DEPTH * Block.SIZE;

        for (int x = firstX; x < maxX; x += Block.SIZE) {
            int yStart = (int) (Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE);

            for (int y = yStart; y < yEnd; y += Block.SIZE) {
                Block block = new Block(new Vector2(x, y), rectangleRenderable);
                if (y == yStart) block.setTag(GROUND_SURFACE_TAG);
                blocks.add(block);
            }
        }
        return blocks;
    }


}
