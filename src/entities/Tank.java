package entities;

import game.InputHandler;
import game.Performance;
import graphics.Colors;
import graphics.Font;
import graphics.Screen;
import level.Level;

public class Tank extends Mob{
	
	private int startingHealth, direction2, lastTick;
	private boolean drifting = false;
	public int layers = 0;
	private int[] image;

	public Tank(Level level){super(level);}
	public Tank(Level l, int x, int y, int health, InputHandler input){
		super(l, "Thomas the Tank Tank", x, y, -9, 9, -9, 9, health, Colors.get(-1, 222, 333, 555), 2, input);
		init();
		level.superBar.total(split(startingHealth));
	}
	private Tank(Level l, int x, int y, int health){
		super(l, "Thomas the Tank Tank", x, y, -9, 9, -9, 9, health, Colors.get(-1, 222, 333, 555), 0, null);
		init();
	}
	
	public void init(){
		state = 0;
		scale = 1;
		shootTickTime = 12;
		startingHealth = health;
		projectileTangible = true;
		drifting = false;
		if(input == null){
			level.enemies++;
			terrainTangible = false;
			autoPilot = true;
			autoShoot = true;
			speed = 1;
		}else{
			autoPilot = false;
			autoShoot = false;
			terrainTangible = true;
			speed = 2;
		}
	}

