package entities;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public class SuperBar extends Entity{

	private long triggerTime;
	private int coolDownTime = 6000;
	private int delayTime = 5000;
	private int total = -1;
	protected int current = 0;
	private double curve = 1.78;
	public double progress = 0;
	protected boolean triggered;

	public SuperBar(Level l) {
		super(l);
		init();
	}
	public SuperBar(Level l, int total) {
		super(l);
		init();
		this.total = total;
	}
	
	public void init(){
		color = Colors.get(-1, 543, -1, 0);
		state = 4;
	}

	public void tick(){
		if(state == 4){
			if(total > 0) state = 0;
		}else{
			if(current < 0) current = 0;
			
			progress = curve(curve, current/(double)total);
			
			switch(state){
			default:
			case 0:
				if(progress >= 1){
					trigger();
				}
				break;
			case 1:
				if(System.currentTimeMillis()-triggerTime >= delayTime || !level.player.superMode){
					state = 2;
				}
				break;
			case 2:
				if(System.currentTimeMillis()-triggerTime > level.player.superTime){
					state = 3;
					current = 0;
					triggered = false;
					level.player.superMode(false);
				}
				break;
			case 3:
				if(System.currentTimeMillis()-triggerTime >= level.player.superTime+coolDownTime){
					state = 0;
					current = 0;
				}
			}
			
			if(state > 0){
				while(current/(double)total > (level.player.superTime-(System.currentTimeMillis()-triggerTime))/(double)(level.player.superTime-delayTime)){
					current--;
				}
				progress = current/(double)total;
			}
		}
		
		tickCount++;
	}
	
	public void trigger(){
		state = 1;
		current = total;
		level.player.superMode(true);
		triggered = true;
		triggerTime = System.currentTimeMillis();
	}

	public void render(Screen screen){
		if(state == 3) color = Colors.get(-1, 543, -1, 0);
		else if(state == 1) color = Colors.get(-1, 543, (tickCount)%556, (tickCount>>2)%556);
		else color = Colors.get(-1, 543, 423, 0);
		screen.staticRender(0, 0, 22*32, color, 2, 0, 1);
		screen.staticRender(0, 8, 22*32, color, 0, 0, 1);
		if(state == 1) color = Colors.get(-1, 543, (tickCount)%556, (tickCount>>2)%556);
		else color = Colors.get(-1, 543, 423, 0);
		for(int dx = 8; dx < screen.width-8; dx+=8){
			screen.staticRender(dx, 0, 1 + 22*32, color, 2, 0, 1);
			screen.staticRender(dx, 8, 1 + 22*32, color, 0, 0, 1);
		}
		for(int dx = 8; dx < screen.width-8; dx++){
			if((current == 0 || (dx-8.)/(screen.width-16) > progress) && state != 1)
				break;
			screen.staticRender(dx, 0, 2 + 22*32, color, 2, 0, 1);
			screen.staticRender(dx, 8, 2 + 22*32, color, 0, 0, 1);
		}
		if(progress < 1) color = Colors.get(-1, 543, -1, 0);
		if(state == 1) color = Colors.get(-1, 543, (tickCount)%556, (tickCount>>2)%556);
		screen.staticRender(screen.width-8, 0, 22*32, color, 3, 0, 1);
		screen.staticRender(screen.width-8, 8, 22*32, color, 1, 0, 1);
	}
	
	public void inc(){if(state == 0) current++;}
	public void dec(){if(state == 0) current--;}
	public void inc(int x){if(state == 0) current += x;}
	public void dec(int x){if(state == 0) current -= x;}
	
	public void total(int dTotal){
		if(state == 4) state = 0;
		total += dTotal/3;
		//total = 5;
	}

}
