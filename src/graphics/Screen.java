package graphics;

public class Screen {
	
	public static final int MAP_WIDTH = 64;
	public static final int MAP_WIDTH_MASK = MAP_WIDTH-1;
	
	public static final byte BIT_MIRROR_X = 0x01;
	public static final byte BIT_MIRROR_Y = 0x02;
	 
	public int[] pixels;
	
	public int x_offset = 0;
	public int y_offset = 0;
	public int width;
	public int height;
	public SpriteSheet[] sheet;
	public Prep prep;
	
	public Screen(){}
	public Screen(int w, int h){
		width = w;
		height = h;
		sheet = new SpriteSheet[2];
		sheet[0] = new SpriteSheet("/SpriteSheet.png");
		sheet[1] = new SpriteSheet("/Mad-Eye Moody.png");
		pixels = new int[width*height];
		prep = new Prep(this);
	}
	
	public void setOffset(int x, int y){
		x_offset = x;
		y_offset = y;
	}
	public int getPixelColor(int x, int y){
		return (x+width*y >= pixels.length || x+width*y < 0)? -1: pixels[x + width*y];
	}
	public int getSheetColor(int s, int x, int y, int color){
		if(x < 0 || y < 0 || x >= sheet[s].width || y >= sheet[s].height) return 216;
		return (color >> (sheet[s].pixels[x + y*sheet[s].width]*8)) & 255;
	}
	
	public void invert(){
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				pixels[x + y*width] = 215-pixels[x+ y*width];
	}
	public class Prep extends Screen{
		
		private Screen screen;
		
		private Prep(Screen screen){
			this.screen = screen;
			this.sheet = screen.sheet;
		}
		
		public void setSize(int width, int height){
			this.width = width;
			this.height = height;
			pixels = new int[width*height];
			for(int x = 0; x < pixels.length; x++) pixels[x] = -1;
		}
		
		public void center(){
			pixels[width/2 + height/2*width] = 215;
		}
		public void borders(){
			for(int x = 0; x < width; x++){
				pixels[x] = 215;
				pixels[x + (height-1)*width] = 215;
			}
			for(int y = 0; y < height; y++){
				pixels[y*width] = 215;
				pixels[width-1 + y*width] = 215;
			}
			
		}
		
		public void render(int xPos, int yPos){
			for(int x = 0; x < width; x++)
				for(int y = 0; y < height; y++)
					screen.single(x+xPos, y+yPos, pixels[x + y*width]);
		}
		
		public void rotate(int xCenter, int yCenter, double shift){
			int[] result = pixels.clone();//new int[width*height];
			for(int x = 0; x < result.length; x++) result[x] = -1;
			for(int x = 0; x < width; x++)
				for(int y = 0; y < height; y++){
					double d = Math.sqrt(Math.pow(x-xCenter, 2)+Math.pow(y-yCenter, 2));
					double theta = Math.atan2(y-yCenter, x-xCenter);
					int xShift = (int)Math.round(d*Math.cos(theta+shift));
					int yShift = (int)Math.round(d*Math.sin(theta+shift));
					if(xShift+xCenter<width && xShift+xCenter>=0 
					&& yShift+yCenter<height && yShift+yCenter>=0)
						result[x + y*width] = pixels[xShift+xCenter + (yShift+yCenter)*width];
				}
			pixels = result;	
		}

	}
	
	/*
	public void render(int[] pixels, int offset, int row){
		for(int yTile = y_offset >> 3; yTile <= (y_offset+height)>>3; yTile++){
			int yMin = yTile*8 - y_offset;
			int yMax = yMin + 8;
			if(yMin < 0) yMin = 0;
			if(yMax > height) yMax = height;
			
			for(int xTile = x_offset >> 3; xTile <= (x_offset+width)>>3; xTile++){
				int xMin = xTile*8 - x_offset;
				int xMax = xMin + 8;
				if(xMin < 0) xMin = 0;
				if(xMax > width) xMax = width;
				
				int tileIndex = (xTile & (MAP_WIDTH_MASK)) + (yTile & (MAP_WIDTH_MASK))*MAP_WIDTH;
				
				for(int y = yMin; y < yMax; y++){
					int sheetPixel = ((y+y_offset) & 7)*sheet[0].width + ((xMin+x_offset) & 7);
					int tilePixel = offset+xMin + y*row;
					for(int x = xMin; x< xMax; x++){
						int color = tileIndex*4 + sheet[0].pixels[sheetPixel++];
						pixels[tilePixel++] = colors[color];
					}
				}
			}
		}
	}
*/
	
	public void render(int xPos, int yPos, int tileIndex, int color){
		render(xPos, yPos, tileIndex, color, 0x00, 0, 1);
	}
	public void render(int xPos, int yPos, int tileIndex, int color, int scale){
		render(xPos, yPos, tileIndex, color, 0x00, 0, scale);
	}

	public void render(int xPos, int yPos, int tileIndex, int color, int mirrorDir, int scale){
		render(xPos, yPos, tileIndex, color, mirrorDir, 0, scale);
	}
	
