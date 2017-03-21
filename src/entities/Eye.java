package entities;

import game.InputHandler;
import game.Performance;
import graphics.Colors;
import graphics.Font;
import graphics.Screen;
import level.Level;

public class Eye extends Mob {
	
	private boolean exploded = false;
	private int targetX, targetY, patience, dx, dy, d, lastX, lastY, lastTick,
			r=48;
	private double xIris, yIris, t, reach, irisT,
			shell = 0.8,
			edge = .35, 
			zoom = Math.PI*r/48,
			scale = 2;
	public EyeTarget target;

	public Eye(Level level){super(level);}
	public Eye(Level level, int x, int y, int health, InputHandler input) {
		super(level, "Angry Monocle", x, y, 0,0,0,0, health, Colors.get(511, 331, 111, 555), 4, input);
		init();
	}
	public void init(){
		state = 0;
		shootTickTime = 33;
		autoPilot = autoShoot = input == null;
		if(autoPilot) level.enemies++;
		target = new EyeTarget(level, this, x, y);
		level.addEntity(target);
		target.target = level.player;
		target.state = 0;
		reach = 240;
		double square = Math.sqrt(2)/2;
		double rectX = Math.cos(Math.PI*3/8);
		double rectY = Math.sin(Math.PI*3/8) - square;
		level.addEntity(new HitBox(level, this, x, y, -square, square, -square, square));
		level.addEntity(new HitBox(level, this, x, y, -square-rectY, square+rectY, -rectX, rectX));
		level.addEntity(new HitBox(level, this, x, y, -rectX, rectX, -square-rectY, square+rectY));
	}

	public void tick(){
		if(state == -1) return;
		if(health <= 0){
			if(autoPilot){
				level.enemies--;
				state = 5;
				if(Math.random() < .71)
					level.addEntity(new PowerUp(level, x, y, 0));
			}else{
				level.complete = true;
			}
			return;
		}
		if(input == null){
			int px = level.player.x;
			int py = level.player.y;
			dx = x - px;
			dy = y - py;
			d = (int)Math.sqrt(dx*dx+dy*dy);
			switch(state){
			default: break;
			case 0:
				speed = 2;
				if(d >= reach){
					t = Math.random()*2*Math.PI;
					x = (int)(px + reach*Math.cos(t));
					y = (int)(py + reach*Math.sin(t));
					lastX = x;
					lastY = y;
					lastTick = 0;
					t = Math.random()*2*Math.PI;
				}
				x = (int)Math.round(lastX + speed*lastTick*Math.cos(t));
				y = (int)Math.round(lastY + speed*lastTick*Math.sin(t));
				if(d < reach/2){
					state = 1;
					target.state = 1;
				}
				break;
			case 1:
				if(target.x == px && target.y == py){
					speed = 1;
					state = 2;
					lastTick = 0;
					targetX = x;
					targetY = y;
					patience = 20;
				}
				break;
			case 2:
				if(Math.sqrt(Math.pow(targetX-x, 2)+Math.pow(targetY-y, 2)) <= speed){ state = 3; break;}
				x = (int)(lastX + speed*lastTick*Math.cos(t));
				y = (int)(lastY + speed*lastTick*Math.sin(t));
				
				if(patience <= 0){
					state = 4;
					target.speed = 0;
					target.state = 0;
					lastTick = 0;
				}
				if(tickCount%33 == 0) shoot();
				break;
			case 3:
				targetX = (int)(px + 2*reach*Math.random() -reach);
				targetY = (int)(py + 2*reach*Math.random() -reach);
				t = Math.atan2(targetY-y, targetX-x);
				lastX = x;
				lastY = y;
				lastTick = 0;
				state = 2;
				break;
			case 4:
				target.speed = lastTick/25;
				if(lastTick >= 600){
					state = 0;
					target.speed = 3;
					lastX = x;
					lastY = y;
					lastTick = 0;
					t = Math.random()*2*Math.PI;
				}
				break;
			case 5:
				return;
			case 6:
				state = -1;
			}
		}else{
			control();
			if(input.space.pressed){
				if(!lastShootState){
					shoot();
					lastShootTick = tickCount;
				}else{
					if(tickCount - lastShootTick >= shootTickTime){
						shoot();
						lastShootTick = tickCount;
					}
				}

			}
			lastShootState = input.space.pressed;
		}
			
		xIris = (x-target.x)/zoom;
		yIris = (y-target.y)/zoom;
		if(Math.sqrt(xIris*xIris + yIris*yIris) > r*edge*scale){
			irisT = Math.atan2(yIris, xIris);
			xIris = r*edge*scale*Math.cos(irisT);
			yIris = r*edge*scale*Math.sin(irisT);
		}
		xIris += 2*r;
		yIris += 2*r;
		
		lastTick++;
		tickCount++;
	}

