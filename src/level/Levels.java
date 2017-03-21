package level;

import entities.*;
import game.InputHandler;

public class Levels{
	
	public static final Settings[] settings = {
		new Settings("/Levels/Tutorial.png", 6*8, 13*8, 100, 386, 290, "Begin", 30, 1),//0
		new Settings("/Levels/Magic Level.png", 150*8, 350, 100, 150*8, 350, "Enter", 100, 2),//1
		new Settings("/Levels/Sand Level.png", 30, 30, 100, 64*8, 64*8, "Next", 125, 3),//2
		new Settings("/Levels/Water Level.png", 32*8, 32*8, 100, 39, 32*8, "Onward", 50, 4),//3
		new Settings("/Levels/Teleport Level.png", 60*8+4, 64*8+1, 100, 64*8, 49*8, "Reset", 30, 8),//4
		new Settings("/Levels/Chaim Level.png", 250*8+4, 12*8+1, 100, 60*8, 19*8, "Okay", 70, 0),//5
		new Settings("/Levels/Small Level.png", 50, 50, 100, 60*8, 19*8, "Okay", 30, 0),//6
		new Settings("/Levels/Magic Level.png", 150*8, 350, 100, 150*8, 350, "Enter", 75, -1),//7
		new Settings("/Levels/Sand Level.png", 30, 30, 100, 64*8, 64*8, "Next", 100, 9),//8
		new Settings("/Levels/Water Level.png", 32*8, 32*8, 100, 39, 32*8, "Onward", 50, 10),//9
		new Settings("/Levels/Teleport Level.png", 60*8+4, 64*8+1, 100, 64*8, 49*8, "Reset", 30, 7)//10
	};
	
	public static class Settings{
		public String ipath;
		public int next;
		public int resetX;
		public int resetY;
		public String boxMessage;
		public int playerX;
		public int playerY;
		public int playerHealth;
		public int startingDamage;
		
		public Settings(String iPath, int playerX, int playerY, int playerHealth, int resetX, int resetY, String boxMessage, int startingDamage, int next){
			this.ipath = iPath;
			this.playerX = playerX;
			this.playerY = playerY;
			this.playerHealth = playerHealth;
			this.resetX = resetX;
			this.resetY = resetY;
			this.boxMessage = boxMessage;
			this.startingDamage = startingDamage;
			this.next = next;
		}
	}

	public static Level get(int x, InputHandler input){
		x = x%settings.length;
		Level result = new Level(settings[x]);
		//result.addEntity(new TestSubject(result, 3, 3, 0));
		//result.addEntity(new Reticule(result, result.player, input));
		switch(x){
		default: break;
		case 0://tutorial
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Player(result, 56*8, 13*8, settings[x].startingDamage, null));
			break;
		case 1://magic
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Tank(result, 1160, 260, settings[x].startingDamage, null));
			break;
		case 2://sand
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Tank(result, 60, 60, settings[x].startingDamage, null));
			break;
		case 3://water
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Tank(result, 60, 60, settings[x].startingDamage, null));
			break;
		case 4://tp
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Tank(result, 0, 0, settings[x].startingDamage, null));
			result.addEntity(new Tank(result, 1500, 1000, settings[x].startingDamage, null));
			break;
		case 7://magic
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Eye(result, 0, 0, settings[x].startingDamage, null));
			result.addEntity(new Tank(result, 0, 0, settings[x].startingDamage, null));
			break;
		case 8://sand
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Eye(result, -600, -600, settings[x].startingDamage, null));
			break;
		case 9://water
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Eye(result, 0, 0, settings[x].startingDamage, null));
			break;
		case 10://tp
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Eye(result, 60, 60, settings[x].startingDamage, null));
			break;
		case 5://chaim
			result.addPlayer(new Player(result, settings[x].playerX, settings[x].playerY, settings[x].playerHealth, input));
			result.addEntity(new Eye(result, 0, 0, settings[x].startingDamage, null));
//			result.addEntity(new Eye(result, 0, 0, settings[x].startingDamage, null));
			break;
		}
		return result;
	}

}