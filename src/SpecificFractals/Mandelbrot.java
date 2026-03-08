package SpecificFractals;

import java.awt.image.BufferedImage;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import RenderWindow.pixelTranslateArgs;
import Rendering.MainEngine;

public class Mandelbrot implements Runnable {
	// task id for multithreading arg updates
	public final long TaskID; // gonna make it public
	private static long tasks = 0;

	private static BufferedImage img;
	public volatile static pixelTranslateArgs args;

	public static CyclicBarrier barrier;
	public static AtomicBoolean running;

	// ------------------------------------------------------
	public int startX;
	public int endX;

	public int height;
	// ------------------------------------------------------
	public final static int MAX_ITERATIONS = MainEngine.getMAX_ITERATIONS();

	// ------------------------------------------------------
	// parameters
	public static double parameterX = 0;
	public static double parameterY = 0;

	// ---------------------------------------------------------------------------------------------------
	public Mandelbrot(int startX, int endX, int height, BufferedImage img) {
		this.TaskID = (tasks++);

		this.startX = startX;
		this.endX = endX;

		this.height = height;

	}

	// ---------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		while (running.get()) {
			try {
				barrier.await();

				if (TaskID == 0)
					pixelTranslateArgs.pixelsToComplex(getArgs()); // update sharerd x & y lists ect

				int color;
				int survivalTime;

				for (int i = startX; i < endX; i++) {
					for (int j = 0; j < height; j++) {

						survivalTime = Mandelbrot.compute(args.x[i], args.y[j], parameterX, parameterY);

						color = Mandelbrot.getColor(survivalTime);
						getImg().setRGB(i, j, color);
					}
				}
				barrier.await();

			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------
	public static int compute(double r, double i, double c_r, double c_i) {
		int counter = 0;

		double r2 = r * r;
		double i2 = i * i;

		while (r2 + i2 < 4.0 && counter < MAX_ITERATIONS) {

			i = 2.0 * r * i + c_i;
			r = r2 - i2 + c_r;

			r2 = r * r;
			i2 = i * i;

			counter++;
		}
		return counter;
	}

	// ---------------------------------------------------------------------------------------------------
	public static int getColorGREYSCALE(int iterations) {
		// double scale = (iterations/(double)MAX_ITERATIONS) * (256); //rgb
		int color = (int) (iterations * (1 << 16) + iterations * (1 << 8) + iterations * (1 << 0));
		return color * (3);
	}

	public static int getColor(int iterations) {
		double scale = (iterations / (double) MAX_ITERATIONS) * (256); // rgb
		scale = Math.pow(scale, 0.99);
		int color = (int) (scale * (1 << 16) + scale * (1 << 8) + scale * (1 << 0));
		return color;
	}
	// ###################################################################################################

	public static AtomicBoolean getRunning() {
		return running;
	}

	public static void setRunning(AtomicBoolean running) {
		Mandelbrot.running = running;
	}

	public static CyclicBarrier getBarrier() {
		return barrier;
	}

	public static void setBarrier(CyclicBarrier barrier) {
		Mandelbrot.barrier = barrier;
	}

	public static pixelTranslateArgs getArgs() {
		return args;
	}

	public static void setArgs(pixelTranslateArgs args) {
		Mandelbrot.args = args;
	}

	public static BufferedImage getImg() {
		return img;
	}

	public static void setImg(BufferedImage img) {
		Mandelbrot.img = img;
	}

	public static long getTasks() {
		return tasks;
	}

	public static double getParameterY() {
		return parameterY;
	}

	public static void setParameterY(double parameterY) {
		Mandelbrot.parameterY = parameterY;
	}

	public static double getParameterX() {
		return parameterX;
	}

	public static void setParameterX(double parameterX) {
		Mandelbrot.parameterX = parameterX;
	}

}
