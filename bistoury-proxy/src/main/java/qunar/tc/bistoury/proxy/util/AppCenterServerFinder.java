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

package qunar.tc.bistoury.proxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.application.api.AppServerService;
import qunar.tc.bistoury.application.api.pojo.AppServer;

import java.util.List;

public class AppCenterServerFinder implements ServerFinder {

    private static final Logger logger = LoggerFactory.getLogger(AppCenterServerFinder.class);

    private final AppServerService appServerService;

    public AppCenterServerFinder(AppServerService appServerService) {
        this.appServerService = appServerService;
    }

    @Override
    public List<AppServer> findAgents(String app) {
        return appServerService.getAppServerByAppCode(app);
    }
}
