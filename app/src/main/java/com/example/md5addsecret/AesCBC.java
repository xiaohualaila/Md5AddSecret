package com.example.md5addsecret;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesCBC {

    private static String sKey="bWFwaWVuY29kZQ==";
    private static String ivParameter="EMNZCGFSBWFWAQ==";
    private static String coding = "utf-8";
    private static String EN_TYPE = "AES";


    public static String decrypt(String sSrc) {
        try {
            IvParameterSpec iv = getIv(ivParameter);
            SecretKey key = getKey(sKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decode = Base64.decode(sSrc.getBytes(coding), Base64.DEFAULT);
            byte[] bytes = cipher.doFinal(decode);
            byte[] decode2 = Base64.decode(bytes, Base64.DEFAULT);
            return new String(decode2, coding);
        } catch (Exception ex) {
            Log.i("sss", ex.getMessage());
        }
        return null;
    }
    /**
     * 向量生成流程:
     * <p>
     * 向量md5加密
     * 截取后8位->这个就是偏移量
     * 生成向量对象返回
     */
    private static IvParameterSpec getIv(String ivString) {
        String md5 = Md5Util.getMd5(ivString);
      //  md5 = md5.substring(md5.length() - 8);
        return new IvParameterSpec(md5.getBytes());
    }

    /**
     * 密钥生成流程:
     * <p>
     * 密钥md5加密,取前16位--->这个才是真的密钥
     * 初始化 DESKeySpec 对象
     * 设置密钥工厂为DES加密
     * 加工成密钥对象返回
     */
    private static SecretKey getKey(String pass) {
        try {

            String enKey = Md5Util.getMd5(pass);
            byte[] raw = enKey.getBytes(coding);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, EN_TYPE);

//            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(EN_TYPE);
//            return keyFactory.generateSecret(dks);
//

            return skeySpec;
        }  catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
