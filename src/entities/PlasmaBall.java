package entities;

import game.Performance;
import graphics.Colors;
import graphics.Screen;
import level.Level;

public class PlasmaBall extends Projectile{
	
	int flipSpeed = 2;
	public long timeOfDeath = 0;
	int deathDelay;
	private boolean wave = false;
	private boolean flash = false;

	public PlasmaBall(Level l, Entity owner, int x, int y, int dir, int color, int variant){
		super(l, owner, x, y, 0, 1, -1, 3, color, dir, 1);
		wave = (variant & 1) == 1;
		flash = ((variant>>1) & 1) == 1;
		init();
	}
	
	public void init(){
		deathDelay = 30;
		if(color == -1) color = Colors.get(-1, 124, 345, 544);
		damage = 1;
		state = 0;
		superMode = owner == level.player && level.player.superMode;
		switch(direction){
		default: break;
		case 3: dx = 1; break;
		case 2: dx = -1; break;
		case 1: dy = 1; break;
		case 0: dy = -1; break;
		case 5: dx = dy = 1; break;
		case 7: dx = 1; dy = -1; break;
		case 4: dx = dy = -1; break;
		case 6: dx = -1; dy = 1; break;
		}
	}

	public void tick(){
		if(isOutOfBounds() 
		|| (tickCount > 60*(Performance.frameRate*.11))) state = -1;
		switch(state){
		default: break;
		case 0:
			if(blockCollision(dx, dy)){
				state = 1;
				projectileTangible = false;
				break;
			}
			move(dx, dy);
			
			if(wave)
				scale = 2 + (int)Math.round(Math.sin(tickCount/8 + System.currentTimeMillis()/150));
			
			Body attacker = hit();
			if(attacker != null) hit(attacker);
			break;
		case 1:
			if(timeOfDeath == 0)
				timeOfDeath = tickCount;
			if(tickCount - timeOfDeath >= deathDelay)
				state = -1;
			break;
		}
		
		tickCount++;
	}

	public void render(Screen screen){
		if(!isOnScreen(screen)){
			return;
		}
		
		screen.prep.setSize(12*scale, 12*scale);

		int rotate = direction%4;
		int tileOffset = ((tickCount>>3)%3);
		if(rotate != 2) rotate = (2*rotate + 1) % 7;
		switch(state){
		default: 
		case 0:
			screen.prep.render(2*scale, 2*scale, 1 + 27*32, color, 2*(tickCount >> flipSpeed), rotate, scale);
			if(flash)
				screen.prep.render(2*scale, 2*scale, 7+tileOffset + 27*32, color, 0, rotate, scale);
			else
				screen.prep.render(2*scale, 2*scale, 13 + 27*32, color, 0, rotate, scale);
			break;
		case 1:
			screen.prep.render(2*scale, 2*scale, 6 + 27*32, color, 0, rotate, scale);
		}
		
		if(direction > 3) screen.prep.rotate(6*scale, 6*scale, Math.PI/4);
		
		screen.prep.render(x-6*scale, y-6*scale);
		updateImage = true;
//		hitBox(screen);
	}
	
	//fucking bullshit
	public boolean isOnScreen(Screen screen){
		return
			((x-8 >= screen.x_offset && x-8 <= screen.x_offset+screen.width
			|| x+8 >= screen.x_offset && x+8 <= screen.x_offset+screen.width) 
			
			&& (y-8 >= screen.y_offset && y-8 <= screen.y_offset+screen.height
			|| y+8 >= screen.y_offset && y+8 <= screen.y_offset+screen.height));
	}
	
	public void hit(Body b){
		Body attacker = (b.isClass(HitBox.class))? 
				((HitBox)b).parent : b;
				
		if((attacker.isClass(Projectile.class) 
			&& (owner == level.player && ((Projectile)attacker).owner != level.player
				|| (owner != level.player && ((Projectile)attacker).owner == level.player)))
		|| (!attacker.isClass(Projectile.class)
			&& ((attacker == level.player && owner != level.player)
				|| (attacker != level.player && owner == level.player)))){
			state = 1;
			projectileTangible = false;
			if(tickCount > 0 && b.tickCount > 0 && !superMode){
				level.addEntity(new Diffusion(level, this, x, y, x+dx+((direction > 1)? 0 : 1), y+dy+((direction > 1)? 1: 0), (color >> 8) &255, .8, 50, false));
				level.addEntity(new Diffusion(level, this, x, y, x+dx+((direction > 1)? 0 : 1), y+dy+((direction > 1)? 1: 0), (color >> 24) &255, .8, 50, false));
				level.addEntity(new Diffusion(level, this, x, y, x+dx, y+dy, (color >> 8) &255, .8, 50, false));
				level.addEntity(new Diffusion(level, this, x, y, x+dx, y+dy, (color >> 8) &255, .8, 50, false));
				level.addEntity(new Diffusion(level, this, x, y, x+dx+((direction > 1)? 0 : -1), y+dy+((direction > 1)? -1: 0), (color >> 8) &255, .8, 50, false));
				level.addEntity(new Diffusion(level, this, x, y, x+dx+((direction > 1)? 0 : -1), y+dy+((direction > 1)? -1: 0), (color >> 24) &255, .8, 50, false));
			}
		}
	}

}
