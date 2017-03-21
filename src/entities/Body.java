package entities;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public abstract class Body extends Entity{

	public int damage = 0;
	protected int dx, dy;
	protected int xMin=0,xMax=0,yMin=0,yMax=0;
	protected boolean terrainTangible = true;
	protected boolean projectileTangible = true;
	protected int standingOn = 0;

	public Body(Level l){super(l);}
	public Body(Level level, int x, int y, int xMin, int xMax, int yMin, int yMax, int color){
		super(level, x, y, color);
		this.xMin = xMin*scale;
		this.xMax = xMax*scale;
		this.yMax = yMax*scale;
		this.yMin = yMin*scale;
	}

	protected boolean bodyCollision(Body b){return bodyCollision(b, true);}
	protected boolean bodyCollision(Body b, int dx, int dy){return bodyCollision(b, dx, dy, true);}
	
	protected boolean bodyCollision(Body b, boolean thurough){
			if(b != this){
				if(x+xMax*scale >= b.x+b.xMin*b.scale && x+xMax*scale <= b.x+b.xMax*b.scale && y+yMax*scale >= b.y+b.yMin*b.scale && y+yMax*scale <= b.y+b.yMax*b.scale
				|| x+xMax*scale >= b.x+b.xMin*b.scale && x+xMax*scale <= b.x+b.xMax*b.scale && y+yMin*scale >= b.y+b.yMin*b.scale && y+yMin*scale <= b.y+b.yMax*b.scale
				|| x+xMin*scale >= b.x+b.xMin*b.scale && x+xMin*scale <= b.x+b.xMax*b.scale && y+yMax*scale >= b.y+b.yMin*b.scale && y+yMax*scale <= b.y+b.yMax*b.scale
				|| x+xMin*scale >= b.x+b.xMin*b.scale && x+xMin*scale <= b.x+b.xMax*b.scale && y+yMin*scale >= b.y+b.yMin*b.scale && y+yMin*scale <= b.y+b.yMax*b.scale
				|| (thurough && b.bodyCollision(this, false))
					)return true;
			}
		return false;
	}
	
	protected boolean bodyCollision(Body b, int dx, int dy, boolean thorough){
			if(b != this){
				if(x+dx+xMax*scale >= b.x+b.xMin*b.scale && x+dx+xMax*scale <= b.x+b.xMax*b.scale && y+dy+yMax*scale >= b.y+b.yMin*b.scale && y+dy+yMax*scale <= b.y+b.yMax*b.scale
				|| x+dx+xMax*scale >= b.x+b.xMin*b.scale && x+dx+xMax*scale <= b.x+b.xMax*b.scale && y+dy+yMin*scale >= b.y+b.yMin*b.scale && y+dy+yMin*scale <= b.y+b.yMax*b.scale
				|| x+dx+xMin*scale >= b.x+b.xMin*b.scale && x+dx+xMin*scale <= b.x+b.xMax*b.scale && y+dy+yMax*scale >= b.y+b.yMin*b.scale && y+dy+yMax*scale <= b.y+b.yMax*b.scale
				|| x+dx+xMin*scale >= b.x+b.xMin*b.scale && x+dx+xMin*scale <= b.x+b.xMax*b.scale && y+dy+yMin*scale >= b.y+b.yMin*b.scale && y+dy+yMin*scale <= b.y+b.yMax*b.scale
				|| (thorough && b.bodyCollision(this, false))
					)return true;
			}
		return false;
	}
	
	protected int standingOn(){
		standingOn = level.getTile(x >> 3, y >> 3).getId();
		return standingOn;
	}
	protected boolean isSolidTile(){
		return level.getTile(x >> 3, y >> 3).solid();
	}
	
	public void hitBox(Screen screen){
		int boxColor = Colors.get(-1, -1, -1, 253);
		for(int i = x+xMin*scale; i <= x+xMax*scale; i++){
			screen.render(i, y+yMin, 0, boxColor);
			screen.render(i, y+yMax, 0, boxColor);
		}
		for(int i = y+yMin*scale+1; i <= y+yMax*scale-1; i++){
			screen.render(x+xMin, i, 0, boxColor);
			screen.render(x+xMax, i, 0, boxColor);
		}
	}
	
	public abstract void hit(Body b);

}
