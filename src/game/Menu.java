package game;

import graphics.Font;
import graphics.Screen;

public class Menu{

	private Game game;
	private double hitRatio = .34;
	private boolean ready = false;
	public int bounces = 0, pattern = 1, color = 0;
	private InputHandler input;
	private int x, y, width, height, speed;
	private int tickCount = 0, screen = 0, style = 0;
	private int dx = 0, dy = 0, switchDelay = 10, switchTime;
	private boolean invert = false;

	public Menu(Game game, InputHandler input){
		this.game = game;
		this.input = input;
		width = Game.WIDTH;
		height = Game.HEIGHT;
		speed = (int)(3);
		x = width / 2;
		y = height / 2;
		while (dx == 0)
			dx = (int) (Math.random() * 100) %speed - 1;
		while (dy == 0)
			dy = (int) (Math.random() * 100) %speed - 1;
	}

	public void tick(){
		x += dx;
		y += dy;
		if(screen < 2 && input.mouse.pressed() 
		&& input.mouse.x >= Game.WIDTH/2 - (int)(128./Game.SCALE)
		&& input.mouse.y >= Game.HEIGHT - (int)(280./Game.SCALE)
		&& input.mouse.x <= Game.WIDTH/2 + (int)(127./Game.SCALE)
		&& input.mouse.y <= Game.HEIGHT - (int)(212./Game.SCALE)){
			Game.state = 0;
			game.resize(320);
			return;
		}
		if(input.pause.pressed){
			Game.state = 0;
			game.resize(320);
			return;
		}
		switch (screen){
		default:
		case 0:
			if(input.mouse.pressed() && tickCount - switchTime >= switchDelay){
				screen = 3;
				switchTime = tickCount;
				break;
			}
			if(x < 0){
				x = 0;
				dx = (int) (Math.random() * 100) %speed + 1;
				bounce();
			}
			if(x > width){
				x = width;
				dx = -((int) (Math.random() * 100) %speed + 1);
				bounce();
			}
			if(y < 0){
				y = 0;
				dy = (int) (Math.random() * 100) %speed + 1;
				bounce();
			}
			if(y > height){
				y = height;
				dy = -((int) (Math.random() * 100) %speed + 1);
				bounce();
			}
			break;
		case 1:
			if(input.mouse.pressed() && tickCount - switchTime >= switchDelay){
				screen = 3;
				switchTime = tickCount;
				break;
			}
			if(x < -width / 2){
				x = -width / 2;
				dx = (int) (Math.random() * 100) %speed + 1;
				bounce();
			}
			if(x > width * 3 / 2){
				x = width * 3 / 2;
				dx = -((int) (Math.random() * 100) %speed + 1);
				bounce();
			}
			if(y < -height / 2){
				y = -height / 2;
				dy = (int) (Math.random() * 100) %speed + 1;
				bounce();
			}
			if(y > height * 3 / 2){
				y = height * 3 / 2;
				dy = -((int) (Math.random() * 100) %speed + 1);
				bounce();
			}
			if(x >= 0 && x <= width && y > height - 5 && y < height && dy < 0 && Math.random() < hitRatio){
				y = height;
				dy = (int) (Math.random() * 100) %speed + 1;
				bounce();
			}
			if(x >= 0 && x <= width && y < 5 && y > 0 && dy > 0 && Math.random() < hitRatio){
				y = 0;
				dy = -((int) (Math.random() * 100) %speed + 1);
				bounce();
			}
			if(y >= 0 && y <= height && x > width - 5 && x < width && dx < 0 && Math.random() < hitRatio){
				x = width;
				dx = (int) (Math.random() * 100) %speed + 1;
				bounce();
			}
			if(y >= 0 && y <= height && x < 5 && x > 0 && dx > 0 && Math.random() < hitRatio){
				x = 0;
				dx = -((int) (Math.random() * 100) %speed + 1);
				bounce();
			}
			break;
		case 2:
			if(input.mouse.pressed()){
				screen = 4;
				switchTime = tickCount;
				break;
			}
			if(input.mouse.x >= 0 && input.mouse.x <= width && input.mouse.y >= 0 && input.mouse.y <= height){
				x = input.mouse.x;
				y = input.mouse.y;
			}else{
				while (dx == 0)
					dx = (int) (Math.random() * 100) %speed - 1;
				while (dy == 0)
					dy = (int) (Math.random() * 100) %speed - 1;
			}
			break;
		case 3:
			if(input.mouse.pressed() && tickCount - switchTime >= switchDelay){
				screen = input.mouse.timesPressed() %speed;
				switchTime = tickCount;
				break;
			}
			dx = input.mouse.x - x;
			if(dx > 5) dx = 5;
			if(dx < -5) dx = -5;
			dy = input.mouse.y - y;
			if(dy > 5) dy = 5;
			if(dy < -5) dy = -5;
			if(Math.abs(input.mouse.x - x) <= 5 && Math.abs(input.mouse.y - y) <= 5)
				screen = 2;
			break;
		case 4:
			ready = true;
			if(input.mouse.pressed() && input.mouse.x >= 0 && input.mouse.x <= width && input.mouse.y >= 0 && input.mouse.y <= height){
				x = input.mouse.x;
				y = input.mouse.y;
				if(tickCount - switchTime > switchDelay)
					pattern = (tickCount - switchTime) / 6;
			}else{
				while (dx == 0)
					dx = (int) (Math.random() * 100) %speed - 1;
				while (dy == 0)
					dy = (int) (Math.random() * 100) %speed - 1;
				screen = input.mouse.timesPressed() % 2;
				switchTime = tickCount;
				pattern = 1;
				bounce();
			}
			if(screen < 2){
				style = (int)(System.currentTimeMillis()%22);
				System.out.println("STYLE " + style%22);
			}
			break;
		}

		tickCount++;
	}