	public void staticRender(int xPos, int yPos, int tileIndex, int color, int mirrorDir, int rotateDir, int scale){
		render(xPos+x_offset, yPos+y_offset, tileIndex, color, mirrorDir, rotateDir, scale);
	}

	public void render(int xPos, int yPos, int tileIndex, int color, int mirrorDir, int rotateDir, int scale){
		xPos -= x_offset;
		yPos -= y_offset;
		rotateDir %= 4;
		boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
		boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;
		if(rotateDir%2 == 1){
			boolean temp = mirrorX;
			mirrorX = mirrorY;
			mirrorY = temp;
		}
		switch(rotateDir){
		default:
		case 0: break;
		case 1: mirrorY = !mirrorY; break;
		case 2: 
			mirrorX = !mirrorX;
			mirrorY = !mirrorY;
			rotateDir = 0; 
			break;
		case 3:	mirrorX = !mirrorX; break;
		}
		int scalMap = scale-1;
		int xTile = tileIndex%32;
		int yTile = tileIndex/32;
		int tileOffset = (xTile << 3)+ (yTile << 3)*sheet[0].width;
		for(int y = 0; y < 8; y++){
			int ySheet = y;
			if(mirrorY) ySheet = 7-y;
			int yPixel = y+yPos +y*scalMap;
			for(int x = 0 ; x < 8; x++){
				int xSheet = x;
				if(mirrorX) xSheet = 7-x;
				int xPixel = x+xPos +x*scalMap;
				
				int col;
				if(rotateDir%2 == 1){
					col = (color >> (sheet[0].pixels[ySheet + xSheet*sheet[0].width + tileOffset]*8)) & 255;
				}else{
					col = (color >> (sheet[0].pixels[xSheet + ySheet*sheet[0].width + tileOffset]*8)) & 255;
				}
				if(col < 216){
					for(int yScale = 0; yScale < scale; yScale++){
						if(yPixel+yScale < 0 || yPixel+yScale >= height) continue;
						for(int xScale = 0; xScale < scale; xScale++){
							if(xPixel+xScale < 0 || xPixel+xScale >= width) continue;
							pixels[xPixel+xScale + (yPixel+yScale)*width] = col;
						}
					}
				}
			}
		}
	}
	
	public void invert(int xPos, int yPos, int tileIndex, int mirrorDir, int rotateDir, int scale){
		xPos -= x_offset;
		yPos -= y_offset;
		rotateDir %= 4;
		boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
		boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;
		if(rotateDir%2 == 1){
			boolean temp = mirrorX;
			mirrorX = mirrorY;
			mirrorY = temp;
		}
		switch(rotateDir){
		default:
		case 0: break;
		case 1: mirrorY = !mirrorY; break;
		case 2: 
			mirrorX = !mirrorX;
			mirrorY = !mirrorY;
			rotateDir = 0; 
			break;
		case 3:	mirrorX = !mirrorX; break;
		}
		int scalMap = scale-1;
		int xTile = tileIndex%32;
		int yTile = tileIndex/32;
		int tileOffset = (xTile << 3)+ (yTile << 3)*sheet[0].width;
		for(int y = 0; y < 8; y++){
			int ySheet = y;
			if(mirrorY) ySheet = 7-y;
			int yPixel = y+yPos +y*scalMap;
			
			for(int x = 0 ; x < 8; x++){
				int xSheet = x;
				if(mirrorX) xSheet = 7-x;
				int xPixel = x+xPos +x*scalMap;
				
				int col;
				if(rotateDir%2 == 1){
					col = ((sheet[0].pixels[ySheet + xSheet*sheet[0].width + tileOffset]));
				}else{
					col = ((sheet[0].pixels[xSheet + ySheet*sheet[0].width + tileOffset]));
				}
				if(col == 3){
					for(int yScale = 0; yScale < scale; yScale++){
						if(yPixel+yScale < 0 || yPixel+yScale >= height) continue;
						for(int xScale = 0; xScale < scale; xScale++){
							if(xPixel+xScale < 0 || xPixel+xScale >= width) continue;
							pixels[xPixel+xScale + (yPixel+yScale)*width] = 215-pixels[xPixel+xScale + (yPixel+yScale)*width];
						}
					}
				}
			}
		}
	}

	public void single(int xPos, int yPos, int color){
		staticSingle(xPos-x_offset, yPos-y_offset, color);
	}
	public void staticSingle(int xPos, int yPos, int color){
		if(xPos >=0 && yPos >= 0 && xPos < width && yPos < height && color >= 0 && color < 216)
			pixels[xPos + yPos*width] = color;
	}
	public void singleInvert(int xPos, int yPos){
		staticSingleInvert(xPos-x_offset, yPos-y_offset);
	}
	public void staticSingleInvert(int xPos, int yPos){
		if(xPos >=0 && yPos >= 0 && xPos < width && yPos < height)
			pixels[xPos + yPos*width] = 215-pixels[xPos + yPos*width];
		//(pixels[xPos + yPos*width]*2)%216; //interesting
	}

