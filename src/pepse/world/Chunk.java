package pepse.world;

import pepse.world.trees.Tree;

import java.util.ArrayList;

/**
 * Represents a "chunk" of the game world.
 * <p>
 * A chunk is a grouping of terrain elements and trees:
 * <ul>
 *     <li>Blocks: static terrain pieces</li>
 *     <li>Trees: trees present in this chunk</li>
 * </ul>
 * This class is useful for managing and generating sections of the world efficiently.
 */
public class Chunk {

    private final ArrayList<Block> blocks;
    private final ArrayList<Tree> trees;

    /**
     * Constructs a new {@code Chunk} with the given blocks and trees.
     *
     * @param blocks the terrain blocks in this chunk
     * @param trees  the trees in this chunk
     */
    public Chunk(ArrayList<Block> blocks, ArrayList<Tree> trees) {
        this.blocks = blocks;
        this.trees = trees;
    }

    /** @return the blocks contained in this chunk */
    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    /** @return the trees contained in this chunk */
    public ArrayList<Tree> getTrees() {
        return trees;
    }
}
