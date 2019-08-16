package qunar.tc.bistoury.application.mysql.service;

import java.util.HashMap;
import java.util.Map;

import qunar.tc.bistoury.application.api.AppServerPidInfoService;

import org.springframework.stereotype.Service;

/**
 * @author xkrivzooh
 * @since 2019/8/16
 */
@Service
public class AppServerPidInfoServiceImpl implements AppServerPidInfoService {

	@Override
	public Map<String, Integer> queryPidInfo(String serverIp) {
		return new HashMap<>();
	}
}
