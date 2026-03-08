package RenderWindow;

class dimentionWindow{ // do not change silding window while the tthingy is running
	public static double xMin;
	public static double yMin;
	
	public static double xMax;
	public static double yMax;
	
	public static double dx;
	public static double dy;
	
	public static void setDimentions(double xMin, double xMax, double yMin, double yMax) {
		dimentionWindow.xMin = xMin;
		dimentionWindow.xMax = xMax;
		dimentionWindow.yMin = yMin;
		dimentionWindow.yMax = yMax;

		dimentionWindow.dx = xMax - xMin;
		dimentionWindow.dy = yMax - yMin;
	}
	
}