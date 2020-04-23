package qunar.tc.bistoury.ui.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 此旧为实现，仅为兼容而存在 建议采用默认更安全的加密加密策略，实现需要
 * 实在需要，请在SPI定义文件META-INF\services\org.springframework.security.crypto.password.PasswordEncoder中配置此实现类
 * 
 * @author qxo
 * @date 2020/01/01
 */
public class PasswordEncoderForOldAESCryptImpl implements PasswordEncoder {

    private final AESCryptServiceImpl aesCryptService = new AESCryptServiceImpl();

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword == null ? null : aesCryptService.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword == null ? false : aesCryptService.encrypt(rawPassword.toString()).equals(encodedPassword);
    }

}
