package Rendering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.*;

import RenderWindow.*;
import IO.mCli;
import IO.mFile;

import org.bytedeco.ffmpeg.global.avcodec;

import SpecificFractals.*;

public abstract class MainEngine {
//##########################################################################################################
	public final static int cores = Runtime.getRuntime().availableProcessors();
//----------------------------------------------------------------------------------------------------------	
	public static Window w = new Window(false, 1); // auto displays

	public final static int width = w.getWidth();
	public final static int height = w.getHeight();
//----------------------------------------------------------------------------------------------------------
	private static int MAX_ITERATIONS;

	static {
		pixelTranslateArgs.setDimentionWindow(-2.0, 0.47, -1.12, 1.12);
	}
	private volatile static double[] sX = pixelTranslateArgs.pixelsRange(width);
	private volatile static double[] sY = pixelTranslateArgs.pixelsRange(height);

	// auto updates first time to complex is called
	private volatile static pixelTranslateArgs args = new pixelTranslateArgs(0, 0, 1, sX, sY);

	static {
		pixelTranslateArgs.pixelsToComplex(args);
	}

	public static BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//----------------------------------------------------------------------------------------------------------

//##########################################################################################################
	public static void alterMAX_ITERATIONS(int n) {
		if (n < 1) // TODO change to formatted string
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

//##########################################################################################################
	public static void windowRender(int depth, int length, int minFrameInterval)
			throws InterruptedException, BrokenBarrierException {

		MainEngine.alterMAX_ITERATIONS(depth);

		// width delta
		final int dw = width / cores;

		// create thread functions
		AtomicBoolean runningFlag = new AtomicBoolean(true);
		CyclicBarrier barrier = new CyclicBarrier(cores); // exe func pauser/starter
		ExecutorService executor = Executors.newFixedThreadPool(cores); // holds the funcs

		Mandelbrot.setRunning(runningFlag);
		Mandelbrot.setBarrier(barrier);
		// Mandelbrot.setImg(img);
		Mandelbrot.setArgs(args);

		for (int i = 0; i <= cores; i++) {
			executor.execute(new Mandelbrot(dw * i, dw * (i + 1), height, img));
		}

		int inverseMax = minFrameInterval / 1000; // milliseconds
		int maxLoops;

		if (inverseMax == 0) {
			maxLoops = (int) (length / (1.0 / 1000.0));
		} else {
			maxLoops = length / inverseMax;
		}

		long now;
		long delta;
		long wait;

		args.pan_x = 0.75;
		Mandelbrot.setParameterX(-0.5251993);
		Mandelbrot.setParameterY(-0.5251993);

		int iterationCounter = 0;
		while (runningFlag.get() && iterationCounter++ < maxLoops) {
			barrier.await();
			now = System.currentTimeMillis();

			Mandelbrot.setParameterY(Mandelbrot.getParameterY() - 0.0005);
			Mandelbrot.setParameterX(Mandelbrot.getParameterX() - 0.0005);

			barrier.await();
			w.display(img);

			delta = System.currentTimeMillis() - now;
			wait = minFrameInterval - delta;

			if (wait > 0) {
				Thread.sleep(wait);
			}

			System.out.println("iteration : " + iterationCounter + " | frame time " + delta + "ms");

		}
		// close
		runningFlag.set(false);
		barrier.await();

		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);

	}

//##########################################################################################################

//-----------------------------------------------------------------------------------------------------------

//-----------------------------------------------------------------------------------------------------------
	/*
	 * public static void fileRenderLOWBITRATE(int depth, int length, int FPS,
	 * String file_name) // old shitty throws InterruptedException,
	 * BrokenBarrierException, IOException { //
	 * ------------------------------------------- FFmpegLogCallback.set();
	 * 
	 * FPS = mCli.getFPS(FPS); int totalFrames = length * FPS;
	 * 
	 * String outPath = mFile.checkFileName(file_name); File file = new
	 * File(mFile.VIDEO_EXPORTING_FILE_PATH, outPath);
	 * 
	 * System.out.println(file.getAbsolutePath());
	 * 
	 * FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file, width, height);
	 * 
	 * recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); //
	 * recorder.setVideoCodecName("libx264"); //sorta just decided
	 * recorder.setFormat("mp4"); recorder.setFrameRate(FPS);
	 * recorder.setVideoBitrate(50_000_000); recorder.start();
	 * 
	 * Java2DFrameConverter converter = new Java2DFrameConverter(); //
	 * -------------------------------------------
	 * 
	 * MainEngine.alterMAX_ITERATIONS(depth);
	 * 
	 * final int rCores = cores * 2;
	 * 
	 * // width delta final int dw = width / rCores; //
	 * ------------------------------------------- // create thread functions
	 * AtomicBoolean runningFlag = new AtomicBoolean(true); CyclicBarrier barrier =
	 * new CyclicBarrier(rCores); ExecutorService executor =
	 * Executors.newFixedThreadPool(rCores);
	 * 
	 * Mandelbrot.setRunning(runningFlag); Mandelbrot.setBarrier(barrier);
	 * Mandelbrot.setImg(img); Mandelbrot.setArgs(args);
	 * 
	 * for (int i = 0; i <= rCores; i++) { executor.execute(new Mandelbrot(dw * i,
	 * dw * (i + 1), height)); } // -------------------------------------------
	 * args.pan_x = 0.75; Mandelbrot.setParameterX(-0.5251993);
	 * Mandelbrot.setParameterY(-0.5251993);
	 * 
	 * System.out.println(); int iterationCounter = 0; while (runningFlag.get() &&
	 * iterationCounter++ < totalFrames) { barrier.await(); // args.zoom
	 * +=0.001*iterationCounter*iterationCounter;
	 * 
	 * Mandelbrot.setParameterY(Mandelbrot.getParameterY() + 0.00001);
	 * Mandelbrot.setParameterX(Mandelbrot.getParameterX() + 0.00001);
	 * Mandelbrot.setParameterX(-iterationCounter / 100.0);
	 * Mandelbrot.setParameterX(+iterationCounter / 100.0);
	 * 
	 * barrier.await(); Frame frame = converter.convert(img);
	 * recorder.record(frame);
	 * 
	 * System.out.printf("\rframe : %s | %.2f%%", iterationCounter, (double) (100 *
	 * iterationCounter) / totalFrames); } System.out.println(); // close
	 * runningFlag.set(false); barrier.await();
	 * 
	 * executor.shutdown(); executor.awaitTermination(5, TimeUnit.SECONDS);
	 * 
	 * recorder.stop(); recorder.release(); recorder.close(); converter.close(); //
	 * -------------------------------------------
	 * System.out.println(String.format("Video saved to : %s",
	 * file.getAbsolutePath())); w.close(); }
	 */

//##########################################################################################################

