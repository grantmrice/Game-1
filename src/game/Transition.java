package game;

import graphics.Font;
import graphics.Screen;
import level.Level;

public class Transition {
	public int state = -1;
	private int[] frame;
	private int x, y, color, speed = 8/Game.SCALE;
	private int tickCount = 0, pattern = 1, startTick = 0;
	private double trans = 0, radius, closeTime = 200, openTime = 150;
	private boolean invert = false;
	private Level level;
	private Screen screen;

	public void tick(){
		switch(state){
		default: return;
		case 0:
			trans = curve( 4.4, radius/closeTime*(tickCount-startTick)/radius);
			if(radius/closeTime*(tickCount-startTick) >= radius){
				state = 1;
				Game.breakout("T0");
			}
			break;
		case 1: 
			state = 2;
			Game.breakout("T1");
			break;
		case 2:
			double a = Math.atan2(level.player.y-screen.y_offset-y, level.player.x-screen.x_offset-x);
			x += (int)Math.round(speed*Math.cos(a));
			y += (int)Math.round(speed*Math.sin(a));
			double d = Math.sqrt(Math.pow(level.player.x-screen.x_offset-x,2)+Math.pow(level.player.y-screen.y_offset-y,2));
			speed = (d < 8/Game.SCALE)? (int)d : 8/Game.SCALE;
			if(d < 1){
				state = 3;
				Game.breakout("T2");
			}
			break;
		case 3: 
			state = 4;
			startTick = tickCount;
			Game.breakout("T3");
			break;
		case 4:
			trans = 1 - curve( -0.8, radius/openTime*(tickCount-startTick)/radius);
			if(tickCount%6 == 0) pattern++;
			if(trans <= 0){
				state = 5;
				Game.breakout("T4");
			}
			break;
		case 5: 
			state = -1;
			Game.breakout("T5");
			break;
		}
		tickCount++;
	}
	
	public void render(Screen screen){
		if(trans > 1) trans = 1;
		if(trans < 0) trans = 0;
		if(state != -1){
			for(int i = 0; i < screen.width; i++)
				for(int j = 0; j < screen.height; j++){
					double d = Math.sqrt(Math.pow(x-i, 2)+Math.pow(y-j, 2)), t = trans;
					int c1, c2, dir = 1;
					if(state == 1); dir = -1;
					
					c1 = (int)(d-radius+radius/closeTime*(tickCount-startTick));
					if(state == 4) c1 += radius; 
					if(c1 < 0){c1 = 0; t = 0;}
					else c1 = (int)(((int)(d*pattern+dir*(tickCount-startTick))&color)*t);
					if(invert) c1 = (int)(215*t-c1);
					
					c2 = (int)(frame[i + j*screen.width]*(1-t));
					screen.staticSingle(i, j, c1+c2);
				}
			String levelName = level.name;
			int x = screen.x_offset + screen.width/2 - (int)(levelName.length()*((26+levelName.length()%2)/2.)*Game.SCALE + levelName.length()%2*2);
			int y = screen.y_offset + screen.height/2 - 7*Game.SCALE;
			int scale = 4*Game.SCALE;
			while(levelName.length()*scale*8 >= screen.width) scale--;
			Font.invertRender(levelName, screen, x, y, scale);
		}
	}
	
	public void update(Level level, Screen screen){
		this.level = level;
		this.screen = screen;
		frame = screen.pixels.clone();
		System.out.println("FRAME");
	}
	public void start(){
		state = 0;
		pattern = 1;
		startTick = tickCount;
		x = level.player.x-screen.x_offset;
		y = level.player.y-screen.y_offset;
		if(x >= screen.width/2 && y >= screen.height/2) radius = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
		if(x >= screen.width/2 && y < screen.height/2) radius = Math.sqrt(Math.pow(x, 2)+Math.pow(screen.height-y, 2));
		if(x < screen.width/2 && y >= screen.height/2) radius = Math.sqrt(Math.pow(screen.width-x, 2)+Math.pow(y, 2));
		if(x < screen.width/2 && y < screen.height/2) radius = Math.sqrt(Math.pow(screen.width-x, 2)+Math.pow(screen.height-y, 2));
		radius += 40;
		
		color = (int)(System.currentTimeMillis()%216);
		System.out.print("TRANS " + color);
		if(System.nanoTime()/10%2 == 0){
			invert = true;
			System.out.println("X");
		}else{
			invert = false;
			System.out.println();
		}
	}
	
	private double curve(double curve, double percent){
		return Math.log((Math.pow(Math.E, curve) - 1) * percent + 1) / curve;
	}
}
