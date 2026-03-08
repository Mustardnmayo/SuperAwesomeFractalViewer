package IO;

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

}
