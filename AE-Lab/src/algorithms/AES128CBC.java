package algorithms;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import applications.AuthEncryption;
import utilities.DataConverter;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class AES128CBC {

	// AES-128-CBC parameters
	private static final int AES_KEY_SIZE = 128; // in bits
	private static final int IV_LENGTH = 16; // in bytes

	private AES128CBC() {
	}

	public static byte[] encrypt(final byte[] thePlaintext) {
		try {
			final SecureRandom random = SecureRandom.getInstanceStrong();
			final KeyGenerator keyGen = KeyGenerator.getInstance("AES");

			final byte[] iv = new byte[IV_LENGTH];
			random.nextBytes(iv);
			final IvParameterSpec ivSpec = new IvParameterSpec(iv);
			System.out.println("IV: " + DataConverter.bytesToHex(iv));

			keyGen.init(AES_KEY_SIZE, random);
			final SecretKey k1 = keyGen.generateKey();
			final SecretKeySpec keySpec = new SecretKeySpec(k1.getEncoded(), "AES");
			System.out.println("K1: " + DataConverter.bytesToHex(k1.getEncoded()));

			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

			final long startTime = System.nanoTime();
			final byte[] cipherText = Base64.getEncoder().encode(cipher.doFinal(thePlaintext));
			final long endTime = System.nanoTime();

			final SecretKey k2 = keyGen.generateKey();
			System.out.println("K2: " + DataConverter.bytesToHex(k2.getEncoded()));

			System.out.println("HMAC-SHA256: " + getHMACSHA256(k2, cipherText));
			System.out.println("Time taken for encryption: " + DataConverter.nanoToMilli(startTime - endTime) + " ms");

			return cipherText;
		} catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}

	public static byte[] decrypt(final String theIV, final String theK1, final String theK2, final String theHMAC,
			final byte[] theCiphertext) {
		try {
			final SecretKey k1 = DataConverter.hexToSecretKey(theK1);
			final SecretKey k2 = DataConverter.hexToSecretKey(theK2);

			final String calculateHMAC = getHMACSHA256(k2, theCiphertext);
			if (!calculateHMAC.equals(theHMAC)) {
				System.out.println("Error: K2 or HMAC mismatch. Decryption process aborted.");
				new AuthEncryption().startOver();
			}

			final byte[] iv = DataConverter.hexToBytes(theIV);
			final IvParameterSpec ivSpec = new IvParameterSpec(iv);
			final SecretKeySpec keySpec = new SecretKeySpec(k1.getEncoded(), "AES");

			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			final long startTime = System.nanoTime();
			final byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(theCiphertext));
			final long endTime = System.nanoTime();

			System.out.println("Time taken for decryption: " + DataConverter.nanoToMilli(startTime - endTime) + " ms");

			return plainText;
		} catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
			System.out.println("Error: K1 mismatch. Please try again or enter '-1' to abort.\n");
			return null;
		}
	}

	public static String getHMACSHA256(final SecretKey theKey, final byte[] theCiphertext) {
		try {
			final Mac mac = Mac.getInstance("HMACSHA256");
			mac.init(theKey);
			return DataConverter.bytesToHex(mac.doFinal(theCiphertext));
		} catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
			return null;
		}
	}

}
