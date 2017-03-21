package entities;

import level.Level;
import level.tiles.Tile;

public abstract class Projectile extends Body{
	
	public Entity owner = null;
	protected boolean superMode = false;
	protected int speed;
	protected int direction = 3;
	
	public Projectile(Level l){super(l);}
	public Projectile(Level l, Entity owner, int x, int y, int xMin, int xMax, int yMin, int yMax, int color, int dir, int speed){
		super(l, x, y, xMin, xMax, yMin, yMax, color);
		this.owner = owner;
		direction = dir;
		this.speed = speed;
	}
	
	public void move(int dx, int dy){
		if(dx != 0 && dy != 0){
			move(dx, 0);
			move(0, dy);
			return;
		}
		x += dx*speed;
		y += dy*speed;
	}
	
	protected boolean isOutOfBounds(){
		if(level == null) return false;
		int dx = 0, dy = 0;
		switch(direction){
		default: break;
		case 3: dx = -8; break;
		case 2: dx = 0; break;
		case 1: dy = -8; break;
		case 0: dy = 0; break;
		}
		return (level.getTile((x+(3+dx)*scale) >> 3, (y+(3+dy)*scale) >> 3) == Tile.VOID);
	}
	
	protected Body hit(){
		for(Entity e: level.entities)
			if(e != this
			&& e != owner  
			&& e.isClass(Body.class)
			&& ((Body)e).projectileTangible
			&& bodyCollision((Body)e)){
				((Body)e).hit(this);
				return (Body)e;
			}
		return null;
	}
	
	public boolean blockCollision(int dx, int dy){
		for(int x = xMin; x < xMax; x++)
			if(isSolidTile(dx, dy, x, yMin)) return true;
		for(int x = xMin; x < xMax; x++)
			if(isSolidTile(dx, dy, x, yMax)) return true;
		for(int y = yMin; y < yMax; y++)
			if(isSolidTile(dx, dy, xMin, y)) return true;
		for(int y = yMin; y < yMax; y++)
			if(isSolidTile(dx, dy, xMax, y)) return true;
		return false;
	}
	
	protected boolean isSolidTile(int dx, int dy, int x, int y){
		if(level == null) return false;
		Tile lastTile = level.getTile((this.x + x) >> 3, (this.y + y) >> 3);
		Tile newTile = level.getTile((this.x + x + dx) >> 3, (this.y + y + dy) >> 3);
		if(!lastTile.solid() && newTile.solid()) return true;
		return false;
	}

}
