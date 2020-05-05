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

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.common.AsyncHttpClientHolder;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.ProxyService;
import qunar.tc.bistoury.ui.util.ProxyInfo;
import qunar.tc.bistoury.ui.util.ProxyInfoParser;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 21:12
 */
@Controller
@RequestMapping("notify")
public class AgentMetaController {

    private static final Logger logger = LoggerFactory.getLogger(AgentMetaController.class);

    private static final AsyncHttpClient httpClient = AsyncHttpClientHolder.getInstance();

    private String proxyAgentMetaRefresh;

    @Resource
    private ProxyService proxyService;

    @PostConstruct
    public void init() {
        DynamicConfigLoader.<LocalDynamicConfig>load("config.properties")
                .addListener(conf -> proxyAgentMetaRefresh = conf.getString("agent.meta.refresh"));
    }

    @RequestMapping("agentMetaUpdate")
    @ResponseBody
    public ApiResult notifyAgentMetaUpdate(@RequestBody List<String> ips) {
        logger.info("notify agent meta update, {}", ips);

        if (ips == null || ips.isEmpty()) {
            return ResultHelper.fail(-2, "no agent ip");
        }

        byte[] byteIps = JacksonSerializer.serializeToBytes(ips);
        List<String> proxyWebSocketUrls = proxyService.getAllProxyUrls();
        for (String proxyWebSocketUrl : proxyWebSocketUrls) {
            Optional<ProxyInfo> optional = ProxyInfoParser.parseProxyInfo(proxyWebSocketUrl);
            if (!optional.isPresent()) {
                continue;
            }
            String url = buildAgentMetaRefreshUrl(optional.get());
            doNotify(url, byteIps);
        }
        return ResultHelper.success(true);
    }

    private String buildAgentMetaRefreshUrl(ProxyInfo proxyInfo) {
        return String.format(proxyAgentMetaRefresh, proxyInfo.getIp(), proxyInfo.getTomcatPort());
    }

    private void doNotify(String url, byte[] byteIps) {
        Request request = httpClient.preparePost(url).setBody(byteIps).build();
        httpClient.executeRequest(request);
    }
}
