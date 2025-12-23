import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.avatar.Avatar;
import pepse.world.avatar.EnergyDisplay;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import java.util.List;

/**
 * The main game manager for the PEPSE game (Procedural Environment Program for Simulation and Exploration).
 * Responsible for initializing the world, day-night cycle, terrain, and the player avatar.
 */
public class PepseGameManager extends GameManager {

    // --- Layer Constants ---
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int DEEP_GROUND_LAYER = Layer.BACKGROUND + 10;
    private static final int UI_LAYER = Layer.UI;

    // --- World & Environment Settings ---
    private static final String GROUND_SURFACE_TAG = "top_block";
    private static final float DAY_CYCLE_LENGTH = 30f;
    private static final float SUN_CYCLE_LENGTH = 60f;
    private static final int TERRAIN_SEED = 30; // Consider making this random later

    // --- Avatar & UI Settings ---
    private static final float AVATAR_SIZE = 50f;
    private static final Vector2 ENERGY_DISPLAY_POS = new Vector2(5, 5);
    private static final Vector2 ENERGY_DISPLAY_SIZE = new Vector2(20, 20);


    /**
     * Default constructor for the Game Manager.
     */
    public PepseGameManager() {
        super();
    }

    /**
     * Entry point for the PEPSE application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {

        new PepseGameManager().run();
    }

    /**
     * Initializes all game components including world geometry, day-night effects, and the player.
     * @param imageReader Contains facilities for reading images from disk.
     * @param soundReader Contains facilities for reading sound files from disk.
     * @param inputListener Contains facilities for getting user input.
     * @param windowController Controls the game window properties.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        Vector2 windowDimension = windowController.getWindowDimensions();

        // 1. Create Background & Sky
        createSky(windowDimension);

        // 2. Create Day-Night Cycle
        createDayNightCycle(windowDimension);

        // 3. Create Terrain
        Terrain terrain = createTerrain(windowDimension);

        // 4. Create Avatar
        Avatar avatar = createAvatar(imageReader, inputListener, windowDimension, terrain);

        // 5. Create UI
        createUI(avatar);
    }

    /**
     * Creates and adds the sky background to the game world.
     */
    private void createSky(Vector2 windowDimension) {
        GameObject sky = Sky.create(windowDimension);
        gameObjects().addGameObject(sky, SKY_LAYER);
    }

    /**
     * Creates the sun, sun halo, and night-time darkness overlay.
     */
    private void createDayNightCycle(Vector2 windowDimension) {
        // Night overlay
        GameObject night = Night.create(windowDimension, DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        // Sun
        GameObject sun = Sun.create(windowDimension, SUN_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, SUN_LAYER);

        // Sun Halo
        GameObject sunHalo = SunHalo.create(sun);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        gameObjects().addGameObject(sunHalo, SUN_HALO_LAYER);
    }

    /**
     * Generates the terrain and sorts blocks into surface or deep ground layers.
     * @return The initialized Terrain object.
     */
    private Terrain createTerrain(Vector2 windowDimension) {
        Terrain terrain = new Terrain(windowDimension, TERRAIN_SEED);
        List<Block> blocks = terrain.createInRange(0, (int) windowDimension.x());

        for (Block block : blocks) {
            if (block.getTag().equals(GROUND_SURFACE_TAG)) {
                // Surface blocks are static objects for collision
                gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
            } else {
                // Deep blocks are background decoration
                gameObjects().addGameObject(block, DEEP_GROUND_LAYER);
            }
        }
        return terrain;
    }

    /**
     * Initializes the player avatar at the correct ground height.
     */
    private Avatar createAvatar(ImageReader imageReader, UserInputListener inputListener,
                                Vector2 windowDimension, Terrain terrain) {
        float avatarX = windowDimension.x() / 2;
        float groundY = terrain.groundHeightAt(avatarX);
        float avatarY = groundY - AVATAR_SIZE;

        Vector2 avatarInitialPos = new Vector2(avatarX, avatarY);
        Avatar avatar = new Avatar(avatarInitialPos, inputListener, imageReader);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);

        return avatar;
    }

    /**
     * Creates the UI elements, such as the energy display.
     */
    private void createUI(Avatar avatar) {
        GameObject energyDisplay = new EnergyDisplay(
                ENERGY_DISPLAY_POS,
                ENERGY_DISPLAY_SIZE,
                avatar::getEnergy
        );
        gameObjects().addGameObject(energyDisplay, UI_LAYER);
    }

}
