package entities;

import graphics.Screen;
import level.Level;

public class Streak extends Particle{
	
	private double angle, speed, length;
	private int startX, startY, duration;
	
	public Streak(Level level, Entity owner, int x, int y, int centerX, int centerY, double speed, double length, int duration, int color){
		super(level, owner, x, y, color);
		angle = Math.sqrt(Math.pow(x-centerX,2)+Math.pow(y-centerY,2)) == 0 ? Math.random()*2*Math.PI : Math.atan2(y-centerY, x-centerX);
		init();
		this.speed = speed;
		this.length = length;
		this.duration = duration;
	}

	public void init(){
		startX = x;
		startY = y;
	}

	public void tick() {
		if(tickCount >= duration) state = -1;
		x = (int)(startX + speed*tickCount*Math.cos(angle));
		y = (int)(startY + speed*tickCount*Math.sin(angle));
		
		tickCount++;
	}

	public void render(Screen screen){
		for(double i = 2; i < length; i+= .5){
			if(Math.abs(length-i) < 2) continue;
			screen.single((int)Math.round(x+1 + (i-length/2)*Math.cos(angle)), (int)Math.round(y+1 + (i-length/2)*Math.sin(angle)), 123);
			screen.single((int)Math.round(x+1 + (i-length/2)*Math.cos(angle)), (int)Math.round(y-1 + (i-length/2)*Math.sin(angle)), 123);
			screen.single((int)Math.round(x-1 + (i-length/2)*Math.cos(angle)), (int)Math.round(y+1 + (i-length/2)*Math.sin(angle)), 123);
			screen.single((int)Math.round(x-1 + (i-length/2)*Math.cos(angle)), (int)Math.round(y-1 + (i-length/2)*Math.sin(angle)), 123);
			screen.single((int)Math.round(x+1 + (i-length/2)*Math.cos(angle)), (int)Math.round(y + (i-length/2)*Math.sin(angle)), 123);
			screen.single((int)Math.round(x+1 + (i-length/2)*Math.cos(angle)), (int)Math.round(y + (i-length/2)*Math.sin(angle)), 123);
			screen.single((int)Math.round(x + (i-length/2)*Math.cos(angle)), (int)Math.round(y+1 + (i-length/2)*Math.sin(angle)), 123);
			screen.single((int)Math.round(x + (i-length/2)*Math.cos(angle)), (int)Math.round(y-1 + (i-length/2)*Math.sin(angle)), 123);
		}
		for(double i = 0; i < length; i+= .5){
			screen.single((int)Math.round(x + (i-length/2)*Math.cos(angle)), (int)Math.round(y + (i-length/2)*Math.sin(angle)), 0);
		}
		
	}

}
