package entities;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public class SuperPlasmaBall extends Projectile{

	protected Mob target;
	protected int health = 5;
	
	public SuperPlasmaBall(Level level){super(level);}
	public SuperPlasmaBall(Level l, Entity owner, int x, int y){
		super(l, owner, x, y, 0,0,0,0, -1, -1, 0);
		init();
	}
	
	public void init(){
		state = 0;
		damage = 1;
		superMode = true;
		setTarget();
	} 
	private void setTarget(){
		target = null;
		for(Entity e: level.entities)
			if(e.isClass(Mob.class) && e.state != -1 && e != level.player){
				target = (Mob)e;
				break;
			}
		if(target == null){
			target = level.player;
			state = 1;
		}else{health--;}
		//System.out.println(target + " "+ health);
	}

	public void tick(){

		if(target.state == -1){setTarget();}
		
		int dx = 0;
		int dy = 0;
		int px = target.x;
		int py = target.y;
		int d = (int)Math.sqrt(Math.pow(x-px, 2) + Math.pow(y-py, 2));
		speed = (target.speed < 0)? 1:target.speed;
		if(d > 16 || target == level.player) speed++;
		if(px > x) dx++;
		if(px < x) dx--;
		if(py > y) dy++;
		if(py < y) dy--;
		move(dx, dy);
		
		switch(state){
		default:
		case 0:
			if(health <= 0){
				state = 1;
				target = level.player;
				break;
			}
			hit();
			break;
		case 1:
			if(d < 5){
				state = -1;
				if(level.player.superMode){
					level.player.autoShoot = true;
				}
			}
			break;
		}
		
		tickCount++;
		
		
	}

	public void render(Screen screen){
		int c = (int)((tickCount+(System.currentTimeMillis()>>5))%548+2);
		for(int xto = 2; xto >= 0; xto--){
			for(int xScreen = 0; xScreen < 3; xScreen++){
				int xTile = xScreen;
				for(int yScreen = 0; yScreen < 3; yScreen++){
					int yTile = yScreen;
					screen.render(x-12 + 8*xScreen, y-12 + 8*yScreen, xTile + 3*xto + (yTile+18)*32, Colors.get(-1, c+3*xto,c-1+3*xto,c-2+3*xto), (xto/2)*((tickCount>>3)&1), 1);
				}
			}
		}
	}

	public void hit(Body b){
	}

}
