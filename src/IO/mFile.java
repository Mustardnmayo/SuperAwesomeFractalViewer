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

	public static int compileRenderAndClearup(File temp_dir, int FPS, String fileName)
			throws IOException, InterruptedException {
		File video_out = new File(mFile.VIDEO_EXPORTING_FILE_PATH, mFile.checkFileName(fileName));
		System.out.printf("Starting compillation to : %s\n", video_out);

		ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-framerate", String.valueOf(FPS), "-i",
				String.format("%s/%s_frame_%s.png", temp_dir.getAbsolutePath(), fileName, "%010d"), "-c:v",
				"libx264rgb", "-pix_fmt", "rgb24", "-crf", "0", video_out.getAbsolutePath());
		pb.inheritIO();

		int exit_code = pb.start().waitFor();

		System.out.printf("first render exit code : %d\n", exit_code);

		System.out.printf("Video saved to : %s\n", video_out.getAbsolutePath());

		// make the file viewable on windows
		System.out.printf("Converting video to format viewable on windows\n");
		String s = video_out.toString();

		ProcessBuilder npb = new ProcessBuilder("ffmpeg", "-i", video_out.toString(), "-pix_fmt", "yuv420p", "-y",
				mFile.checkFileName(s.subSequence(0, s.length() - 4) + "_viewable"));

		npb.inheritIO();
		exit_code = npb.start().waitFor();
		System.out.printf("windows conversion exit code : %d\n", exit_code);

		System.out.printf("deleted old unviewable render success : %b\n", video_out.delete());
		// -------------------------------------------
		// deleting files
		for (File f : temp_dir.listFiles()) {
			f.delete();
		}
		temp_dir.delete();
		// -------------------------------------------
		return exit_code;
	}

}
