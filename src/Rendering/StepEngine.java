package Rendering;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import RenderWindow.KeyboardKeys;
import RenderWindow.Window;
import RenderWindow.pixelTranslateArgs;
import SpecificFractals.Mandelbrot;

public final class StepEngine {
	// ------------------------------------------------------
	public final static int cores = Runtime.getRuntime().availableProcessors() * 2;
	// ------------------------------------------------------
	public static Window w = new Window(false, 1);

	public final static int width = w.getWidth();
	public final static int height = w.getHeight();
	// ------------------------------------------------------
	private static int MAX_ITERATIONS;
	// ------------------------------------------------------
	static {
		pixelTranslateArgs.setDimentionWindow(-2.0, 0.47, -1.12, 1.12);
	}
	// ------------------------------------------------------
	private volatile static double[] sX = pixelTranslateArgs.pixelsRange(width);
	private volatile static double[] sY = pixelTranslateArgs.pixelsRange(height);
	// ------------------------------------------------------
	public volatile static pixelTranslateArgs args = new pixelTranslateArgs(0, 0, 1, sX, sY); // auto updates first time
																								// to complex is called
	// ------------------------------------------------------
	static {
		pixelTranslateArgs.pixelsToComplex(args);
	}
	// ------------------------------------------------------
	public static BufferedImage front = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	public static BufferedImage back = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	// ------------------------------------------------------
	public static boolean paused = false;
	public static double stepForwards = 1;

	// ------------------------------------------------------

//##########################################################################################################	
	// not gonna implement record yet
	public static void windowRender(int depth, boolean record) throws InterruptedException, BrokenBarrierException {
		if (record) {

		} else {
			windowRenderNoExport(depth);
		}
	}

//----------------------------------------------------------------------------------------------------------
	public static void swapBuffers() {
		BufferedImage temp = front;
		front = back;
		back = temp;
	}

//----------------------------------------------------------------------------------------------------------
	public static void changeDeltas(double factor) {
		var SEDHd = StepEngineDeltaHandeler.deltas;
		for (Map.Entry<StepEngineDeltaHandeler.RC, Double> entry : SEDHd.entrySet()) {
			var key = entry.getKey();
			if (key == StepEngineDeltaHandeler.RC.stepForward || key == StepEngineDeltaHandeler.RC.stepBackward) {
				continue;
			}
			entry.setValue(entry.getValue() * StepEngine.stepForwards);
		}
		StepEngineDeltaHandeler.deltas.replaceAll((key, value) -> value * factor);
	}

	// ------------------------------------------------------
	public static void applyDeltas() {

	}

	// ------------------------------------------------------
	public static void windowRenderNoExport(int depth) throws InterruptedException, BrokenBarrierException {
		MainEngine.alterMAX_ITERATIONS(depth);

		// width delta
		final int dw = width / cores;

		// create thread functions
		AtomicBoolean runningFlag = new AtomicBoolean(true);
		CyclicBarrier barrier = new CyclicBarrier(cores); // exe func pauser/starter
		ExecutorService executor = Executors.newFixedThreadPool(cores); // holds the funcs

		Mandelbrot.setRunning(runningFlag);
		Mandelbrot.setBarrier(barrier);

		Mandelbrot.setImg(front);
		Mandelbrot.setArgs(args);

		for (int i = 0; i <= cores; i++) {
			executor.execute(new Mandelbrot(dw * i, dw * (i + 1), height, front));
		}

		int maxLoops = (int) 1e+06;
		int iterationCounter = 0;

		/*
		 * gonna figure out mouse dragging later
		 */

		while (runningFlag.get() && iterationCounter++ < maxLoops) {
			barrier.await();

			pause_loop: while (paused) {
				Thread.sleep(100);
				if (!paused) {
					break pause_loop;
				}

				changeDeltas(StepEngine.stepForwards);

				swapBuffers();
				Mandelbrot.setImg(front);
				w.display(front);
			}

			// processing
			swapBuffers();
			Mandelbrot.setImg(front);// shouldnt be necessary i think?
			w.display(front);
			// handle delta

			barrier.await();
		}
		// close
		runningFlag.set(false);
		barrier.await();

		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);

		// w.close();
	}

//##########################################################################################################
	public static int getMAX_ITERATIONS() {
		return MAX_ITERATIONS;
	}

	public static void setMAX_ITERATIONS(int ITERATIONS) {
		MAX_ITERATIONS = ITERATIONS;
	}
//##########################################################################################################	

	public static void main(String[] args) {

	}

}
