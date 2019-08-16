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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import qunar.tc.bistoury.application.api.AppServerService;
import qunar.tc.bistoury.application.api.pojo.AppServer;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.security.LoginContext;

/**
 * @author leix.xie
 * @date 2019/7/3 16:35
 * @describe
 */
@Controller
@RequestMapping("api/app/server/")
public class AppServerController {

    @Autowired
    private AppServerService appServerService;

    @ResponseBody
    @RequestMapping("list")
    public ApiResult getAppServerByAppCode(final String appCode) {
        return ResultHelper.success(this.appServerService.getAppServerByAppCode(appCode));
    }

    @ResponseBody
    @RequestMapping("autoJMapHistoEnable")
    public ApiResult changeAutoJMapHistoEnable(final String serverId, final boolean enable) {
        String loginUser = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.success(this.appServerService.changeAutoJMapHistoEnable(serverId, enable, loginUser));
    }

    @ResponseBody
    @RequestMapping("autoJStackEnable")
    public ApiResult changeAutoJStackEnable(final String serverId, final boolean enable) {
        String loginUser = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.success(this.appServerService.changeAutoJStackEnable(serverId, enable, loginUser));
    }

    @ResponseBody
    @RequestMapping("delete")
    public ApiResult deleteAppServerByServerId(final String serverId) {
        String loginUser = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.success(this.appServerService.deleteAppServerByServerId(serverId, loginUser));
    }

    @ResponseBody
    @RequestMapping("save")
    public ApiResult saveAppServer(final AppServer appServer) {
        String loginUser = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.success(this.appServerService.saveAppServer(appServer, loginUser));
    }
}
