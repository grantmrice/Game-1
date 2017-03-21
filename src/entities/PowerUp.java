package entities;

import graphics.Screen;
import level.Level;

public class PowerUp extends Body{

	public final int id;
	private int lastTick;
	
	public PowerUp(Level level, int x, int y, int id) {
		super(level, x, y, -6, 6, -6, 6, 0);
		this.id = id;
		init();
	}

	public void hit(Body b){
		
		
	}

	public void init() {
		state = 0;
		scale = 1;
		projectileTangible = false;
		damage = 0;
	}

	public void tick() {
		switch(state){
		default: break;
		case 0:
			if(bodyCollision(level.player) && tickCount > 200){
				lastTick = 0;
				state = 1;
				level.player.hit(this);
			}
			break;
		case 1:
			if(7-lastTick/10. <= 0) state = -1;
			break;
		}
		lastTick++;
		tickCount++;
	}

	public void render(Screen screen){
//		hitBox(screen);
		switch(state){
		default: break;
		case 0:
			for(double t = 0; t < Math.PI*2; t += Math.PI*2/3){
				screen.ring((int)Math.round(x+4*Math.cos(t+tickCount/10.)), (int)Math.round(y+4*Math.sin(t+tickCount/10.)), 2, 36+2*6+4, 5*36+4*6+4, 1);
			}
			screen.ring(x, y, 7, 4*36+4*6+4, 0, 0);
			break;
		case 1:
			for(double t = 0; t < Math.PI*2; t += Math.PI*2/3){
				screen.ring((int)Math.round(x+(4+lastTick)*Math.cos(t+tickCount/10.)), (int)Math.round(y+(4+lastTick)*Math.sin(t+tickCount/10.)), 2, 36+2*6+4, 5*36+4*6+4, 1);
			}
			screen.ring(x, y, (int)(7-lastTick/7.),  4*36+4*6+4, 0, 0);
			break;
		}
	}

}
