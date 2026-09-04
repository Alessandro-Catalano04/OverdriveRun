package model;
 
/**
 * JumpOrb: An interactive orb that boosts the cube upward only when the player
 * actively presses the jump button at the moment of contact.
 *
 * This differs from LaunchPad in that the launch is conditional:
 * if jumpRequested is false the orb has no effect this tick.
 * onCollision returns CONDITIONED_JUMP and the engine
 * applies ORB_JUMP_POWER only when the jump flag is set.
 */
public class JumpOrb extends AbstractEntity {
 
    /**
     * Creates a jump orb at the given position.
     *
     * @param x left edge 
     * @param y top edge 
     * @param n repeat count
     */
    public JumpOrb(int x, int y, int n) {
        super(x, y, GameConstants.SIZE, GameConstants.SIZE, n);
    }
 
    /**
     * Signals that a conditional jump should be attempted.
     * The engine will apply the velocity boost only if the player is
     * currently pressing the jump input.
     *
     * @return CONDITIONED_JUMP
     */
    @Override
    public Effect onCollision(Cube cube, double velocityY, int previousCubeY) {
        return Effect.CONDITIONED_JUMP;
    }
 
    @Override
    public String getSpritePath() { return "/assets/JumpOrb.png"; }
 
    @Override
    protected Hitbox createHitbox(int x, int y, int n) {
        return new Hitbox(x, y, GameConstants.SIZE, GameConstants.SIZE);
    }
}