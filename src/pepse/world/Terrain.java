package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Terrain {


    private static final float GROUND_INITIAL_Y_RATIO  = 2f / 3f;
    private static final String GROUND_SURFACE = "top_block";
    private final Vector2 windowDimensions;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final float groundHeightAtX0;
    private static final int TERRAIN_DEPTH = 20;



    public Terrain(Vector2 windowDimensions, int seed) {
        this.windowDimensions = windowDimensions;
        groundHeightAtX0 = windowDimensions.y() * GROUND_INITIAL_Y_RATIO ;
    }

    // TODO random ground surface
    public float groundHeightAt(float x) {
        return groundHeightAtX0;
    }

    public List<Block> createInRange(int minX, int maxX) {
        RectangleRenderable rectangleRenderable = new RectangleRenderable(ColorSupplier.approximateColor(
                BASE_GROUND_COLOR));

        List<Block> blocks = new ArrayList<>();
        int firstX = (int)Math.floor((float)minX / Block.SIZE) * Block.SIZE;

        for (int x = firstX; x < maxX; x += Block.SIZE) {
            int yStart = (int) (Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE);

            for (int y = yStart; y < windowDimensions.y(); y += Block.SIZE) {
                Block block = new Block(new Vector2(x, y), rectangleRenderable);
                if (y == yStart) {
                    block.setTag(GROUND_SURFACE);
                }
                blocks.add(block);
            }
        }

        return blocks;
    }

}
