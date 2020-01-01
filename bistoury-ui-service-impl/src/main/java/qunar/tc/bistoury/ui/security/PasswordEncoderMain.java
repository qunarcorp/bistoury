 package qunar.tc.bistoury.ui.security;

import qunar.tc.bistoury.ui.service.impl.UserServiceImpl;

public class PasswordEncoderMain {

    public static void main(String[] args) {
        String rawPassword = args == null || args.length < 1 ? "admin" : args[0];
        String encodePwd = UserServiceImpl.encodePwd(rawPassword);
        System.out.println("rawPassword: " + rawPassword + " encodePwd: " + encodePwd);
    }

}
