package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.ui.service.URLRedirectService;

import javax.annotation.PostConstruct;

/**
 * @author leix.xie
 * @date 2019/7/10 15:37
 * @describe
 */
@Service
public class LinkRedirectServiceImpl implements URLRedirectService {

    private DynamicConfig dynamicConfig;

    @PostConstruct
    public void init() {
        DynamicConfigLoader.load("url_redirect.properties").addListener(config -> dynamicConfig = config);
    }

    @Override
    public String getURLByName(final String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "url name cannot be null or empty");
        String url = dynamicConfig.getString(name);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "url cannot be null or empty");
        return url;
    }
}
