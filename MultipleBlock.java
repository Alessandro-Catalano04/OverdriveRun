package dash;
 
public class MultipleBlock extends AbstractEntity {
 
    public MultipleBlock(int x, int y, int n) {
        super(x, y, 40, 40, n, EntityType.BLOCK);
    }
 
    /**
     * Atterraggio se il cubo stava cadendo ed era sopra il blocco nel frame precedente.
     * Altrimenti collisione laterale → Game Over.
     *
     * @param velocityY     velocità verticale (positiva = verso il basso)
     * @param previousCubeY posizione Y del cubo nel frame precedente
     */
    @Override
    public Effect onCollision(Cube cube, double velocityY, int previousCubeY) {
        double previousCubeBottom = previousCubeY + GameConstants.SIZE;
        double blockTop           = this.getHitbox().getY();
 
        boolean fallingDown = velocityY >= 0;
        boolean wasAbove    = previousCubeBottom <= blockTop + GameConstants.PLATFORM_SAFE_ZONE;
 
        if (fallingDown && wasAbove) {
            return Effect.LAND;
        } else {
            return Effect.GAME_OVER;
        }
    }
 
    @Override
    public String getSpritePath() {
        return "/assets/block.png";
    }
 
    @Override
    protected Hitbox createHitbox(int x, int y, int width, int height, int n) {
        return new Hitbox(x, y, 40 * n, 40);
    }
}