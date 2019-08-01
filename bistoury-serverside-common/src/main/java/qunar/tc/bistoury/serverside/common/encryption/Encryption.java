package qunar.tc.bistoury.serverside.common.encryption;

import java.io.IOException;

public interface Encryption {

    String encrypt(String source) throws EncryptException;

    String decrypt(String source) throws DecryptException;

    class EncryptException extends IOException {
        public EncryptException(Throwable cause) {
            super(cause);
        }
    }

    class DecryptException extends IOException {
        public DecryptException(Throwable cause) {
            super(cause);
        }
    }
}