	public void render(Screen screen){
		for (int i = 0; i < screen.width; i++)
			for (int j = 0; j < screen.height; j++)
				switch(style%22){
				default:
					screen.staticSingle(i, j, ((int)(j-tickCount/pattern)) & (int)(216.*i/(screen.width/2) %216)); 
					if(i >= screen.width/2) screen.singleInvert(i, j);
				break; case 0:
					screen.staticSingle(i, j, ((int)(Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*(pattern)-tickCount)&color));
				break; case 1:
					screen.staticSingle(i, j,((int)(i+i/(j+1))/pattern - tickCount) & color); //donut fallse
				break; case 3:
					screen.staticSingle(i, j,((int)((i-j)*Math.tan((i+j+x+y))/pattern) - tickCount) & color); //touching waves
				break; case 4:
					screen.staticSingle(i, j,((int)((int)Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*Math.sin(i-j/Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))))*pattern - tickCount) & color); //the enigma
				break; case 5:
					screen.staticSingle(i, j,((int)((int)Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*Math.sin((x-y)/Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))))*pattern - tickCount) & color); //warp tunnel
				break; case 6:
					screen.staticSingle(i, j,((int)((int)Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*Math.sin(pattern/Math.sqrt((x-i)*(x-i)+(y-j)*(y-j)))) - tickCount) & color); //TUNNNEL SURPRISE
				break; case 7:
					screen.staticSingle(i, j,((int)(Math.sqrt((x-i)*(x-j)&(y-j)*(y-i))) * pattern - tickCount) & color); //serpinski square butterfly shit
				break; case 8:
					screen.staticSingle(i, j,((int)(Math.sqrt((x-i)*(x-i)&(y-j)*(y-j))) * pattern - tickCount) & color); // & star
				break; case 9:
					screen.staticSingle(i, j,((int)(Math.sqrt((x-i)*(x-i)*(y-j)*(y-j))) * pattern - tickCount) & color); //multiple asymtotes
				break; case 10:
					screen.staticSingle(i, j,((int)(Math.sqrt((x-i)*(x-i)&(y-j)*(y-i))) * pattern - tickCount) & color); //BLADE
				break; case 11:
					screen.staticSingle(i, j,((int)(((i)|(j))*Math.sin(Math.sqrt(i+j))) * pattern - tickCount) & color); //OCEAN WAVES
				break; case 12:
					screen.staticSingle(i, j,((int)(((i)|(j))*Math.sin(Math.sqrt(i|j))) * pattern - tickCount) & color); //SCALES
				break; case 13:
					screen.staticSingle(i, j,((int)(((x-i)|(y-j))*Math.sin(Math.sqrt(i|j))) * pattern - tickCount) & color); //SERPINSKI'S NIGHTMARE
				break; case 14:
					screen.staticSingle(i, j,(((int)((x-i)*(y-j)/((screen.width/2-(int)Math.abs(screen.width/2-i)+1)|(screen.height/2-Math.abs(screen.height/2-j)+1))) * pattern - tickCount) & color)); //mountain tunnel
				break; case 15:
					screen.staticSingle(i, j,(((int)((i^j)&(i*j)) * pattern - tickCount) & color)); //mix and match effects
				break; case 16:
					screen.staticSingle(i, j,(((int)(((x-i)*(y-j))%(Math.abs(x-i+y-j)+1)) * pattern - tickCount) & color)); //milky way
				break; case 17:
					screen.staticSingle(i, j,(((int)(((x-i)*(y-j))%(i+j+1)) * pattern - tickCount) & color)); //slantworld
				break; case 18:
					screen.staticSingle(i, j,(((int)((x-i)^(y-j)) * pattern - tickCount) & color)); //trippy tiling
				break; case 19:
					screen.staticSingle(i, j,((int) ((Math.pow(x-i, 2) + Math.pow(y-j, 2)) * pattern - tickCount) & color)); //nice cirle floor tiling
				break; case 20:
					 screen.staticSingle(i, j, ((int)(Math.sqrt(Math.pow(x&i, 2) +Math.pow(y&j, 2))*pattern-tickCount)&color)); //cubanism
				break; case 21:
					 screen.staticSingle(i, j, (int)(Math.sqrt(Math.pow(x-i, 2) +Math.pow(y-j, 2))*52500000)%216); //limit breaker
					 break;
				}
