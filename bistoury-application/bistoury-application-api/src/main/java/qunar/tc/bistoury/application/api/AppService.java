package qunar.tc.bistoury.application.api;

import java.util.Set;

import qunar.tc.bistoury.application.api.pojo.Application;

/**
 * @author xkrivzooh
 * @since 2019/8/14
 */
public interface AppService {

	Set<String> getApps(String userCode);

	Application getAppInfo(String appCode);

    boolean checkUserPermission(String appCode, String usercode);
}
