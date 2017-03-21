package entities;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public class OrbitOrbs extends Particle{
	
	protected boolean reverse;
	protected int width;
	protected int height;
	protected int speed;
	private int x_offset;
	private int y_offset;

	public OrbitOrbs(Level l){super(l);}
	public OrbitOrbs(Level l, Entity owner, int x, int y, int width, int height, int x_offset, int y_offset, int color, boolean reverse, int speed) {
		super(l, owner, x, y-height/2, color);
		this.width = width;
		this.height = height;
		this.reverse = reverse;
		this.speed = speed;
		this.x_offset = x_offset;
		this.y_offset = y_offset;
		init();
	}
	
	public void init(){
		x = 0;
	}

	public void tick(){
		switch(state){
		case 0:
			y = -height/2;
			move(-1, 0);
			if(x <= -width/2) state++;
			break;
		case 1:
			x = -width/2;
			move(0, 1);
			if(y >= height/2) state++;
			break;
		case 2:
			y = height/2;
			move(1, 0);
			if(x >= width/2) state++;
			break;
		case 3:
			x = width/2;
			move(0, -1);
			if(y <= -height/2) state = 0;
			break;
		}
		
		tickCount++;
	}

	public void render(Screen screen){
		int xScreen = x;
		if(reverse){xScreen *= -1;}
		screen.render(xScreen+owner.x+x_offset-4, y+owner.y+y_offset-4, 7 + 19*32, Colors.get(-1, 352, 132, 425), 2*((tickCount>>4)&1), 1);
	}
	
	protected void move(int dx, int dy){
		if(speed > 0){
			x += dx*speed;
			y += dy*speed;
		}else if(tickCount%(-speed) == 0){
			x++;
			y++;
		}
	}

}
