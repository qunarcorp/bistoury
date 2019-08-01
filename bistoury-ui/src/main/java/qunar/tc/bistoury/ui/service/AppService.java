package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.serverside.support.Application;

import java.util.Map;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2018 2018/10/31 13:56
 */
public interface AppService {

    Set<String> getApps(String userCode);

    Application getAppInfo(String appCode);
}
