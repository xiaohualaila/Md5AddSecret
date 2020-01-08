package com.example.util;

import android.util.Base64;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESUtil {
    /**
     * 客户端的向量参数
     */
    public static String CLIENTS_IV = "EMNZCGFSBWFWAQ==";               //向量/偏移量

    /**
     * 新版本客户端的密钥
     * 新版本服务端的密钥,向量
     */
    public static String CLIENTS_KEY = "bWFwaWVuY29kZQ==";          //加密密钥


    private static String CIPHER_TYPE = "AES/CBC/PKCS7Padding"; //设定参数

    private static String EN_TYPE = "AES";
    private static String coding = "utf-8";

    /**
     * 解密流程:
     * <p>
     * 获取向量对象
     * 获取密钥对象
     * 初始化Cipher对象
     * 设置Cipher的参数
     * base64反编需要解密的密文获取密文字节数组
     * DES解密获取明文字节数组
     * 把明文字节数组生成字符串对象返回
     *
     * @param info 需要解密的内容
     */
    public static String decrypt(String info, String ivString, String keyString) {
        try {
            IvParameterSpec iv = getIv(ivString);
            SecretKey key = getKey(keyString);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] decode = Base64.decode(info.getBytes(), Base64.DEFAULT);
            byte[] bytes = cipher.doFinal(decode);//AES解密
            byte[] decode2 = Base64.decode(bytes, Base64.DEFAULT);
            String result = new String(decode2,coding);
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 向量生成流程:
     * <p>
     * 向量md5加密
     * 截取前16位->这个就是偏移量
     * 生成向量对象返回
     */
    private static IvParameterSpec getIv(String ivString) {
        String md5 = Md5Util.getMd5(ivString);
        md5 = md5.substring(0, 16);
//        Log.i("sss","Iv " + md5);
        return new IvParameterSpec(md5.getBytes());
    }

    /**
     * 密钥生成流程:
     * <p>
     * 密钥md5加密,取后16位--->这个才是真的密钥
     * 初始化 DESKeySpec 对象
     * 设置密钥工厂为AES加密
     * 加工成密钥对象返回
     */
    private static SecretKey getKey(String pass) {
        try {
            String enKey = Md5Util.getMd5(pass);
            enKey = enKey.substring(enKey.length() - 16);
            byte[] raw = enKey.getBytes(coding);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, EN_TYPE);
            return skeySpec;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}