	public void render(Screen screen) {

//		if(tickCount%2 == 0) return;
		for(int xTile = -2*r; xTile < 2*r; xTile++){
			int xScreen = x + xTile;
			for(int yTile = -2*r; yTile < 2*r; yTile++){
				int yScreen = y + yTile;
				if(Math.sqrt(Math.pow(xScreen-x,2)+Math.pow(yScreen-y,2)) > r*shell) continue;
				double t = Math.atan2(yTile, xTile);
				int xSheet = (int)((r - Math.sqrt(r*r - (xTile*xTile+yTile*yTile)))*Math.cos(t)*scale + xIris);
				int ySheet = (int)((r - Math.sqrt(r*r - (xTile*xTile+yTile*yTile)))*Math.sin(t)*scale + yIris);
				screen.single(xScreen, yScreen, screen.getSheetColor(1, xSheet, ySheet, color));
			}
		}
		
		//aura
		for(int i = (int)(-r*shell-15); i < (int)(r*shell+15); i++)
			for(int j = (int)(-r*shell-15); j < (int)(r*shell+15); j++){
				double d = Math.sqrt(Math.pow(i, 2)+Math.pow(j, 2));
				if(d >=r *shell-1 
				&& d < r*shell+6 + 7*Math.cos(i*j+tickCount/12.) + 4*Math.sin((i*i+j*j)/5)
				&& ((int)(d*7- tickCount/1.3)&48) > 3 
				&& true)
					screen.single(x+i, y+j, 36*((int)(i*i+j*j)%5));
			}
		
		//particle death
		if(state == 5 && !exploded){
			state = -1;
			exploded = true;
			for(int i = (int)(-r*shell-15); i < (int)(r*shell+15); i++)
				for(int j = (int)(-r*shell-15); j < (int)(r*shell+15); j++){
					int pColor = screen.getPixelColor(x-screen.x_offset+i, y-screen.y_offset+j);
					if(pColor/6%6 < pColor%6 + pColor/36 && Math.sqrt(Math.pow(i, 2)+Math.pow(j, 2)) <= r*shell+15)
							level.addEntity(new Diffusion(level, this, x+i, y+j, x, y, pColor, 3, 330, false));
				}
		}
		
		Font.render(patience, screen, x, y-50, Colors.get(-1, 0, 0, 535), 1);
		Font.render(health, screen, x-25, y-50, Colors.get(-1, 0, 0, 423), 1);
		//center(screen);
	}

	public void renderb(Screen screen) {
		xIris = (x-screen.x_offset-input.mouse.x);
		yIris = (y-screen.y_offset-input.mouse.y);
		if(Math.hypot(xIris, yIris) > r){
			double t = Math.atan2(yIris, xIris);
			xIris = (int)(r*Math.cos(t));
			yIris = (int)(r*Math.sin(t));
		}
		xIris += 96;
		yIris += 96;
		for(int xTile = -96; xTile < 96; xTile++){
			int xScreen = x + xTile;
			int xSheet = xTile + (int)xIris;
			for(int yTile = -96; yTile < 96; yTile++){
				int yScreen = y + yTile;
				int ySheet = yTile + (int)yIris;
				if(Math.sqrt(Math.pow(xScreen-x, 2)+Math.pow(yScreen-y, 2)) > r) continue;
				screen.single(xScreen, yScreen, screen.getSheetColor(1, xSheet, ySheet, color));
			}
		}
		//center(screen);
	}

