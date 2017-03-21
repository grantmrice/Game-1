package entities;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public class Missile extends Projectile{
	
	private double angle, lastAngle;
	private int yFlip, lastX, lastY, shift, lastTick;
	private boolean newAngle;//, exploded;

	public Missile(Level l, Entity owner, int x, int y, double angle) {
		super(l, owner, x, y, 0,0,0,0, Colors.get(-1, 333, 222, 445), -1, 4);
		this.angle = angle;
		init();
	}
	public void init(){
		state = 0;
		scale = 1;
		projectileTangible = false;
		damage = 7;
		lastAngle = 0;
		lastX = x;
		lastY = y;
		newAngle = true;
//		exploded = false;
		lastTick = 0;
		shift = 0;
		for(int i = -5; i < 6; i++)
			level.addEntity(new HitBox(level, this, 4*i));
	}

	public void tick(){
		if(isOutOfBounds()){
			state = -1;
			return;
		}
		switch(state){
		default: break;
		case 0:
//			angle = tickCount/30.;
			
			newAngle = false;
			if(angle != lastAngle){
				newAngle = true;
				lastAngle = angle;
				lastX = x;
				lastY = y;
				lastTick = 1;
			}
			x = (int)Math.round(lastX +speed*lastTick*Math.cos(angle));
			y = (int)Math.round(lastY - speed*lastTick*Math.sin(angle));

			double a = (tickCount/5.);
			if(a%1 == 0){
				if(a%6 < 3){
					shift++;
					yFlip = 0;
				}else{
					shift--;
					yFlip = 1;
				}
			}
			if(damage <= 0) state = 1;
			break;
		case 1:
			state = 2;
			lastTick = 0;
			break;
		case 2:
			if(lastTick >= 2){
				state = -1;
			}
			break;
		}
		lastTick++;
		tickCount++;
	}

	public void render(Screen screen){
//		if(tickCount%2 == 0) return;
		if(isOffScreen(screen)) return;
		
		screen.prep.setSize(60*scale, 60*scale);
		
		screen.prep.render( //Wiggly stage
				18 + 8, 26,
				15 + (tickCount/5)%6 + (21)*32, color);
		
		for(int xTile =0; xTile < 2; xTile++){
			screen.prep.render( // block stages
					18 + 18*xTile, 26,
					15 + 5-((tickCount/5)%6) + (22)*32, color);
			screen.prep.render( //cone
					47, 32 - 8*xTile,
					9 + 7-((tickCount/5)%8) + (18+xTile)*32, color, 2, scale);
			
			for(int yTile =0; yTile < 2; yTile++){
				screen.prep.render( //fins
						2 + 8*xTile, 20 + 8*yTile + ((yTile==0)? 8:-8)*yFlip + 2*yFlip,
						7+xTile+(2*shift) + (21+yTile)*32, color,
						2*yFlip, scale);
			}
		}
		//filler
		for(int i = 0; i < 6; i++)
			for(int j = 0;j < 2; j++)
			screen.prep.single(34+j, 26+i, 36*2 + 6*2 + 2);
		
		screen.prep.render( //diamond stage
				39, 24,
				10 + 7-((tickCount/5)%8) + 20*32, color, 2, 1);
		
		
		for(int j = 0; j < screen.prep.height; j++)
			for(int i = 0; i < 60 -.5*Math.abs(30-j-1.5) -speed*tickCount; i++)
				screen.prep.pixels[i + screen.prep.width*j] = -1;

		screen.prep.rotate(30*scale, 30*scale, angle);
		
		image = screen.prep.pixels.clone();
		
		switch(state){
		default: break;
		case 0:
		case 1:screen.prep.render(x-30*scale, y-30*scale);
			break;
		case 2:
//			exploded = true;
			for(int i = 0; i < screen.prep.width; i++)
				for(int j = 0; j < screen.prep.width; j++)
					if(screen.prep.getPixelColor(i, j) != -1)
						level.addEntity(new Diffusion(level, this, x-30+i, y-30+j, (int)(x+.85*(i-30)), y-(int)Math.round(Math.cos(angle)), screen.prep.getPixelColor(i, j), 1, 60, true));
			break;
		}
//		center(screen);
	}

	public void hit(Body b){
		Body attacker = (b.isClass(entities.HitBox.class))? 
				((entities.HitBox)b).parent : b;
		if(attacker == this || attacker == owner) return;
		if(b.isClass(Missile.class) && ((Missile)b).owner != owner){
			b.damage -= damage;
			damage -= b.damage;
			return;
		}
		if(attacker.isClass(Projectile.class)
			&& (owner == level.player && ((Projectile)attacker).owner != level.player
				|| (owner != level.player && ((Projectile)attacker).owner == level.player))){
			damage -= b.damage;
		}
		
		if(!attacker.isClass(Projectile.class)
			&& ((attacker == level.player && owner != level.player)
				|| (attacker != level.player && owner == level.player))){
			state = 1;
			projectileTangible = false;
		}
	}
	
	private boolean isOffScreen(Screen screen){
		return (x+30*scale - screen.x_offset < 0
			|| x-30*scale - screen.x_offset > screen.width
			|| y+30*scale - screen.y_offset < 0
			|| y-30*scale - screen.y_offset > screen.width);
	}
	
	public class HitBox extends entities.HitBox{

		private int x_offset, y_offset, offset;
		boolean lastTileSolid;
		
		public HitBox(Level level, Missile parent, int offset) {
			super(level, 0, 0, -2,2,-2, 1, Colors.get(-1, -1, -1, -1));
			this.parent = parent;
			this.offset = offset;
			init();
		}

		public void init() {
			damage = 0;
			terrainTangible = false;
			projectileTangible = true;
			lastTileSolid = true;
		}

		public void tick(){
			if(parent.state != 0){
				state = -1;
				return;
			}
			for(Entity e: level.entities){
				if(e != this
				&& e != owner
				&& e.isClass(Body.class)
				&& ((Body)e).projectileTangible
				&& bodyCollision((Body)e)){
					((Body)e).hit(parent);
					parent.hit((Body)e);
				}
			}
			if(((Missile)parent).newAngle){
				x_offset = (int)Math.round((offset*Math.cos(((Missile)parent).angle) - 2*Math.sin(((Missile)parent).angle))*((Missile)parent).scale);
				y_offset = -(int)Math.round((offset*Math.sin(((Missile)parent).angle) + Math.cos(((Missile)parent).angle))*((Missile)parent).scale);
			}

			x = parent.x + x_offset*parent.scale;
			y = parent.y + y_offset*parent.scale;
			if(isSolidTile() && !lastTileSolid) parent.state = 1;
			lastTileSolid = isSolidTile();
			
			tickCount++;
		}

		public void render(Screen screen){
//			hitBox(screen);
		}

		public void hit(Body b){parent.hit(b);}
		
	}
}
