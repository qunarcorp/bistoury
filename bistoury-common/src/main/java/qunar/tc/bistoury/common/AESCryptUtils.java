package qunar.tc.bistoury.common;

import com.google.common.base.Charsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * @author leix.xie
 * @date 2019/11/4 19:52
 * @describe
 */
public class AESCryptUtils {

    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final String AES_ALGORITHM = "AES";
    // 请不要修改此secret key
    private static final byte[] SECRET_KEY = "Q-V6TJrJeqzt_79q".getBytes(Charsets.UTF_8);

    private static final Key key = new SecretKeySpec(SECRET_KEY, AES_ALGORITHM);

    public static byte[] encrypt(byte[] data) throws Exception {
        if (data == null || data.length == 0) {
            return EMPTY_BYTES;
        }
        final Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data) throws Exception {
        if (data == null || data.length == 0) {
            return EMPTY_BYTES;
        }
        final Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }
}
