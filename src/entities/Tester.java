package entities;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public class Tester extends Entity{

	public Tester(Level l, int x, int y) {
		super(l, x, y, Colors.get(0, -1, -1, 555));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	public void render(Screen screen) {
		screen.render(x, y, 0, color);
	}

}
