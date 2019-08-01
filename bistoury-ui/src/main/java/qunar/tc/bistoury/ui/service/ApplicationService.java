package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.serverside.support.Application;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 20:13
 * @describe
 */
public interface ApplicationService {
    List<Application> getAllApplications(String userCode);

    List<String> getAppOwner(String appCode);

    int save(Application application);
}
