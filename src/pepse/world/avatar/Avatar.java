package pepse.world.avatar;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.world.trees.Fruit;


import java.awt.event.KeyEvent;

public class Avatar extends GameObject{

    // --- Physics Constants ---
    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(50, 50);
    private static final float GRAVITY = 600;
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;

    // --- Energy Constants ---
    private static final int MAX_ENERGY = 100;
    private static final int ENERGY_LOSS_RUN = 2;
    private static final int ENERGY_LOSS_JUMP = 20;
    private static final int ENERGY_LOSS_AIR_JUMP = 50;
    private static final int ENERGY_RECOVERY = 1;
    private static final int ENERGY_FRUIT_BONUS = 10;


    // --- Animation Constants ---
    private static final double FRAME_DURATION = 0.25;
    private static final String[] STANDING_IMGS = {
            "assets/idle_0.png", "assets/idle_1.png", "assets/idle_2.png", "assets/idle_3.png"
    };
    private static final String[] RUNNING_IMGS = {
            "assets/run_0.png", "assets/run_1.png", "assets/run_2.png",
            "assets/run_3.png", "assets/run_4.png", "assets/run_5.png"
    };
    private static final String[] JUMPING_IMGS = {
            "assets/jump_0.png", "assets/jump_1.png", "assets/jump_2.png", "assets/jump_3.png"
    };
    private static final String GROUND_SURFACE_TAG = "top_block";
    public static final String FRUIT_TAG = "fruit";
    private boolean onGround;


    // --- Members ---
    private final UserInputListener inputListener;
    private int energy;
    private final AnimationRenderable standingAnimation;
    private final AnimationRenderable runningAnimation;
    private final AnimationRenderable jumpingAnimation;
    private State curruntState;

    /**
     * Enum representing the possible movement states of the avatar.
     */
    private enum State {IDLE, RUNNING, JUMPING}


    public Avatar(Vector2 topLeftCorner,UserInputListener inputListener, ImageReader imageReader) {

        // Initialize GameObject
        super(topLeftCorner, AVATAR_DIMENSIONS, new AnimationRenderable(STANDING_IMGS, imageReader, false, FRAME_DURATION));

        // Load Animations
        this.standingAnimation = new AnimationRenderable(STANDING_IMGS, imageReader, false, FRAME_DURATION);
        this.runningAnimation = new AnimationRenderable(RUNNING_IMGS, imageReader, false, FRAME_DURATION);
        this.jumpingAnimation = new AnimationRenderable(JUMPING_IMGS, imageReader, false, FRAME_DURATION);

        this.curruntState = State.IDLE;
        this.energy = MAX_ENERGY;
        this.inputListener = inputListener;
        this.onGround = true;

        // Physics Setup
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        float xVel = handleHorizontalMovement();
        handleJump();
        handleEnergyRecovery(xVel);
        updateState(xVel);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if(GROUND_SURFACE_TAG.equals(other.getTag())){
            onGround = true;
            this.transform().setVelocityY(0);
        }

        if(FRUIT_TAG.equals(other.getTag())){
            ((Fruit) other).disappear(); // safe casting
            energy = Math.min(MAX_ENERGY, energy + ENERGY_FRUIT_BONUS);
        }
    }


    /**
     * Returns the current energy level.
     */
    public float getEnergy() {
        return energy;
    }


    // --- Private Helper Methods ---

    /**
     * Calculates horizontal velocity based on input and manages energy loss for running.
     * Allows movement in the air even without energy.
     */
    private float handleHorizontalMovement() {
        float xVel = 0;
        boolean moveLeft = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean moveRight = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);

        if (moveLeft != moveRight) {
            // Condition: Can move if in air OR if on ground with enough energy
            if (!onGround || energy >= ENERGY_LOSS_RUN) {
                xVel = moveLeft ? -VELOCITY_X : VELOCITY_X;

                // Decrease energy only when moving on the ground
                if (onGround) {
                    energy -= ENERGY_LOSS_RUN;
                }
            }
        }

        transform().setVelocityX(xVel);
        return xVel;
    }

    /**
     * Handles jumping logic and associated energy costs.
     */
    private void handleJump() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (onGround && energy >= ENERGY_LOSS_JUMP) {
                transform().setVelocityY(VELOCITY_Y);
                energy -= ENERGY_LOSS_JUMP;
                onGround = false;
            } else if (!onGround && energy >= ENERGY_LOSS_AIR_JUMP) {
                transform().setVelocityY(VELOCITY_Y);
                energy -= ENERGY_LOSS_AIR_JUMP;
            }
        }
    }

    /**
     * Recovers energy when the avatar is stationary on the ground.
     */
    private void handleEnergyRecovery(float xVel) {
        if (getVelocity().y() == 0 && xVel == 0) {
            energy = Math.min(MAX_ENERGY, energy + ENERGY_RECOVERY);
        }
    }

    /**
     * Synchronizes the visual animation and direction with the physical state.
     */
    private void updateState(float xVel) {
        State newState;

        // Determine State
        if (Math.abs(getVelocity().y()) > 1f) {
            newState = State.JUMPING;
        } else if (Math.abs(xVel) > 1f) {
            newState = State.RUNNING;
        } else {
            newState = State.IDLE;
        }

        // Handle Horizontal Flip
        if (xVel != 0) {
            renderer().setIsFlippedHorizontally(xVel < 0);
        }

        // Update Renderable if state changed
        if (newState != curruntState) {
            curruntState = newState;
            switch (curruntState) {
                case IDLE -> renderer().setRenderable(standingAnimation);
                case RUNNING -> renderer().setRenderable(runningAnimation);
                case JUMPING -> renderer().setRenderable(jumpingAnimation);
            }
        }
    }
}
