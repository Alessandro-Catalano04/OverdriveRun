package model;
 
/**
 * LaunchPad: A low-profile pad that automatically launches the cube upward on contact.
 *
 * Unlike a normal jump, the launch is unconditional — the player does not need to
 * press any key. onCollision always returns JUMP.
 *
 * The sprite is only 10 px tall so it sits flush with the ground without creating a visual step.
 */
public class LaunchPad extends AbstractEntity {
 
    /**
     * Creates a launch pad at the given position.
     *
     * @param x left edge of the pad in px
     * @param y top edge of the pad in px
     * @param n repeat count
     */
    public LaunchPad(int x, int y, int n) {
        super(x, y, GameConstants.SIZE, 10, n);
    }
 
    /**
     * Contact with the pad always triggers an automatic upward impulse.
     *
     * @return JUMP unconditionally
     */
    @Override
    public Effect onCollision(Cube cube, double velocityY, int previousCubeY) {
        return Effect.JUMP;
    }
 
    @Override
    public String getSpritePath() { return "/assets/pad.png"; }
 
    @Override
    protected Hitbox createHitbox(int x, int y, int n) {
        return new Hitbox(x, y, GameConstants.SIZE * n, GameConstants.PAD_HEIGHT);
    }
}