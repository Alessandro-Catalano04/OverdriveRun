package model;

/**
 * An interactive orb that boosts the cube upward only when a jump request is
 * pending at the moment of contact.
 *
 * This differs from LaunchPad in that the launch is conditional: the
 * orb returns CONDITIONED_JUMP and the engine applies
 * ORB_JUMP_POWER only if the player has asked to jump.
 */
public class JumpOrb extends AbstractEntity {

    /**
     * Creates a jump orb at the given position.
     *
     * @param x left edge
     * @param y top edge
     * @param n horizontal repeat count, kept for the shared entity contract
     */
    public JumpOrb(int x, int y, int n) {
        super(x, y, GameConstants.SIZE, GameConstants.SIZE, n);
    }

    /**
     * Signals that a conditional jump should be attempted.
     *
     * @return CONDITIONED_JUMP
     */
    @Override
    public Effect onCollision(double velocityY, int previousCubeY) {
        return Effect.CONDITIONED_JUMP;
    }

    @Override
    public String getSpritePath() { return "/assets/JumpOrb.png"; }

    /**
     * The orb is always a single square tile: unlike the tiled entities its
     * hitbox does not grow with n.
     */
    @Override
    protected Hitbox createHitbox(int x, int y, int n) {
        return new Hitbox(x, y, GameConstants.SIZE, GameConstants.SIZE);
    }
}