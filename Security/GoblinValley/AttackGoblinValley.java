
/*****************************************************************************
*	BY TURNING IN THIS FILE, YOU ARE ASSERTING:
*		1) THAT ADDITIONS TO THIS CODE ARE YOUR ORGINAL, INDIVIDUAL WORK
*		2) THAT YOU HAVE NOT AND WILL NOT SHARE OR POST YOUR SOLUTION
*		3) THAT YOU ARE COMPLYING WITH THE STATED ACADEMIC MISCONDUCT POLICIES FOR THE COURSE, THE SCHOOL OF COMPUTING, AND THE COLLEGE OF ENGINEERING.
*****************************************************************************/

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.InputStream;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import java.security.Key;
import java.security.Security;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

import java.math.BigInteger;

public class AttackGoblinValley {
	static BigInteger g = new BigInteger(
			"129115595377796797872260754286990587373919932143310995152019820961988539107450691898237693336192317366206087177510922095217647062219921553183876476232430921888985287191036474977937325461650715797148343570627272553218190796724095304058885497484176448065844273193302032730583977829212948191249234100369155852168");
	static BigInteger p = new BigInteger(
			"165599299559711461271372014575825561168377583182463070194199862059444967049140626852928438236366187571526887969259319366449971919367665844413099962594758448603310339244779450534926105586093307455534702963575018551055314397497631095446414992955062052587163874172731570053362641344616087601787442281135614434639");

	public static void main(String[] args) throws Exception {
		String serverIP;
		int serverPortNum;
		int listenPortNum;
		String outFile;

		System.out.println("\n\n************************************************");
		System.out.println("************************************************\n\n");

		if (args.length == 4) {
			serverIP = args[0];
			serverPortNum = Integer.parseInt(args[1]);
			listenPortNum = Integer.parseInt(args[2]);
			outFile = args[3];
			System.out.println("****** Starting attack ******");
			byte[] decryptedSuperSecretData = attack(serverIP, serverPortNum, listenPortNum);
			writeToFileWrapper(decryptedSuperSecretData, outFile);
		} else {
			System.out.println("Usage: ");
			System.out.println(
					"\tAttackGoblinValley <IP address of server to attack> <port # of server to attack> <port number to listen on> <destination file for decrypted SuperSecretData>");
		}

		System.out.println("\n\n************************************************");
		System.out.println("************************************************\n\n");
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

	// Code adapted from
	// https://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l/2197650#2197650
	private static String byteArrayToStringOfHex(byte[] bytes) {
		StringBuffer stringBufferOfHex = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if (i % 16 == 0) {
				stringBufferOfHex.append('\n');
			}
			// For explanation of &FF, see
			// https://stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				stringBufferOfHex.append('0');
			}
			stringBufferOfHex.append(hex);
			stringBufferOfHex.append(' ');
		}
		return stringBufferOfHex.toString();
	}

	private static Key calculateSessionKeyUsingDH(PublicKey receivedValue, PrivateKey secretExponent, int keyLength) {
		try {
			KeyAgreement keyAgree = KeyAgreement.getInstance("DiffieHellman");
			keyAgree.init(secretExponent);
			keyAgree.doPhase(receivedValue, true);
			byte[] dhCalculationResult = keyAgree.generateSecret();
			byte[] selectedBytesForSessionKey = new byte[keyLength];
			System.arraycopy(dhCalculationResult, 0, selectedBytesForSessionKey, 0, keyLength);
			Key key = new SecretKeySpec(selectedBytesForSessionKey, "AES");
			return key;
		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	private static byte[] attack(String serverIP, int serverPortNum, int listenPortNum) 
	{
		try {
			System.out.println("****** Launching fake server ******\n");
			// Listen for a connection from a client
			ServerSocket listener = new ServerSocket(listenPortNum);
			System.out.println("** Listening for client connection on port " + listenPortNum + " **\n");
			Socket clientConnection = listener.accept();
			System.out.println("Found Client!");
			DataOutputStream clientout = new DataOutputStream(clientConnection.getOutputStream());
			DataInputStream clientin = new DataInputStream(clientConnection.getInputStream());
			
			// Connects to the server
			System.out.println("** Connecting to server on port " + serverPortNum + " **\n");
			Socket serverConnection = new Socket(serverIP, serverPortNum);
			System.out.println("Found Server!");
			DataOutputStream serverout = new DataOutputStream(serverConnection.getOutputStream());
			DataInputStream serverin = new DataInputStream(serverConnection.getInputStream());

			System.out.println("Attempting to receive and send messages.");
			
			// Creates valid exponent m and calculates g^m mod p			
			DHParameterSpec dhSpec = new DHParameterSpec(p, g);
			KeyPairGenerator diffieHellmanGen = KeyPairGenerator.getInstance("DiffieHellman");
			diffieHellmanGen.initialize(dhSpec);
			KeyPair serverPair = diffieHellmanGen.generateKeyPair();
			PrivateKey m = serverPair.getPrivate();
			PublicKey gToTheM = serverPair.getPublic();
			
			
			// C -> A: g^x mod p || A -> S g^m mod p
			byte[] message1 = new byte[clientin.readInt()];
			clientin.readFully(message1);
			System.out.println("Received message1!");
			serverout.writeInt(gToTheM.getEncoded().length);
			serverout.write(gToTheM.getEncoded());
			serverout.flush();
			System.out.println("Sent message1!");
			
			// S -> A: g^y mod p || A -> C g^y mod p
			byte[] message2 = new byte[serverin.readInt()];
			serverin.readFully(message2);
			System.out.println("Received message2!");
			clientout.writeInt(message2.length);
			clientout.write(message2);
			clientout.flush();
			System.out.println("Sent message2!");


			// C -> A -> S: Sign_C(g^y mod p)
			byte[] message3 = new byte[512];
			clientin.readFully(message3);
			System.out.println("Received message3!");
			serverout.write(message3);
			serverout.flush();
			System.out.println("Sent message3!");

			
			// S -> A -> C: Sign_S(g^m mod p) || E(g^my mod p, SuperSecretData)
			byte[] message4 = new byte[serverin.readInt()];
			serverin.readFully(message4);
			System.out.println("Received message4!");
			clientout.writeInt(message4.length);
			clientout.write(message4);
			clientout.flush();
			System.out.println("Sent message4!");

			
			System.out.println("Sent and received all messages!");
			
			// Decrypts the sent cipher text
			X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(message2);
			KeyFactory keyfactoryDH = KeyFactory.getInstance("DH");
			PublicKey gToTheY = keyfactoryDH.generatePublic(x509Spec);
			Key aesSessionKey = calculateSessionKeyUsingDH(gToTheY, m, 32);

			byte iv[] = new byte[12];
			byte cipherText[] = new byte[message4.length-524];
			System.arraycopy(message4, 512, iv, 0, 12);
			System.arraycopy(message4, 524, cipherText, 0, message4.length - 524);

			GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
			Cipher encAESsessionCipher = Cipher.getInstance("AES/GCM/NoPadding");
			encAESsessionCipher.init(Cipher.DECRYPT_MODE, aesSessionKey, gcmSpec);
			byte[] decryptedSecret = encAESsessionCipher.doFinal(cipherText);
			return decryptedSecret;
		} catch(Exception e) {
			System.out.println("Oh no! " + e);
		}
		return "Failure".getBytes();
	}
}
