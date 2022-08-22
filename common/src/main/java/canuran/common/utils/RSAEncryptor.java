package canuran.common.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Objects;

/**
 * RSA加解密。
 *
 * @author canuran
 */
public class RSAEncryptor {

    // 加密规则
    private static final String ALGORITHM = "RSA";

    // RSA加密类型
    private static final String PADDING = "RSA/NONE/NoPadding";

    // 加密提供者
    private static final String PROVIDER = "BC";

    // 加密公钥
    private final RSAPublicKey publicKey;

    // 加密私钥
    private final RSAPrivateKey privateKey;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public RSAEncryptor() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
            keyPairGen.initialize(256, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            publicKey = (RSAPublicKey) keyPair.getPublic();
            privateKey = (RSAPrivateKey) keyPair.getPrivate();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new IllegalStateException("初始化加密工具失败！");
        }
    }

    public RSAEncryptor(RSAPublicKey publicKey) {
        this(publicKey, null);
    }

    public RSAEncryptor(RSAPrivateKey privateKey) {
        this(null, privateKey);
    }

    public RSAEncryptor(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        if (publicKey == null && privateKey == null) {
            throw new IllegalArgumentException("公钥和私钥不能都为空");
        }
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * 用公钥加密任意数组。
     * Cipher是线程不安全的。
     */
    public byte[] encrypt(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, Objects.requireNonNull(publicKey, "必须拥有公钥才能加密"));
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 用公钥加密字符串。
     */
    public String encryptString(String text) {
        return byteToHexStr(encrypt(text.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 使用私钥解密加密后的数组。
     * Cipher是线程不安全的。
     */
    public byte[] decrypt(byte[] text) {
        try {
            Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, Objects.requireNonNull(privateKey, "必须拥有私钥才能解密"));
            return cipher.doFinal(text);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 使用私钥解密字符串。
     */
    public String decryptString(String str) {
        return new String(decrypt(hexStrToByte(str)), StandardCharsets.UTF_8);
    }

    /**
     * 将16进制字符串转换为二进制。
     */
    public static byte[] hexStrToByte(String hexStr) {
        if (hexStr == null) {
            return null;
        }
        byte[] result = new byte[hexStr.length() >> 1];
        for (int i = 0; i < result.length; i++) {
            int index = i << 1;
            int high = Integer.parseInt(hexStr.substring(index, index + 1), 16);
            int low = Integer.parseInt(hexStr.substring(index + 1, index + 2), 16);
            result[i] = (byte) (high << 4 | low);
        }
        return result;
    }

    /**
     * 将二进制转换成16进制。
     */
    public static String byteToHexStr(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 获取整数公私钥共用模。
     */
    public BigInteger getModulus() {
        return publicKey == null ? privateKey.getModulus() : publicKey.getModulus();
    }

    /**
     * 获取整数公钥指数。
     */
    public BigInteger getPublicExponent() {
        return publicKey.getPublicExponent();
    }

    /**
     * 获取整数私钥指数。
     */
    public BigInteger getPrivateExponent() {
        return privateKey.getPrivateExponent();
    }

    /**
     * 使用模和指数生成RSA公钥。
     */
    public static RSAPublicKey createPublicKey(BigInteger modulus, BigInteger exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, new BouncyCastleProvider());
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用模和指数生成RSA私钥。
     */
    public static RSAPrivateKey createPrivateKey(BigInteger modulus, BigInteger exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, new BouncyCastleProvider());
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, exponent);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试加密工具并提供示例。
     */
    public static void main(String[] args) {
        try {
            RSAEncryptor encryptor = new RSAEncryptor();
            System.out.println(encryptor.publicKey.getModulus().equals(encryptor.privateKey.getModulus()) + "\n");
            for (int i = 0; i < 10; i++) {
                String originalText = "123456789012345678901234567890" + i;
                System.out.println("原始：" + originalText);

                // 加密
                byte[] cipherText = encryptor.encrypt(originalText.getBytes("UTF-8"));
                String hex = byteToHexStr(cipherText);
                System.out.println("加密：" + hex);

                // 解密
                String plainText = new String(encryptor.decrypt(hexStrToByte(hex)), "UTF-8");
                System.out.println("解密：" + plainText + "\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}