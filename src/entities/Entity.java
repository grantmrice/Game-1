package entities;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public abstract class Entity {

	public int state = 0;
	public int x, y;
	public int tickCount = 0;
	protected Level level;
	protected int[] image = null;
	protected boolean updateImage = true;
	public int scale = 1;
	public int color;

	public Entity(Level l){init(l);}
	public Entity(Level level, int x, int y, int color){
		init(level);
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public final void init(Level l){
		level = l;
	}
	
	public boolean isClass(Class<?> c){
		for(Class<?> r = getClass(); r != null; r = r.getSuperclass())
			if(c == r) return true;
		return false;
	}
	
	public void center(Screen screen){
		screen.render(x, y, 0, Colors.get(-1, -1, 34, 555));
	}
	
	public double curve(double curve, double percent){
		return Math.log((Math.pow(Math.E, curve) - 1) * percent + 1) / curve;
	}
	public double dist(Entity other){
		return Math.sqrt(Math.pow(x-other.x, 2)+Math.pow(y-other.y, 2));
	}
	public double angle(Entity other){
		return Math.atan2(y-other.y, x-other.x);
	}
	
	public abstract void init();
	public abstract void tick();
	public abstract void render(Screen screen);
	

}
