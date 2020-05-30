/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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


    private static final String RSA_PUBLIC_KEY = "/rsa-public-key.pem";

    private static final String RSA_PRIVATE_KEY = "/rsa-private-key.pem";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSAEncryption() throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        this(RSA_PUBLIC_KEY, RSA_PRIVATE_KEY);
    }

    public RSAEncryption(String publicKeyPath, String privateKeyPath) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
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
