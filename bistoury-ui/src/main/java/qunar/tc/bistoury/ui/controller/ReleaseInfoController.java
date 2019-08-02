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
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.ReleaseInfoService;

/**
 * @author leix.xie
 * @date 2019/7/10 10:19
 * @describe
 */
@Controller
@RequestMapping("api/release/info/")
public class ReleaseInfoController {

    @Autowired
    private ReleaseInfoService releaseInfoService;

    @ResponseBody
    @RequestMapping("parse")
    public ApiResult parseReleaseInfo(final String content) {
        return ResultHelper.success(this.releaseInfoService.parseReleaseInfo(content));
    }

    @ResponseBody
    @RequestMapping("path")
    public ApiResult defaultReleaseInfoPath() {
        return ResultHelper.success(this.releaseInfoService.getDefaultReleaseInfoPath());
    }
}
