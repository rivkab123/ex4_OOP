// ========================= Flora.java =========================
package pepse.world;

import danogl.util.Vector2;
import pepse.world.trees.Tree;

import java.util.ArrayList;
import java.util.Random;

/**
 * Responsible for procedural vegetation generation (trees) in the game world.
 * <p>
 * The {@code Flora} class does not automatically add objects to the game world.
 * It only creates {@link Tree} objects in a given horizontal range and returns them.
 * <p>
 * Trees are generated deterministically using a seed, with constraints such as:
 * <ul>
 *     <li>Spacing between trees</li>
 *     <li>A safe zone in the first chunk for avatar spawning</li>
 *     <li>Random odds for tree generation</li>
 * </ul>
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
     * Callback interface to provide ground height at a given x-coordinate.
     * <p>
     * Implementations should return the y-coordinate of the terrain for the given x.
     */
    public interface GroundHeightProvider {
        float groundHeightAt(float x);
    }

    /**
     * Constructs a Flora generator with a given seed.
     *
     * @param groundHeightProvider function to obtain ground heights
     * @param seed                 seed for deterministic tree generation
     */
    public Flora(GroundHeightProvider groundHeightProvider, int seed) {
        this.groundHeightProvider = groundHeightProvider;
        this.random = new Random(seed);
    }

    /**
     * Constructs a Flora generator with a default deterministic seed.
     *
     * @param groundHeightProvider function to obtain ground heights
     */
    public Flora(GroundHeightProvider groundHeightProvider) {
        this(groundHeightProvider, DEFAULT_SEED);
    }

    /**
     * Generates {@link Tree} objects in the horizontal range [minX, maxX].
     * <p>
     * Ensures spacing between trees, avoids the avatar safe zone in the first chunk,
     * and snaps ground height to the Block grid.
     *
     * @param minX minimum X-coordinate (inclusive)
     * @param maxX maximum X-coordinate (inclusive)
     * @return list of trees generated in the range
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
