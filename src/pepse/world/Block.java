package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a static square block in the game world.
 * <p>
 * Blocks are immutable terrain elements:
 * <ul>
 *   <li>Have a fixed square size</li>
 *   <li>Do not move or respond to forces</li>
 *   <li>Prevent other objects from intersecting them</li>
 * </ul>
 */
public class Block extends GameObject {

    /** Width and height of a single block in pixels */
    public static final int SIZE = 30;

    /**
     * Constructs a new {@code Block}.
     *
     * @param topLeftCorner the top-left position of the block
     * @param renderable the visual representation of the block
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);

        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}

