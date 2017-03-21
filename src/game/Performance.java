package game;

public class Performance {

	private static long startTime;
	public static int frameRate;
	public static int tickRate;
	public static int tickRateDiff, frameRateDiff;
	private static int maxT = 0;
	private static int maxF = 0;
	private static int minT = Integer.MAX_VALUE;
	private static int minF = Integer.MAX_VALUE;
	
	public static void init(){
		startTime= System.currentTimeMillis();
	}
	
	public static void record(int f, int t){
		if(System.currentTimeMillis()-startTime > 2000){
			frameRateDiff = 2*f - frameRate;
			tickRateDiff = 2*t - tickRate;
			frameRate = 2*f;
			tickRate = 2*t;
			if(frameRate > maxF) maxF = frameRate;
			if(frameRate < minF) minF = frameRate;
			if(tickRate > maxT) maxT = tickRate;
			if(tickRate < minT) minT = tickRate;
		}
	}
	
	public static void report(){
		System.out.println("Performance Report:"
				+ "\nTPS: " + maxT +"/"+ minT
				+ "\nFPS: " + maxF +"/"+ minF
				);
	}
	
}
