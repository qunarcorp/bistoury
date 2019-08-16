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

import qunar.tc.bistoury.application.api.ApplicationService;
import qunar.tc.bistoury.application.api.pojo.Application;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.security.LoginContext;

/**
 * @author leix.xie
 * @date 2019/7/2 20:07
 * @describe
 */
@Controller
@RequestMapping("api/application/")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @ResponseBody
    @RequestMapping("list")
    public ApiResult getAppList() {
        String userCode = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.success(this.applicationService.getAllApplications(userCode));
    }

    @ResponseBody
    @RequestMapping("owner")
    public ApiResult getAppOwner(final String appCode) {
        return ResultHelper.success(this.applicationService.getAppOwner(appCode));
    }

    @ResponseBody
    @RequestMapping("save")
    public ApiResult save(Application application) {
        String loginUser = LoginContext.getLoginContext().getLoginUser();
        boolean admin = LoginContext.getLoginContext().isAdmin();
        return ResultHelper.success(this.applicationService.save(application, loginUser, admin));
    }
}
