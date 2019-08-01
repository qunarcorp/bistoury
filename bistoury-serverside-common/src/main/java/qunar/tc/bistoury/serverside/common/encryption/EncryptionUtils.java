package qunar.tc.bistoury.serverside.common.encryption;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.springframework.core.io.ClassPathResource;
import qunar.tc.bistoury.common.FileUtil;
import qunar.tc.bistoury.serverside.agile.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class EncryptionUtils {

    public static PublicKey loadRSAPublicKey(String path) throws IOException, InvalidKeySpecException {
        ClassPathResource pathResource = new ClassPathResource(path);
        byte[] bb = FileUtil.readBytes(pathResource.getInputStream());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(bb));
        try {
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            // ignore;
            return null;
        }
    }

    public static PrivateKey loadRSAPrivateKey(String path) throws IOException, InvalidKeySpecException {
        ClassPathResource pathResource = new ClassPathResource(path);
        byte[] bb = FileUtil.readBytes(pathResource.getInputStream());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decode(bb));
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (NoSuchAlgorithmException e) {
            // ignore;
            return null;
        }
    }

    public static SecretKey loadDesKey(String path) throws InvalidKeySpecException, IOException, InvalidKeyException {
        String s = FileUtil.readString(new File(EncryptionUtils.class.getResource(path).getPath()), Charsets.UTF_8.name());
        DESKeySpec spec = new DESKeySpec(Base64.decode(s));
        try {
            return SecretKeyFactory.getInstance("DES").generateSecret(spec);
        } catch (NoSuchAlgorithmException e) {
            // ignore
            return null;
        }
    }

    public static KeyPair createKeyPair(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(1024);
        return generator.generateKeyPair();
    }

    public static SecretKey createKey(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator generator = KeyGenerator.getInstance(algorithm);
        return generator.generateKey();
    }

    public static void serializeKey(String dstFile, Key key) throws IOException {
        Files.write(Base64.encode(key.getEncoded()), new File(dstFile), Charsets.UTF_8);
    }

    public static String decryptDes(String data, String keyString) throws Encryption.DecryptException {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            DESKeySpec keySpec = new DESKeySpec(keyString.getBytes(Charsets.UTF_8));
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytes = Base64.decode(data);
            return new String(cipher.doFinal(bytes), Charsets.UTF_8);
        } catch (Exception e) {
            throw new Encryption.DecryptException(e);
        }
    }
}
