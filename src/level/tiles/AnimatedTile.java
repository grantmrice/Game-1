package level.tiles;

public class AnimatedTile extends BasicTile{
	
	private int[][] animationTileCoords;
	private int currentAnimationIndex;
	private long lastIterationTime;
	private int animationSwitchDelay;

	public AnimatedTile(int id, int[][] animationCoords, int tColor, int levelColor, int switchDelay) {
		super(id, animationCoords[0][0], animationCoords[0][1], tColor, levelColor);
		animationTileCoords = animationCoords;
		currentAnimationIndex  = 0;
		lastIterationTime = System.currentTimeMillis();
		animationSwitchDelay = switchDelay;
	}
	
	public void tick(){
		if((System.currentTimeMillis() - lastIterationTime) >= animationSwitchDelay){
			lastIterationTime = System.currentTimeMillis();
			currentAnimationIndex = (currentAnimationIndex+1) % animationTileCoords.length;
			tileId = (animationTileCoords[currentAnimationIndex][0] + animationTileCoords[currentAnimationIndex][1]*32);
		}
	}

}
