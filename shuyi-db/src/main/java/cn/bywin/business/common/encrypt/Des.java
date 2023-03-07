package cn.bywin.business.common.encrypt;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class Des {
    public static String genKey() throws Exception {
        //1.生成KEY
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");//Key的生成器
        keyGenerator.init(56);//指定keySize
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] bytesKey = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString( bytesKey );
    }
    public static String encryptLocal( String data, String sourceFile ) throws Exception {
        final InputStream resourceAsStream = Des.class.getResourceAsStream(sourceFile );
        byte[] bytekey = new byte[ resourceAsStream.available()];
        resourceAsStream.read( bytekey );
        String key = new String( bytekey );
        return encrypt( data, key);
    }
    public static String encrypt( String data, String strKey ) throws Exception {

        byte[] bytesKey = Base64.getDecoder().decode(strKey);
        //2.KEY转换
        DESKeySpec desKeySpec = new DESKeySpec(bytesKey);//实例化DESKey秘钥的相关内容
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");//实例一个秘钥工厂，指定加密方式
        Key convertSecretKey = factory.generateSecret(desKeySpec);
        //3.加密    DES/ECB/PKCS5Padding--->算法/工作方式/填充方式
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");//通过Cipher这个类进行加解密相关操作
        cipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
        byte[] result = cipher.doFinal(data.getBytes());//输入要加密的内容
        return EnHex.bytesToHexString(result);
    }

    public static String decryptLocal( String data, String sourceFile ) throws Exception {
        final InputStream resourceAsStream = Des.class.getResourceAsStream(sourceFile );
        byte[] bytekey = new byte[ resourceAsStream.available()];
        resourceAsStream.read( bytekey );
        String key = new String( bytekey );
        return decrypt( data, key);
    }

    public static String decrypt( String data, String strKey ) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        byte[] bytesKey = Base64.getDecoder().decode(strKey);
        //2.KEY转换
        DESKeySpec desKeySpec = new DESKeySpec(bytesKey);//实例化DESKey秘钥的相关内容
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");//实例一个秘钥工厂，指定加密方式
        Key convertSecretKey = factory.generateSecret(desKeySpec);
        //3.加密    DES/ECB/PKCS5Padding--->算法/工作方式/填充方式
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");//通过Cipher这个类进行加解密相关操作
        cipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
        byte[] result = EnHex.hexStringToBytes(data );
        //4.解密
        cipher.init(Cipher.DECRYPT_MODE, convertSecretKey);
        result = cipher.doFinal(result);
        //System.out.println("解密结果：" + new String(result));
        return new String(result);
    }
}
