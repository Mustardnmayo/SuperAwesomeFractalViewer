package RenderWindow;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class KeyboardHandling implements KeyListener{
	public static Window w = null;
	private static boolean toggleMode = true;
//############################################################################################################
	KeyboardHandling(Window w){
		KeyboardHandling.w = w;
	}
//------------------------------------------------------------------------------------------------------------
	@Override
	public void keyTyped(KeyEvent e) {
		return;
		/*
		Integer key = Integer.valueOf(e.getExtendedKeyCode());
		System.out.println(key + " : Typed");
		*/
	}
//------------------------------------------------------------------------------------------------------------
	@Override
	public void keyPressed(KeyEvent e) {
		return;
		/*
		Integer key = Integer.valueOf(e.getExtendedKeyCode());
		System.out.println(key + " : Pressed");
		KeyboardKeys.addKey(key);
		*/
	}
//------------------------------------------------------------------------------------------------------------
	@Override
	public void keyReleased(KeyEvent e) {
		Integer key = Integer.valueOf(e.getExtendedKeyCode());
		if (isToggleMode()) {
			KeyboardKeys.toggleKey(key);
		} else {
			KeyboardKeys.removeKey(key);			
		}
		//System.out.println(key + " : Released");
	}
//------------------------------------------------------------------------------------------------------------	
	public static boolean isToggleMode() {
		return toggleMode;
	}
//------------------------------------------------------------------------------------------------------------
	public static void setToggleMode(boolean toggleMode) {
		KeyboardHandling.toggleMode = toggleMode;
	}
}
