package entities;

import level.Level;

public abstract class HitBox extends Body{

	public Body parent;
	
	public HitBox(Level level, int x, int y, int xMin, int xMax, int yMin, int yMax, int color) {
		super(level, x, y, xMin, xMax, yMin, yMax, color);
	}
	
}
