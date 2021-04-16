/*****************************************************************************/
// BY TURNING IN THIS FILE, YOU ARE ASSERTING:
// 		1) THAT ADDITIONS TO THIS CODE ARE YOUR ORGINAL, INDIVIDUAL WORK
// 		2) THAT YOU HAVE NOT AND WILL NOT SHARE OR POST YOUR SOLUTION
// 		3) THAT YOU ARE COMPLYING WITH THE STATED ACADEMIC MISCONDUCT
// 				POLICIES FOR THE COURSE, THE SCHOOL OF COMPUTING, AND THE COLLEGE OF
// 				ENGINEERING.
/*****************************************************************************/

import java.io.Console;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import java.util.Arrays;

public class AlterCTREncryptedFileWithoutKey
{
  public static void main(String[] args) throws Exception
  {
    String originalPlaintextFile;
    String targetPlaintextFile;
    String originalEncryptedFile;
    String alteredEncryptedFile;

    System.out.println("\n\n************************************************");
    System.out.println("************************************************\n\n");

    if (args.length==4) {
      originalPlaintextFile = args[0];
      targetPlaintextFile = args[1];
      originalEncryptedFile = args[2];
      alteredEncryptedFile = args[3];

      System.out.println("****** Original Plaintext ******");
      byte[] originalPlaintext = readFromFileWrapper(originalPlaintextFile);
      System.out.println("\n****** Plaintext You're Altering the Encrypted File to Decrypt to Instead ******");
      byte[] targetPlaintext = readFromFileWrapper(targetPlaintextFile);
      System.out.println("\n****** Original Encrypted File ******");
      byte[] originalEncrypted = readFromFileWrapper(originalEncryptedFile);
      System.out.println("\n****** Altering Encrypted File ******");
      byte[] alteredEncrypted = attack(originalPlaintext, targetPlaintext, originalEncrypted);
      writeToFileWrapper(alteredEncrypted, alteredEncryptedFile);
    } else {
      System.out.println("Usage: ");
      System.out.println("\tThe first argument should be the original plaintext file.");
      System.out.println("\tThe second argument should be the desired plaintext to which the altered ciphertext will decrypt. The files must be the same size.");
      System.out.println("\tThe third argument should be the original encrypted file.");
      System.out.println("\tThe fourth argument should be the file to which the altered encrypted file will be written.");
    }

    System.out.println("\n\n************************************************");
    System.out.println("************************************************\n\n");
  }

  // Same as from EncryptionTool.java
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

  // Same as from EncryptionTool.java
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

  // Same as from EncryptionTool.java
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

  private static byte[] attack(byte[] originalPlaintext, byte[] targetPlaintext, byte[] dataToAlter) {
    try 
    {
    	byte[] ciphertext = new byte[originalPlaintext.length];
    	System.arraycopy(dataToAlter, 16, ciphertext, 0, ciphertext.length);
    	    	
    	for(int i = 0; i < ciphertext.length; i++)
    	{
    		dataToAlter[16+i] = (byte)(targetPlaintext[i] ^ (ciphertext[i] ^ originalPlaintext[i]));
    	}
    	
    	return dataToAlter;
    } 
    catch (Exception e) 
    {
      System.out.println("Oh no! " + e);
      return null;
    }
  }
}
