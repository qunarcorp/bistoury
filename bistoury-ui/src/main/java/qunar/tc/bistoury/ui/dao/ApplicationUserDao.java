package qunar.tc.bistoury.ui.dao;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 11:14
 * @describe
 */
public interface ApplicationUserDao {
    List<String> getAppCodesByUserCode(String userCode);

    List<String> getUsersByAppCode(String appCode);

    int addAppUser(String userCode, String appCode);

    void batchAddAppUser(List<String> userCodes, String addCode);

    int removeAppUser(String userCode, String appCode);
}
