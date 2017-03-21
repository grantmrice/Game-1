package level.tiles;

import graphics.Colors;
import graphics.Screen;
import level.Level;

public abstract class Tile {
	
	public static final Tile[] tiles = new Tile[256];
	public static final Tile VOID = new BasicSolidTile(0, 0, 0, 0, 0xFF000000);
	public static final Tile STONE = new BasicSolidTile(1, 1, 0, Colors.get(-1, 333, 221, -1), 0xFF555555);
	public static final Tile STONE2 = new BasicSolidTile(7, 3, 1, Colors.get(-1, 333, 221, -1), 0xFFAAAAAA);
	public static final Tile GRASS = new BasicTile(2, 3, 0, Colors.get(-1, 131, 141, -1), 0xFF00FF00);
	public static final Tile WATER = new AnimatedTile(3, new int[][] {{0,3},{1,3},{2,3}, {1,3}}, Colors.get(-1, 004, 115, -1), 0xFF0000FF, 500);
	public static final Tile MAGIC = new AnimatedTile(4, new int[][] {{1,5},{2,5},{3,5},{5,5},{6,5},{7,5},{6,5},{5,5},{3,5},{2,5},{1,5}}, Colors.get(512, 551, 315, 240), 0xFFAA42EA, 160);
	public static final Tile SAND = new AnimatedTile(5, new int[][] {{4,0},{5,0},{6,0},{7,0},{8,0},{9,0},{10,0},{11,0}}, Colors.get(-1, 543, 553, -1), 0xFFFFFF00, 200);
	public static final Tile TP = new BasicTile(6, 2, 0, Colors.get(333, 555, 0, 111), 0xFF00FFFF);
	public static final Tile LAVA = new AnimatedTile(8, new int[][] {{0,2},{1,2},{2,2},{3,2},{4,2},{5,2},{6,2},{7,2},{8,2}}, Colors.get(410,531,220, -1), 0xFFFF0000, 300);
	public static final Tile PUSH = new AnimatedTile(9, new int[][] {{0,4},{1,4},{2,4},{3,4},{4,4},{5,4},{6,4},{7,4}}, Colors.get(512, 551, 315, 240), 0xFFAA42EA, 6);

	protected byte Id;
	protected boolean solid;
	protected boolean emitter;
	private int levelColor;
	
	public Tile(int id, boolean isSolid, boolean isEmitter, int lColor){
		Id = (byte)id;
		if(tiles[Id] != null) throw new RuntimeException("Duplicate ID on " + Id);
		solid = isSolid;
		emitter = isEmitter;
		tiles[Id] = this;
		levelColor = lColor;
	}
	
	public int getLevelColor(){return levelColor;}
	public byte getId(){return Id;}
	public boolean solid(){return solid;}
	public boolean emitter(){return emitter;}
	
	public abstract void tick();
	public abstract void render(Screen screen, Level level, int x, int y);

}