//********************Every color scheme, including negative brights*************/
//				{screen.staticSingle(i, j, ((int)(j-tickCount/pattern)) & (int)(216.*i/(screen.width/2) %216)); if(i >= screen.width/2) screen.singleInvert(i, j);}
//				screen.staticSingle(i, j,((int)(i+i/(j+1))/pattern - tickCount) & color); //donut fallse
//		screen.staticSingle(i, j,((int)((x-i)*(x+i)/(Math.abs(x-j)+1)/(Math.abs(y-j)+1))/pattern - tickCount) & color);
//		screen.staticSingle(i, j,((int)((i-j)*Math.tan((i+j+x+y))/pattern) - tickCount) & color); //touching waves
//		screen.staticSingle(i, j,((int)((i-j)*Math.tan((i+j+x+y)/pattern)) - tickCount) & color);
//		screen.staticSingle(i, j,((int)((int)Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*Math.sin(i-j/Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))))*pattern - tickCount) & color); //the enigma
//		screen.staticSingle(i, j,((int)((int)Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*Math.sin((x-y)/Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))))*pattern - tickCount) & color); //warp tunnel
//		screen.staticSingle(i, j,((int)((int)Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*Math.sin((i-j)/Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))))*pattern - tickCount) & color);
//		screen.staticSingle(i, j,((int)((int)Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*Math.sin(pattern/Math.sqrt((x-i)*(x-i)+(y-j)*(y-j)))) - tickCount) & color); //TUNNNEL SURPRISE
//		screen.staticSingle(i, j,((int)(Math.sqrt((x-i)*(x-j)&(y-j)*(y-i))) * pattern - tickCount) & color); //serpinski square butterfly shit
//		screen.staticSingle(i, j,((int)(Math.sqrt((x-i)*(x-i)&(y-j)*(y-j))) * pattern - tickCount) & color); // & star
//		screen.staticSingle(i, j,((int)(Math.sqrt((x-i)*(x-i)*(y-j)*(y-j))) * pattern - tickCount) & color); //multiple asymtotes
//		screen.staticSingle(i, j,((int)(Math.sqrt((x-i)^(x-i)*(y-j)*(y-i))) * pattern - tickCount) & color);
//		screen.staticSingle(i, j,((int)(Math.sqrt((x-i)*(x-i)&(y-j)*(y-i))) * pattern - tickCount) & color); //BLADE
//		screen.staticSingle(i, j,((int)(((i)|(j))*Math.sin(Math.sqrt(i+j))) * pattern - tickCount) & color); //OCEAN WAVES
//		screen.staticSingle(i, j,((int)(((i)|(j))*Math.sin(Math.sqrt(i|j))) * pattern - tickCount) & color); //SCALES
//		screen.staticSingle(i, j,((int)(((x-i)|(y-j))*Math.sin(Math.sqrt(i|j))) * pattern - tickCount) & color); //SERPINSKI'S NIGHTMARE
//		screen.staticSingle(i, j,((int)(((x-i)|(y-j))*Math.sin(Math.sqrt(i*j))) * pattern - tickCount) & color);
//		screen.staticSingle(i, j,((int)((i+j)&(i-j)) * pattern - tickCount) & color);
//		screen.staticSingle(i, j,(((int)((x-i)*(y-j)/((screen.width/2-(int)Math.abs(screen.width/2-i)+1)|(screen.height/2-Math.abs(screen.height/2-j)+1))) * pattern - tickCount) & color)); //mountain tunnel
//		screen.staticSingle(i, j,(((int)((x-i)*i/(double)(j*(y-j))) * pattern - tickCount) & color));
//		screen.staticSingle(i, j,(((int)((x-i)/(double)(y*j)) * pattern - tickCount) & color)); // just some pretty colors
//		screen.staticSingle(i, j,(((int)((i^j)&(i+j)) * pattern - tickCount) & color)); //less interesting scales
//		screen.staticSingle(i, j,(((int)((i^j)&(i*j)) * pattern - tickCount) & color)); //mix and match effects
//		screen.staticSingle(i, j,(((int)(((x-i)*(y-j))%(Math.abs(x-i+y-j)+1)) * pattern - tickCount) & color)); //milky way
//		screen.staticSingle(i, j,(((int)(((x-i)*(y-j))%(i+j+1)) * pattern - tickCount) & color)); //slantworld
//		screen.staticSingle(i, j,(((int)((x-i)^(y-j)) * pattern - tickCount) & color)); //trippy tiling
//		screen.staticSingle(i, j,(((int)((x-i)%(Math.abs(y-j)+1)) * pattern - tickCount) & color));
//		screen.staticSingle(i, j,(((i&(j)) * pattern - tickCount) & color));
//		screen.staticSingle(i, j,(((int)(i/(j+1)) * pattern - tickCount) & color));
//		screen.staticSingle(i, j,(((int)(x/(j+1)) * pattern - tickCount) & color));
//		screen.staticSingle(i, j,((int) (((int)Math.pow(x-i, 2) & (int)Math.pow(y-j, 2)) * pattern - tickCount) & color));
//		screen.staticSingle(i, j,((int) ((Math.pow(x-i, 2) * Math.pow(y-j, 2)) * pattern - tickCount) & color));
//		screen.staticSingle(i, j,((int) ((Math.pow(x-i, 2) + Math.pow(y-j, 2)) * pattern - tickCount) & color)); //nice cirle floor tiling
//		screen.staticSingle(i, j,((int) (Math.sqrt(Math.pow(x*i, 2) + Math.pow(y*j, 2)) * pattern - tickCount) & color));
//		 screen.staticSingle(i, j, ((int)(Math.sqrt(Math.pow(x&i, 2) +Math.pow(y&j, 2))*pattern-tickCount)&color)); //cubanism
//		 screen.staticSingle(i, j, ((int)(Math.sqrt(Math.pow(x-i, 2) *Math.pow(y-j, 2))*pattern-tickCount)&color));
		
