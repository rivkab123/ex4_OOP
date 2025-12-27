// ======================= PepseGameManager.java =======================
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.avatar.Avatar;
import pepse.world.avatar.EnergyDisplay;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;

import java.util.ArrayList;

import pepse.world.trees.Fruit;
import pepse.world.trees.Tree;

/**
 * Manages the Pepse game world:
 * - Initializes terrain, flora, sky, day/night cycle, and avatar
 * - Dynamically loads and unloads chunks as the avatar moves
 * - Handles UI elements such as the energy display
 */
public class PepseGameManager extends GameManager {

    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int DEEP_GROUND_LAYER = Layer.BACKGROUND + 10;
    private static final int UI_LAYER = Layer.UI;

    private static final String SURFACE_TAG = "surface";
    private static final float DAY_CYCLE_LENGTH = 30f;
    private static final float SUN_CYCLE_LENGTH = 60f;
    private static final int TERRAIN_SEED = 30;

    private static final float AVATAR_SIZE = 50f;
    private static final Vector2 ENERGY_DISPLAY_POS = new Vector2(5, 5);
    private static final Vector2 ENERGY_DISPLAY_SIZE = new Vector2(20, 20);
    private static final int FIRST_CHUNKS = 3;

    private Avatar avatar;
    private Terrain terrain_generator;
    private Flora flora_generator;
    private BiListDeque<Chunk> chunks;
    private int current_chunk;
    private Vector2 windowDimensions;


    /**
     * Entry point for the Pepse game.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * Initializes the game world: sky, day/night cycle, terrain, flora, avatar, and UI.
     *
     * @param imageReader Image reader utility.
     * @param soundReader Sound reader utility.
     * @param inputListener User input listener.
     * @param windowController Window controller.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        windowDimensions = windowController.getWindowDimensions();

        createSky();
        createDayNightCycle();
        createFirstChunks();
        createAvatar(imageReader, inputListener);
        createUI();
    }

    /**
     * Updates the game state each frame.
     *
     * @param delta Time elapsed since last update in seconds.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        handleAvatarLocation();
    }

    private void handleAvatarLocation() {
        int W = (int) windowDimensions.x();
        int avatarX = (int) avatar.getCenter().x();
        int chunkId = Math.floorDiv(avatarX, W);

        if (chunkId == current_chunk) return;

        int dir = Integer.compare(chunkId, current_chunk); // +1 right, -1 left

        int toEnable = chunkId + dir;        // new forward neighbor
        int toDisable = chunkId - 2 * dir;   // old far neighbor behind

        // Enable/create forward neighbor
        if (chunks.isValidIndex(toEnable)) {
            enableChunk(chunks.get(toEnable));
        } else {
            int minX = toEnable * W;
            int maxX = (toEnable + 1) * W;
            createChunkIn(minX, maxX);
        }

        // Disable far neighbor behind
        if (chunks.isValidIndex(toDisable)) {
            disableChunk(chunks.get(toDisable));
        }

        current_chunk = chunkId;
    }

    private void disableChunk(Chunk chunk) {
        for (Block block : chunk.getBlocks()) {
            if (SURFACE_TAG.equals(block.getTag())) {
                gameObjects().removeGameObject(block, Layer.STATIC_OBJECTS);
            } else {
                gameObjects().removeGameObject(block, DEEP_GROUND_LAYER);
            }
        }

        for (Tree tree : chunk.getTrees()) {
            gameObjects().removeGameObject(tree.getTreeBase(), Layer.STATIC_OBJECTS);

            for (GameObject leaf : tree.getTreeLeaves()) {
                gameObjects().removeGameObject(leaf, Layer.FOREGROUND);
            }

            for (Fruit fruit : tree.getFruits()) {
                gameObjects().removeGameObject(fruit, Layer.STATIC_OBJECTS);
            }
        }
    }

    private void enableChunk(Chunk chunk) {
        for (Block block : chunk.getBlocks()) {
            // NOTE: for avatar stability, it's OK if deep blocks don't collide,
            // but surface blocks MUST collide & be in a collidable layer.
            if (SURFACE_TAG.equals(block.getTag())) {
                gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
            } else {
                gameObjects().addGameObject(block, DEEP_GROUND_LAYER);
            }
        }

        for (Tree tree : chunk.getTrees()) {
            gameObjects().addGameObject(tree.getTreeBase(), Layer.STATIC_OBJECTS);

            for (GameObject leaf : tree.getTreeLeaves()) {
                gameObjects().addGameObject(leaf, Layer.FOREGROUND);
            }

            for (Fruit fruit : tree.getFruits()) {
                gameObjects().addGameObject(fruit, Layer.STATIC_OBJECTS);
            }
        }
    }

    private void createFirstChunks() {
        terrain_generator = new Terrain(windowDimensions, TERRAIN_SEED);
        flora_generator = new Flora(terrain_generator::groundHeightAt);
        chunks = new BiListDeque<>();

        int windowsDimX = (int) windowDimensions.x();
        int initialX = -windowsDimX;

        for (int i = 0; i < FIRST_CHUNKS; i++) {
            int minX = initialX + windowsDimX * i;
            int maxX = initialX + windowsDimX * (i + 1);
            createChunkIn(minX, maxX);
        }

        current_chunk = 0;
    }

    private void createChunkIn(int minX, int maxX) {
        ArrayList<Tree> trees = flora_generator.createInRange(minX, maxX);
        ArrayList<Block> blocks = terrain_generator.createInRange(minX, maxX);
        Chunk chunk = new Chunk(blocks, trees);

        enableChunk(chunk);

        if (minX < 0) {
            chunks.addFirst(chunk);
        } else {
            chunks.addLast(chunk);
        }
    }

    private void createSky() {
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, SKY_LAYER);
    }

    private void createDayNightCycle() {
        GameObject night = Night.create(windowDimensions, DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        GameObject sun = Sun.create(windowDimensions, SUN_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, SUN_LAYER);

        GameObject sunHalo = SunHalo.create(sun);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        gameObjects().addGameObject(sunHalo, SUN_HALO_LAYER);
    }

    private void createAvatar(ImageReader imageReader, UserInputListener inputListener) {
        float avatarX = windowDimensions.x() / 2;
        float groundY = (float) (Math.floor(terrain_generator.groundHeightAt(avatarX) / Block.SIZE) * Block.SIZE);
        float avatarY = groundY - AVATAR_SIZE;

        Vector2 avatarInitialPos = new Vector2(avatarX, avatarY);
        avatar = new Avatar(avatarInitialPos, inputListener, imageReader);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);

        Vector2 avatarCenter = avatarInitialPos.add(new Vector2(AVATAR_SIZE, AVATAR_SIZE).mult(0.5f));
        Vector2 offset = windowDimensions.mult(0.5f).subtract(avatarCenter);

        setCamera(new Camera(avatar, offset, windowDimensions, windowDimensions));
    }

    private void createUI() {
        GameObject energyDisplay = new EnergyDisplay(
                ENERGY_DISPLAY_POS,
                ENERGY_DISPLAY_SIZE,
                avatar::getEnergy
        );
        gameObjects().addGameObject(energyDisplay, UI_LAYER);
    }
}
