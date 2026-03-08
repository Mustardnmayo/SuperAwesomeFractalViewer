package IO;

import java.io.File;
import java.io.IOException;

public abstract class mFile {
	// ----------------------------------------------------------------------------------------------------------

	public static File VIDEO_EXPORTING_FILE_PATH;
	static {
		VIDEO_EXPORTING_FILE_PATH = new File("video-renders");
		if (!(VIDEO_EXPORTING_FILE_PATH.exists() && VIDEO_EXPORTING_FILE_PATH.isDirectory())) {
			VIDEO_EXPORTING_FILE_PATH.mkdir();
		}
	};

	// ----------------------------------------------------------------------------------------------------------

	public static String checkFileName(String name) throws IOException {
		int sentinel = 0;

		do {
			File path = new File(VIDEO_EXPORTING_FILE_PATH, String.format("%s_%s.mp4", name, String.valueOf(sentinel)));

			if (!(path.exists())) {
				break;
			}
			System.out.println(String.format("path : %s does not exist", path.toString()));
		} while (sentinel++ < 1000);

		return String.format("%s_%s.mp4", name, String.valueOf(sentinel));
	}
	// ----------------------------------------------------------------------------------------------------------

	public static File tempDirectory() throws IOException { // already uses video exporting path
		File tempDir = new File(mFile.VIDEO_EXPORTING_FILE_PATH, "temp");
		return tempDir;
	}
	// ----------------------------------------------------------------------------------------------------------

}
