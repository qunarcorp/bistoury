package qunar.tc.bistoury.ui.service;

/**
 * @author keli.wang
 */
public interface AESCryptService {
    String encrypt(final String data);

    String decrypt(final String data);
}
