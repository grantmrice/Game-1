package entities;

import game.InputHandler;
import graphics.Colors;
import graphics.Screen;
import level.Level;

public class DaftPunk extends Mob{
	private int shadow;

	public DaftPunk(Level level, String name, int x, int y, int xMin, int xMax, int yMin, int yMax, int health,
			int color, int speed, InputHandler input) {
		super(level, name, x, y, xMin, xMax, yMin, yMax, health, color, speed, input);
		// TODO Auto-generated constructor stub
	}

public void render(Screen screen) {
	screen.render(x, y, 0+28*32, color);
	screen.render(x+8, y, 1+28*32, color);
	screen.render(x, y+8, 0+29*32, color);
	screen.render(x+8, y+8, 1+29*32, color);
//	int a = shadow/3;
//	int xTile = 8+2*a;

	int c1=-1, c2=-1, c3=-1;

	//== for wipe effect
	System.out.println(shadow);
	if(shadow >=0){
		for(int i = 0; i < 5; i++){
			c1=-1; c2=-1; c3=-1;
			if(0+3*i <= shadow) c2 = 555;
			if(1+3*i <= shadow) c1 = 555;
			if(2+3*i <= shadow) c3 = 555;
			screen.render(x, y, 8+2*i+28*32, Colors.get(-1,c1,c2,c3));
			screen.render(x+8, y,8+2*i+1+28*32, Colors.get(-1,c1,c2,c3));
			screen.render(x, y+8, 8+2*i+29*32, Colors.get(-1,c1,c2,c3));
			screen.render(x+8, y+8, 8+2*i+1+29*32, Colors.get(-1,c1,c2,c3));
		}
	}
	
}

@Override
public void tick() {
	// TODO Auto-generated method stub
	
}

@Override
public void superMode(boolean toggle) {
	// TODO Auto-generated method stub
	
}

@Override
public void hit(Body b) {
	// TODO Auto-generated method stub
	
}

@Override
public void init() {
	// TODO Auto-generated method stub
	
}

@Override
public void tp() {
	// TODO Auto-generated method stub
	
}

}
