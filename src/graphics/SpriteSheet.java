package graphics;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	public String path;
	public int width;
	public int height;
	public int[] pixels;
	
	public SpriteSheet(String path) {
		BufferedImage image = null;
		try{
			image = ImageIO.read(SpriteSheet.class.getResource(path));
		}catch(Exception e){System.out.println("FUCKD UP WITH THE SPRITE SHEET");}
		
		if(image == null) return;
		
		this.path = path;
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
		
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = (pixels[i] & 0xff)/64; 
		}
	}

}
