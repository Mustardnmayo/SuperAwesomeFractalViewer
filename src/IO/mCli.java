package IO;

import java.util.Set;

import RenderWindow.Window;

import java.util.HashSet;

import java.util.Scanner;

public abstract class mCli {
	// ----------------------------------------------------------------------------------------------------------

	public static int getFPS(int FPS) {
		Scanner scanner = new Scanner(System.in);
		// ----------
		if (FPS % 30 != 0) {
			int temp = FPS - (FPS % 30);
			System.out.println(String.format("NON STANDARD FPS %s\n\tchange to use %s instead? (y/n)", FPS, temp));

			// ----------
			while (temp != FPS) {
				String input = scanner.next();
				System.out.println(input);
				if (input.equals("y")) {
					System.out.println(String.format("changing FPS to %s", temp));
					FPS = temp;
					break;
				} else if (input.equals("n")) {
					break;
				} else {
					System.out.println(String.format("invalid input : '%s' ", input));
				}
			}
			// ----------
		}
		// ----------
		scanner.close();
		return FPS;
	}
	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Replaces all illegal chars with hyphen
	 * 
	 * @param name
	 * @return
	 */
	public static String sanitizeFileName(String name) {
		// only expected to be called once
		if (name == null || name.length() == 0) {
			return String.format("x-%x", Runtime.getRuntime().hashCode());
		}

		final Set<Character> illegalChars = new HashSet<>();
		{
			illegalChars.add('<');
			illegalChars.add('>');
			illegalChars.add(':');
			illegalChars.add('"');
			illegalChars.add('/');
			illegalChars.add('\\');
			illegalChars.add('|');
			illegalChars.add('?');
			illegalChars.add('*');
			illegalChars.add('\0');
		}

		for (char c : name.toCharArray()) {
			if (illegalChars.contains(c)) {
				name.replace(c, '-');
			}
		}

		return name;
	}
	// ----------------------------------------------------------------------------------------------------------

	public static void fillDimentions(Window w) {
		// TODO make proper
		w.width = 1920;
		w.height = 1080;
	}

}
