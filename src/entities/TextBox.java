package entities;

import graphics.Font;
import graphics.Screen;
import level.Level;

public class TextBox extends Body{
	
	int orbs = 0;
	int lastOrbTick = 0;
	int orbDelay;
	String text;
	
	public TextBox(Level l){super(l);}
	public TextBox(Level l, String message, int x, int y, int color) {
		super(l, x, y, 0,message.length()*8, -8, 8, color);
		init();
		this.text = message;
		int shift;
		for(shift = 1; shift <= text.length()*4/3 +3; shift <<= 1);
		shift /= 2;
		//keeps everything working up to 32 characters
		if(((text.length()*4/3+3)) % shift  == 0) shift /=2;
		else shift = (text.length()*4/3+3)&shift;
		
		this.orbDelay = (2*(text.length()*8+8) +32) / shift;
	}
	
	public void init(){}
	public void hit(Body b){}
//	(2*(text.length()*8+8) +32)
	public void tick(){
		if(orbs < 10 && tickCount >= lastOrbTick+orbDelay){
			if(tickCount%(4*orbDelay) == 0){
				level.addEntity(new OrbitOrbs(level, this, x, y, text.length()*8+8, 17, 20, 0, color, true,1));
				orbs++;
			}
			level.addEntity(new OrbitOrbs(level, this, x, y, text.length()*8+8, 17, 20, 0, color, false, 1));
			orbs++;
			lastOrbTick = tickCount;
		}
		
		tickCount++;
	}

	public void render(Screen screen) {
		String message = "";
		char[] chars = text.toCharArray();
		int d = (int)(Math.sqrt(Math.pow(x-level.player.x, 2) + Math.pow(y-level.player.y, 2))/(screen.width/2));
		if(d >= 4) message = text.substring(0,1);
		else for(int i = 0; i < text.length()-d; i++)
			message += chars[i];
		int width = message.length()*8;
		int xScreen = x + 4*(5-width/8);
		int yScreen = y-4;
		if(xScreen < screen.x_offset) xScreen = screen.x_offset;
		if(yScreen < screen.y_offset+16) yScreen = screen.y_offset+16;
		if(xScreen+width >= screen.x_offset+screen.width) xScreen = screen.x_offset+screen.width-width;
		if(yScreen+8 >= screen.y_offset+screen.height) yScreen = screen.y_offset+screen.height-8;
		Font.render(message, screen, xScreen, yScreen, color, 1);
	}

}
