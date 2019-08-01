package qunar.tc.bistoury.ui.service.impl;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.ui.model.ReleaseInfo;
import qunar.tc.bistoury.ui.service.ReleaseInfoService;
import qunar.tc.bistoury.ui.util.PropertiesReleaseInfoParse;
import qunar.tc.bistoury.ui.util.ReleaseInfoParse;

import javax.annotation.PostConstruct;

/**
 * @author leix.xie
 * @date 2019/7/10 10:27
 * @describe
 */
@Service
public class ReleaseInfoServiceImpl implements ReleaseInfoService {


    private static final ReleaseInfoParse RELEASE_INFO_PARSE = new PropertiesReleaseInfoParse();
    private static final String DEFAULT_RELEASE_INFO_PATH = "../webapps/releaseInfo.properties";
    private static final String DEFAULT = "default";
    private String defaultReleaseInfoPath = DEFAULT_RELEASE_INFO_PATH;

    @PostConstruct
    public void init() {
        DynamicConfigLoader.load("releaseInfo_config.properties", false).addListener(dynamicConfig -> {
            defaultReleaseInfoPath = dynamicConfig.getString(DEFAULT, DEFAULT_RELEASE_INFO_PATH);
        });
    }

    @Override
    public ReleaseInfo parseReleaseInfo(String content) {
        return RELEASE_INFO_PARSE.parseReleaseInfo(content);
    }

    @Override
    public String getDefaultReleaseInfoPath() {
        return defaultReleaseInfoPath;
    }
}
