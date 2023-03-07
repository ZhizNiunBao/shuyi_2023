package cn.bywin.business.hetu;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * hetu 密码加密工具类
 * @author firepation
 */
public class RsaUtil {
    
    public static final String RSA = "RSA";
    public static final String RSA_PADDING = "RSA/ECB/OAEPWITHSHA256AndMGF1Padding";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 构建 RSA 密钥对
     * @return 密钥对
     */
    public static RsaKeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        return new RsaKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * 私钥解密
     * @param privateKeyText 私钥
     * @param decrptText 密文
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String privateKeyText, String decrptText) throws Exception {
        byte[] decrptByte = Base64.getDecoder().decode(decrptText);
        byte[] privateKeyByte = Base64.getDecoder().decode(privateKeyText);

        KeyFactory factory = KeyFactory.getInstance(RSA);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyByte);
        RSAPrivateKey priKey = (RSAPrivateKey) factory.generatePrivate(pkcs8EncodedKeySpec);

        Cipher cipher = Cipher.getInstance(RSA_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(decrptByte));
    }


    /**
     * 公钥加密
     * @param publicKeyText  公钥
     * @param encryptText    需加密文本
     * @return 加密后的密文
     */
    public static String encryptByPublicKey(String publicKeyText, String encryptText)
            throws Exception {
        byte[] publickKeyByte = Base64.getDecoder().decode(publicKeyText);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publickKeyByte);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(RSA)
                .generatePublic(x509EncodedKeySpec);

        Cipher cipher = Cipher.getInstance(RSA_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] result = cipher.doFinal(encryptText.getBytes());
        return Base64.getEncoder().encodeToString(result);
    }
}
