package com.zy.phone;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.util.Base64;
/**
 * º”√‹¿‡
 * @author lws
 *
 */
public class RSACodeHelper {
	private static final String RSA_PUBLIC =
			"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQCIOdh8zuJqT/XNfQnwQqq/dA\n"
			+ "Z5RJSuEyhzi6vv3TRBrPMWglQEyAzLH8gJBRkJx+cWKHVUz0bfjg4d7iC9TAhqSd\n"
			+ "JGYHjrhF6RU1iwjC6kSsI2IHIH2JiK+FdAuAyUjnqkLS8HOuw1M3Bd5fluNlKlHb\n"
			+ "Zo6tdNOi33qHynmL1wIDAQAB";
	private static final String TAG = "RSACodeHelper";
	private static final String RSATYPE = "RSA/ECB/PKCS1Padding"; 
	public PublicKey mPublicKey; 
									
	public PrivateKey mPrivateKey; 

	public void init() {
		KeyPairGenerator keyPairGen = null;
		try { 
			keyPairGen = KeyPairGenerator.getInstance("RSA"); 
			keyPairGen.initialize(1024); 
			KeyPair keyPair = keyPairGen.generateKeyPair(); 
			mPublicKey = keyPair.getPublic(); 
			mPrivateKey = keyPair.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes = base64Dec(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	public static PrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes = base64Dec(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	public String cPubEncrypt(String str) {
		String strEncrypt = null; 
		try {
			Cipher cipher = Cipher.getInstance(RSATYPE); 
			byte[] plainText = str.getBytes(); 
			cipher.init(Cipher.ENCRYPT_MODE, mPublicKey); 
			byte[] enBytes = cipher.doFinal(plainText);
			strEncrypt = base64Enc(enBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} finally {
			return strEncrypt;
		}
	}

	public static String sPubEncrypt(String str) {

		byte[] strEncrypt = new byte[0];
		try { 
			int K = str.length() % 64;	
			int Y = str.length() / 64;
			for (int i = 0; i < str.length(); i += 64) {
				if(i+64>str.length()){
					PublicKey publicKey = getPublicKey(RSA_PUBLIC); 
					Cipher cipher = Cipher.getInstance(RSATYPE); 
					byte[] plainText = str.substring(i, str.length()).trim().getBytes();
					cipher.init(Cipher.ENCRYPT_MODE, publicKey);
					byte[] enBytes = cipher.doFinal(plainText);

					strEncrypt = arraycat(strEncrypt, enBytes);
				}else {
					PublicKey publicKey = getPublicKey(RSA_PUBLIC); 
					Cipher cipher = Cipher.getInstance(RSATYPE); 
					byte[] plainText = str.substring(i, i+64).trim().getBytes(); 
					cipher.init(Cipher.ENCRYPT_MODE, publicKey);
					byte[] enBytes = cipher.doFinal(plainText);

					strEncrypt = arraycat(strEncrypt, enBytes);
				}
				
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			return base64Enc(strEncrypt);

		}
	}

	private static byte[] arraycat(byte[] buf1, byte[] buf2) {
		byte[] bufret = null;
		int len1 = 0;
		int len2 = 0;
		if (buf1 != null)
			len1 = buf1.length;
		if (buf2 != null)
			len2 = buf2.length;
		if (len1 + len2 > 0)
			bufret = new byte[len1 + len2];
		if (len1 > 0)
			System.arraycopy(buf1, 0, bufret, 0, len1);
		if (len2 > 0)
			System.arraycopy(buf2, 0, bufret, len1, len2);
		return bufret;
	}

	public String cPubDecrypt(String encString) {
		Cipher cipher = null;
		String strDecrypt = null;
		try {
			cipher = Cipher.getInstance(RSATYPE);
			cipher.init(Cipher.DECRYPT_MODE, mPublicKey); 
			byte[] enBytes = base64Dec(encString); 
			byte[] deBytes = cipher.doFinal(enBytes);
			strDecrypt = new String(deBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} finally {
			return strDecrypt;
		}
	}

	public String cPriDecrypt(String encString) {
		Cipher cipher = null;
		String strDecrypt = null;
		try {
			cipher = Cipher.getInstance(RSATYPE);
			cipher.init(Cipher.DECRYPT_MODE, mPrivateKey); 
			byte[] enBytes = base64Dec(encString); 
			byte[] deBytes = cipher.doFinal(enBytes);
			strDecrypt = new String(deBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} finally {
			return strDecrypt;
		}
	}

	public String cPriEncrypt(String deString) {
		String strEncrypt = null; 
		try {
			Cipher cipher = Cipher.getInstance(RSATYPE); 
			byte[] plainText = deString.getBytes(); 
			cipher.init(Cipher.ENCRYPT_MODE, mPrivateKey); 
			byte[] enBytes = cipher.doFinal(plainText);
			strEncrypt = base64Enc(enBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} finally {
			return strEncrypt;
		}
	}

	public static String base64Enc(byte[] enBytes) {
		return Base64.encodeToString(enBytes, Base64.DEFAULT);
	}

	public static byte[] base64Dec(String str) {
		return Base64.decode(str, Base64.DEFAULT);
	}
}