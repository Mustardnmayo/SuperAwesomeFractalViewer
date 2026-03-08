package RenderWindow;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public final class KeyboardKeys {
	private static ArrayList<Integer> keys = new ArrayList<>();
//------------------------------------------------------------------------------------------------------------
	public static void addKey(Integer keyCode) {
		System.out.println("Keycode : " + keyCode);
		if (keys.contains(keyCode) == false) keys.add(keyCode);
		System.out.println(keys.toString());
	}
//------------------------------------------------------------------------------------------------------------
	public static void removeKey(Integer keyCode) { //should work
		keys.remove(keyCode);
		System.out.println(keys.toString());
	}
//------------------------------------------------------------------------------------------------------------
	public static void clearKeys() {
		keys.clear();
	}
//------------------------------------------------------------------------------------------------------------
	public static void toggleKey(Integer keyCode) {
		if (keyCode == KeyEvent.VK_BACK_SPACE) {
			clearKeys();
			System.out.println(Keys());
			return;
		}
		
		if (keys.contains(keyCode)) {
			removeKey(keyCode);
		} else {
			addKey(keyCode);
		}
		System.out.println(Keys());
	}
//------------------------------------------------------------------------------------------------------------
	public static String Keys() { //maybe delete
		int n = keys.toArray().length;
		char[] keys_arr = new char[n];
		for (int i=0; i<n; i++) {
			keys_arr[i] = (char) keys.get(i).intValue();
		}
		return "[ " + new String(keys_arr) + " ]";
	}
//############################################################################################################
	public static Integer getKey(int idx) {
		return keys.get(idx);
	}
	
//------------------------------------------------------------------------------------------------------------
	public static Integer[] getKeys() {
		return (Integer[]) keys.toArray();
	}
	
}
