package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.ui.model.User;

/**
 * @author leix.xie
 * @date 2019/7/4 11:00
 * @describe
 */
public interface UserService {
    boolean login(User user);

    int register(User user);

    boolean isAdmin(String userCode);
}
