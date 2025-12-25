import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.avatar.Avatar;
import pepse.world.avatar.EnergyDisplay;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pepse.world.trees.Fruit;
import pepse.world.trees.Tree;

public class PepseGameManager extends GameManager {

    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int DEEP_GROUND_LAYER = Layer.BACKGROUND + 10;
    private static final int UI_LAYER = Layer.UI;

    private static final String GROUND_SURFACE_TAG = "top_block";
    private static final float DAY_CYCLE_LENGTH = 30f;
    private static final float SUN_CYCLE_LENGTH = 60f;
    private static final int TERRAIN_SEED = 30;

    private static final float AVATAR_SIZE = 50f;
    private static final Vector2 ENERGY_DISPLAY_POS = new Vector2(5, 5);
    private static final Vector2 ENERGY_DISPLAY_SIZE = new Vector2(20, 20);

    private final Random RANDOM = new Random();

    private static final int TREE_ODDS = 1;
    private static final int TOTAL_ODDS = 10;
    private final ArrayList<Fruit> allFruits = new ArrayList<>();

    private GameObject cycleTaskAnchor;


    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        Vector2 windowDimension = windowController.getWindowDimensions();

        createSky(windowDimension);
        createDayNightCycle(windowDimension);

        Terrain terrain = createTerrain(windowDimension);

        Avatar avatar = createAvatar(imageReader, inputListener, windowDimension, terrain, windowController);

        createUI(avatar);
    }

    private void startDayNightCycleFruitRespawn() {
        new ScheduledTask(
                cycleTaskAnchor,                 // owner that always exists
                DAY_CYCLE_LENGTH,     // 30 seconds
                true,                 // repeat forever
                this::respawnFruitsAtCycleEnd
        );
    }

    private void respawnFruitsAtCycleEnd() {
        for (Fruit fruit : allFruits) {
            fruit.respawn();
        }
    }


    private void createSky(Vector2 windowDimension) {
        GameObject sky = Sky.create(windowDimension);
        gameObjects().addGameObject(sky, SKY_LAYER);
    }

    private void createDayNightCycle(Vector2 windowDimension) {
        GameObject night = Night.create(windowDimension, DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        GameObject sun = Sun.create(windowDimension, SUN_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, SUN_LAYER);

        GameObject sunHalo = SunHalo.create(sun);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        gameObjects().addGameObject(sunHalo, SUN_HALO_LAYER);

        cycleTaskAnchor = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
        gameObjects().addGameObject(cycleTaskAnchor, Layer.BACKGROUND);
        startDayNightCycleFruitRespawn();

    }

    private Terrain createTerrain(Vector2 windowDimension) {
        Terrain terrain = new Terrain(windowDimension, TERRAIN_SEED);
        List<Block> blocks = terrain.createInRange(0, (int) windowDimension.x());

        for (Block block : blocks) {
            if (GROUND_SURFACE_TAG.equals(block.getTag())) {
                gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
                addTree(block.getTopLeftCorner());
            } else {
                gameObjects().addGameObject(block, DEEP_GROUND_LAYER);
            }
        }
        return terrain;
    }

    private void addTree(Vector2 blockTopLeftCorner) {
        if (RANDOM.nextInt(TOTAL_ODDS) != TREE_ODDS) {
            return;
        }

        Tree tree = new Tree(blockTopLeftCorner);
        gameObjects().addGameObject(tree.getTreeBase(), Layer.STATIC_OBJECTS);

        for (GameObject leaf : tree.getTreeLeaves()) {
            gameObjects().addGameObject(leaf, Layer.FOREGROUND);
        }

        for (Fruit fruit : tree.getFruits()) {
            gameObjects().addGameObject(fruit, Layer.STATIC_OBJECTS);
            allFruits.add(fruit);
        }
    }

    private Avatar createAvatar(ImageReader imageReader,
                                UserInputListener inputListener,
                                Vector2 windowDimension,
                                Terrain terrain,
                                WindowController windowController) {
        float avatarX = windowDimension.x() / 2;
        float groundY = (float) (Math.floor(terrain.groundHeightAt(avatarX) / Block.SIZE) * Block.SIZE);
        float avatarY = groundY - AVATAR_SIZE;

        Vector2 avatarInitialPos = new Vector2(avatarX, avatarY);
        Avatar avatar = new Avatar(avatarInitialPos, inputListener, imageReader);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);

        Vector2 avatarCenter = avatarInitialPos.add(new Vector2(AVATAR_SIZE, AVATAR_SIZE).mult(0.5f));
        Vector2 offset = windowController.getWindowDimensions().mult(0.5f).subtract(avatarCenter);

        setCamera(new Camera(
                avatar,
                offset,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()
        ));

        return avatar;
    }

    private void createUI(Avatar avatar) {
        GameObject energyDisplay = new EnergyDisplay(
                ENERGY_DISPLAY_POS,
                ENERGY_DISPLAY_SIZE,
                avatar::getEnergy
        );
        gameObjects().addGameObject(energyDisplay, UI_LAYER);
    }
}
