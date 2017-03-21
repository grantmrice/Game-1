package entities;

import game.InputHandler;
import graphics.Colors;
import graphics.Font;
import graphics.Screen;
import level.Level;

public class JoseChesterfield extends Mob{

	public JoseChesterfield(Level level, int x, int y, int health, InputHandler input) {
		super(level, "'Murica Man", x, y, -2,3, -4, 7, health, Colors.get(-1, 422, 525, 452), 3, input);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tp() {
		x= (int)(Math.random()*level.width*8);
		y= (int)(Math.random()*level.height*8);
	}

	@Override
	public void superMode(boolean toggle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hit(Body b) {
		health--;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		control();
		
		if(input.space.pressed() && tickCount-lastShootTick >= 30){
			level.addEntity(new PlasmaBall(level, this, x, y, direction, Colors.get(-1, 143, 125, 111), 2));
			lastShootTick = tickCount;
		}
		
		tickCount++;
	}


	public void render(Screen screen) {
		int color = this.color;
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
		if(direction < 2){
			for(int xScreen = 0; xScreen < 3; xScreen++){
				int xTile = xScreen;
				for(int yScreen = 0; yScreen < 3; yScreen++){
					int yTile = yScreen;
					if(yFlip == 2) yTile = 2-yTile;
					x_offset = 0;
					y_offset = 0;
					if(swimming){
						screen.render(x-12+x_offset + 8*xScreen, y-12+y_offset + 8*yScreen, xTile+27 + (yTile+23)*32, waterColor, yFlip, 1);
					}else{
						if(superMode) color = Colors.get(-1, 222, (tickCount*3)%556, 555);
						screen.render(x-12+x_offset + 8*xScreen, y-12+y_offset + 8*yScreen, xTile+12 + (yTile+23)*32, color, yFlip, 1);
						screen.render(x-12+x_offset + 8*xScreen, y-12+y_offset + 8*yScreen, xTile+15+trackTileOffset + (yTile+23)*32, color, yFlip, 1);
					}
					if(!superMode) color = Colors.get(-1, 222, 333, numSteps%555);
					else color = Colors.get(-1, 222, (tickCount*3)%556, 0);
				}
			}
			for(int xScreen = 0; xScreen < 3; xScreen++){
				int xTile = xScreen;
				for(int yScreen = 0; yScreen < 3; yScreen++){
					int yTile = yScreen;
					if(yFlip == 2) yTile = 2-yTile;
					x_offset = 0;
					y_offset = 0;
					screen.prep.setSize(24, 24);
					screen.prep.render(8*xScreen, 8*yScreen, xTile+24 + (yTile+23)*32, color, yFlip, 1);
					screen.prep.rotate(12, 12, tickCount/10.);
					screen.prep.render(x-12, y-12);
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
				screen.render(x-11+x_offset+waterShift, y+1, 27*32, waterColor, 2, 1);
				screen.render(x-3+x_offset+waterShift, y+1, 2 + 27*32, waterColor, 2, 1);
				screen.render(x+5+x_offset+waterShift, y+1, 27*32, waterColor, 3, 1);
			}
			for(int xScreen = 0; xScreen < 3; xScreen++){
				if(!superMode) color = Colors.get(-1, 222, 333, numSteps%555);
				else color = Colors.get(-1, 222, (tickCount*3)%556, 0);
				int xTile = xScreen;
				if(xFlip == 1) xTile = 2-xTile;
				for(int yScreen = 0; yScreen < 2; yScreen++){
					if(swimming && yScreen > 0) break;
					int yTile = yScreen;
					screen.render(x-12+x_offset + 8*xScreen, y-8+y_offset + 8*yScreen, xTile + (yTile+25)*32, color, xFlip, 1);
				}
				if(!swimming){
					if(!superMode) color = Colors.get(-1, 222, 333, 555);
					else color = Colors.get(-1, 222, (tickCount*3)%556, 555);
					screen.render(x-12+x_offset + 8*xScreen, y+y_offset, xTile+wheelTileOffset + 24*32, color, xFlip, 1);
					screen.render(x-12+x_offset + 8*xScreen, y+y_offset, xTile+trackTileOffset + 23*32, color, xFlip, 1);
				}
			}
		}
		
		Font.render(Integer.toString(health), screen, x-Integer.toString(health).length()*4-1, y-12, Colors.get(-1, 0,0,0), 1);

	}

}
