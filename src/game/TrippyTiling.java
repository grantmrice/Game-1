package game;

import graphics.Screen;
import level.Level;

public class TrippyTiling {
	public int state = -1;
	private int[] frame;
	private int x, y, color, radius = 300, speed = 2;
	private int tickCount = 0, pattern = 1;
	private Level level;
	private Screen screen;

	public void tick(){
		switch(state){
		default: return;
		case 0:
			if(tickCount == radius) state = 1;
			break;
		case 1: state = 2; break;
		case 2:
			double a = Math.atan2(level.player.y-screen.y_offset-y, level.player.x-screen.x_offset-x);
			x += (int)(speed*Math.cos(a));
			y += (int)(speed*Math.sin(a));
			if(Math.abs(level.player.x-screen.x_offset-x) < 3
			&& Math.abs(level.player.y-screen.y_offset-y) < 3)
				state = 3;
			break;
		case 3: state = 4; break;
		case 4:
			if(tickCount%13 == 0) pattern++;
			break;
		case 5: state = -1;
		}
		tickCount++;
	}
	
	public void render(Screen screen){
		//screen.render(frame);
		if(state != -1)
			for(int i = 0; i < screen.width; i++)
				for(int j = 0; j < screen.height; j++){
					double d = (x-i)*(y-j), t = curve( 4.8, tickCount/300.);
					int c1, c2, dir = 1;
					if(t > 1) t = 1;
					if(state == 1); dir = -1;
					
					c1 = (int)(d-radius+tickCount);
					if(c1 < 0){c1 = 0; t = 0;}
					else c1 = (int)(((int)(d*pattern+dir*tickCount)&color)*t); 
					
					c2 = (int)(frame[i + j*screen.width]*(1-t));
					screen.staticSingle(i, j, c1+c2);
				}
	}
	
	public void update(Level level, Screen screen){
		System.out.println(screen.x_offset +" " +screen.y_offset +"-trans");
		this.level = level;
		this.screen = screen;
		frame = screen.pixels.clone();
		color = (int)(System.currentTimeMillis()%216);
	}
	public void start(){
		state = 0;
		x = level.player.x-screen.x_offset;
		y = level.player.y-screen.y_offset;
		System.out.println(x +" " +y +"-trans");
	}
	
	public void setColor(){
		color = (int)(System.currentTimeMillis()%216);
	}
	private double curve(double curve, double percent){
		return Math.log((Math.pow(Math.E, curve) - 1) * percent + 1) / curve;
	}
}