	public void tp() {
		x = (int)(Math.random()*level.width*8);
		y = (int)(Math.random()*level.height*8);
		// TODO Auto-generated method stub

	}

	public void superMode(boolean toggle) {
		// TODO Auto-generated method stub

	}

	public void hit(Body attacker){
		if(attacker == null
		|| !attacker.isClass(Projectile.class)
		|| (this == level.player && ((Projectile)attacker).owner != this)
		|| (this != level.player && ((Projectile)attacker).owner == level.player)){
			switch(state){
			default: patience++; break;
			case 0: 
				state = 1;
				if(autoPilot) target.state = 1;
				break;
			case 2: patience--; break;
			case 4: health -= attacker.damage; break;
			}
		}
	}
	
	private void shoot(){
		 level.addEntity(new Missile(level, this, x, y, Math.atan2(y-target.y, target.x-x)));
		 lastShootTick = tickCount;
	}
	
	public class EyeTarget extends Entity{

		private Eye parent;
		private Entity target;
		private int targetX, targetY, speed,dx, dy, d, lastX, lastY, lastTick;
		double t;
		
		public EyeTarget(Level level, Eye parent, int x, int y) {
			super(level, x, y, 0);
			this.parent = parent;
			init();
		}

		public void init(){
			speed = 3;
		}

		public void tick(){
			if(parent.state == -1) state = -1;
			if(target != null){
				targetX = target.x;
				targetY = target.y;
			}
			
			int speed_;
			d = (int)Math.sqrt(dx*dx+dy*dy);
			
			switch(state){
			default:
				break;
			case 0:
				double reach = 2.1*r;
				if(d >= reach){
					t = Math.atan2(dy, dx);
					dx = (int)(reach*Math.cos(t));
					dy = (int)(reach*Math.sin(t));
					lastX = dx;
					lastY = dy;
					lastTick = 0;
					t = Math.random()*2*Math.PI;
				}
				dx = (int)Math.round(lastX + speed*lastTick*Math.cos(t));
				dy = (int)Math.round(lastY + speed*lastTick*Math.sin(t));
				x = parent.x + dx;
				y = parent.y + dy;
				lastTick++;
				break;
			case 1:
				dx = x - targetX;
				dy = y - targetY;
				
				speed_ = speed;
				if(d > speed){
					t = Math.atan2(-dy, -dx);
					x += (int)Math.round(speed_*Math.cos(t));
					y += (int)Math.round(speed_*Math.sin(t));
				}else{
					x -= dx;
					y -= dy;
				}
				break;
			}
			
			tickCount++;
		}

		public void render(Screen screen){
//			center(screen);
		}
		
	}
	
	public class HitBox extends entities.HitBox{
		
		public HitBox(Level level, Eye parent, int x, int y, double xMin, double xMax, double yMin, double yMax) {
			super(level, x, y, 0,0,0,0,0);
			this.parent = parent;
			this.xMin = (int)(xMin*parent.r*shell);
			this.yMin = (int)(yMin*parent.r*shell);
			this.xMax = (int)(xMax*parent.r*shell);
			this.yMax = (int)(yMax*parent.r*shell);
			init();
		}

		public void init(){
			damage = 0;
			terrainTangible = false;
			projectileTangible = true;
		}

		public void tick(){
			if(parent.state == -1){
				state = -1;
				return;
			}
			while(bodyCollision(level.player)){
				double t = Math.atan2(level.player.y-parent.y, level.player.x-parent.x);
				level.player.x += (int)(2*Math.cos(t));
				level.player.y += (int)(2*Math.sin(t));
			}
			x = parent.x;
			y = parent.y;
		}

		public void render(Screen screen){
//			hitBox(screen);
		}

		public void hit(Body b){
			parent.hit(b);
		}
		
	}

}

