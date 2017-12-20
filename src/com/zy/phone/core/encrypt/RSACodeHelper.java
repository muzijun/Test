package com.zy.phone.core.encrypt;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import javax.crypto.Cipher;

import com.erm.integralwall.core.encrypt.RSACodeHelper;

import android.util.Base64;

/**
 * 加密类
 * 作者：liemng on 2017/3/31
 * 邮箱：859686819@qq.com
 *
 */
public class RSACodeHelper {
	
	private static final String TAG = RSACodeHelper.class.getSimpleName();
	
	private static final String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoXbk+id3B5ko6/NrNPCE6Cs9h"
													+"+GETvxGp+jSUWXPARXQhXYbeJzo2w8dwFDrnkNHpFare/ad+VcL3eEWQC9wdF9oA"
													+"kHmZeJN7l8D1swhEZTuZ0cyXBMtzcm92K2NJUYjWdssw+GqhfcI7uhTGMQ4bzYxZ"
													+"ZJE/WoT3siwTMlQgiQIDAQAB";
	
	private static final String RSA_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKhduT6J3cHmSjr8"+
													"2s08IToKz2H4YRO/Ean6NJRZc8BFdCFdht4nOjbDx3AUOueQ0ekVqt79p35Vwvd4"+
													"RZAL3B0X2gCQeZl4k3uXwPWzCERlO5nRzJcEy3Nyb3YrY0lRiNZ2yzD4aqF9wju6"+
													"FMYxDhvNjFlkkT9ahPeyLBMyVCCJAgMBAAECgYAlHqzFxf2XT1+US8UttJEay+FX"+
													"FzzH7rtFP32yK1qizG1DIeynM/zYsCdbZYprHXm6KuXGTiRMbToT93dWv9aiU+Ch"+
													"juqG8ZP/8BUrHL3co11ODFKl2r3UqGfMjV4Zs5VMujNatqqsg9znOJXKKJWexOHR"+
													"KT/Rz8N/+tzwp9OSVQJBANE6yKWUb8IepC0LROoENOx8BrRzP9xwsCXBLf2dSiG+"+
													"Vf8iOb0m3pRqSQ+SuRRoHr3trIEndZy8tnq7YSy17QcCQQDOAIPhZacUblR7rmMz"+
													"sX1+Rvbjb5cp/XklzO8XofnXc5wZDBQzAGDoFAR/uFeW8Dx7EOY0BzT1DiegRqkL"+
													"FrHvAkATQrcPzw9OrmVspBnu9P4uOuifGfDqCRGeGB90tlMJsizWdL8d84MP5Izf"+
													"OyGAiGk0ELoNlaVVDWY6/B7g78pnAkAljYx9C1Xg8JfwEAM/iiyRV4hsP4xz+CQc"+
													"kvZG/Z0Y/JdJLZ2FCp2f0P5c9hLpjhPIb3U3qNSrk4//tMHeJp37AkAk7LsWlmDq"+
													"In3ngpD/U/CuIYMwhvmWUB0cb7ztfm/1pi2vOVN/Ay0TTJEvU7SO75l96Wo5Tfj6"+
													"DVk2ISbFxGSh";
	
	private static final String RSATYPE = "RSA/ECB/PKCS1Padding"; 
	
