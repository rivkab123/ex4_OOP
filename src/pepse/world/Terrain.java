package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.utils.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Terrain {


    private static final float GROUND_INITIAL_Y_RATIO  = 2f / 3f;
    private static final double NOISE_FACTOR = 210;
    private static final String GROUND_SURFACE = "top_block";
    private final Vector2 windowDimensions;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final float groundHeightAtX0;
    private static final int TERRAIN_DEPTH = 20;
    private final NoiseGenerator noiseGenerator;

    public Terrain(Vector2 windowDimensions, int seed) {
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = windowDimensions.y() * GROUND_INITIAL_Y_RATIO ;
        this.noiseGenerator = new NoiseGenerator(seed, (int)groundHeightAtX0);
    }

    // TODO random ground surface
    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, NOISE_FACTOR);
        return groundHeightAtX0 + noise;
    }

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
                if (y == yStart) block.setTag(GROUND_SURFACE);
                blocks.add(block);
            }
        }
        return blocks;
    }


}
