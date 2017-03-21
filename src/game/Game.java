package game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import entities.Entity;
import entities.Mob;
import graphics.Colors;
import graphics.Font;
import graphics.Screen;
import level.Level;
import level.Levels;
import level.tiles.Tile;

public class Game extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int WIDTH = 640;
	public static int HEIGHT = (int)(WIDTH*9./12);
	public static int SCALE = 1280/WIDTH;
	public static final String NAME  = "Game 1";
	public static Dimension DIMENSIONS = new Dimension(WIDTH*SCALE, HEIGHT*SCALE);
	
	protected JFrame frame;
	
	public boolean running = false;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels =((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	private int[] colors = new int[6*6*6];
	
	private Screen screen;
	public InputHandler input;
	
	public Transition trans;
	public Menu menu;
	public Level level;
	public Mob player;
	public Entity target;
	
	public static int state = 1;
	private static boolean breakout = false;
	public static void breakout(){System.out.println("BROKEN");breakout = true;}
	public static void breakout(String x){System.out.println("BROKEN "+ x);breakout = true;}
	private int lvl = 5;
	private boolean pause;
	protected boolean[] cheats = new boolean[6];
	
	public Game(){
		setMinimumSize(DIMENSIONS);
		setMaximumSize(DIMENSIONS);
		setPreferredSize(DIMENSIONS);
		
		frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		trans = new Transition();
		for(int i = 0; i < cheats.length; i++)
			cheats[i] = false;
	}
	
	public synchronized void start(){
		running = true;
		new Thread(this).start();
	}
	
	public synchronized void stop(){
		running = false;
		frame.dispose();
	}
	
	public void run(){
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000./60;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double elapsed = 0;
		
		init();

		System.out.println("Starting level " + lvl);
		
		while(running){
			long now = System.nanoTime();
			elapsed += (now-lastTime)/nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			breakout = false;
			while(elapsed >= .5){
				ticks++;
				tick();
				elapsed--;
				shouldRender = true;
				if(breakout) break;
			}
			switch(state){
			default: break;
			case 0:
				if(level.complete){
					state = 2;
					trans.update(level, screen);
					trans.start();
					Performance.report();
				}
				break;
			case 2:
				//System.out.println(trans.state);
				if(trans.state == 1){
					if(level.nextLevel) lvl = level.next;
					if(lvl < 0){
						lvl = 0;
						for(int i = 0; i < Levels.settings.length; i++){
							int shift;
							for(shift = 1; shift <= Levels.settings[i].startingDamage; shift <<= 1);
							shift /= 3;
							Levels.settings[i].startingDamage += shift;
						}
					}
					run();
					return;
				}
				if(trans.state == 3){
					level.render_tiles(screen, screen.x_offset, screen.y_offset);
					level.renderEntities(screen);
					trans.update(level, screen);
				}
				if(trans.state == 5) state = 0;
				break;
			case 3:
				break;
			}
			try{
				Thread.sleep(2);
			}catch(Exception e){
				System.out.print("SHOULDN:T HAVE VAILED");
			}
			if(shouldRender){
				frames++;
				render();
			}
			
			if(System.currentTimeMillis() - lastTimer >= 500){
				lastTimer += 500;
				System.out.println(2*frames +" "+ 2*ticks +" "+ level.entities.size());
				Performance.record(frames, ticks);
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick(){
		player.input.update();
		switch(state){
		default: return;
		case 0:
			if(!pause) level.tick();
			for(Tile t: Tile.tiles){
				if(t == null){ break;
				}else t.tick();
			}
			break;
		case 1: menu.tick(); break;
		case 2:
		case 3: trans.tick(); break;
		} 
		cheats();
		//can't be rendering anything else on top of it
		//for(int i = 0; i < pixels.length; i++) pixels[i] = (i+count) << 13;
		
	}
	
	public void render(){
		BufferStrategy strategy = getBufferStrategy();
		if(strategy == null){
			createBufferStrategy(2);
			return;
		}
		
		int x_offset = level.target.x - (screen.width/2);
		int y_offset = level.target.y - screen.height/2;
		
		if(state != 1){
			level.render_tiles(screen, x_offset, y_offset);
			level.renderEntities(screen);
		}
		
		switch(state){
		default: break;
		case 0:
			
			if(pause)
				Font.staticRender("Pause", screen, screen.width/2-70, screen.height/2, Colors.get(-1, -1, -1, 555), 4);
			break;
		case 1: menu.render(screen); break;
		case 2:
		case 3: trans.render(screen); break;
		}
		
		if(level.invert) screen.invert();
		
		for(int y = 0; y < screen.height; y++)
			for(int x = 0; x < screen.width; x++){
				int colorCode = screen.pixels[x + y*screen.width];
				if(colorCode < 216) pixels[x + y*screen.width] = colors[colorCode];
			}
		
		Graphics g = strategy.getDrawGraphics();
		g.setColor(Color.ORANGE);
		g.drawRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0 , getWidth(), getHeight(), null);
		g.dispose();
		strategy.show();
		
	}
	
	public void init(){
		int index = 0;
		for(int r = 0; r < 6; r++)
			for(int g = 0; g < 6; g++)
				for(int b = 0; b < 6; b++){
					int rr = r*255/5;
					int gg = g*255/5;
					int bb = b*255/5;
					colors[index++] = rr << 16 | gg << 8 | bb;
				}
		screen = new Screen(WIDTH, HEIGHT);
		input = new InputHandler(this);
		level = Levels.get(lvl, input);
		menu = new Menu(this, input);
		player = level.player;
		target = level.target;
		if(state != 1) screen.setOffset(player.x - (screen.width/2), player.y - (screen.height/2));
		trans.update(level, screen);
		Performance.init();
		System.out.println("INIT");
	}
	
	public static void main(String args[]){
		new Game().start();
	}
	
	private void cheats(){
		if(!cheats[0] && input.lvlSkip.pressed){
			level.nextLevel = true;
			level.complete = true;
		}
		if(!cheats[1] && input.superMode.pressed){
			level.player.superMode(!level.player.superMode);
		}
		if(!cheats[2] && input.reset.pressed){
			level.nextLevel = false;
			level.complete = true;
		}
		if(!cheats[3] && input.kick.pressed){
			level.player.x += (int)(Math.random()*10-5);
			level.player.y += (int)(Math.random()*10-5);
		}
		if(!cheats[4] && input.pause.pressed){
			pause = !pause;
		}
		if(!cheats[5] && input.tp.pressed){
			level.player.tp();
		}
		cheats[0] = input.lvlSkip.pressed;
		cheats[1] = input.superMode.pressed;
		cheats[2] = input.reset.pressed;
		cheats[3] = input.kick.pressed;
		cheats[4] = input.pause.pressed;
		cheats[5] = input.tp.pressed;
	}
	
	public void resize(int width){
		WIDTH = width;
		HEIGHT = (int)(WIDTH*9./12);
		SCALE = 1280/WIDTH;
		DIMENSIONS = new Dimension(WIDTH*SCALE, HEIGHT*SCALE);
		screen = new Screen(WIDTH, HEIGHT);
		trans.update(level, screen);
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels =((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		setMinimumSize(DIMENSIONS);
		setMaximumSize(DIMENSIONS);
		setPreferredSize(DIMENSIONS);
	}
}
