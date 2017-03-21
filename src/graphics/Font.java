package graphics;

public class Font {
	
	private static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "0123456789";

	public static void render(String message, Screen screen, int x, int y, int color, int scale){
		message = message.toUpperCase();
		for(int i = 0; i < message.length(); i++){
			int chardex = chars.indexOf(message.charAt(i));
			if(chardex >= 0){
				if(chardex >= 26) screen.render(x + i*8*scale, y, chardex-26 + 31*32, color);
				else screen.render(x + i*8*scale, y, chardex + 30*32, color, 0, scale);
			}
		}
	}
	public static void render(int value, Screen screen, int x, int y, int color, int scale){
		render(Integer.toString(value), screen, x, y, color, scale);
	}

	public static void staticRender(String message, Screen screen, int x, int y, int color, int scale){
		x += screen.x_offset;
		y += screen.y_offset;
		render(message, screen, x, y, color, scale);
	}
	public static void staticRender(int value, Screen screen, int x, int y, int color, int scale){
		staticRender(Integer.toString(value), screen, x, y, color, scale);
	}
	
	public static void invertRender(String message, Screen screen, int x, int y, int scale){
		message = message.toUpperCase();
		for(int i = 0; i < message.length(); i++){
			int chardex = chars.indexOf(message.charAt(i));
			if(chardex >= 0){
				if(chardex >= 26) screen.invert(x + i*8*scale, y, chardex-26 + 31*32, 0 , 0,scale);
				else screen.invert(x + i*8*scale, y, chardex + 30*32, 0, 0, scale);
			}
		}
	}
}