	public void box(int xMin, int yMin, int xMax, int yMax, int color){
		staticBox(xMin-x_offset, yMin-y_offset, xMax-x_offset, yMax-y_offset, color);
	}
	public void staticBox(int xMin, int yMin, int xMax, int yMax, int color){
		for(int x = xMin; x <= xMax; x++){
			staticSingle(x, yMin, color);
			staticSingle(x, yMax, color);
		}
		for(int y = yMin+1; y <= yMax-1; y++){
			staticSingle(xMin, y, color);
			staticSingle(xMax, y, color);
		}
	}
	public void boxInvert(int xMin, int yMin, int xMax, int yMax){
		staticBoxInvert(xMin-x_offset, yMin-y_offset, xMax-x_offset, yMax-y_offset);
	}
	public void staticBoxInvert(int xMin, int yMin, int xMax, int yMax){
		for(int x = xMin; x <= xMax; x++){
			staticSingleInvert(x, yMin);
			staticSingleInvert(x, yMax);
		}
		for(int y = yMin+1; y <= yMax-1; y++){
			staticSingleInvert(xMin, y);
			staticSingleInvert(xMax, y);
		}
	}

	public void fill(int xMin, int yMin, int xMax, int yMax, int color){
		staticFill(xMin-x_offset, yMin-y_offset, xMax-x_offset, yMax-y_offset, color);
	}
	public void staticFill(int xMin, int yMin, int xMax, int yMax, int color){
		for(int x = xMin; x <= xMax; x++)
			for(int y = yMin; y <= yMax; y++)
				staticSingle(x, y, color);
	}
	public void fillInvert(int xMin, int yMin, int xMax, int yMax){
		staticFillInvert(xMin-x_offset, yMin-y_offset, xMax-x_offset, yMax-y_offset);
	}
	public void staticFillInvert(int xMin, int yMin, int xMax, int yMax){
		for(int x = xMin; x <= xMax; x++)
			for(int y = yMin; y <= yMax; y++)
				staticSingleInvert(x, y);
	}
	
	//0 - normal
	//1 - fill
	//2 - invert fill
	//4 - invert edge
	public void ring(int xPos, int yPos, int radius, int edgeColor, int fillColor, int variant){
		staticRing(xPos-x_offset, yPos-y_offset, radius, edgeColor, fillColor, variant);
	}
	public void staticRing(int xPos, int yPos, int radius, int edgeColor, int fillColor, int variant){
		if(radius <= 0) return;
		int lastY = Integer.MAX_VALUE, lastX = lastY;
		for(double t = 0; t < Math.PI; t+= 1./radius){
			int xEdge = (int)Math.round(radius*Math.cos(t));
			int yEdge = (int)Math.round(radius*Math.sin(t));
			if(lastY != yEdge && (variant & 3) > 0)
				for(int xFill = xEdge; xFill > -xEdge; xFill--){
					if((variant & 3) == 1){
						staticSingle(xPos+xFill, yPos+yEdge, fillColor);
						staticSingle(xPos+xFill, yPos-yEdge, fillColor);
					}
					if((variant & 3) == 2){
						staticSingleInvert(xPos+xFill, yPos+yEdge);
						if(t != 0)
						staticSingleInvert(xPos+xFill, yPos-yEdge);
					}
				}
			if((variant & 4) == 0){
				staticSingle(xPos+xEdge, yPos+yEdge, edgeColor);
				staticSingle(xPos+xEdge, yPos-yEdge, edgeColor);
				if(t == 0)
				staticSingle(xPos-xEdge, yPos+yEdge, edgeColor);
			}else if(lastY != yEdge || lastX != xEdge){
				staticSingleInvert(xPos+xEdge, yPos+yEdge);
				if(t != 0)
				staticSingleInvert(xPos+xEdge, yPos-yEdge);
				if(t == 0)
				staticSingleInvert(xPos-xEdge, yPos+yEdge);
			}
			lastY = yEdge;
			lastX = xEdge;
		}
	}
	public void staticRingu(int xPos, int yPos, int radius, int edgeColor, int fillColor, int variant){
		for(int xPixel = -radius; xPixel < radius; xPixel++){
			int xScreen = xPos + xPixel;
			for(int yPixel = -radius; yPixel < radius; yPixel++){
				int yScreen = yPos + yPixel;
				double d = Math.hypot(xPos-xScreen, yPos-yScreen);
				if(d > radius) continue;
				else if(d > radius-1)
					if(((variant >> 2) & 1) == 0)
						staticSingle(xScreen, yScreen, edgeColor);
					else 
						staticSingleInvert(xScreen, yScreen);
				else if(((variant) & 1) == 1)
					if(((variant >> 1) & 1) == 0)
						staticSingle(xScreen, yScreen, fillColor);
					else 
						staticSingleInvert(xScreen, yScreen);
			}
		}
	}
}
