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

import org.springframework.stereotype.Service;

import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
import qunar.tc.bistoury.ui.model.ReleaseInfo;
import qunar.tc.bistoury.ui.service.ReleaseInfoService;
import qunar.tc.bistoury.ui.util.PropertiesReleaseInfoParser;
import qunar.tc.bistoury.ui.util.ReleaseInfoParse;

import javax.annotation.PostConstruct;

/**
 * @author leix.xie
 * @date 2019/7/10 10:27
 * @describe
 */
@Service
public class ReleaseInfoServiceImpl implements ReleaseInfoService {


    private static final ReleaseInfoParse RELEASE_INFO_PARSE = new PropertiesReleaseInfoParser();
    private static final String DEFAULT_RELEASE_INFO_PATH = "../webapps/releaseInfo.properties";
    private static final String DEFAULT = "default";
    private String defaultReleaseInfoPath = DEFAULT_RELEASE_INFO_PATH;

    @PostConstruct
    public void init() {
        DynamicConfig<LocalDynamicConfig> dynamicConfig = DynamicConfigLoader.load("releaseInfo_config.properties", false);
        dynamicConfig.addListener(aDynamicConfig -> {
            defaultReleaseInfoPath = aDynamicConfig.getString(DEFAULT, DEFAULT_RELEASE_INFO_PATH);
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
