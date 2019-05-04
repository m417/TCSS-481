package applications;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import algorithms.AES128CBC;
import utilities.FileManager;
import utilities.TerminalMsg;

public final class AuthEncryption {

	private final Scanner myScanner;

	public AuthEncryption() {
		myScanner = new Scanner(System.in);
	}

	public final void start() {
		TerminalMsg.promptIntroduction();
		TerminalMsg.promptOptions();
		this.onOptionEntered(myScanner.next());
	}

	public final void startOver() {
		System.out.println();
		this.start();
	}

	private void onOptionEntered(final String theOption) {
		switch (theOption) {
		case "1":
			this.optionEncrypt();
			break;
		case "2":
			this.optionDecrypt();
			break;
		case "3":
			TerminalMsg.promptGoodBye();
			myScanner.close();
			System.exit(0);
		default: // invalid input
			TerminalMsg.promptChoice();
			this.onOptionEntered(myScanner.next());
		}
	}

	private void optionEncrypt() {
		System.out.print("Enter the target filename: ");
		String fileName = null;

		while (fileName == null) {
			fileName = myScanner.next();
			if (!FileManager.isFileExists(fileName)) {
				if ("-1".equals(fileName.toLowerCase())) {
					this.startOver();
				}
				System.out.println("\nError: File not found. Please try again or enter '-1' to abort.");
				System.out.print("Enter the target filename: ");
				fileName = null;
			}
		}

		this.encProcess(fileName);

		System.out.print("\nDo you want to try decrypting the file right away? (Y/N): ");
		if ("y".equals(myScanner.next().toLowerCase())) {
			final String fileExt = FileManager.getExtension(fileName).orElseGet(() -> "");
			final String newName = fileName.substring(0, fileName.length() - fileExt.length()) + "-encrypted";
			final String newFilename = newName + fileExt;
			this.decProcess(newFilename);
		} else {
			this.startOver();
		}
	}

	private void optionDecrypt() {
		System.out.print("Enter the target filename: ");
		String fileName = null;

		while (fileName == null) {
			fileName = myScanner.next();
			if (!FileManager.isFileExists(fileName)) {
				if ("-1".equals(fileName.toLowerCase())) {
					this.startOver();
				}
				System.out.println("\nError: File not found. Please try again or enter '-1' to abort.");
				System.out.print("Enter the target filename: ");
				fileName = null;
			}
		}

		this.decProcess(fileName);
	}

	private void encProcess(final String theFilename) {
		final Path filePath = Paths.get(theFilename).toAbsolutePath();
		TerminalMsg.promptEncrypt(filePath);

		final File inputFile = new File(theFilename);
		final byte[] plainText = FileManager.readFile(inputFile);

		FileManager.writeFile(AES128CBC.encrypt(plainText), theFilename, true);
	}

	private void decProcess(final String theFilename) {
		final Path filePath = Paths.get(theFilename).toAbsolutePath();
		TerminalMsg.promptDecrypt(filePath);

		final File encryptedFile = new File(theFilename);
		byte[] plainText = null;

		while (plainText == null) {
			try {
				System.out.print("Enter IV: ");
				final String iv = myScanner.next();
				if ("-1".equals(iv.toLowerCase())) {
					this.startOver();
				}
				System.out.print("Enter K1: ");
				final String k1 = myScanner.next();
				if ("-1".equals(k1.toLowerCase())) {
					this.startOver();
				}
				System.out.print("Enter K2: ");
				final String k2 = myScanner.next();
				if ("-1".equals(k2.toLowerCase())) {
					this.startOver();
				}
				System.out.print("Enter HMAC: ");
				final String hmac = myScanner.next();
				if ("-1".equals(hmac.toLowerCase())) {
					this.startOver();
				}

				plainText = AES128CBC.decrypt(iv, k1, k2, hmac, FileManager.readFile(encryptedFile));
			} catch (final IllegalArgumentException ex) {
				System.out.println("Error: Input mismatch. Please try again or enter '-1' to abort.\n");
				plainText = null;
			}
		}

		FileManager.writeFile(plainText, theFilename, false);
		this.startOver();
	}

}
