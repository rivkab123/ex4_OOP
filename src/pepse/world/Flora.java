// ========================= Flora.java =========================
package pepse.world;

import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Flora is responsible ONLY for vegetation generation (trees).
 *
 * It does NOT add objects to the game world. It only CREATES Tree objects
 * in a given X-range and returns them to the caller (PepseGameManager),
 * similar to how Terrain.createInRange works.
 *
 * Uses a callback (interface) so Flora doesn't depend on Terrain implementation.
 */
public class Flora {

    // ---- Generation tuning ----
    private static final int STEP_X = Block.SIZE;    // sample every block
    private static final int TREE_ODDS = 1;          // 1 out of TOTAL_ODDS
    private static final int TOTAL_ODDS = 10;
    private static final int MIN_TREE_GAP_BLOCKS = 10; // avoid trees too close (in blocks)
    private static final int DEFAULT_SEED = 1337;

    private final GroundHeightProvider groundHeightProvider;
    private final Random random;

    /**
     * Callback interface (program to interface, not implementation)
     */
    public interface GroundHeightProvider {
        float groundHeightAt(float x);
    }

    /**
     * Create Flora with a ground-height callback + deterministic seed.
     */
    public Flora(GroundHeightProvider groundHeightProvider, int seed) {
        this.groundHeightProvider = groundHeightProvider;
        this.random = new Random(seed);
    }

    /**
     * Create Flora with a ground-height callback + default seed.
     */
    public Flora(GroundHeightProvider groundHeightProvider) {
        this(groundHeightProvider, DEFAULT_SEED);
    }

    /**
     * Create trees in [minX, maxX] and return them.
     * The caller decides which layers to add trunk/leaves/fruits into.
     */
    public ArrayList<Tree> createInRange(int minX, int maxX) {
        ArrayList<Tree> trees = new ArrayList<>();

        // snap range to STEP_X
        int start = (minX / STEP_X) * STEP_X;
        int end = (maxX / STEP_X) * STEP_X;

        int lastTreeX = start - MIN_TREE_GAP_BLOCKS * STEP_X;

        for (int x = start; x <= end; x += STEP_X) {

            if (x - lastTreeX < MIN_TREE_GAP_BLOCKS * STEP_X) {
                continue;
            }

            if (random.nextInt(TOTAL_ODDS) != TREE_ODDS) {
                continue;
            }

            float groundY = groundHeightProvider.groundHeightAt(x);
            groundY = (float)(Math.floor(groundY / Block.SIZE) * Block.SIZE);

            Tree tree = new Tree(new Vector2(x, groundY)); // preferred (no dummy Block)
            trees.add(tree);

            lastTreeX = x;
        }

        return trees;
    }
}
