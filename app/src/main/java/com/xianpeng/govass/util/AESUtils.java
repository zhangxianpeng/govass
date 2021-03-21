package com.xianpeng.govass.util;

import android.util.Base64;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密类 与前端统一key iv
 */
public class AESUtils {
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private static final String CHARSET = "utf-8";
    /**
     * 建议为16位或32位
     */
    private static final String KEY = "govass0123456789";
    /**
     * 必须16位
     * 初始化向量IV不可以为32位，否则异常java.security.InvalidAlgorithmParameterException: Wrong IV length: must be 16 bytes long
     */
    private static final String IV = "govass0123456789";

    /**
     * 加密
     */
    public static String encrypt(String context) {
        try {
            byte[] decode = context.getBytes(CHARSET);
            byte[] bytes = createKeyAndIv(decode, Cipher.ENCRYPT_MODE);
            return Base64.encodeToString(bytes,Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取key & iv
     */
    private static byte[] createKeyAndIv(byte[] context, int opmode) throws Exception {
        byte[] key = KEY.getBytes(CHARSET);
        byte[] iv = IV.getBytes(CHARSET);
        return cipherFilter(context, opmode, key, iv);
    }

    /**
     * 执行操作
     */
    private static byte[] cipherFilter(byte[] context, int opmode, byte[] key, byte[] iv) throws Exception {
        Key secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(opmode, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(context);
    }

    /**
     * 主方法测试
     */
    public static void main(String[] args) {
//        String context = "Aa@987654321";
//        System.out.println("元数据" + context);
//        String encrypt = encrypt(context);
//        System.out.println("JAVA加密之后：" + encrypt);
//        System.out.println("android加密之后：" + encryptAndroid(context));
    }
}