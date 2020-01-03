package com.example.md5addsecret;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by C4BOM on 2018/6/28.
 * GoodLuck No Bug
 */
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String info, String ivString, String keyString) {
        try {
            IvParameterSpec iv = getIv(ivString);
            SecretKey key = getKey(keyString);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);


          //  byte[] decode = Base64.decode(info.getBytes(coding), Base64.DEFAULT);
           // byte[] decode = Base64.decode(info, Base64.DEFAULT);
            byte[] decode = base64_decode(info);


//            Log.i("sss","Base64 解密结果 "+toHex(decode));
            byte[] bytes = cipher.doFinal(decode);
       //     Log.i("sss","AES 解密结果 "+new String(bytes, coding));
            byte[] decode2 = Base64.decode(new String(bytes, coding), Base64.DEFAULT);

//            String decode2 =  new String(bytes, coding);
            String decodeStr =  decodeUnicode(new String(decode2, coding));

            byte[] uncompr =  uncompress(decodeStr.getBytes());
            return new String(uncompr, coding);
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
        }
        return null;
    }

    public static byte[] base64_decode(String value){
        if(value.length()%4!=0){
            for(int i=0;i<value.length()%4;i++){
                value+="=";
            }
        }

        return  Base64.decode(value, Base64.DEFAULT);
    }


    private static final char[] DIGITS
            = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static final String toHex(byte[] data) {
        final StringBuffer sb = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            sb.append(DIGITS[(data[i] >>> 4) & 0x0F]);
            sb.append(DIGITS[data[i] & 0x0F]);
        }
        return sb.toString();
    }

    /**
     * 加密代码流程:
     * <p>
     * 获取向量对象
     * 获取密钥对象
     * 初始化Cipher
     * 初始化加密方式
     * 获取DES加密后的字符数组
     * base64编码加密后的密文数组
     * 密文数组生成字符串对象返回
     *
     * @param info 需要加密的内容
     */
    public static String encrypt(String info) {
        try {
            IvParameterSpec iv = getIv(CLIENTS_IV);
            SecretKey key = getKey(CLIENTS_KEY);
            Cipher cipher = Cipher.getInstance(CIPHER_TYPE, "BC");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] bytes = cipher.doFinal(info.getBytes(coding));
            byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
            return new String(encode, coding);
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
        } catch (NoSuchProviderException e) {
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
        md5 = md5.substring(0,16);
        Log.i("sss","Iv " + md5);
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
            Log.i("sss","enKey " + enKey);
            byte[] raw = enKey.getBytes(coding);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, EN_TYPE);
            return skeySpec;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解码 Unicode \\uXXXX
     * @param str
     * @return
     */
    public static String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher( str );
        int start = 0 ;
        int start2 = 0 ;
        StringBuffer sb = new StringBuffer();
        while( m.find( start ) ) {
            start2 = m.start() ;
            if( start2 > start ){
                String seg = str.substring(start, start2) ;
                sb.append( seg );
            }
            String code = m.group( 1 );
            int i = Integer.valueOf( code , 16 );
            byte[] bb = new byte[ 4 ] ;
            bb[ 0 ] = (byte) ((i >> 8) & 0xFF );
            bb[ 1 ] = (byte) ( i & 0xFF ) ;
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append( String.valueOf( set.decode(b) ).trim() );
            start = m.end() ;
        }
        start2 = str.length() ;
        if( start2 > start ){
            String seg = str.substring(start, start2) ;
            sb.append( seg );
        }
        return sb.toString() ;
    }

    /**
     *     GZIP解压缩
     */
    public static byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }


}