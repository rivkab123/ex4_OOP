package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a tree in the game world.
 * <p>
 * A tree is composed of:
 * <ul>
 *   <li>A trunk (rectangular {@link GameObject})</li>
 *   <li>Leaves ({@link Leaf} objects)</li>
 *   <li>Fruits ({@link Fruit} objects)</li>
 * </ul>
 * The tree's structure and contents are generated deterministically
 * based on its ground position.
 * <p>
 * <b>Note:</b> The four-color palette applies only to fruits.
 * Leaves are always green and manage their own color noise and animation.
 */
public class Tree {

    private final Random random;
    private static final long RANDOM_SEED_X_MULTIPLIER = 31L;
    private static final long RANDOM_SEED_Y_MULTIPLIER = 1L;

    // ---- Trunk constraints (pixels) ----
    private static final int TRUNK_MIN_HEIGHT = 150;
    private static final int TRUNK_MAX_HEIGHT = 350;
    private static final int TRUNK_MIN_WIDTH  = 50;
    private static final int TRUNK_MAX_WIDTH  = 80;
    private static final String TRUNK_TAG = "surface";

    // ---- Canopy / Leaves (pixels) ----
    private static final int LEAF_SIZE = 20;              // each leaf is 20x20 (you set this)
    private static final int CANOPY_MIN_HALF_SIZE = 100;  // half-size of square canopy
    private static final int CANOPY_MAX_HALF_SIZE = 150;

    private static final float LEAF_DENSITY = 0.70f;
    private static final float FRUIT_DENSITY = 0.10f;     // fruits are rarer than leaves

    private static final float LEAF_SIZE_FACTOR = 0.8f;
    private static final float HALF_FACTOR = 0.5f;

    // ---- Colors ----
    private static final Color TRUNK_COLOR = new Color(100, 50, 20);

    // ---- Parts ----
    private final GameObject trunk;
    private final List<Leaf> leaves = new ArrayList<>();
    private final List<Fruit> fruits = new ArrayList<>();


    /**
     * Constructs a new {@code Tree} rooted at the given ground position.
     *
     * @param groundTopLeft the top-left position of the tree trunk base
     */
    public Tree(Vector2 groundTopLeft) {

        long seed = (long) groundTopLeft.x() * RANDOM_SEED_X_MULTIPLIER
                + (long) groundTopLeft.y() * RANDOM_SEED_Y_MULTIPLIER;
        this.random = new Random(seed);

        int trunkHeight = randInt(TRUNK_MIN_HEIGHT, TRUNK_MAX_HEIGHT);
        int trunkWidth  = randInt(TRUNK_MIN_WIDTH, TRUNK_MAX_WIDTH);
        int canopyHalf  = randInt(CANOPY_MIN_HALF_SIZE, CANOPY_MAX_HALF_SIZE);

        this.trunk = createTrunk(groundTopLeft, trunkWidth, trunkHeight);
        createLeavesAndFruits(canopyHalf);
    }

    // ===== Getters =====

    /** @return the trunk GameObject */
    public GameObject getTreeBase() {
        return trunk;
    }

    /** @return list of all leaves in the tree */
    public List<Leaf> getTreeLeaves() {
        return leaves;
    }

    /** @return list of all fruits in the tree */
    public List<Fruit> getFruits() {
        return fruits;
    }


    private GameObject createTrunk(Vector2 groundTopLeft, int trunkWidth, int trunkHeight) {
        Vector2 trunkTopLeft = groundTopLeft.subtract(new Vector2(trunkWidth * HALF_FACTOR, trunkHeight));

        GameObject trunk = new GameObject(
                trunkTopLeft,
                new Vector2(trunkWidth, trunkHeight),
                new RectangleRenderable(TRUNK_COLOR)
        );

        trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        trunk.setTag(TRUNK_TAG);
        return trunk;
    }

    private void createLeavesAndFruits(int canopyHalfSizePx) {
        Vector2 trunkTopLeft = trunk.getTopLeftCorner();
        Vector2 trunkDim = trunk.getDimensions();

        // top-center of trunk
        Vector2 trunkTopCenter = trunkTopLeft.add(new Vector2(trunkDim.x() * HALF_FACTOR, 0f));

        int canopySize = 2 * canopyHalfSizePx;
        Vector2 canopyTopLeft = trunkTopCenter.subtract(new Vector2(canopySize * HALF_FACTOR, canopySize * HALF_FACTOR));

        int cols = canopySize / LEAF_SIZE;
        int rows = canopySize / LEAF_SIZE;

        Vector2 leafSize = new Vector2(LEAF_SIZE, LEAF_SIZE);
        Vector2 fruitSize = leafSize.mult(LEAF_SIZE_FACTOR);

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Vector2 cellTopLeft = canopyTopLeft.add(new Vector2(i * LEAF_SIZE, j * LEAF_SIZE));

                if (random.nextFloat() <= LEAF_DENSITY) {
                    Leaf leaf = new Leaf(cellTopLeft, leafSize);
                    leaves.add(leaf);
                }

                if (random.nextFloat() <= FRUIT_DENSITY) {
                    // center fruit within the leaf cell
                    Vector2 fruitTopLeft = cellTopLeft.add(leafSize.subtract(fruitSize).mult(HALF_FACTOR));
                    Fruit fruit = new Fruit(fruitTopLeft, fruitSize);
                    fruits.add(fruit);
                }

            }
        }
    }

    // inclusive
    private int randInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
