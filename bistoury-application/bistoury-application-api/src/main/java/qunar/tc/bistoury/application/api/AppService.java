package qunar.tc.bistoury.application.api;

import qunar.tc.bistoury.application.api.pojo.Application;

import java.util.Set;

/**
 * @author xkrivzooh
 * @since 2019/8/14
 */
public interface AppService {

    Set<String> getApps(String userCode);

    Application getAppInfo(String appCode);

    boolean checkUserPermission(String appCode, String usercode, final boolean isAdmin);

    boolean checkUserPermission(String appCode, String usercode);

}
