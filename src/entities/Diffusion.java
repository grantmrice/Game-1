package entities;

import graphics.Screen;
import level.Level;

public class Diffusion extends Particle{
	
	private double angle, speed;
	private int x_offset, y_offset, startX, startY, duration;
	private boolean fade;

	public Diffusion(Level level, Entity owner, int x, int y, int centerX, int centerY, int color, double speed, int duration, boolean fade){
		super(level, owner, x, y, color);
		angle = Math.sqrt(Math.pow(x-centerX,2)+Math.pow(y-centerY,2)) == 0 ? Math.random()*2*Math.PI : Math.atan2(y-centerY, x-centerX);
		init();
		this.speed = speed;
		this.duration = duration;
		this.fade = fade;
	}

	public void init(){
		speed = 1;
		duration = Integer.MAX_VALUE;
		fade = true;
		x_offset = 0;
		y_offset = 0;
		startX = x;
		startY = y;
	}

	public void tick(){
		if(color < 0 || tickCount >= duration) state = -1;
		x = (int)(startX + speed*tickCount*Math.cos(angle));
		y = (int)(startY + speed*tickCount*Math.sin(angle));
		x += x_offset;
		y += y_offset;
		x_offset += (int)(Math.random()*100)%5 - 2;
		y_offset += (int)(Math.random()*100)%5 - 2;
		
		if(fade && tickCount > 30)
			switch((int)(Math.random()*1997)%5){
			case 0: color--; break;
			case 1: color -= 6; break;
			case 2: color -= 36; break;
			case 3: color++; break;
			case 4: color += 26; break;
			}
		
		tickCount++;
	}

	public void render(Screen screen){
		if(isOffScreen(screen)){
			state = -1;
			return;
		}
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 2; j++)
				screen.single(x+i, y+j, color);
		}
//		center(screen);
	}
	
	private boolean isOffScreen(Screen screen){
		return (x - screen.x_offset < 0
			|| x - screen.x_offset > screen.width
			|| y - screen.y_offset < 0
			|| y - screen.y_offset > screen.width);
	}

}
