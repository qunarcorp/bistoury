package qunar.tc.bistoury.ui.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginManager {
    void login(String loginId, HttpServletResponse response);

    boolean isLogin(HttpServletRequest request);

    String current(HttpServletRequest request);

    String token(HttpServletRequest request);

    void logout(HttpServletResponse response);
}
