package Rendering;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.function.Consumer;

import RenderWindow.KeyboardKeys;
import SpecificFractals.Mandelbrot;

final class StepEngineDeltaHandeler {
	public volatile Integer[] keys = KeyboardKeys.getKeys();

	//@formatter:off
	public enum RC{ //recognized commands
		zoomIn,			
		zoomOut,		
		stepForward,	
		stepBackward,	
		incX,			
		incY,			
		decX, 			
		decY,			
		alterParameterX,
		alterParameterY,
		
		togglePause,	
	}
	//@formatter:on

	public final static HashMap<Integer, RC> ControlMapping = new HashMap<>();

	public volatile static HashMap<RC, Double> deltas = new HashMap<>();
	public final static HashMap<RC, Consumer<String>> DeltaMapping = new HashMap<>();

	static {
		// controls
		ControlMapping.put(KeyEvent.VK_EQUALS, RC.zoomIn);
		ControlMapping.put(KeyEvent.VK_MINUS, RC.zoomOut);

		ControlMapping.put(KeyEvent.VK_PERIOD, RC.stepForward);
		ControlMapping.put(KeyEvent.VK_COMMA, RC.stepBackward);

		ControlMapping.put(KeyEvent.VK_D, RC.incX); // y might be upsidedown
		ControlMapping.put(KeyEvent.VK_W, RC.incY);
		ControlMapping.put(KeyEvent.VK_A, RC.decX);
		ControlMapping.put(KeyEvent.VK_S, RC.decY);

		ControlMapping.put(KeyEvent.VK_W, RC.alterParameterX);
		ControlMapping.put(KeyEvent.VK_W, RC.alterParameterY);

		ControlMapping.put(KeyEvent.VK_K, RC.togglePause);

		// deltas
		deltas.put(RC.zoomIn, 0.1);
		deltas.put(RC.zoomOut, -0.1);

		deltas.put(RC.stepForward, 1.0);
		deltas.put(RC.stepBackward, -1.0); // reverse the deltas then apply them

		deltas.put(RC.incX, 0.1); // y might be upsidedown
		deltas.put(RC.incY, 0.1);
		deltas.put(RC.decX, 0.1);
		deltas.put(RC.decY, 0.1);

		deltas.put(RC.alterParameterX, 0.1);
		deltas.put(RC.alterParameterY, 0.1);

		deltas.put(RC.togglePause, 1.0);

		// funcs
		DeltaMapping.put(RC.zoomIn, s -> zoomIn());
		DeltaMapping.put(RC.zoomOut, s -> zoomOut());

		DeltaMapping.put(RC.stepForward, s -> stepForward());
		DeltaMapping.put(RC.stepBackward, s -> stepBackward());

		DeltaMapping.put(RC.incX, s -> incX()); // y might be upsidedown
		DeltaMapping.put(RC.incY, s -> incY());
		DeltaMapping.put(RC.decX, s -> decX());
		DeltaMapping.put(RC.decY, s -> decY());

		DeltaMapping.put(RC.alterParameterX, s -> alterParameterX());
		DeltaMapping.put(RC.alterParameterY, s -> alterParameterY());

		DeltaMapping.put(RC.togglePause, s -> togglePause());
	}

//----------------------------------------------------------------------------------------------------------
	private static void zoomIn() {
		StepEngine.args.zoom += deltas.get(RC.zoomIn);
	}

	private static void zoomOut() {
		StepEngine.args.zoom -= deltas.get(RC.zoomOut);
	}

	private static void stepForward() {
		StepEngine.stepForwards = deltas.get(RC.stepForward).doubleValue();
	}

	private static void stepBackward() {
		StepEngine.stepForwards = deltas.get(RC.stepBackward).doubleValue();
	}

	private static void incX() {
		StepEngine.args.pan_x += deltas.get(RC.incX);
	}

	private static void incY() {
		StepEngine.args.pan_x += deltas.get(RC.incY);
	}

	private static void decX() {
		StepEngine.args.pan_x -= deltas.get(RC.decX);
	}

	private static void decY() {
		StepEngine.args.pan_y -= deltas.get(RC.decY);
	}

	private static void alterParameterX() {
		Mandelbrot.parameterX += deltas.get(RC.alterParameterX);
	}

	private static void alterParameterY() {
		Mandelbrot.parameterY += deltas.get(RC.alterParameterY);
	}

	private static void togglePause() {
		StepEngine.paused = !StepEngine.paused;
	}

}