	/**
	 * 加密
	 * @param str
	 * @return
	 */
	public static String encrypt(String str){
		byte[] cipherText = new byte[0];
		try { 
			for (int i = 0; i < str.length(); i += 64) {
				if(i+64>str.length()){
					PublicKey publicKey = getPublicKey(); 
					Cipher cipher = Cipher.getInstance(RSATYPE); 
					byte[] plainText = str.substring(i, str.length()).trim().getBytes();
					cipher.init(Cipher.ENCRYPT_MODE, publicKey);
					byte[] enBytes = cipher.doFinal(plainText);

					cipherText = arraycat(cipherText, enBytes);
				}else {
					PublicKey publicKey = getPublicKey(); 
					Cipher cipher = Cipher.getInstance(RSATYPE); 
					byte[] plainText = str.substring(i, i+64).trim().getBytes(); 
					cipher.init(Cipher.ENCRYPT_MODE, publicKey);
					byte[] enBytes = cipher.doFinal(plainText);

					cipherText = arraycat(cipherText, enBytes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//加密后的数据
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
//        RSAPublicKey pubKey;
//        byte[] cipherText;
//        Cipher cipher;
//		try {
//			cipher = Cipher.getInstance(RSATYPE);          
//			pubKey = (RSAPublicKey) getPublicKey();
//			
//			cipher.init(Cipher.ENCRYPT_MODE, pubKey);  
//			cipherText = cipher.doFinal(str.getBytes());  
//			//加密后的东西  
//	        return new String(cipherText);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		return null;  
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
	
	/**
	 * 解密
	 * @param cipherText
	 * @return
	 */
	public static String decrypt(String str){
		byte[] cipherText = Base64.decode(str, Base64.DEFAULT);
		RSAPrivateKey privKey;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(RSATYPE);          
			privKey = (RSAPrivateKey) getPrivateKey();
			//开始解密  
			cipher.init(Cipher.DECRYPT_MODE, privKey);   
			byte[] plainText = cipher.doFinal(cipherText);  
			return new String(plainText);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;  
	}
	
	/**
	 *生成私钥  公钥 
	 */
	public static void generation(){
		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			SecureRandom secureRandom = new SecureRandom(new Date().toString().getBytes());  
	        keyPairGenerator.initialize(1024, secureRandom);  
	        KeyPair keyPair = keyPairGenerator.genKeyPair();  
	        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();  
	        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();  
	        /**公钥*/
	        String publicKey = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);
	        /**私钥*/
	        String privateKey = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	/**
	 * 获取公钥
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey() throws Exception {  
		byte[] keyBytes = Base64.decode(RSA_PUBLIC_KEY, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory kf = KeyFactory.getInstance("RSA");   
        return kf.generatePublic(spec);  
    }  
	
	/**
     * 获取私钥
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey()throws Exception {  
		byte[] keyBytes = Base64.decode(RSA_PRIVATE_KEY, Base64.DEFAULT);
        PKCS8EncodedKeySpec spec =new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory kf = KeyFactory.getInstance("RSA");  
        return kf.generatePrivate(spec);  
   }  
	
/*********************************************最新实现*********************************************/
    /** *//** 
     * RSA最大加密明文大小 
     */  
    private static final int MAX_ENCRYPT_BLOCK = 117;  
      
    /** *//** 
     * RSA最大解密密文大小 
     */  
    private static final int MAX_DECRYPT_BLOCK = 128;  

    /** *//** 
     * 加密算法RSA 
     */  
    public static final String KEY_ALGORITHM = "RSA";  

    
    /** *//** 
     * <P> 
     * 私钥解密 
     * </p> 
     *  
     * @param encryptedData 已加密数据 
     * @param privateKey 私钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptByPrivateKey(byte[] encryptedData)  
            throws Exception {  
        byte[] keyBytes = decode(RSA_PRIVATE_KEY);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance(RSATYPE);  
        cipher.init(Cipher.DECRYPT_MODE, privateK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
    
    /** *//** 
     * <p> 
     * 公钥加密 
     * </p> 
     *  
     * @param data 源数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptByPublicKey(byte[] data)  
            throws Exception {  
        byte[] keyBytes = decode(RSA_PUBLIC_KEY);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(RSATYPE);  
        cipher.init(Cipher.ENCRYPT_MODE, publicK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    }  
 
    /** *//** 
     * <p> 
     * BASE64字符串解码为二进制数据 
     * </p> 
     *  
     * @param base64 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decode(String base64) throws Exception {  
        return Base64.decode(base64, Base64.DEFAULT);  
    }  
    
    /** *//** 
     * <p> 
     * 二进制数据编码为BASE64字符串 
     * </p> 
     *  
     * @param bytes 
     * @return 
     * @throws Exception 
     */  
    public static String encode(byte[] bytes) throws Exception {  
        return Base64.encodeToString(bytes, Base64.DEFAULT);  
    }  

}