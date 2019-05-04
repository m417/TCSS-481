package utilities;

import java.nio.file.Path;

public final class TerminalMsg {

	private TerminalMsg() {
	}

	public static void promptIntroduction() {
		System.out.println(">>>>");
		System.out.println("Welcome to our cryptosystem!\n");
	}

	public static void promptOptions() {
		System.out.println("Here are your options:");
		System.out.println("\t1) Encrypt a file");
		System.out.println("\t2) Decrypt a file");
		System.out.println("\t3) Exit");
		promptChoice();
	}

	public static void promptChoice() {
		System.out.print("\nPlease enter a valid option (invalid options are ignored): ");
	}

	public static void promptGoodBye() {
		System.out.println("\nThanks for using our cryptosystem!");
		System.out.println("Terminating program....");
		System.out.println(">>>>");
	}

	public static void promptEncrypt(final Path thePath) {
		System.out.println("\n----------------------");
		System.out.println("AES-128-CBC Encryption");
		System.out.println("File: " + thePath);
		System.out.println("----------------------");
	}

	public static void promptDecrypt(final Path thePath) {
		System.out.println("\n----------------------");
		System.out.println("AES-128-CBC Decryption");
		System.out.println("File: " + thePath);
		System.out.println("----------------------");
	}

}
