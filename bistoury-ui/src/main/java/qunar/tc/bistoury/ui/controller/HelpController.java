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

package qunar.tc.bistoury.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;

/**
 * @author: leix.xie
 * @date: 2018/11/8 15:12
 * @describe：
 */
@Controller
public class HelpController {

    private static String version;

    static {
        DynamicConfig<LocalDynamicConfig> dynamicConfig = DynamicConfigLoader.load("config.properties");
        dynamicConfig.addListener(conf -> version = conf.getString("agent.lastVersion", "1.0"));
    }

    @ResponseBody
    @RequestMapping("version")
    public Object getLatestVersion() {
        return version;
    }
}
