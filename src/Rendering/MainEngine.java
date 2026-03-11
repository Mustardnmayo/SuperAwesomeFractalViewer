package Rendering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import RenderWindow.*;
import IO.mCli;
import IO.mFile;

import SpecificFractals.*;

public abstract class MainEngine {
//##########################################################################################################
	public final static int cores = Runtime.getRuntime().availableProcessors();
	private static volatile pixelTranslateArgs args;
	private static int MAX_ITERATIONS;

	private static volatile double[] sX;
	private static volatile double[] sY;

//##########################################################################################################
	public static void alterMAX_ITERATIONS(int n) {
		if (n < 1)
			throw new IllegalArgumentException("New iterations too small : " + n);
		if (n > 1e5)
			throw new IllegalArgumentException("New iterations too big : " + n);

		MainEngine.MAX_ITERATIONS = n;
	}

//-----------------------------------------------------------------------------------------------------------
	public static int getMAX_ITERATIONS() {
		return MainEngine.MAX_ITERATIONS;
	}

//##########################################################################################################
	public static void setArgsZoom(double val) {
		if (args != null) {
			args.zoom = val;
		}
	}

	public static double getArgsZoom() {
		if (args != null) {
			return args.zoom;
		}
		return 0.0;
	}

//##########################################################################################################

	/**
	 * The Behemoth...
	 * 
	 * @param depth
	 * @param length
	 * @param FPS
	 * @param mode
	 * @param fileName
	 * @param argsUpdate
	 * @param startingOffset
	 * @throws IOException
	 */
	public static void Render(int depth, int length, int FPS, String mode, String fileName, Runnable argsUpdate,
			Runnable startingOffset) throws IOException {
		MainEngine.alterMAX_ITERATIONS(depth);

		// ################################################## multi-threading
		// preparation
		ExecutorService executor = Executors.newFixedThreadPool(cores);

		AtomicBoolean Running = new AtomicBoolean(true);
		CyclicBarrier barrier = new CyclicBarrier(cores);

		AtomicInteger iterationCounter = new AtomicInteger(0);

		// ################################################## pixel location preparation

		Runnable task = null;
		Runnable afterTask = null;

		Window w = new Window(false, 1); // auto displays

		if (w.hasDimentions == false) {
			mCli.fillDimentions(w);
		}

		final int width = w.getWidth();
		final int height = w.getHeight();

		pixelTranslateArgs.setDimentionWindow(-2.0, 0.47, -1.12, 1.12); // General Mandelbrot starting point
		sX = pixelTranslateArgs.pixelsRange(width);
		sY = pixelTranslateArgs.pixelsRange(height);

		MainEngine.args = new pixelTranslateArgs(0, 0, 1, sX, sY);

		pixelTranslateArgs.pixelsToComplex(args);

		if (startingOffset != null) {
			startingOffset.run();
		}

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// ################################################## task designation

		if (mode.equals("Window")) {

			task = () -> {
				w.display(img);

			};

		} else if (mode.equals("File")) {

			File temp_dir = mFile.tempDirectory();
			temp_dir.mkdir();
			System.out.printf("Temporary directory created at : %s", temp_dir.getAbsolutePath());

			// ------------------------

			final int totalFrames = length * FPS;

			// ------------------------

			long startTime = System.currentTimeMillis();

			// ------------------------

			task = () -> {
				final int counter = iterationCounter.get();

				File output_file = new File(temp_dir, String.format("%s_frame_%010d.png", fileName, counter));

				try {
					ImageIO.write(img, "png", output_file);
				} catch (IOException e) {
					Running.set(false);
					e.printStackTrace();
				}

				if (counter > totalFrames) {
					Running.set(false);
				}

				System.out.printf("\rframe : %s | %.2f%% | %.2fs", counter, (100 * counter) / (double) totalFrames,
						(System.currentTimeMillis() - startTime) / 1000.0);
			};

			// ------------------------

			afterTask = () -> {
				int exit_code = 1;
				try {
					exit_code = mFile.compileRenderAndClearup(temp_dir, FPS, fileName);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				} finally {
					System.exit(exit_code);
				}
			};
		}

		// ##################################################

		Mandelbrot.setRunning(Running);
		Mandelbrot.setBarrier(barrier);
		Mandelbrot.setImg(img);
		Mandelbrot.setArgs(MainEngine.args);

		// ##################################################

		final int dw = width / cores;
		for (int i = 0; i <= cores; i++) {
			executor.execute(new Mandelbrot(dw * i, dw * (i + 1), height, img));
		}

		// ##################################################

		try {
			while (Running.get()) {
				iterationCounter.incrementAndGet();

				RenderStep(barrier, task);
				if (argsUpdate != null) {
					argsUpdate.run();
				}
			}
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
			System.exit(1); // TEMP
		}

		// ------------------------

		if (afterTask != null) {
			afterTask.run();
		}

	}

// -----------------------------------------------------------------------------------------------------------

	private static void RenderStep(CyclicBarrier barrier, Runnable task)
			throws InterruptedException, BrokenBarrierException {
		/*
		 * The Mandelbrot objects that do the actual rendering also use this barrier,
		 * they await the barrier, then start rendering, then await the barrier again
		 * this way, I can run the task in-between rendering
		 * 
		 */
		barrier.await();

		if (task != null) {
			task.run();
		}

	}

//##########################################################################################################
	public static void main(String[] args) {

		/*
		 * storing args of good ones frames, init, delta //1500 (50*0.01) +0.0005
		 * (50*0.01) +0.0005
		 * 
		 */

		Runnable startingOffset = () -> {
			// Mandelbrot.setParameterX((50 * 0.01) + 0.0005);
			// Mandelbrot.setParameterY((50 * 0.01) + 0.0005);

			Mandelbrot.setParameterX(-0.5251993 + (50 * 0.01) + 0.5);
			Mandelbrot.setParameterY(-0.5251993 + (50 * 0.01));

			MainEngine.args.pan_x += 0.6;
		};

		Runnable updateArgs = () -> {
			Mandelbrot.setParameterX(Mandelbrot.getParameterX() - 5e-5);
			Mandelbrot.setParameterY(Mandelbrot.getParameterY() - 1e-4);
			// setArgsZoom(getArgsZoom() + 1e-2);
		};

		String fileName = "";
		fileName = mCli.sanitizeFileName(fileName);

		try {
			Render(256, 40, 60, "Window", fileName, updateArgs, startingOffset);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
//##########################################################################################################

}
