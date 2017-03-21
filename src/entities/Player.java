package entities;

import game.InputHandler;
import graphics.Colors;
import graphics.Font;
import graphics.Screen;
import level.Level;

public class Player extends Mob{

	private int maxHealth = 250;
	private int healthTickTime = 12;
	private int healthDelay = 0;
	private int shadow = -1;
	private boolean brighten = false;
	private boolean placed = false;
	private boolean visible = true;
	protected int shots, shotsTimer;

	public Player(Level level, int x, int y, int health, InputHandler input) {
		super(level, "Real Nigga #6", x, y, -3, 2, -4, 4, health, Colors.get(-1,000,221,113), 2, input);
		init();
	}
	
	public void init(){
		state = 0;
		scale = 1;
		shots = 1;
		shotsTimer = 0;
		superTime = 1000*13;
		shootTickTime = 10;
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
		int dx = 0;
		int dy = 0;
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


			standingOn();
			switch(standingOn){
			default: break;
			case 3:
				if(!superMode){
					swimming = true;
					if(tickCount%healthTickTime == 0)
						health--;
				}
				break;
			case 4:
				if(tickCount%(healthTickTime/2) == 0 
				&& health < maxHealth
				&& healthDelay < 0){
					health++;
				}
				break;
			case 6:
				if(!superMode) tp();
				break;
			case 8:
				if(!superMode && tickCount%4 == 0) health--;
				level.addEntity(new Flame(level, this, x-5, y, x-2, y+9, 36*5 + 6*3+ 1*0, 2, 5, false));
				level.addEntity(new Flame(level, this, x+3, y, x-2, y+9, 36*5 + 6*3+ 1*0, 2, 5, false));
				for(int i = 1; i < 8; i++)
					level.addEntity(new Flame(level, this, x-5+i, y+1, x-2, y+9, 36*5 + 6*3+ 1*0, 2, 5, false));
				break;
			}
			swimming = standingOn == 3 || standingOn == 8;

			if(superMode){
				swimming = false;
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
						shotsTimer--;
						switch(shots){
						default: break;
						case 1:
							level.addEntity(new PlasmaBall(level, this, x-1, y, direction, -1,2));
							break;
						case 3:
							int  plasmaDir = direction;
							switch(direction){
							default: break;
							case 0: plasmaDir = 7; break;
							case 1: plasmaDir = 6; break;
							case 2: plasmaDir = 4; break;
							case 3: plasmaDir = 5; break;
							}
							level.addEntity(new PlasmaBall(level, this, x-1, y, direction+4, -1,2));
							level.addEntity(new PlasmaBall(level, this, x-1, y, direction, -1,2));
							level.addEntity(new PlasmaBall(level, this, x-1, y, plasmaDir, -1,2));
							break;
						}
						lastShootTick = tickCount;
					}else{
						shoot();
					}
				}
			}
			break;
		case 1:
			direction = 1;
			if(shadow >= 0 && tickCount%2 == 0)
				if(brighten) shadow--;
				else shadow++;
			if(shadow == 15){
				visible = false;
				terrainTangible = false;
				projectileTangible = false;
				brighten = true;
			}
			if(shadow == -1 && !placed){
				brighten = false; 
				x = (int)(Math.random()*level.width*8);
				y = (int)(Math.random()*level.height*8);
				visible = true;
				terrainTangible = !superMode;
				projectileTangible = true;
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
		if(shotsTimer <= 0) shots = 1;
		if(!autoPilot) lastShootState = input.space.pressed;
		healthDelay--;
		tickCount++;
	}

	public void render(Screen screen) {
		if(visible){
			if(superMode) color = Colors.get(-1,(tickCount*3)%556,221,0);
			else if(!autoPilot) color = Colors.get(-1,000,221,numSteps%556); //multicolored shirt
			int xTile = 0;
			int yTile = 28;
			int walkingSpeed = 4;
			int flipTop = (numSteps >> walkingSpeed) & 1;
			int flipBottom = (numSteps >> walkingSpeed) & 1;
			
			if(superMode){
				flipTop = (tickCount >> 2) & 1;
				flipBottom = (tickCount >> 2) & 1;
			}else{
				if(direction == 0) xTile += 2;
				else if(direction > 1){
					xTile += 4 + 2*((numSteps >> walkingSpeed) & 1);
					flipTop = (direction-1)%2;
					flipBottom = (direction-1)%2;
				}
			}
			
			int modifier = 8*scale;
			int x_offset = x-modifier/2+4*scale;
			int y_offset = y-modifier/2-7*scale;

			if(state == 1){
				flipTop = 0;
				flipBottom = 0;
			}
			
			if(swimming){
				y_offset += 4;
				if(standingOn == 3){
					int waterColor = 0;
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
					screen.render(x_offset-8, y_offset+3, 0+27*32, waterColor, 0x00, 1);
					screen.render(x_offset, y_offset+3, 0+27*32, waterColor, 0x01, 1);
				}
			}
			screen.render(x_offset-modifier + modifier*flipTop, y_offset, 		   xTile + yTile*32 , color, flipTop, scale);
			screen.render(x_offset-modifier*flipTop,			y_offset, 		   (xTile+1) + yTile*32 , color, flipTop, scale);
			if(!swimming){
				screen.render(x_offset-modifier + modifier*flipTop, y_offset+modifier, xTile + (yTile+1)*32 , color, flipBottom, scale);
				screen.render(x_offset-modifier*flipTop, 			y_offset+modifier, (xTile+1) + (yTile+1)*32 , color, flipBottom, scale);
			}
			
			Font.render(Integer.toString(health), screen, x-Integer.toString(health).length()*4-1, y-20, Colors.get(-1, 0,0,0), 1);
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
						if(1+3*i < shadow) c1 = 0;
						if(2+3*i < shadow) c3 = 0;
					}
					int swimShift = 0;
					if(swimming) swimShift = 4;
					screen.render(x-8, y+swimShift-11, 8+2*i+28*32, Colors.get(-1,c1,c2,c3));
					screen.render(x, y+swimShift-11,8+2*i+1+28*32, Colors.get(-1,c1,c2,c3));
					screen.render(x-8, y+swimShift-3, 8+2*i+29*32, Colors.get(-1,c1,c2,c3));
					screen.render(x, y+swimShift-3, 8+2*i+1+29*32, Colors.get(-1,c1,c2,c3));
				}
				
			}
		}
		
		switch(shots){
		default: break;
		case 3:
			for(double t = 0; t < Math.PI*2; t += Math.PI*2/3){
				screen.staticRing((int)Math.round(10+4*Math.cos(t+tickCount/20.)), (int)Math.round(40+4*Math.sin(t+tickCount/20.)), 2, 36+2*6+4, 5*36+4*6+4, 1);
			}
			Font.staticRender(shotsTimer, screen, 20, 36, Colors.get(-1, 0, 0, 0), 1);
			break;
		}
