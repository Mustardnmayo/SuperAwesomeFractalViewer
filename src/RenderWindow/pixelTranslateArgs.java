package RenderWindow;

import java.util.Arrays;
import java.util.stream.IntStream;

//###########################################################################################################

public class pixelTranslateArgs {
	public volatile double[] x;
	public volatile double[] y;

	public volatile double pan_x;
	public volatile double pan_y;

	public volatile double zoom;

	public pixelTranslateArgs(double pan_x, double pan_y, double zoom, double[] x, double[] y) {
		this.x = x;
		this.y = y;

		this.pan_x = pan_x;
		this.pan_y = pan_y;

		this.zoom = zoom;
		pixelsToComplex(this);
	}

	public static void pixelsToComplex(pixelTranslateArgs args) {
		pixelsToComplex(args.pan_x, args.pan_y, args.zoom, args.x, args.y);
	}

	public static void pixelsToComplex(double pan_x, double pan_y, double zoom, double[] sX, double[] sY) {
		final int nx = sX.length;
		final int ny = sY.length;

		// pixel centers
		double cx = (nx - 1) / 2.0;
		double cy = (ny - 1) / 2.0;

		// change to apply (mapping pixels to scaled graph)
		double scale_x = dimentionWindow.dx / nx / zoom;
		double scale_y = dimentionWindow.dy / ny / zoom;

		// graph centers
		double cart_center_x = dimentionWindow.xMin + dimentionWindow.dx * 0.5 + pan_x;
		double cart_center_y = dimentionWindow.yMin + dimentionWindow.dy * 0.5 + pan_y;

		double current_x = cart_center_x - cx * scale_x;
		for (int i = 0; i < nx; i++) {
			sX[i] = current_x;
			current_x += scale_x;
		}

		double current_y = cart_center_y - cy * scale_y;
		for (int i = 0; i < ny; i++) {
			sY[i] = current_y;
			current_y += scale_y;
		}
	}

	public static double[] pixelsRange(int end) {
		IntStream tmp = IntStream.range(0, end);
		double[] ret = tmp.asDoubleStream().toArray();
		return ret;
	}

//------------------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return String.format("<ptc_Args> %s \n" + "pan_x : %s \n" + "pan_y : %s \n" + "zoom  : %s \n" + "x     : %s \n"
				+ "y     : %s \n", this.hashCode(), pan_x, pan_y, zoom, Arrays.toString(x), Arrays.toString(y));
	}

//------------------------------------------------------------------------------------------------------------
	public static void setDimentionWindow(double xMin, double xMax, double yMin, double yMax) {
		dimentionWindow.setDimentions(xMin, xMax, yMin, yMax);
	}

//------------------------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		double[] sX = pixelsRange(2560);
		double[] sY = pixelsRange(1080);

		dimentionWindow.setDimentions(-2.0, 0.47, -1.12, 1.12);
		pixelTranslateArgs pTA = new pixelTranslateArgs(0, 0, 1, sX, sY);

		System.out.println(pTA);
		pTA.zoom += 1;
		pixelsToComplex(pTA);
		System.out.println(pTA);
	}
}

//###########################################################################################################
