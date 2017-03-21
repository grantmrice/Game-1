package level.tiles;

import graphics.Screen;
import level.Level;

public class BasicTile extends Tile{
	
	protected int tileId;
	protected int tileColor;
	
	public BasicTile(int id, int x, int y, int tColor, int levelColor) {
		super(id, false, false, levelColor);
		tileId = x + y*32;
		tileColor = tColor;
	}

	public void tick(){}
	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x, y, tileId, tileColor);
		
	}

}
