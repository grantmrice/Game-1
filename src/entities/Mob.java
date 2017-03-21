package entities;

import game.InputHandler;
import level.Level;
import level.tiles.Tile;

public abstract class Mob extends Body{

	public InputHandler input = null;
	protected String name;
	protected int speed;
	protected int numSteps = 0;
	protected boolean isMoving = false;
	protected int direction = 1;
	protected int scale = 1;
	protected boolean swimming = false;
	public int health = 100;
	public boolean superMode = false;
	protected int superTime;
	protected int shootTickTime = 10;
	protected boolean lastShootState = false;
	protected int lastShootTick;
	protected boolean autoShoot = true;
	protected boolean autoPilot = true;

	public Mob(Level level){super(level);}
	public Mob(Level level, String name, int x, int y, int xMin, int xMax, int yMin, int yMax, int health, int color, int speed, InputHandler input){
		super(level, x, y, xMin, xMax, yMin, yMax, color);
		this.name = name;
		this.speed = speed;
		this.input = input;
		this.health = health;
	}
	
	public Mob speed(int x){speed = x; return this;}
	
	public abstract void tp();
	public abstract void superMode(boolean toggle);

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
	public boolean isOutOfBounds(int dx, int dy){
		for(int x = xMin; x < xMax; x++)
			if(isVoid(dx, dy, x, yMin)) return true;
		for(int x = xMin; x < xMax; x++)
			if(isVoid(dx, dy, x, yMax)) return true;
		for(int y = yMin; y < yMax; y++)
			if(isVoid(dx, dy, xMin, y)) return true;
		for(int y = yMin; y < yMax; y++)
			if(isVoid(dx, dy, xMax, y)) return true;
		return false;
	}
	
	public boolean move(int dx, int dy){
		if(dx != 0 && dy != 0){
			boolean mx = move(dx, 0); 
			boolean my = move(0, dy); 
			numSteps--;
			return mx || my;
		}
		numSteps++;
		if((terrainTangible && !blockCollision(dx, dy)) || (!terrainTangible && !isOutOfBounds(dx, dy))){
			if(dy < 0) direction = 0;
			if(dy > 0) direction = 1;
			if(dx < 0) direction = 2;
			if(dx > 0) direction = 3;
			if(speed > 0){
				x += dx*speed;
				y += dy*speed;
			}else if(tickCount%(-speed) == 0){
				switch(direction){
				default:
				case 0: y--; break;
				case 1: y++; break;
				case 2: x--; break;
				case 3: x++; break;
				}
			}
			return true;
		}
		return false;
	}
	
	public void control(){
		dx = 0; dy = 0;
		if(input.up.pressed) dy--;
		if(input.down.pressed) dy++;
		if(input.left.pressed) dx--;
		if(input.rite.pressed) dx++;
		
		if(dx != 0 || dy != 0){
			move(dx, dy);
			isMoving = true;
		}else{isMoving = false;}
	}
	
	protected boolean isVoid(int dx, int dy, int x, int y){
		if(level == null) return false;
		Tile lastTile = level.getTile((this.x + x) >> 3, (this.y + y) >> 3);
		Tile newTile = level.getTile((this.x + x + dx) >> 3, (this.y + y + dy) >> 3);
		if(lastTile.getId() != 0 && newTile.getId() == 0) return true;
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
