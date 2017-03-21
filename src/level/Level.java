package level;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import entities.Entity;
import entities.Mob;
import entities.EndBox;
import entities.SuperBar;
import graphics.Screen;
import level.tiles.Tile;

public class Level {

	private int resetX;
	private int resetY;
	public int next = 0;
	public String boxMessage, name = "";
	public boolean invert = false;
	public boolean complete = false;
	public int tickCount = 0;
	public int enemies = 0;
	public boolean nextLevel = false;
	public SuperBar superBar;
	public Mob player;
	public Entity target;
	private byte[] tiles;
	public int width;
	public int height;
	public List<Entity> entities = new ArrayList<Entity>();
	public List<Entity> newbies = new ArrayList<Entity>();
	private String imagePath = null;
	private BufferedImage image;
	
	public Level(Levels.Settings sets){
		resetX = sets.resetX;
		resetY = sets.resetY;
		boxMessage = sets.boxMessage;
		next = sets.next;
		String iPath = sets.ipath;
		if(iPath != null){
			imagePath = iPath;
			loadLevelFromFile();
			//name = iPath.substring(8, iPath.indexOf(" "));
		}else{
			width = 64;
			height = 64;
			tiles = new byte[width * height];
			generate_level();
		}
		superBar = new SuperBar(this);
	}
	
	private void loadLevelFromFile() {
		try{
			image = ImageIO.read(Level.class.getResource(imagePath));
			width = image.getWidth();
			height = image.getHeight();
			tiles = new byte[width*height];
			loadTiles();			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void loadTiles(){
		int[] tileColors = image.getRGB(0, 0, width, height, null, 0, width);
		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++){
				tileCheck: for(Tile t: Tile.tiles){
					if(t != null && t.getLevelColor() == tileColors[x + y*width]){
						tiles[x + y*width] = t.getId();
						break tileCheck;
					}
				}
			}
	}
	
	@SuppressWarnings("unused")
	private void saveLevel(){
		try{
			ImageIO.write(image, "png", new File(Level.class.getResource(imagePath).getFile()));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void alterTile(int x, int y, Tile newTile){
		tiles[x + y*width] = newTile.getId();
		image.setRGB(x,  y, newTile.getLevelColor());
	}

	public void generate_level(){
		
		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++){
				if(x * y%10 < 7)
					tiles[x+y*width] = Tile.GRASS.getId();
				else
					tiles[x+y*width] = Tile.STONE.getId();
			}
	}
	
	public void render_tiles(Screen screen, int x_offset, int y_offset){
		if(x_offset < 0) x_offset = 0;
		if(x_offset > ((width << 3)-screen.width)) x_offset = ((width << 3)-screen.width);
		if(y_offset < 0) y_offset = 0;
		if(y_offset > ((height << 3)-screen.height)) y_offset = ((height << 3)-screen.height);
		
		screen.setOffset(x_offset, y_offset);

		for(int y = y_offset >> 3; y < (y_offset + screen.height >> 3) + 1; y++)
			for(int x = x_offset >> 3; x < (x_offset + screen.width >> 3) + 1; x++){
				getTile(x, y).render(screen, this, x << 3, y << 3);
			}
		
	}
	public void renderEntities(Screen screen){
		for(Entity e: entities) e.render(screen);
		superBar.render(screen);
	}

	public void addEntity(Entity entity){
		if(entities.size() > 0)
			newbies.add(entity);
		else{
			entities.add(entity);
		}
	}

	public void addPlayer(Mob entity){
		addEntity(entity);
		player = entity;
		if(target == null) target = player;
	}
	public void targetPlayer(){
		target = player;
	}
	
	
	public void tick(){
		for(Entity e: entities) e.tick();
		for(Entity n: newbies) n.tick();
		if(enemies == 0 && !nextLevel){
			nextLevel = true;
			addEntity(new EndBox(this, boxMessage, resetX, resetY));
		}
		
		entities.addAll(newbies);
		newbies = new ArrayList<Entity>();
		
		for(int i = 0; i < entities.size(); i++)
			if(entities.get(i).state < 0){
				entities.remove(i);
				i--;
			}
		superBar.tick();
		
		tickCount++;
	}
	
	public Tile getTile(int x, int y){
		if(x < 0|| x >= width || y < 0 || y >= height) return Tile.VOID;
		return Tile.tiles[tiles[x+y*width]];
	}

}