	public void tick(){
		if(health <= 0){
			if(autoPilot){
				level.enemies--;
				state = 4;
			}else{
				level.complete = true;
			}
		}

		if(autoPilot){
			int px = level.player.x;
			int py = level.player.y;
			direction2 = direction;
			
			switch(state){
			default: break;
			case 0:
				dx = dy = 0;
				if(px-x > 0) dx++;
				if(px-x < 0) dx--;
				move();
				dx = 0;
				if(py-y+2 > 0) dy++;
				if(py-y+2 < 0) dy--;
				move();
				speed = 1;

				dx = px-x;
				dy = py-y;
				if(dx < 5 && dx > -5){
					speed = -2;
					direction = (dy > 0)? 1 : 0;
					shoot();
					if(dy < 90 && dy > -90 && health < startingHealth*.7){
						dx = (Math.random() > .5)? 1 : -1;
						state = 1;
					}
				}
				if(dy < 3 && dy > -7){
					speed = -2;
					direction = (dx > 0)? 3 : 2;
					shoot();
					if(dx < 90 && dx > -90 && health < startingHealth*.7){
						dy = (Math.random() > .5)? 1 : -1;
						state = 2;
					}
				}
				break;
			case 1:
				speed = 1;
				dy = 0;
				drifting = true;
				direction2 = (py-y > 0)? 1 : 0;
				if(Math.random() < 0.0037) dx *= -1;
				if(px-x > 40){
					x++;
					dx = 1;
					direction2 = 3;
				}else if(px-x < -40){
					x--;
					dx = -1;
					direction2 = 2;
				}else{
					move();
					shoot();
				}
				if(px-x > 100 || px-x < -100)
					state = 0;
				if(((py-y > 0)? py-y : y-py) < 20){
					state = 3;
					lastTick = 0;
					drifting = false;
					dx = 0;
					dy = (py-y > 0)? -1 : 1;
				}
				break;
			case 2:
				speed = 1;
				dx = 0;
				drifting = true;
				direction2 = (px-x > 0)? 3 : 2;
				if(Math.random() < 0.0037) dy *= -1;
				if(py-y > 40){
					y++;
					dy = 1;
					direction2 = 1;
				}else if(py-y < -40){
					y--;
					dy = -1;
					direction2 = 0;
				}else{
					move();
					shoot();
				}
				if(py-y > 100 || py-y < -100)
					state = 0;
				if(((px-x > 0)? px-x : x-px) < 20){
					state = 3;
					lastTick = 0;
					drifting = false;
					dy = 0;
					dx = (px-x > 0)? -1 : 1;
				}
				break;
			case 3:
				speed = (lastTick >> 6) + 1;
				move();
				if(dist(level.player) > 200 
				|| x < 30 || y < 30
				|| 8*level.width-x < 30 || 8*level.height-y < 30){
					state = 0;
					speed = 1;
				}
				break;
			case 4:
				state = -1;
				for(int i = 0; i < 30; i+= (Performance.frameRate < 60)? 11-Performance.frameRate/6. : 1)
					for(int j = 0; j < 30; j+= (Performance.frameRate < 60)? 11-Performance.frameRate/6. : 1)
						if(image[i + 30*j] != -1)
							level.addEntity(new Diffusion(level, this, x-15+i, y-15+j, x, y, image[i + 30*j], 1, 50, true));
				if(Math.random() < .15)
					level.addEntity(new PowerUp(level, x, y, 0));
				return;
			}
			
			while(bodyCollision(level.player)){
				double t = Math.atan2(py-y, px-x);
				level.player.x += (int)(2*Math.cos(t));
				level.player.y += (int)(2*Math.sin(t));
			}
			
			if(health <= startingHealth/2 && startingHealth/2 > 0){
				double t = Math.atan2(py-y, px-x);
				level.addEntity(new Tank(level, (int)(px + 300*Math.cos(t)), (int)(py + 300*Math.sin(t)), startingHealth/2));
				startingHealth = startingHealth/2;
			}
			
		}else{
			if(superMode){
				switch(state){
				default: break;
				case 0:
					control();
					if(input.space.pressed){
						Reticule ret = new Reticule(level, this, input);
						level.addEntity(ret);
						level.target = ret;
						state = 1;
					}
					break;
				}
			}else{
				control();
				if(!autoShoot && input.space.pressed){
					shoot();
				}
			}
		}

		if(direction2 < 2){
			xMin = -10*scale;
			xMax = 10*scale;
			yMin = -11*scale;
			yMax = 11*scale;
		}else{
			xMin = -9*scale;
			xMax = 10*scale;
			yMin = -3*scale;
			yMax = 4*scale;
		}
		
		swimming = false;
		standingOn();
		switch(standingOn){
		default: break;
		case 3:
			swimming = !superMode;
			break;
		case 8:
			if(!superMode){
				if(tickCount%9 == 0) health--;
				swimming = true;
			}
			switch(direction2){
			default: break;
			case 0:
				level.addEntity(new Diffusion(level, this, x-7, y-4, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+6, y-4, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-6, y-5, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+5, y-5, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-5, y-6, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+4, y-6, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-4, y-7, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+3, y-7, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				for(int i = -2; i <= 1; i++)
					level.addEntity(new Diffusion(level, this, x+i, y-7, x+i, y-18, 36*5 + 6*3+ 1*0, 2, 5, false));
				
				level.addEntity(new Diffusion(level, this, x-7, y+2, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+6, y+2, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-6, y+3, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+5, y+3, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-5, y+4, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+4, y+4, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-4, y+5, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+3, y+5, x-2, y-18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				for(int i = -2; i <= 1; i++)
					level.addEntity(new Diffusion(level, this, x+i, y+5, x+i, y+18, 36*5 + 6*3+ 1*0, 2, 5, false));
				
				for(int i = -2; i <=1; i++){
					level.addEntity(new Diffusion(level, this, x+6, y+i, x+16, y+18, 36*5 + 6*3+ 1*0, 2, 2, false));
					level.addEntity(new Diffusion(level, this, x-7, y+i, x-17, y+18, 36*5 + 6*3+ 1*0, 2, 2, false));
				}
				break;
			case 1:
				level.addEntity(new Diffusion(level, this, x-7, y-4, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+6, y-4, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-6, y-5, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+5, y-5, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-5, y-6, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+4, y-6, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-4, y-7, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+3, y-7, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				for(int i = -2; i <= 1; i++)
					level.addEntity(new Diffusion(level, this, x+i, y-7, x+i, y+18, 36*5 + 6*3+ 1*0, 2, 5, false));
				
				level.addEntity(new Diffusion(level, this, x-7, y+2, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+6, y+2, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-6, y+3, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+5, y+3, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-5, y+4, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+4, y+4, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x-4, y+5, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				level.addEntity(new Diffusion(level, this, x+3, y+5, x-2, y+18, 36*5 + 6*3+ 1*0, 1.6, 5, false));
				for(int i = -2; i <= 1; i++)
					level.addEntity(new Diffusion(level, this, x+i, y+5, x+i, y+18, 36*5 + 6*3+ 1*0, 2, 5, false));
				
				for(int i = -2; i <=1; i++){
					level.addEntity(new Diffusion(level, this, x+6, y+i, x+16, y+18, 36*5 + 6*3+ 1*0, 2, 2, false));
					level.addEntity(new Diffusion(level, this, x-7, y+i, x-17, y+18, 36*5 + 6*3+ 1*0, 2, 2, false));
				}
				break;
			case 2:
				level.addEntity(new Diffusion(level, this, x-5, y+2, x-2, y+11, 36*5 + 6*3+ 1*0, 2, 5, false));
				level.addEntity(new Diffusion(level, this, x+7, y+2, x-2, y+11, 36*5 + 6*3+ 1*0, 2, 5, false));
				for(int i = 1; i < 12; i++)
					level.addEntity(new Diffusion(level, this, x-5+i, y+3, x-2, y+11, 36*5 + 6*3+ 1*0, 2, 5, false));
				break;
			case 3:
				level.addEntity(new Diffusion(level, this, x-9, y+2, x-2, y+11, 36*5 + 6*3+ 1*0, 2, 4, false));
				level.addEntity(new Diffusion(level, this, x+3, y+2, x-2, y+11, 36*5 + 6*3+ 1*0, 2, 4, false));
				for(int i = -3; i < 8; i++)
					level.addEntity(new Diffusion(level, this, x-5+i, y+3, x-2, y+11, 36*5 + 6*3+ 1*0, 2, 4, false));
				break;
			}
			break;
		}
		
		lastTick++;
		tickCount++;
	}

	public void render(Screen screen){
		screen.prep.setSize(30, 30);
		int direction;
		int color = this.color;
		if(drifting) direction = direction2;
		else direction = this.direction;
		if(superMode) color = Colors.get(-1, 222, (tickCount*3)%556, 555);
		int xFlip = direction%2; 
		int yFlip = direction*2;
		int trackTileOffset = ((numSteps>>3)%3) * 3;
		int wheelTileOffset = ((numSteps>>3)%4) * 3;
		int x_offset = 0;
		int y_offset = 0;
		int waterColor = 0;
		if(swimming){
			y_offset += 4;
			if(standingOn == 3){
				if(tickCount%60 < 15){
					waterColor = Colors.get(-1, -1, 225, -1);
				}else if(tickCount%60 < 30){
					if(!isMoving) y_offset -= 1;
					waterColor = Colors.get(-1, 225, 115, -1);
				}else if(tickCount%60 < 45){
					waterColor = Colors.get(-1, 115, -1, tickCount%556);
				}else{
					waterColor = Colors.get(-1, 225, 115, -1);
					y_offset -= 1;
				}
			}
		}
		if(direction < 2){
			for(int xScreen = 0; xScreen < 3; xScreen++){
				int xTile = xScreen;
				for(int yScreen = 0; yScreen < 3; yScreen++){
					int yTile = yScreen;
					if(yFlip == 2) yTile = 2-yTile;
					x_offset = 0;
					y_offset = 0;
					if(swimming){
						if(standingOn == 3)
							screen.prep.render(15-12+x_offset + 8*xScreen, 15-12+y_offset + 8*yScreen, xTile+27 + (yTile+23)*32, waterColor, yFlip, 1);
					}else{
						if(superMode) color = Colors.get(-1, 222, (tickCount*3)%556, 555);
						screen.prep.render(15-12+x_offset + 8*xScreen, 15-12+y_offset + 8*yScreen, xTile+12 + (yTile+23)*32, color, yFlip, 1);
						screen.prep.render(15-12+x_offset + 8*xScreen, 15-12+y_offset + 8*yScreen, xTile+15+trackTileOffset + (yTile+23)*32, color, yFlip, 1);
					}
					if(!superMode) color = Colors.get(-1, 222, 333, numSteps%555);
					else color = Colors.get(-1, 222, (tickCount*3)%556, 0);
					screen.prep.render(15-12+x_offset + 8*xScreen, 15-12+y_offset + 8*yScreen, xTile+24 + (yTile+23)*32, color, yFlip, 1);
				}
			}
		}else{
			x_offset = 0;
			y_offset = 0;
			int waterShift = 0;
			if(direction == 2) waterShift = 1;
			else waterShift = -3;
			if(swimming){
				y_offset += 4;
				if(standingOn == 3){
					screen.prep.render(15-11+x_offset+waterShift, 15+1, 27*32, waterColor, 2, 1);
					screen.prep.render(15-3+x_offset+waterShift, 15+1, 2 + 27*32, waterColor, 2, 1);
					screen.prep.render(15+5+x_offset+waterShift, 15+1, 27*32, waterColor, 3, 1);
				}
			}
			for(int xScreen = 0; xScreen < 3; xScreen++){
				if(!superMode) color = Colors.get(-1, 222, 333, numSteps%555);
				else color = Colors.get(-1, 222, (tickCount*3)%556, 0);
				int xTile = xScreen;
				if(xFlip == 1) xTile = 2-xTile;
				for(int yScreen = 0; yScreen < 2; yScreen++){
					if(swimming && yScreen > 0) break;
					int yTile = yScreen;
					screen.prep.render(15-12+x_offset + 8*xScreen, 15-8+y_offset + 8*yScreen, xTile + (yTile+25)*32, color, xFlip, 1);
				}
				if(!swimming){
					if(!superMode) color = Colors.get(-1, 222, 333, 555);
					else color = Colors.get(-1, 222, (tickCount*3)%556, 555);
					screen.prep.render(15-12+x_offset + 8*xScreen, 15+y_offset, xTile+wheelTileOffset + 24*32, color, xFlip, 1);
					screen.prep.render(15-12+x_offset + 8*xScreen, 15+y_offset, xTile+trackTileOffset + 23*32, color, xFlip, 1);
				}
			}
		}
		image = screen.prep.pixels.clone();
		screen.prep.render(x-15, y-15);
		Font.render(Integer.toString(health), screen, x-Integer.toString(health).length()*4-1, y-12, Colors.get(-1, 0,0,0), 1);

	}

	public void hit(Body b){
		health -= b.damage;
		if(autoPilot && b.isClass(Projectile.class)){
			if(((Projectile)b).owner == level.player){
				if(!((Projectile)b).superMode)
					level.superBar.inc();
			}else
				health += b.damage;
		}
	}

	public void superMode(boolean toggle) {
		superMode = toggle;
		terrainTangible = !toggle;
		level.invert = toggle;
		state = 0;
	}
	
	public void control(){
		dx = dy = 0;
		if(input.up.pressed) dy--;
		if(input.down.pressed) dy++;
		if(input.left.pressed) dx--;
		if(input.rite.pressed) dx++;
		
		if(dx != 0 || dy != 0){
			if(dx != 0 && dy != 0){
				drifting = true;
				if(direction < 2){
					if(dx>0) direction2 = 3;
					if(dx<0) direction2 = 2;
					move(0,dy);
				}else{
					if(dy>0) direction2 = 1;
					if(dy<0) direction2 = 0;
					move(dx,0);
				}
			}else{
				drifting = false;
				direction2 = direction;
				move(dx,dy);
			}
			isMoving = true;
		}else{isMoving = false;}
	}
	
	public boolean move(){
		if(dx != 0 && dy != 0){
			boolean mx = move(dx, 0); 
			boolean my = move(0, dy); 
			numSteps--;
			return mx || my;
		}
		numSteps++;
		boolean tankTouch = false;
		int touchSpeed = 1;
		if(speed > 1) touchSpeed = speed;
		if(autoPilot)
			for(Entity e: level.entities)
				if(e.getClass() == Tank.class && bodyCollision((Body)e, dx*touchSpeed, dy*touchSpeed)){
					tankTouch= true;
					if(x - e.x > 0) x++;
					if(x - e.x < 0) x--;
					if(y - e.y > 0) y++;
					if(y - e.y < 0) y--;
					if(dy < 0) direction = 0;
					if(dy > 0) direction = 1;
					if(dx < 0) direction = 2;
					if(dx > 0) direction = 3;
					return false;
				}
		if(((terrainTangible && !blockCollision(dx, dy)) || (!terrainTangible && !isOutOfBounds(dx, dy))) && !tankTouch){
			if(dy < 0) direction = 0;
			if(dy > 0) direction = 1;
			if(dx < 0) direction = 2;
			if(dx > 0) direction = 3;
			if(speed > 0){
				x += dx*speed;
				y += dy*speed;
			}else if(tickCount%(-speed) == 0){
				if(dy < 0) y--;
				if(dy > 0) y++;
				if(dx < 0) x--;
				if(dx > 0) x++;
			}
			return true;
		}
		return false;
	}
	
	private void shoot(){
		if(tickCount%shootTickTime == 1){
			int dx = 0; int dy = 0;
			switch(direction2){
			default: break;
			case 3: dx = 9; dy = -2; break;
			case 2: dx = -9; dy = -2; break;
			case 1: dx = 0; dy = 10; break;
			case 0: dx = 0; dy = -10; break;
			}
			if(swimming) dy += 4;
			if(direction2 > 1) dy -= 1;
			level.addEntity(new PlasmaBall(level, this, x+dx, y+dy, direction2, Colors.get(-1, 422, -1, 354), 1));//543 for fireballs
		}
	}
	
	private static int split(int x){
		if(x == 0) return 0;
		int result = x - x/2;
		result += 2*split(x/2);
		return result;
	}
	
	public void tp() {
		// TODO Auto-generated method stub
		
	}

}