//		center(screen);
//		hitBox(screen);
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
		if(b.isClass(Projectile.class) && ((Projectile)b).owner != this)
			healthDelay = 200;
		if(autoPilot){
		}else{
			level.superBar.dec(b.damage*3);
			if(superMode) superMode(false);
			autoShoot = false;
		}
		if(b.getClass() == PowerUp.class)
			switch(((PowerUp)b).id){
			default: break;
			case 0:
				shots = 3;
				shotsTimer += 50;
			}
	}
	
	public void tp(){
		shadow = 0;
		state = 1;
	}
	
	private void shoot(){
		if(tickCount - lastShootTick >= shootTickTime && tickCount > 0){
//			level.addEntity(new Missile(level, this, x, y, direction));
//			level.addEntity(new Explosion(level, this, x, y, 0));
//			if(tickCount > 0) return;
			shotsTimer--;
			switch(shots){
			default: break;
			case 1:
				level.addEntity(new PlasmaBall(level, this, x-1, y, direction, -1,2));
				break;
			case 3:
				int  plasmaDir = direction;
				switch(direction){
				default: break;
				case 0: plasmaDir = 7; break;
				case 1: plasmaDir = 6; break;
				case 2: plasmaDir = 4; break;
				case 3: plasmaDir = 5; break;
				}
				level.addEntity(new PlasmaBall(level, this, x-1, y, direction+4, -1,2));
				level.addEntity(new PlasmaBall(level, this, x-1, y, direction, -1,2));
				level.addEntity(new PlasmaBall(level, this, x-1, y, plasmaDir, -1,2));
				break;
			}
			lastShootTick = tickCount;
		}
	}
	

}
