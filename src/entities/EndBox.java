package entities;

import graphics.Colors;
import level.Level;

public class EndBox extends TextBox{
	
	int orbs = 0;
	int lastOrbTick = 0;
	int orbDelay;
	String text;
	
	public EndBox(Level l){super(l);}
	public EndBox(Level l, String message, int x, int y){
		super(l, message, x, y, Colors.get(-1, -1, -1, 414));
		init();
	}

	public void tick(){
		super.tick();
		if(tickCount >= 60*2 && !level.player.superMode && bodyCollision(level.player)) level.complete = true;
		
	}
	
	public void hit(Body b){
		level.complete =
			tickCount >= 60*3
			&& !level.player.superMode
			&& b.isClass(Projectile.class)
			&& !((Projectile)b).superMode
			&& ((Projectile)b).owner == level.player;
	}

}
