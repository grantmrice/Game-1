package game;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class InputHandler implements KeyListener,MouseListener{
	
	Game game;
	
	public InputHandler(Game game){
		this.game = game;
		game.addKeyListener(this);
		game.addMouseListener(this);
	}

	public class Key{
		private int timesPressed = 0;
		public boolean pressed = false;
		public void toggle(boolean isPressed){
			pressed = isPressed;
			if(pressed) timesPressed++;
		}
		public boolean pressed(){return pressed;}
		public int timesPressed(){return timesPressed;}
	}
	public class Mouse{
		public int x, y;
		private int timesPressed = 0;
		private boolean pressed = false, inWindow = false;
		public void toggle(boolean isPressed){
			pressed = isPressed;
			if(pressed) timesPressed++;
		}
		public boolean pressed(){return pressed;}
		public boolean inWindow(){return inWindow;}
		public int timesPressed(){return timesPressed;}
	}

	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key rite = new Key();
	public Key space = new Key();
	public Key lvlSkip = new Key();
	public Key superMode = new Key();
	public Key reset = new Key();
	public Key kick = new Key();
	public Key pause = new Key();
	public Key tp = new Key();
	public Key asdf = new Key();
	public Mouse mouse = new Mouse();


	
	//public List<Key> keys = new ArrayList<Key>();

	public void keyPressed(KeyEvent e) {
		toggle(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		toggle(e.getKeyCode(), false);
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public void toggle(int keyCode, boolean isPressed){
		if(keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP){up.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_S|| keyCode == KeyEvent.VK_DOWN){down.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_A|| keyCode == KeyEvent.VK_LEFT){left.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_D|| keyCode == KeyEvent.VK_RIGHT){rite.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_ESCAPE){pause.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_SPACE){space.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_L){lvlSkip.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_M){superMode.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_R){reset.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_K){kick.toggle(isPressed);System.out.println(kick.timesPressed);}
		if(keyCode == KeyEvent.VK_T){tp.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_F){asdf.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_BACK_SPACE){game.stop();}
	}

	public void update(){
		mouse.x = MouseInfo.getPointerInfo().getLocation().x;
		mouse.y = MouseInfo.getPointerInfo().getLocation().y;
		mouse.x-=game.frame.getX()+3;
		mouse.y-=game.frame.getY()+32;
		mouse.x = (int)Math.round(mouse.x*1280./1293/Game.SCALE);
		mouse.y = (int)Math.round(mouse.y*960./973/Game.SCALE);
	}
	
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		mouse.inWindow = true;
	}

	public void mouseExited(MouseEvent e) {
		mouse.inWindow = false;
	}

	public void mousePressed(MouseEvent e) {
		mouse.toggle(true);
		mouse.x = (int)((e.getX()-(e.getX()*(973./960-1)))/Game.SCALE);
		mouse.y = (int)((e.getY()-(e.getY()*(734./720-1)))/Game.SCALE);
	}

	public void mouseReleased(MouseEvent e) {
		mouse.toggle(false);
		
	}
	
}