//**************the real deal right here***********************/		
//		screen.staticSingle(i, j, ((int)(Math.sqrt((x-i)*(x-i)+(y-j)*(y-j))*pattern-tickCount)&color));
//		 screen.staticSingle(i, j, ((int)(Math.sqrt(Math.pow(x-i, 2) +Math.pow(y-j,2))*(1*Math.sin(System.currentTimeMillis()))-tickCount/100)&(color%216))); //Fucking piece of shit //don't
//		 screen.staticSingle(i, j, (int)(Math.sqrt(Math.pow(x-i, 2) +Math.pow(y-j, 2))*(tickCount/7))&bounces);
//		 screen.staticSingle(i, j, (int)(Math.sqrt(Math.pow(x-i, 2) +Math.pow(y-j, 2))*52500000)%216); //limit breaker
		Font.invertRender("GAME 1",
				screen, screen.width/2 - (int)Math.floor(432./Game.SCALE),
				screen.height/2 - (int)(192./Game.SCALE),
				(int)Math.ceil(18./Game.SCALE));
		if(ready){
			if(this.screen < 2){
				screen.staticFillInvert(
						screen.width/2 - (int)(128./Game.SCALE),
						screen.height - (int)(280./Game.SCALE),
						screen.width/2 + (int)(126./Game.SCALE),
						screen.height - (int)(212./Game.SCALE));
				Font.invertRender("Play",
						screen, screen.width/2 - (int)(128./Game.SCALE),
						screen.height - (int)(280./Game.SCALE),
						8/Game.SCALE);
			}
		}else{
			Font.invertRender("Click to take control", screen, (int)(120./Game.SCALE), (int)(screen.height*2./3), (int)(4./Game.SCALE - Game.WIDTH/1280));
		}
		
		if(invert) screen.invert();

	}

	private void bounce(){
		bounces++;
		color = (int) ((System.currentTimeMillis()) % 216);
		invert = System.nanoTime()%2 == 0;
		System.out.println("MENU " + color + (invert? "X":""));
	}

}