	public static void Render(BufferedImage imgOut, int depth, int length, int FPS, int cores, String mode,
			String fileName) throws IOException {
		MainEngine.alterMAX_ITERATIONS(depth);

		int iterationCounter = 0;

		Callable<Boolean> task = null;

		if (mode.equals("Window")) {
			task = () -> {
				w.display(imgOut);
				return true;
			};
		} else if (mode.equals("file")) {

			File temp_dir = mFile.tempDirectory();
			temp_dir.mkdir();
			// temp_dir.deleteOnExit();
			System.out.printf("Temporary directory created at : %s", temp_dir.getAbsolutePath());

			// -------------------------------------------
			FPS = mCli.getFPS(FPS);
			int totalFrames = length * FPS;
			// -------------------------------------------

			task = () -> {
				File output_file = new File(temp_dir, String.format("%s_frame_%010d.png", fileName, iterationCounter));
				ImageIO.write(img, "png", output_file);

				return true;
			};
		}

		ExecutorService executor = Executors.newFixedThreadPool(cores);

		AtomicBoolean Running = new AtomicBoolean(true);
		CyclicBarrier barrier = new CyclicBarrier(cores);

		Mandelbrot.setRunning(Running);
		Mandelbrot.setBarrier(barrier);
		Mandelbrot.setImg(img);
		Mandelbrot.setArgs(args);

		final int dw = width / cores;

		for (int i = 0; i <= cores; i++) {
			executor.execute(new Mandelbrot(dw * i, dw * (i + 1), height, img));
		}

		boolean keepGoing = true;
		try {
			while (keepGoing) {
				// iterationCounter++;

				//
				keepGoing = RenderStep(Running, barrier, task);
				//
			}
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}

	}

