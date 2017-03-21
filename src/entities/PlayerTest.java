package entities;

import game.InputHandler;
import graphics.Colors;
import graphics.Screen;
import level.Level;

public class PlayerTest extends Mob{

	private int healthTickTime = 12;
	private int shootTickTime = 10;
	private boolean lastShootState = false;
	private int lastShootTick;
	private int shadow = -1;
	private boolean brighten = false;
	private boolean placed = false;
	private boolean visible = true;

	public PlayerTest(Level level, int x, int y, int health, InputHandler input) {
		super(level, "Real Nigga #6", x, y, -5, 2, -4, 4, health, Colors.get(-1,000,221,113), 2, input);
		init();
		this.health = health;
	}
	
	public void init(){
		state = 0;
		scale = 1;
		superTime = 1000*13;
		if(input == null){
			name  = "doodoo head";
			autoPilot = true;
			autoShoot = true;
			speed = 1;
			shootTickTime = 16;
			level.enemies++;
		}else{
			autoPilot = false;
			autoShoot = false;
		}
	}

	public void tick() {
		int dx = 0; int dy = 0;
		
		switch(state){
		default:
		case 0:
			if(autoPilot){
				if(health <= 0){
					state = -1;
					level.enemies--;
				}
				int px = level.player.x;
				int py = level.player.y;

				if(x-px < 3 && x-px > -6){
					if(y > py){
						direction = 0;
						shoot();
					}else{
						direction = 1;
						shoot();
					}
				}else if(y-py < 5 && y-py > -2){
					if(x > px){
						direction = 2;
						shoot();
					}else{
						direction = 3;
						shoot();
					}
				}
				if(Math.sqrt(Math.pow(x-px, 2) + Math.pow(y-py, 2)) > 40){
					if(x > px) dx--;
					else if(x < px) dx++;
					if(y > py) dy--;
					else if(y < py) dy++;
				}
				
			}else{
				if(health <= 0) level.complete = true;
				control();
			}
			
			if(dx != 0 || dy != 0){
				move(dx, dy);
				isMoving = true;
			}else{isMoving = false;}


			if(level.getTile(x >> 3, y >> 3).getId() == 3 && !superMode){
				swimming = true;
				if(tickCount%healthTickTime == 0){
					health--;
				}
			}
			if(swimming && level.getTile(x >> 3, y >> 3).getId() != 3)
				swimming = false;
			if(level.getTile(x >> 3, y >> 3).getId() == 4){
				if(tickCount%(healthTickTime/2) == 0){
					health++;
				}
			}

			if(superMode){
				if(autoShoot){
					if(input.space.pressed){
						level.addEntity(new SuperPlasmaBall(level, this, x-1, y));
						autoShoot = false;
					}switch(0){
					default:
					case 0:
							if(tickCount%(shootTickTime) == 0){
								level.addEntity(new PlasmaBall(level, this, x-13, y-15, 3, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
								level.addEntity(new PlasmaBall(level, this, x+11, y+8, 2, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
								level.addEntity(new PlasmaBall(level, this, x+11, y-15, 1, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
								level.addEntity(new PlasmaBall(level, this, x-13, y+8, 0, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
								level.addEntity(new PlasmaBall(level, this, x+11, y-15, 2, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
								level.addEntity(new PlasmaBall(level, this, x-13, y+8, 3, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
								level.addEntity(new PlasmaBall(level, this, x-13, y-15, 1, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
								level.addEntity(new PlasmaBall(level, this, x+11, y+8, 0, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
							}
					case 1:
						if(tickCount%(shootTickTime/2) == 0){
							level.addEntity(new PlasmaBall(level, this, x-1, y-15, 0, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
							level.addEntity(new PlasmaBall(level, this, x-1, y+6, 1, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
							level.addEntity(new PlasmaBall(level, this, x-13, y-3, 2, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
							level.addEntity(new PlasmaBall(level, this, x+11, y-3, 3, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
						}
					}

				}else{
					if(input.space.pressed){
						if(!lastShootState){
							level.addEntity(new PlasmaBall(level, this, x-1, y, direction, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
							lastShootTick = tickCount;
						}else{
							if(tickCount - lastShootTick >= shootTickTime){
								level.addEntity(new PlasmaBall(level, this, x-1, y, direction, Colors.get(-1, 124, numSteps%556, (tickCount*3)%556), 3));
								lastShootTick = tickCount;
							}
						}

					}
				}
			}else if(!autoShoot){
				if(input.space.pressed){
					if(!lastShootState){
						shadow = 0;
						state = 1;
					}else{
					}
				}
			}

			
			break;
		case 1:
			if(shadow >= 0 && tickCount%2 == 0)
				if(brighten) shadow--;
				else shadow++;
			if(shadow == 15){
				visible = false;
				brighten = true;
			}
			if(shadow == -1 && !placed){
				brighten = false; 
				x = (int)(Math.random()*level.width*8);
				y = (int)(Math.random()*level.height*8);
				visible = true;
				shadow = 0;
				placed = true;
			}else if(placed){
				placed = false;
				state = 0;
			}

			if(input.space.pressed){
				if(!lastShootState){
					shadow = 0;
					state = 1;
				}
			}
		}
		if(!autoPilot) lastShootState = input.space.pressed;
		tickCount++;
	}

	public void render(Screen screen) {
		if(visible){
			screen.render(x, y, 0+28*32, color);
			screen.render(x+8, y, 1+28*32, color);
			screen.render(x, y+8, 0+29*32, color);
			screen.render(x+8, y+8, 1+29*32, color);
		}
		if(state == 1){
			int c1,c2,c3;
			if(shadow >=0){
				for(int i = 0; i < 5; i++){
					if(brighten){
						c1=0; c2=0; c3=0;
						if(0+3*i == 15-shadow) c2 = 555;
						if(1+3*i == 15-shadow) c1 = 555;
						if(2+3*i == 15-shadow) c3 = 555;
						if(0+3*i < 15-shadow) c2 = -1;
						if(1+3*i < 15-shadow) c1 = -1;
						if(2+3*i < 15-shadow) c3 = -1;
					}else{
						c1=-1; c2=-1; c3=-1;
						if(0+3*i == shadow) c2 = 555;
						if(1+3*i == shadow) c1 = 555;
						if(2+3*i == shadow) c3 = 555;
						if(0+3*i < shadow) c2 = 0;
						if(1+3*i < shadow) c1 = 420;
						if(2+3*i < shadow) c3 = 0;
					}
					screen.render(x, y, 8+2*i+28*32, Colors.get(-1,c1,c2,c3));
					screen.render(x+8, y,8+2*i+1+28*32, Colors.get(-1,c1,c2,c3));
					screen.render(x, y+8, 8+2*i+29*32, Colors.get(-1,c1,c2,c3));
					screen.render(x+8, y+8, 8+2*i+1+29*32, Colors.get(-1,c1,c2,c3));
				}
				
			}
		}
	}

	public void superMode(boolean t){
		superMode = t;
		terrainTangible = !t;
		autoShoot = t;
		if(t){
			shootTickTime = 6;
		}else{
			direction = 1;
			shootTickTime = 10;
		}
	}
	
	public void hit(Body b){
		health -= b.damage;
		if(autoPilot){
			
		}else{
			level.superBar.dec(b.damage*3);
			if(superMode) superMode(false);
			autoShoot = false;
		}
	}
	
	private void shoot(){
		if(tickCount - lastShootTick >= shootTickTime && tickCount > 0){
			level.addEntity(new PlasmaBall(level, this, x-1, y, direction, -1,2));
			lastShootTick = tickCount;
		}
	}

	@Override
	public void tp() {
		// TODO Auto-generated method stub
		
	}
	

}
