package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.ui.service.AESCryptService;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

/**
 * @author keli.wang
 */
@Service
public class AESCryptServiceImpl implements AESCryptService {
    private static final Logger LOG = LoggerFactory.getLogger(AESCryptServiceImpl.class);

    private static final String AES_ALGORITHM = "AES";
    // 请不要修改此secret key
    private static final byte[] SECRET_KEY = "Q-V6TJrJeqzt_79q".getBytes(Charsets.UTF_8);

    private final Key key = new SecretKeySpec(SECRET_KEY, AES_ALGORITHM);

    @Override
    public String encrypt(String data) {
        try {
            if (Strings.isNullOrEmpty(data)) {
                return Strings.nullToEmpty(data);
            }
            final Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] encryptedBytes = cipher.doFinal(data.getBytes(Charsets.UTF_8));
            return new String(Base64.getEncoder().encode(encryptedBytes), Charsets.UTF_8);
        } catch (Exception e) {
            LOG.error("无法加密字符串。data={}", data, e);
            throw new RuntimeException("加密字符串失败", e);
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            final Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            final byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data.getBytes(Charsets.UTF_8)));
            return new String(decryptedBytes, Charsets.UTF_8);
        } catch (Exception e) {
            LOG.error("解密字符串失败。data={}", data, e);
            throw new RuntimeException("解密字符串失败", e);
        }
    }
}
