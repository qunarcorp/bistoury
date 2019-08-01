package qunar.tc.bistoury.ui.dao;

import qunar.tc.bistoury.serverside.support.Application;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 10:46
 * @describe
 */
public interface ApplicationDao {
    int updateApplication(Application application);

    Application getApplicationByAppCode(String appCode);

    List<Application> getApplicationsByAppCodes(List<String> appCodes);

    int createApplication(Application application);

    List<Application> getAllApplications();

    boolean checkExist(String appCode);
}
