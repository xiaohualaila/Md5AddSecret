package com.example.util;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

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
    private static String coding = "GBK";

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


            byte[] decode = Base64.decode(info.getBytes(coding), Base64.DEFAULT);
            byte[] bytes = cipher.doFinal(decode);
            byte[] decode2 = Base64.decode(bytes, Base64.DEFAULT);

            String unicode_str = CoderUtils.convertStringToUTF8(new String(decode2,coding));
         //   Log.i("sss", unicode_str);
//            String utf_md5_s = Md5Util.getMd5(result);
//            Log.i("sss", "utf_md5_s  " + utf_md5_s);



            byte[] z_bytes_ = ZlibCompress.decompress(unicode_str.getBytes());//zlib 解压
            String result = new String(z_bytes_,"utf-8");
            Log.i("sss","解密结果 "+result);

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
     * 字节转十六进制
     * @param b 需要进行转换的byte字节
     * @return  转换后的Hex字符串
     */
    public static String byteToHex(byte b){
        String hex = Integer.toHexString(b & 0xFF);
        if(hex.length() < 2){
            hex = "0" + hex;
        }
        return hex;
    }

    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }


    public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }

    public static byte[] base64_decode(String value) {
        if (value.length() % 4 != 0) {
            for (int i = 0; i < value.length() % 4; i++) {
                value += "=";
            }
        }

        return Base64.decode(value, Base64.DEFAULT);
    }


    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }


    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if (ub == Character.UnicodeBlock.BASIC_LATIN) {
                //英文及数字等
                sb.append(myBuffer[i]);
            } else if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                //全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                //汉字
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
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
//            Log.i("sss","enKey " + enKey);
            byte[] raw = enKey.getBytes(coding);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, EN_TYPE);
            return skeySpec;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解码 Unicode \\uXXXX
     *
     * @param str
     * @return
     */
    public static String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher(str);
        int start = 0;
        int start2 = 0;
        StringBuffer sb = new StringBuffer();
        while (m.find(start)) {
            start2 = m.start();
            if (start2 > start) {
                String seg = str.substring(start, start2);
                sb.append(seg);
            }
            String code = m.group(1);
            int i = Integer.valueOf(code, 16);
            byte[] bb = new byte[4];
            bb[0] = (byte) ((i >> 8) & 0xFF);
            bb[1] = (byte) (i & 0xFF);
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append(String.valueOf(set.decode(b)).trim());
            start = m.end();
        }
        start2 = str.length();
        if (start2 > start) {
            String seg = str.substring(start, start2);
            sb.append(seg);
        }
        return sb.toString();
    }




    /**
     * GZIP解压缩
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