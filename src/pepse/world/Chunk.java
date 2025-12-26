package pepse.world;

import pepse.world.trees.Tree;

import java.util.ArrayList;

public class Chunk {
    private final ArrayList<Block> blocks;
    private final ArrayList<Tree> trees;

    public Chunk(ArrayList<Block> blocks, ArrayList<Tree> trees) {
        this.blocks = blocks;
        this.trees = trees;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public ArrayList<Tree> getTrees() {
        return trees;
    }
}
