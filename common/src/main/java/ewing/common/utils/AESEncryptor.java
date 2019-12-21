package ewing.common.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * AES可逆加密算法，加密后转换成Base64编码。
 *
 * @author Ewing
 */
public class AESEncryptor {
    // 线程安全的加解密用的密钥
    private final SecretKeySpec skeySpec;
    private final IvParameterSpec ivSpec;

    public AESEncryptor(String secretKey, String ivParameter) {
        try {
            skeySpec = new SecretKeySpec(secretKey.getBytes("ASCII"), "AES");
            ivSpec = new IvParameterSpec(ivParameter.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密数据
     */
    public byte[] encrypt(byte[] source) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
            return cipher.doFinal(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密字符串
     */
    public String encryptString(String source) {
        try {
            byte[] bytes = encrypt(source.getBytes("UTF-8"));
            // 使用BASE64转码
            return Base64Utils.encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密数据
     */
    public byte[] decrypt(byte[] source) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
            return cipher.doFinal(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密字符串
     */
    public String decryptString(String source) {
        try {
            // 先用BASE64解码
            byte[] bytes = Base64Utils.decodeFromString(source);
            return new String(decrypt(bytes), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Throwable {
        // AES-128-CBC加密模式密码是16个字节的字符串
        String secretKey = "1234567890ABCDEF";
        // CBC模式使用向量增加加密算法的强度 密钥为16字节
        String ivParameter = "ABCDEF1234567890";

        AESEncryptor encryptor = new AESEncryptor(secretKey, ivParameter);

        // 需要加密的字串
        String source = "1234567890ABCDEFGHIJKLMNOPQRES";
        System.out.println("原文：" + source + "\n长度：" + source.length());

        // 加密
        String encryptStr = encryptor.encryptString(source);
        System.out.println("加密：" + encryptStr + "\n长度：" + encryptStr.length());

        // 解密
        String decryptStr = encryptor.decryptString(encryptStr);
        System.out.println("解密：" + decryptStr);
    }
} 