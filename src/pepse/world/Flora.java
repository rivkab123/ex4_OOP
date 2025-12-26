// ========================= Flora.java =========================
package pepse.world;

import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.trees.Tree;

import java.util.ArrayList;
import java.util.Random;

/**
 * Flora is responsible ONLY for vegetation generation (trees).
 *
 * It creates Tree objects in a given X-range and returns them to the caller.
 * It does NOT add objects to the game world.
 */
public class Flora {

    // ---- Generation tuning ----
    private static final int STEP_X = Block.SIZE;   // sample every block
    private static final int TREE_ODDS = 1;         // 1 out of TOTAL_ODDS
    private static final int TOTAL_ODDS = 10;
    private static final int MIN_TREE_GAP_BLOCKS = 10;

    // ---- Determinism ----
    private static final int DEFAULT_SEED = 1337;

    // ---- First-chunk safe zone (avatar spawn) ----
    private static final int FIRST_CHUNK_MIN_X = 0;
    private static final int FIRST_CHUNK_MAX_X = 800;

    // leave empty space for avatar
    private static final int AVATAR_SAFE_MIN_X = 300;
    private static final int AVATAR_SAFE_MAX_X = 500;

    private final GroundHeightProvider groundHeightProvider;
    private final Random random;

    /**
     * Callback interface (program to interface, not implementation)
     */
    public interface GroundHeightProvider {
        float groundHeightAt(float x);
    }

    public Flora(GroundHeightProvider groundHeightProvider, int seed) {
        this.groundHeightProvider = groundHeightProvider;
        this.random = new Random(seed);
    }

    public Flora(GroundHeightProvider groundHeightProvider) {
        this(groundHeightProvider, DEFAULT_SEED);
    }

    /**
     * Create trees in [minX, maxX] and return them.
     */
    public ArrayList<Tree> createInRange(int minX, int maxX) {
        ArrayList<Tree> trees = new ArrayList<>();

        // snap range to STEP_X
        int start = (minX / STEP_X) * STEP_X;
        int end   = (maxX / STEP_X) * STEP_X;

        boolean isFirstChunk =
                minX == FIRST_CHUNK_MIN_X && maxX == FIRST_CHUNK_MAX_X;

        int lastTreeX = start - MIN_TREE_GAP_BLOCKS * STEP_X;

        for (int x = start; x <= end; x += STEP_X) {

            // keep avatar spawn corridor clear
            if (isFirstChunk && x >= AVATAR_SAFE_MIN_X && x <= AVATAR_SAFE_MAX_X) {
                continue;
            }

            // keep spacing between trees
            if (x - lastTreeX < MIN_TREE_GAP_BLOCKS * STEP_X) {
                continue;
            }

            // chance to spawn
            if (random.nextInt(TOTAL_ODDS) != TREE_ODDS) {
                continue;
            }

            // ground height snapped to grid
            float groundY = groundHeightProvider.groundHeightAt(x);
            groundY = (float)(Math.floor(groundY / Block.SIZE) * Block.SIZE);

            trees.add(new Tree(new Vector2(x, groundY)));
            lastTreeX = x;
        }

        return trees;
    }
}
