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

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.ProxyService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Controller
@Component
public class ConfigController {

    private static final Random random = new Random(System.currentTimeMillis());

    @Resource
    private ProxyService proxyService;

    @RequestMapping("getProxyWebSocketUrl")
    @ResponseBody
    public ApiResult getProxyWebSocketUrl(@RequestParam String agentIp) {
        if (Strings.isNullOrEmpty(agentIp)) {
            return ResultHelper.fail(-2, "no agent ip");
        }

        List<String> result = proxyService.getWebSocketUrl(agentIp);

        if (!result.isEmpty()) {
            //status 为100是new proxy, 0是old proxy
            return ResultHelper.success(100, "new proxy", result.get(random.nextInt(result.size())));
        } else {
            return ResultHelper.fail(1, "no proxy for agent");
        }
    }
}
