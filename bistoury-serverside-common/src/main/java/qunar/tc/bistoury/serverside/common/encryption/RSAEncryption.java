package qunar.tc.bistoury.serverside.common.encryption;

import com.google.common.base.Charsets;
import com.ning.http.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class RSAEncryption implements Encryption {

    private static final String ALGORITHM = "RSA";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSAEncryption(String publicKeyPath, String privateKeyPath) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.publicKey = EncryptionUtils.loadRSAPublicKey(publicKeyPath);
        this.privateKey = EncryptionUtils.loadRSAPrivateKey(privateKeyPath);

        // fail fast
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

    }

    @Override
    public String encrypt(String source) throws EncryptException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = source.getBytes(Charsets.UTF_8);
            return Base64.encode(cipher.doFinal(bytes));
        } catch (Exception e) {
            throw new EncryptException(e);
        }
    }

    @Override
    public String decrypt(String source) throws DecryptException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.decode(source)), Charsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptException(e);
        }
    }
}
