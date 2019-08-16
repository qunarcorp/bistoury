package qunar.tc.bistoury.application.api;

import java.util.Map;

/**
 * @author xkrivzooh
 * @since 2019/8/16
 */
public interface AppServerPidInfoService {

	/**
	 * 根据serverIp查询server机器上部署的应用的PID信息
	 * @param serverIp agent所在的server的ip信息
	 * @return key: appCode, value: pid
	 */
	Map<String, Integer> queryPidInfo(String serverIp);

}
