package qunar.tc.bistoury.application.mysql.service;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.application.api.AppServerPidInfoService;

import java.util.Map;

/**
 * @author xkrivzooh
 * @since 2019/8/16
 */
@Service
public class AppServerPidInfoServiceImpl implements AppServerPidInfoService {

	@Override
	public Map<String, Integer> queryPidInfo(String serverIp) {
		throw new UnsupportedOperationException("默认不支持从proxy获取PID信息");
	}
}
