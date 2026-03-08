package RenderWindow;

public final class MousePos {
	public volatile static int prevX = 0;
	public volatile static int prevY = 0;
	
	public volatile static int currX = 0;
	public volatile static int currY = 0;
//------------------------------------------------------------------------------------------------------------	
	public static void cycle(int x, int y) {
		prevX = currX;
		prevY = currY;
		
		currX = x;
		currY = y;		
		//System.out.printf("(%d,%d) -> (%d,%d)\n", prevX,prevY,currX,currY);
	}
//------------------------------------------------------------------------------------------------------------

}
