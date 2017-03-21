package entities;

import java.util.ArrayList;
import game.InputHandler;
import graphics.Colors;
import graphics.Screen;
import level.Level;

public class Reticule extends Particle{
	
	Reticule parent, child;
	boolean grown = false;
	private int growTickTime = 60;
	private int radius, layer;
	private int speed = 2;
	private int x1, y1;
	private InputHandler input;
	private ArrayList<Mob> targets = new ArrayList<Mob>();

	public Reticule(Level l, Tank owner, InputHandler input) {
		super(l, owner, owner.x, owner.y, Colors.get(-1, -1, -1, 411));
		this.input = input;
		init();
	}
	private Reticule(Reticule parent){
		super(parent.level, parent.owner, parent.x, parent.y, parent.color);
		this.parent = parent;
		init();
	}

	public void init() {
		((Tank)owner).layers++;
		if(parent == null){
			state = 0;
			layer = 0;
			radius = 5;
		}else{
			state = 1;
			radius = 0;
			layer = parent.layer + 1;
			this.input = parent.input;
		}
		double square = Math.sqrt(2)/2;
		double rectX = Math.cos(Math.PI*3/8);
		double rectY = Math.sin(Math.PI*3/8) - square;
		level.addEntity(new HitBox(level, this, x, y, -square, square, -square, square, 0, 0, 0, 0));
		level.addEntity(new HitBox(level, this, x, y, -rectX, rectX, 0, rectY, 0, square, 0, 1));
		level.addEntity(new HitBox(level, this, x, y, -rectX, rectX, -rectY, 0, 0, -square, 0, -1));
		level.addEntity(new HitBox(level, this, x, y, 0, rectY, -rectX, rectX, square, 0, 1, 0));
		level.addEntity(new HitBox(level, this, x, y, -rectY, 0, -rectX, rectX, -square, 0, -1, 0));
	}

	public void tick() {
		if(state == 0){
			if(input.kick.pressed){kill(); return;}
			control();
		}else if(state < 3){
			int dx = 0, dy = 0;
			if(x > parent.x) dx--;
			if(x < parent.x) dx++;
			if(y < parent.y) dy++;
			if(y > parent.y) dy--;

			if(dx != 0 || dy != 0){
				move(dx, dy);
			}else{}
		}
		
		switch(state){
		default: break;
		case 0:
			//radius = tickCount/6;
			break;
		case 1:
			radius++;
			if(radius >= 5* Math.pow(9./7, layer)) state = 2;
			break;
		case 2:
			grown = true;
			if(radius > 0) radius--;
			else state = 1;
			break;
		case 3: state = 4; break;
		case 4: 
			ArrayList<Mob> gotten = new ArrayList<Mob>();
			for(Mob b: targets)
				if(!gotten.contains(b)){
					b.health -= ((Tank)owner).layers-layer;
					gotten.add(b);
				}
			state = 5;
			break;
		case 5: 
			radius = radius * 10/11;
			if(radius <= 0){ 
				state = -1;
				if(child == null){
					owner.state = 0;
					level.targetPlayer();
				}
				return;
			}
			break;
		}
		
		if(!input.space.pressed && state < 3) state = 3;
		
		if(parent == null && tickCount >= growTickTime && child == null){
			child = new Reticule(this);
			level.addEntity(child);
		}else if(grown && child == null){
			child = new Reticule(this);
			level.addEntity(child);
		}
		
		tickCount++;
	}

	public void render(Screen screen){
		screen.ring(x, y, radius, 180, radius%216, 2);
	}
	
	
	public void shit(Screen screen){
		screen.render(x-4, y-4, 7 + 19*32, color);
		for(int xTile = 0; xTile < 3; xTile++)
			for(int yTile = 0; yTile < 3; yTile++)
				screen.render(x1-12 + xTile*8, y1-12 + yTile*8, xTile + (yTile+18)*32, color);
	}

	public void control(){
		int dx = 0, dy = 0;
		if(input.up.pressed) dy--;
		if(input.down.pressed) dy++;
		if(input.left.pressed) dx--;
		if(input.rite.pressed) dx++;
		
		if(dx != 0 || dy != 0){
			move(dx, dy);
		}else{}
	}
	public void move(int dx, int dy){
		if((((Tank)owner).layers+1)*curve(-1.31,Math.random()) < layer) return;
		if(speed > 0){
			x += dx*speed;
			y += dy*speed;
		}else if(tickCount%(-speed) == 0){
			if(dy < 0) y--;
			if(dy > 0) y++;
			if(dx < 0) x--;
			if(dx > 0) x++;
		}
	}
	
	protected void kill(){
		state = -1;
		if(child != null) child.kill();
	}
	
	public class HitBox extends Body{

		private double minX,minY,maxX,maxY,xOff, yOff;
		private int x_offset, y_offset;
		private Reticule parent;
		
		public HitBox(Level level, Reticule parent, int x, int y, double xMin, double xMax, double yMin, double yMax, double xOff, double yOff, int x_offset, int y_offset) {
			super(level, x, y, 0,0,0,0, Colors.get(-1, -1, -1, -1));
			this.xOff = xOff;
			this.yOff = yOff;
			this.parent = parent;
			minX = xMin;
			minY = yMin;
			maxX = xMax;
			maxY = yMax;
			this.x_offset = x_offset;
			this.y_offset = y_offset;
			init();
		}

		public void init() {
			damage = 0;
			terrainTangible = false;
			projectileTangible = false;
		}

		public void tick(){
			if(parent.state == -1){
				state = -1;
				return;
			}
			xMin = (int)(minX*parent.radius);
			yMin = (int)(minY*parent.radius);
			xMax = (int)(maxX*parent.radius);
			yMax = (int)(maxY*parent.radius);
			if(parent.state == 3){
				for(Entity e: level.entities)
					if(e.isClass(Mob.class)
					&& e != level.player
					&& bodyCollision((Body)e)){
						((Body)e).hit(this);
						parent.targets.add((Mob)e);
					}
				state = -1;
				}
			x = parent.x + (int)(xOff*parent.radius) + x_offset;
			y = parent.y + (int)(yOff*parent.radius) + y_offset;
		}

		public void render(Screen screen){
			//hitBox(screen);
		}

		public void hit(Body b){}
		
	}

}
