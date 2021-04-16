// Implements the server side of:
// 		C -> S: g^x mod p
// 		S -> C: g^y mod p
// 		C -> S: Sign_C(g^y mod p)
// 		S -> C: Sign_S(g^x mod p) || E(g^xy mod p, SuperSecretData)

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import java.math.BigInteger;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;

public class GoblinValleyServer {

	// Diffie-Hellman g and p values
	//
	// OKish to hardcode in if p is big enough, but a problem if a bunch of servers
	// use the same g and p (because, as described in lecture, that means it
	// becomes worth it to pre-compute and store g^y mod p for a lot of values of
	// y in a lookup table)
	static BigInteger g = new BigInteger("129115595377796797872260754286990587373919932143310995152019820961988539107450691898237693336192317366206087177510922095217647062219921553183876476232430921888985287191036474977937325461650715797148343570627272553218190796724095304058885497484176448065844273193302032730583977829212948191249234100369155852168");
	static BigInteger p = new BigInteger("165599299559711461271372014575825561168377583182463070194199862059444967049140626852928438236366187571526887969259319366449971919367665844413099962594758448603310339244779450534926105586093307455534702963575018551055314397497631095446414992955062052587163874172731570053362641344616087601787442281135614434639");

	static int portNum;
	static byte[] clientPubKey;
	static byte[] serverPrivKey;
	static byte[] superSecretData;

	public static void main(String[] args) {
		System.out.println("\n\n************************************************");
		System.out.println("************************************************\n\n");

		if (args.length==4) {
			System.out.println("****** Using command line port ******\n");
			portNum = Integer.parseInt(args[0]);
			System.out.println("port: " + portNum + "\n");
			System.out.println("****** Using command line client public key ******\n");
			clientPubKey = readFromFileWrapper(args[1]);
			System.out.println("****** Using command line server private key ******\n");
			serverPrivKey = readFromFileWrapper(args[2]);
			System.out.println("****** Using command line SuperSecretData ******\n");
			superSecretData = readFromFileWrapper(args[3]);
			try {
				System.out.println("****** Launching server ******\n");
				// Listen for connections from clients
				ServerSocket listener = new ServerSocket(portNum);
				ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
				System.out.println("** Listening for connections on port " + portNum + " **\n");
				while (true) {
					// For each connection, spin off a new protocol instance
					Socket connection = listener.accept();
					threadPool.execute(new ProtocolInstance(connection));
				}
			} catch(Exception e) {
				System.out.println("Oh no! " + e);
			}
		} else {
			System.out.println("Usage: ");
			System.out.println("\tGoblinValleyServer <port #> <filename of client public key> <filename of server private key> <filename of SuperSecretData>");
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

  //  Code adapted from https://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l/2197650#2197650
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

	// From https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
	private static byte[] stringOfHexToByteArray(String s) {
		// s must be an even-length string.
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
			+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
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

	private static boolean isSignatureValid(byte[] encodedPublicKey, byte[] content, byte[] signatureToVerify) {
		try {
			// Obviously client and server need to know what algorithms they're using for
			// signing so that they can use the same ones (but it's not a big secret
			// from the attacker)
			Signature verifyObject = Signature.getInstance("SHA256withRSA");
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey pubRSAKey = kf.generatePublic(new X509EncodedKeySpec(encodedPublicKey));
			verifyObject.initVerify(pubRSAKey);
			verifyObject.update(content);
			return verifyObject.verify(signatureToVerify);
		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return false;
		}
	}

	private static byte[] signMessage(byte[] encodedPrivKey, byte[] message) {
		try {
			// Obviously client and server need to know what algorithms they're using for
			// signing so that they can use the same ones (but it's not a big secret
			// from the attacker)
			Signature signObject = Signature.getInstance("SHA256withRSA");
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privRSAKey = kf.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivKey));
			signObject.initSign(privRSAKey);
			signObject.update(message);
			return signObject.sign();
		} catch (Exception e) {
			System.out.println("Oh no! " + e);
			return null;
		}
	}

	// Implements one instance of the protocol
	private static class ProtocolInstance implements Runnable {
		Socket socket;
		PrivateKey y;
		PublicKey gToTheY;
		PublicKey gToTheX;
		Key aesSessionKey;

		public ProtocolInstance(Socket connection) {
			this.socket = connection;
		}

		public void run() {
			DataOutputStream out;
			DataInputStream in;

			try {
				System.out.println("\n\tStarting protocol instance...");
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());

				// C -> S: g^x mod p
				//
				// As the length of the incoming message varies, we read the length as
				// an int first. Then we read in the message (length given by the
				// previsouly read int)
				byte[] message1 = new byte[in.readInt()];
				in.readFully(message1);

				setRandomYandCalculateGToTheY();

				// S -> C: g^y mod p
				//
				// First send the length of the message we'll be sending
				out.writeInt(gToTheY.getEncoded().length);
				out.write(gToTheY.getEncoded());
				out.flush();

				// C -> S: Sign_C(g^y mod p)
				byte[] message3 = new byte[512];
				in.readFully(message3);

				if(isSignatureValid(clientPubKey, gToTheY.getEncoded(), message3) == true) {
					System.out.println("\t\tClient signature valid.\n");

					// S -> C:  Sign_S(g^x mod p) || E(g^xy mod p, SuperSecretData)
					byte[] message4 = new byte[524 + 16 + superSecretData.length];

					System.arraycopy(signMessage(serverPrivKey, message1), 0, message4, 0, 512);

					X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(message1);
					KeyFactory keyfactoryDH = KeyFactory.getInstance("DH");
					gToTheX = keyfactoryDH.generatePublic(x509Spec);
					aesSessionKey = calculateSessionKeyUsingDH(gToTheX, y, 32);

					SecureRandom random = new SecureRandom();
					byte iv[] = new byte[12];
					random.nextBytes(iv);
					GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
					Cipher encAESsessionCipher = Cipher.getInstance("AES/GCM/NoPadding");
					encAESsessionCipher.init(Cipher.ENCRYPT_MODE, aesSessionKey, gcmSpec);
					byte[] ciphertext = encAESsessionCipher.doFinal(superSecretData);

					System.arraycopy(encAESsessionCipher.getIV(), 0, message4, 512, 12);
					System.arraycopy(ciphertext, 0, message4, 524, ciphertext.length);

					out.writeInt(message4.length);
					out.write(message4);
					out.flush();
					System.out.println("\t\tSuperSecretData sent.\n");
				} else {
					System.out.println("\t\tClient signature not valid! Attack detected.\n");
				}
				socket.close();
				System.out.println("\tConnection closed.\n");
			} catch (Exception e) {
				System.out.println("Oh no! " + e);
			}
		}

		private void setRandomYandCalculateGToTheY() {
			try {
				DHParameterSpec dhSpec = new DHParameterSpec(p, g);
				KeyPairGenerator diffieHellmanGen = KeyPairGenerator.getInstance("DiffieHellman");
				diffieHellmanGen.initialize(dhSpec);
				KeyPair serverPair = diffieHellmanGen.generateKeyPair();
				y = serverPair.getPrivate();
				gToTheY = serverPair.getPublic();
			} catch (Exception e) {
				System.out.println("Oh no! " + e);
			}
		}
	}
}
