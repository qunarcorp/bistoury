package qunar.tc.bistoury.ui.security;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    public void test() {
        PasswordEncoder[] passwordEncoders =  new PasswordEncoder[] {
            new Pbkdf2PasswordEncoder(),
            new BCryptPasswordEncoder(),
            new SCryptPasswordEncoder()
        };
        final String rawPassword = "password";
        for (PasswordEncoder passwordEncoder : passwordEncoders) {
            final String encoded = passwordEncoder.encode(rawPassword);
            System.out.println(passwordEncoder.getClass()+" src: "+rawPassword + " => "+encoded);
            Assert.assertTrue(passwordEncoder.matches(rawPassword, encoded));
        }
    }
}