	private static boolean RenderStep(AtomicBoolean flag, CyclicBarrier barrier, Callable<Boolean> task)
			throws InterruptedException, BrokenBarrierException {
		barrier.await();

		boolean result = true;
		if (task != null) {
			try {
				result = task.call().booleanValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		barrier.await();

		return flag.get() && result;
	}

//##########################################################################################################
	public static void fileRender(int depth, int length, int FPS, String file_name)
			throws InterruptedException, BrokenBarrierException, IOException {
		// -------------------------------------------
		File temp_dir = mFile.tempDirectory();
		temp_dir.mkdir();
		// temp_dir.deleteOnExit();
		System.out.printf("Temporary directory created at : %s", temp_dir.getAbsolutePath());

		// -------------------------------------------
		FPS = mCli.getFPS(FPS);
		int totalFrames = length * FPS;
		// -------------------------------------------
		MainEngine.alterMAX_ITERATIONS(depth);

		// width delta
		final int dw = width / cores;
		// -------------------------------------------
		// create thread functions
		AtomicBoolean runningFlag = new AtomicBoolean(true);
		CyclicBarrier barrier = new CyclicBarrier(cores); // exe func pauser/starter
		ExecutorService executor = Executors.newFixedThreadPool(cores); // holds the funcs

		Mandelbrot.setRunning(runningFlag);
		Mandelbrot.setBarrier(barrier);
		Mandelbrot.setImg(img);
		Mandelbrot.setArgs(args);

		for (int i = 0; i <= cores; i++) {
			executor.execute(new Mandelbrot(dw * i, dw * (i + 1), height, img));
		}
		// -------------------------------------------
		args.pan_x = 0.75;
		Mandelbrot.setParameterX(-0.5251993 - (50 * 0.01));
		Mandelbrot.setParameterY(-0.5251993 + (50 * 0.01));

		int iterationCounter = 0;
		while (runningFlag.get() && iterationCounter++ < totalFrames) {
			barrier.await();

			Mandelbrot.setParameterY(Mandelbrot.getParameterY() - 0.0005);
			Mandelbrot.setParameterX(Mandelbrot.getParameterX() + 0.0005);

			barrier.await();
			// file stuff frame count can be an issue because of padding
			File output_file = new File(temp_dir, String.format("%s_frame_%010d.png", file_name, iterationCounter));
			ImageIO.write(img, "png", output_file);

			System.out.printf("\rframe : %s | %.2f%%", iterationCounter,
					(double) (100 * iterationCounter) / totalFrames);
		}
		System.out.println();
		// close
		runningFlag.set(false);
		barrier.await();

		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);
		// -------------------------------------------
		// compile into video
		File video_out = new File(mFile.VIDEO_EXPORTING_FILE_PATH, mFile.checkFileName(file_name));
		System.out.printf("Starting compillation to : %s\n", video_out);

		ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-framerate", String.valueOf(FPS), "-i",
				String.format("%s/%s_frame_%s.png", temp_dir.getAbsolutePath(), file_name, "%010d"), "-c:v",
				"libx264rgb", "-pix_fmt", "rgb24", "-crf", "0", video_out.getAbsolutePath());
		// ffmpeg -i julia_0.mp4 -pix_fmt yuv420p julia_0_viewable.mp4
		pb.inheritIO();
		int exit_code = pb.start().waitFor();
		System.out.printf("first render exit code : %d\n", exit_code);

		System.out.printf("Video saved to : %s\n", video_out.getAbsolutePath());

		// stupid manually make the file visible on windows stupid windows
		System.out.printf("Converting video to format viewable on windows\n");
		String s = video_out.toString();

		ProcessBuilder npb = new ProcessBuilder("ffmpeg", "-i", video_out.toString(), "-pix_fmt", "yuv420p", "-y",
				mFile.checkFileName((String) s.subSequence(0, s.length() - 4) + "_viewable"));

		npb.inheritIO();
		exit_code = npb.start().waitFor();
		System.out.printf("windows conversion exit code : %d\n", exit_code);

		System.out.printf("deleted old unviewable render success : %s\n", video_out.delete());
		// -------------------------------------------
		// deleting files
		for (File f : temp_dir.listFiles()) {
			f.delete();
		}
		temp_dir.delete();
		// -------------------------------------------
		// end window to show user process is done
		w.close();
		System.exit(exit_code);
	}

//########################################################################################################## //just convenience
	public static void wr() {
		try {
			windowRender(256, 10, 16);
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	public static void fr() {
		try {
			fileRender(256, 20, 60, "x005" + "t1"); // remember to increment for library creation
		} catch (InterruptedException | BrokenBarrierException | IOException e) {
			e.printStackTrace();
		}
	}

//##########################################################################################################
	public static void main(String[] args) {

		/*
		 * storing args of good ones frames, init, delta //1500 (50*0.01) +0.0005
		 * (50*0.01) +0.0005
		 * 
		 * //
		 */

		wr();
		// fr();

	}
//##########################################################################################################

}
