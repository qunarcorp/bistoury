package qunar.tc.bistoury.ui.dao;

import qunar.tc.bistoury.ui.model.User;

/**
 * @author leix.xie
 * @date 2019/7/4 11:01
 * @describe
 */
public interface UserDao {
    User getUserByUserCode(String userCode);

    int registerUser(User user);
}
