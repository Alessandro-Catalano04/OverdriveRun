package dash;

public class MultipleBlock extends AbstractEntity{
	
	private Hitbox hitbox;

    public MultipleBlock(int x, int y, int n) {
        super(x, y, 40, 40, n, EntityType.BLOCK);
        this.hitbox = new Hitbox(x, y, 40 * n, 40);
        this.setHitbox(hitbox);
    }

}