package cn.bywin.business.common.encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * RSA算法页面加密/解密工具类
 */
public class RSAPage {
    private final Logger LOGGER = LoggerFactory.getLogger(RSAPage.class);
    /**
     * 算法名称
     */
    private final String ALGORITHM = "RSA";
    /**
     * 默认密钥大小
     */
    private final int KEY_SIZE = 1024;
    /**
     * 密钥对生成器
     */
    private KeyPairGenerator keyPairGenerator = null;

    private KeyFactory keyFactory = null;
    /**
     * 缓存的密钥对
     */
    private KeyPair keyPair = null;

    private RSAPublicKey rsaPublicKey;

    private RSAPrivateKey rsaPrivateKey;

    /**
     * Base64 编码/解码器 JDK1.8
     */
    private static Base64.Decoder decoder = Base64.getDecoder();
    private static Base64.Encoder encoder = Base64.getEncoder();

    /**
     * 私有构造器
     */
    public RSAPage() throws Exception {
        keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyFactory = KeyFactory.getInstance(ALGORITHM);
    }

    /**
     * 生成密钥对
     * 将密钥分别用Base64编码保存到#publicKey.properties#和#privateKey.properties#文件中
     * 保存的默认名称分别为publicKey和privateKey
     */
    public void generateKeyPair() throws Exception {

        /** 初始化密钥工厂 */

        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom(UUID.randomUUID().toString().replaceAll("-", "").getBytes()));
        keyPair = keyPairGenerator.generateKeyPair();

        rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        //publicKeyString = encoder.encodeToString(rsaPublicKey.getEncoded());
        //privateKeyString = encoder.encodeToString(rsaPrivateKey.getEncoded());
    }

    /**
     * 从文件获取RSA公钥
     *
     * @return RSA公钥
     * @throws InvalidKeySpecException
     */
    private RSAPublicKey genPublicKey(String strKey) throws Exception {
        byte[] keyBytes = decoder.decode(strKey);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
    }

    /**
     * 从文件获取RSA私钥
     *
     * @return RSA私钥
     * @throws InvalidKeySpecException
     */
    private RSAPrivateKey genPrivateKey(String strKey) throws Exception {
        byte[] keyBytes = decoder.decode(strKey);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    /**
     * RSA公钥加密
     *
     * @param content 等待加密的数据
     * @return 加密后的密文(16进制的字符串)
     */
    public String encryptByPublic(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.rsaPublicKey);
        //该密钥能够加密的最大字节长度
        //int splitLength = ((RSAPublicKey) rsaPublicKey).getModulus().bitLength() / 8 - 11;
        int MAX_ENCRYPT_BLOCK = ((RSAPublicKey) rsaPublicKey).getModulus().bitLength() / 8 - 11;
        int inputLen = content.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        System.out.println(MAX_ENCRYPT_BLOCK);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(content, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(content, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        return EnHex.bytesToHexString(encryptedData);

//        byte[][] arrays = EnHex.splitBytes(content, splitLength);
//        StringBuffer stringBuffer = new StringBuffer();
//        for (byte[] array : arrays) {
//            stringBuffer.append(EnHex.bytesToHexString(cipher.doFinal(array)));
//        }
//        return stringBuffer.toString();
    }

    /**
     * RSA公钥加密
     *
     * @param content 等待加密的数据
     * @return 加密后的密文(16进制的字符串)
     */
    public String encrypt64ByPublic(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.rsaPublicKey);
        //该密钥能够加密的最大字节长度
        //int splitLength = ((RSAPublicKey) rsaPublicKey).getModulus().bitLength() / 8 - 11;
        int MAX_ENCRYPT_BLOCK = ((RSAPublicKey) rsaPublicKey).getModulus().bitLength() / 8 - 11;
        int inputLen = content.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        System.out.println(MAX_ENCRYPT_BLOCK);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(content, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(content, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        return Base64.getEncoder().encodeToString(encryptedData);
//        byte[][] arrays = EnHex.splitBytes(content, splitLength);
//        byte[] resultBytes = {};
//        byte[] cache = {};
//
//        for (byte[] array : arrays) {
//            cache = cipher.doFinal(array);
//            resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
//            System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
//        }
//        return Base64.getEncoder().encodeToString(resultBytes);
    }

    /**
     * RSA私钥加密
     *
     * @param content 等待加密的数据
     * @return 加密后的密文(16进制的字符串)
     */
    public String encryptByPrivate(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.rsaPrivateKey);
        //该密钥能够加密的最大字节长度
        //int splitLength = ((RSAPrivateKey)rsaPrivateKey).getModulus().bitLength() / 8 -11;
        int MAX_ENCRYPT_BLOCK = ((RSAPrivateKey) rsaPrivateKey).getModulus().bitLength() / 8 - 11;
        int inputLen = content.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        System.out.println(MAX_ENCRYPT_BLOCK);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(content, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(content, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        return EnHex.bytesToHexString(encryptedData);

//            byte[][] arrays = EnHex.splitBytes(content,splitLength);
//            StringBuffer stringBuffer = new StringBuffer();
//            for(byte[] array : arrays){
//                stringBuffer.append(EnHex.bytesToHexString(cipher.doFinal(array)));
//            }
//            return stringBuffer.toString();
    }

    /**
     * RSA私钥加密
     *
     * @param content 等待加密的数据
     * @return 加密后的密文(16进制的字符串)
     */
    public String encrypt64ByPrivate(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.rsaPrivateKey);

        int MAX_ENCRYPT_BLOCK = ((RSAPrivateKey) rsaPrivateKey).getModulus().bitLength() / 8 - 11;

        int inputLen = content.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        System.out.println(MAX_ENCRYPT_BLOCK);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(content, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(content, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        return Base64.getEncoder().encodeToString(encryptedData);

       /* //该密钥能够加密的最大字节长度
        int splitLength = ((RSAPrivateKey)rsaPrivateKey).getModulus().bitLength() / 8 -11;
        byte[][] arrays = EnHex.splitBytes(content,splitLength);
        byte[] resultBytes = {};
        byte[] cache = {};

        for(byte[] array : arrays){
            cache = cipher.doFinal(array);
            resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
            System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
            //stringBuffer.deleteCharAt( stringBuffer.length()-1);
        }

        return Base64.getEncoder().encodeToString(resultBytes);*/

        //return stringBuffer.toString();
    }

    /**
     * RSA私钥解密
     *
     * @param content 等待解密的数据
     * @return 解密后的明文
     */
    public String decryptByPrivate(String content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.rsaPrivateKey);
        //该密钥能够加密的最大字节长度
        byte[] contentBytes = EnHex.hexStringToBytes(content);
        int MAX_DECRYPT_BLOCK = ((RSAPrivateKey) rsaPrivateKey).getModulus().bitLength() / 8;
        int inputLen = contentBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(contentBytes, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(contentBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);

//        byte[][] arrays = EnHex.splitBytes(contentBytes,splitLength);
//        StringBuffer stringBuffer = new StringBuffer();
//        String sTemp = null;
//        for (byte[] array : arrays){
//            stringBuffer.append(new String(cipher.doFinal(array)));
//        }
//        return stringBuffer.toString();
    }

    /**
     * RSA私钥解密
     *
     * @param content 等待解密的数据
     * @return 解密后的明文
     */
    public String decrypt64ByPrivate(String content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.rsaPrivateKey);
        //该密钥能够加密的最大字节长度
        byte[] contentBytes = Base64.getDecoder().decode(content);
        int MAX_DECRYPT_BLOCK = ((RSAPrivateKey) rsaPrivateKey).getModulus().bitLength() / 8;
        int inputLen = contentBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(contentBytes, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(contentBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
//        byte[] contentBytes = Base64.getDecoder().decode(content);
//        byte[][] arrays = EnHex.splitBytes(contentBytes,splitLength);
//        StringBuffer stringBuffer = new StringBuffer();
//        String sTemp = null;
//        for (byte[] array : arrays){
//            stringBuffer.append(new String(cipher.doFinal(array)));
//        }
//        return stringBuffer.toString();
    }

    /**
     * RSA公钥解密
     *
     * @param content 等待解密的数据
     * @return 解密后的明文
     */
    public String decryptByPublic(String content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.rsaPublicKey);
        //该密钥能够加密的最大字节长度
        //int splitLength = ((RSAPublicKey)rsaPublicKey).getModulus().bitLength() / 8;
        byte[] contentBytes = EnHex.hexStringToBytes(content);
        int MAX_DECRYPT_BLOCK = ((RSAPublicKey) rsaPublicKey).getModulus().bitLength() / 8;
        int inputLen = contentBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(contentBytes, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(contentBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
//        byte[][] arrays = EnHex.splitBytes(contentBytes,splitLength);
//        StringBuffer stringBuffer = new StringBuffer();
//        String sTemp = null;
//        for (byte[] array : arrays){
//            stringBuffer.append(new String(cipher.doFinal(array)));
//        }
//        return stringBuffer.toString();
    }

    /**
     * RSA公钥解密
     *
     * @param content 等待解密的数据
     * @return 解密后的明文
     */
    public String decrypt64ByPublic(String content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.rsaPublicKey);
        byte[] contentBytes = Base64.getDecoder().decode(content);
        int MAX_DECRYPT_BLOCK = ((RSAPublicKey) rsaPublicKey).getModulus().bitLength() / 8;
        int inputLen = contentBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(contentBytes, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(contentBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);

        //该密钥能够加密的最大字节长度
//        int splitLength = ((RSAPublicKey)rsaPublicKey).getModulus().bitLength() / 8;
//        byte[] contentBytes = Base64.getDecoder().decode(content);
//        byte[][] arrays = EnHex.splitBytes(contentBytes,splitLength);
//        StringBuffer stringBuffer = new StringBuffer();
//        String sTemp = null;
//        for (byte[] array : arrays){
//            stringBuffer.append(new String(cipher.doFinal(array)));
//        }
//        return stringBuffer.toString();

    }

    public String getPublicKey() {
        return encoder.encodeToString(rsaPublicKey.getEncoded());
    }

    public void setPublicKey(String publicKeyString) throws Exception {
        this.rsaPublicKey = genPublicKey(publicKeyString);
    }

    public String getPrivateKey() {
        return encoder.encodeToString(rsaPrivateKey.getEncoded());
    }

    public void setPrivateKey(String privateKeyString) throws Exception {
        this.rsaPrivateKey = genPrivateKey(privateKeyString);
    }
}