package entities;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public class TestSubject extends Entity{
	
	double theta;

	public TestSubject(Level level, int x, int y, int color) {
		super(level, x, y, color);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick() {
		theta = tickCount/100.;
		x = level.player.x;
		y = level.player.y;
		// TODO Auto-generated method stub
		tickCount++;
	}

	@Override
	public void render(Screen screen){
		screen.prep.setSize(16, 16);
		screen.prep.render(4,4, 3, Colors.get(-1, 122, 523, 142));
		//screen.prep.render(4,4, 3 + 27*32, Colors.get(-1, 122, 235, 102));
		screen.prep.rotate(8, 8, theta);
		screen.prep.render(x, y);
	}
	
}
