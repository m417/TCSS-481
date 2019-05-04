package utilities;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class DataConverter {

	private static final int NANO_PER_MILLI = 1000000;

	private static final char[] MY_HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	private DataConverter() {
	}

	private static byte hexToByte(final String theHexString) {
		final int firstDigit = toDigit(theHexString.charAt(0));
		final int secondDigit = toDigit(theHexString.charAt(1));
		return (byte) ((firstDigit << 4) + secondDigit);
	}

	private static int toDigit(final char theHexChar) {
		final int digit = Character.digit(theHexChar, 16);
		if (digit == -1) {
			throw new IllegalArgumentException();
		}
		return digit;
	}

	public static byte[] hexToBytes(final String theHexString) {
		if (theHexString.length() % 2 == 1) {
			throw new IllegalArgumentException();
		}

		final byte[] bytes = new byte[theHexString.length() / 2];
		for (int i = 0; i < theHexString.length(); i += 2) {
			bytes[i / 2] = hexToByte(theHexString.substring(i, i + 2));
		}
		return bytes;
	}

	public static String bytesToHex(final byte[] theBytes) {
		final char[] hexChars = new char[theBytes.length * 2];
		for (int j = 0; j < theBytes.length; j++) {
			int v = theBytes[j] & 0xFF;
			hexChars[j * 2] = MY_HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = MY_HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static SecretKey hexToSecretKey(final String theHexString) {
		return new SecretKeySpec(hexToBytes(theHexString), "AES");
	}

	public static int nanoToMilli(final long theNano) {
		return (int) Math.abs(theNano / NANO_PER_MILLI);
	}

}
