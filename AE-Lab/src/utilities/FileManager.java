package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class FileManager {

	private FileManager() {
	}

	public static byte[] readFile(final File theFile) {
		try {
			return Files.readAllBytes(Paths.get(theFile.getName()));
		} catch (final IOException ex) {
			System.out.println("Unexpected IOException..."); // Shouldn't happen
			return null;
		}
	}

	public static void writeFile(final byte[] theData, final String theFilename, final boolean theMode) {
		try {
			final String fileName = removeExtension(theFilename);
			final String fileExt = getExtension(theFilename).orElseGet(() -> "");
			if (theMode) {
				final String name = fileName + "-encrypted" + fileExt;
				final Path filePath = Paths.get(name).toAbsolutePath();
				Files.write(filePath, theData);
				System.out.println("Your file has been encrypted and saved as \"" + filePath + "\"");
			} else {
				final String name = fileName + "-decrypted" + fileExt;
				final Path filePath = Paths.get(name).toAbsolutePath();
				Files.write(filePath, theData);
				System.out.println("Your file has been decrypted and saved as \"" + filePath + "\"");
			}
		} catch (final IOException ex) {
			System.out.println("Unexpected IOException..."); // Shouldn't happen
		}
	}

	public static boolean isFileExists(final String thePath) {
		final File f = new File(thePath);
		return f.exists() && !f.isDirectory();
	}

	public static Optional<String> getExtension(final String theFilename) {
		return Optional.ofNullable(theFilename).filter(f -> f.contains("."))
				.map(f -> f.substring(theFilename.lastIndexOf(".")));
	}

	public static String removeExtension(final String theFilename) {
		return theFilename.substring(0, theFilename.lastIndexOf("."));
	}

}
