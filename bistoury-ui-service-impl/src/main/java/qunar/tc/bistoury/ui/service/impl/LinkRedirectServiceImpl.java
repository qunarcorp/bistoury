/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
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
        DynamicConfig<LocalDynamicConfig> dynamicConfig = DynamicConfigLoader.load("url_redirect.properties");
        dynamicConfig.addListener(config -> this.dynamicConfig = config);
    }

    @Override
    public String getURLByName(final String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "url name cannot be null or empty");
        String url = dynamicConfig.getString(name);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "url cannot be null or empty");
        return url;
    }
}
