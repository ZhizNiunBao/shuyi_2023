package cn.bywin.business.hetu;

/**
 * 密钥对
 * @author firepation
 */
public class RsaKeyPair {

    private String publicKey;
    private String privateKey;

    public RsaKeyPair(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }


}
