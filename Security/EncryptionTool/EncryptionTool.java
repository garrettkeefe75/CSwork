/*****************************************************************************/
// BY TURNING IN THIS FILE, YOU ARE ASSERTING:
// 		1) THAT ADDITIONS TO THIS CODE ARE YOUR ORGINAL, INDIVIDUAL WORK
// 		2) THAT YOU HAVE NOT AND WILL NOT SHARE OR POST YOUR SOLUTION
// 		3) THAT YOU ARE COMPLYING WITH THE STATED ACADEMIC MISCONDUCT
// 				POLICIES FOR THE COURSE, THE SCHOOL OF COMPUTING, AND THE COLLEGE OF
// 				ENGINEERING.
/*****************************************************************************/

// Interesting Trivia:
//
// 		For historical (export control) reasons, the cryptography APIs are organized
// 		into two distinct packages:
//
//			* The java.security and java.security.* packages contain classes that are not
// 					subject to export controls (like Signature and MessageDigest)
//
// 			* The javax.crypto package contains classes that are subject to export controls
// 					(like Cipher and KeyAgreement)
//
// 		See https://docs.oracle.com/en/java/javase/11/security/java-security-overview1.html

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.SecretKey;

import java.util.Arrays;

public class EncryptionTool
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("\n\n************************************************");
		System.out.println("************************************************\n\n");

		if (args.length==4 && args[0].equals("-encAESCTR")) {
			System.out.println("****** Key ******");
			byte[] encodedKey = readFromFileWrapper(args[1]);
			System.out.println("\n****** Plaintext ******");
			byte[] plaintext = readFromFileWrapper(args[2]);
			System.out.println("\n****** Encrypting with AES-CTR ******");
			byte[] retVal = encryptAESCTR(encodedKey, plaintext);
			writeToFileWrapper(retVal, args[3]);
		}

		else if (args.length==4 && args[0].equals("-decAESCTR")) {
			System.out.println("****** Key ******");
			byte[] encodedKey = readFromFileWrapper(args[1]);
			System.out.println("\n****** Ciphertext ******");
			byte[] ciphertext = readFromFileWrapper(args[2]);
			System.out.println("\n****** Decrypting with AES-CTR ******");
			byte[] retVal = decryptAESCTR(encodedKey, ciphertext);
			writeToFileWrapper(retVal, args[3]);
		}

		else if (args.length==4 && args[0].equals("-encAESCBC")) {
			System.out.println("****** Key ******");
			byte[] encodedKey = readFromFileWrapper(args[1]);
			System.out.println("\n****** Plaintext ******");
			byte[] plaintext = readFromFileWrapper(args[2]);
			System.out.println("\n****** Encrypting with AES-CBC ******");
			byte[] retVal = encryptAESCBC(encodedKey, plaintext);
			writeToFileWrapper(retVal, args[3]);
		}

		else if (args.length==4 && args[0].equals("-decAESCBC")) {
			System.out.println("****** Key ******");
			byte[] encodedKey = readFromFileWrapper(args[1]);
			System.out.println("\n****** Ciphertext ******");
			byte[] ciphertext = readFromFileWrapper(args[2]);
			System.out.println("\n****** Decrypting with AES-CBC ******");
			byte[] retVal = decryptAESCBC(encodedKey, ciphertext);
			writeToFileWrapper(retVal, args[3]);
		}

		else if (args.length==4 && args[0].equals("-encAESGCM")) {
			System.out.println("****** Key ******");
			byte[] encodedKey = readFromFileWrapper(args[1]);
			System.out.println("\n****** Plaintext ******");
			byte[] plaintext = readFromFileWrapper(args[2]);
			System.out.println("\n****** Encrypting with AES-GCM ******");
			byte[] retVal = encryptAESGCM(encodedKey, plaintext);
			writeToFileWrapper(retVal, args[3]);
		}

		else if (args.length==4 && args[0].equals("-decAESGCM")) {
			System.out.println("****** Key ******");
			byte[] encodedKey = readFromFileWrapper(args[1]);
			System.out.println("\n****** Ciphertext ******");
			byte[] ciphertext = readFromFileWrapper(args[2]);
			System.out.println("\n****** Decrypting with AES-GCM ******");
			byte[] retVal = decryptAESGCM(encodedKey, ciphertext);
			writeToFileWrapper(retVal, args[3]);
		}

		else if (args.length==4 && args[0].equals("-encHybridRSA") ) {
			System.out.println("****** Key ******");
			byte[] encodedKey = readFromFileWrapper(args[1]);
			System.out.println("\n****** Plaintext ******");
			byte[] plaintext = readFromFileWrapper(args[2]);
			System.out.println("\n****** Encrypting with RSA ******");
			byte[] retVal = encryptHybridRSA(encodedKey, plaintext);
			writeToFileWrapper(retVal, args[3]);
		}

		else if (args.length==4 && args[0].equals("-decHybridRSA") ) {
			System.out.println("****** Key ******");
			byte[] encodedKey = readFromFileWrapper(args[1]);
			System.out.println("\n****** Ciphertext ******");
			byte[] ciphertext = readFromFileWrapper(args[2]);
			System.out.println("\n****** Decrypting with RSA ******");
			byte[] retVal = decryptHybridRSA(encodedKey, ciphertext);
			writeToFileWrapper(retVal, args[3]);
		}

		else if (args.length==2 && args[0].equals("-genAESKey")) {
			System.out.println("****** Generating 256-bit AES key ******\n");
			writeToFileWrapper(generateAESKey(256), args[1]);
		}

		else if (args.length==3 && args[0].equals("-genRSAKeyPair")) {
			System.out.println("****** Generating 4096-bit RSA keypair ******\n");
			byte[][] keyPair = generateRSAKeyPair(4096);
			System.out.println("\n\n\n****** Public ******");
			writeToFileWrapper(keyPair[0], args[1]);
			System.out.println("\n****** Private ******");
			writeToFileWrapper(keyPair[1], args[2]);
		}

		else {
			System.out.println("This is a simple program to encrypt and decrypt files. Command line usage options:\n");
			System.out.println("\tAES CTR mode encrypt");
			System.out.println("\t\t-encAESCTR <key filename> <plaintext filename> <ciphertext filename>\n");
			System.out.println("\tAES CTR mode decrypt");
			System.out.println("\t\t-decAESCTR <key filename> <ciphertext filename> <plaintext filename>\n");
			System.out.println("\tAES CBC mode encrypt");
			System.out.println("\t\t-encAESCBC <key filename> <plaintext filename> <ciphertext filename>\n");
			System.out.println("\tAES CBC mode decrypt");
			System.out.println("\t\t-decAESCBC <key filename> <ciphertext filename> <plaintext filename>\n");
			System.out.println("\tAES GCM mode encrypt");
			System.out.println("\t\t-encAESGCM <key filename> <plaintext filename> <ciphertext filename>\n");
			System.out.println("\tAES GCM mode decrypt");
			System.out.println("\t\t-decAESGCM <key filename> <ciphertext filename> <plaintext filename>\n");
			System.out.println("\tencrypt with a hybrid RSA system");
			System.out.println("\t\t-encHybridRSA <key filename> <plaintext filename> <ciphertext filename>\n");
			System.out.println("\tdecrypt with a hybrid RSA system");
			System.out.println("\t\t-decHybridRSA <key filename> <ciphertext filename> <plaintext filename>\n");
			System.out.println("\tgenerate a 256-bit AES key");
			System.out.println("\t\t-genAESKey <output filename>\n");
			System.out.println("\tgenerate a 4096-bit RSA key pair");
			System.out.println("\t\t-genRSAKeyPair <public key output filename> <private key output filename>");
		}

		System.out.println("\n\n************************************************");
		System.out.println("************************************************\n\n");
	}

	private static byte[] readFromFileWrapper(String filename) {
		System.out.println("\n** Reading from file (" + filename + ") **\n");
		try {
			RandomAccessFile rawDataFromFile = new RandomAccessFile(filename, "r");
			byte[] contents = new byte[(int)rawDataFromFile.length()];
			rawDataFromFile.read(contents);
			rawDataFromFile.close();

			System.out.println(byteArrayToStringOfHex(contents) + "\n\n");
			System.out.println("** Attempting to display bytes as text **\n");
			String s = new String(contents);
			System.out.println(s + "\n\n\n");

			return contents;
		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	private static void writeToFileWrapper(byte[] contents, String filename) {
		System.out.println("\n" + byteArrayToStringOfHex(contents) + "\n\n");
		System.out.println("** Attempting to display bytes as text **\n");
		String s = new String(contents);
		System.out.println(s + "\n");
		System.out.println("\n** Writing to file (" + filename + ") **\n\n\n");
		try {
			FileOutputStream outToFile = new FileOutputStream(filename);
			outToFile.write(contents);
			outToFile.close();
		} catch (Exception e) {
			System.out.println("Oh no! " + e);
		}
	}

	// Code adapted from https://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l/2197650#2197650
	private static String byteArrayToStringOfHex(byte[] bytes) {
		StringBuffer stringBufferOfHex = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if (i % 16 == 0) {
				stringBufferOfHex.append('\n');
			}
			// For explanation of &FF, see https://stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				stringBufferOfHex.append('0');
			}
    	stringBufferOfHex.append(hex);
			stringBufferOfHex.append(' ');
    }
		return stringBufferOfHex.toString();
	}

	private static byte[] encryptHybridRSA(byte[] encodedKey, byte[] plaintext) {
		try {
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encodedKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

			// Note: RSA DOES NOT use ECB - this is misleading
			// The Bouncy Castle Provider supports the more appropriately named "RSA/NONE/OAEPwithSHA256andMGF1Padding"
			Cipher RSACipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
			RSACipher.init(Cipher.ENCRYPT_MODE, publicKey);

			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256);
			Key AESKey = keyGen.generateKey();
			
			byte[] encryptedKey = RSACipher.doFinal(AESKey.getEncoded());
			byte[] encryptedPlaintext = encryptAESGCM(AESKey.getEncoded(), plaintext);

			byte[] output = new byte[encryptedKey.length + encryptedPlaintext.length];
			System.arraycopy(encryptedKey, 0, output, 0, encryptedKey.length);
			System.arraycopy(encryptedPlaintext, 0, output, encryptedKey.length, encryptedPlaintext.length);
			return output;
		}
		catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	public static byte[] decryptHybridRSA(byte[] encodedKey, byte[] input) {
		try {
			//prepare private key
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encodedKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(privKeySpec);

			// Note: RSA DOES NOT use ECB - this is misleading
			// The Bouncy Castle Provider supports the more appropriately named "RSA/NONE/OAEPwithSHA256andMGF1Padding"
			Cipher RSACipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
			RSACipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			//chop off the encrypted AES key from the actual message.
			byte[] encryptedKey = new byte[512];
			byte[] encryptedMessage = new byte[input.length - 512];
			System.arraycopy(input, 0, encryptedKey, 0, 512);
			System.arraycopy(input, 512, encryptedMessage, 0, encryptedMessage.length);

			//decrypt the AES key and use it to decrpyt the message.
			byte[] decryptedKey = RSACipher.doFinal(encryptedKey);
			byte[] decryptedInput = decryptAESGCM(decryptedKey, encryptedMessage);

			return decryptedInput;
		}
		catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	public static byte[] decryptAESCTR(byte[] encodedKey, byte[] input) {
		try {
			byte iv[] = new byte[16];
			//Copy the iv from the front of the cipher text
			System.arraycopy(input, 0, iv, 0, iv.length);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, "AES");
			Cipher decAESCTRcipher = Cipher.getInstance("AES/CTR/NoPadding");
			decAESCTRcipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

			//Remove the iv and resize the ciphertext
			byte[] ciphertext = new byte[input.length-iv.length];
			System.arraycopy(input, iv.length, ciphertext, 0, input.length-iv.length);

			byte[] plaintext = decAESCTRcipher.doFinal(ciphertext);

			byte[] output = new byte[plaintext.length];
			System.arraycopy(plaintext, 0, output, 0, plaintext.length);
			return output;

		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	public static byte[] decryptAESCBC(byte[] encodedKey, byte[] input) {
		try {
			byte iv[] = new byte[16];
			//Copy the iv from the front of the cipher text
			System.arraycopy(input, 0, iv, 0, iv.length);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, "AES");

			Cipher encAESCBCcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			encAESCBCcipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

			//Remove the iv and resize the ciphertext
			byte[] ciphertext = new byte[input.length-iv.length];
			System.arraycopy(input, iv.length, ciphertext, 0, input.length-iv.length);
			
			byte[] plaintext = encAESCBCcipher.doFinal(ciphertext);

			byte[] output = new byte[plaintext.length];
			System.arraycopy(plaintext, 0, output, 0, plaintext.length);
			return output;

		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	public static byte[] decryptAESGCM(byte[] encodedKey, byte[] input) {
		try {
			byte iv[] = new byte[12];
			//Copy the iv from the front of the cipher text
			System.arraycopy(input, 0, iv, 0, iv.length);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, "AES");
			Cipher encAESGCMcipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
			encAESGCMcipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);

			//Remove the iv and resize the ciphertext
			byte[] ciphertext = new byte[input.length-iv.length];
			System.arraycopy(input, iv.length, ciphertext, 0, input.length-iv.length);
			
			byte[] plaintext = encAESGCMcipher.doFinal(ciphertext);

			byte[] output = new byte[plaintext.length];
			System.arraycopy(plaintext, 0, output, 0, plaintext.length);
			return output;

		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	private static byte[] generateAESKey(int keySize) {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(keySize);

			return keyGen.generateKey().getEncoded();

		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	private static byte[][] generateRSAKeyPair(int keySize) {
		try {
			KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA");
			keyPair.initialize(keySize);

			KeyPair kp = keyPair.generateKeyPair();

			byte[][] retVal = new byte[2][];
			retVal[0] = kp.getPublic().getEncoded();
			retVal[1] = kp.getPrivate().getEncoded();
			return retVal;

		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	private static byte[] encryptAESCTR(byte[] encodedKey, byte[] plaintext) {
		try {
			SecureRandom random = new SecureRandom();
			byte iv[] = new byte[16];
			random.nextBytes(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, "AES");
			Cipher encAESCTRcipher = Cipher.getInstance("AES/CTR/NoPadding");
			encAESCTRcipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

			byte[] ciphertext = encAESCTRcipher.doFinal(plaintext);

			byte[] output = new byte[iv.length + ciphertext.length];
			System.arraycopy(iv, 0, output, 0, iv.length);
			System.arraycopy(ciphertext, 0, output, iv.length, ciphertext.length);
			return output;

		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	private static byte[] encryptAESCBC(byte[] encodedKey, byte[] plaintext) {
		try {
			SecureRandom random = new SecureRandom();
			byte iv[] = new byte[16];
			random.nextBytes(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, "AES");

			// Technically this isn't PKCS5 padding:
			//
			// "PKCS#5 padding is identical to PKCS#7 padding, except that it has only
			// been defined for block ciphers that use a 64-bit (8-byte) block size."
			// (see https://en.wikipedia.org/wiki/Padding_(cryptography)#PKCS#5_and_PKCS#7)
			//
			// For AES, the SunJCE crypto provider only allows "NOPADDING",
			// "PKCS5PADDING", or "ISO10126PADDING" for padding descriptions
			// (see https://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html#SunJCEProvider)
			//
			// The Bouncy Castle Provider supports the more appropriately named "AES/CBC/PKCS7Padding"
			Cipher encAESCBCcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			encAESCBCcipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

			byte[] ciphertext = encAESCBCcipher.doFinal(plaintext);

			byte[] output = new byte[iv.length + ciphertext.length];
			System.arraycopy(iv, 0, output, 0, iv.length);
			System.arraycopy(ciphertext, 0, output, iv.length, ciphertext.length);
			return output;

		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	private static byte[] encryptAESGCM(byte[] encodedKey, byte[] plaintext) {
		try {
			SecureRandom random = new SecureRandom();
			byte iv[] = new byte[12];
			random.nextBytes(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, "AES");
			Cipher encAESGCMcipher = Cipher.getInstance("AES/GCM/NoPadding");
			// 128 is length of tag, not length of iv
			// (see https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/crypto/spec/GCMParameterSpec.html)
			GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
			encAESGCMcipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);

			byte[] ciphertext = encAESGCMcipher.doFinal(plaintext);

			byte[] output = new byte[iv.length + ciphertext.length];
			System.arraycopy(iv, 0, output, 0, iv.length);
			System.arraycopy(ciphertext, 0, output, iv.length, ciphertext.length);
			return output;

		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}
}
