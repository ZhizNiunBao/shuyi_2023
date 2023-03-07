package cn.bywin.business.hetu;

/**
 * @author firepation
 */
public class EncryptFlag {

    public static final Integer YES = 1;

    public static final Integer NO = 0;

    private EncryptFlag() {

    }

    public static Boolean isEncrypt(Integer encryptFlag) {
        if (encryptFlag == null) {
            return false;
        }
        return encryptFlag.equals(YES);
    }
}
