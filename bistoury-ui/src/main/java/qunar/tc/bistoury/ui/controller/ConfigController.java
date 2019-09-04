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

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.common.AsyncHttpClientHolder;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.ProxyService;
import qunar.tc.bistoury.ui.util.ProxyInfo;
import qunar.tc.bistoury.ui.util.ProxyInfoParse;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
@Component
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    private static final String SCHEMA = "ws://";
    private static final String PATH = "/ws";
    private static final String COLON = ":";

    private static final AsyncHttpClient httpClient = AsyncHttpClientHolder.getInstance();

    private static final TypeReference<ApiResult<AgentInfo>> AGENT_TYPE_REFERENCE = new TypeReference<ApiResult<AgentInfo>>() {
    };

    private static final Random random = new Random(System.currentTimeMillis());

    private String proxyAgent;

    @Resource
    private ProxyService proxyService;

    @PostConstruct
    public void init() {
        DynamicConfigLoader.<LocalDynamicConfig>load("config.properties").addListener(conf -> proxyAgent = conf.getString("agent.proxy"));
    }

    @RequestMapping("getProxyWebSocketUrl")
    @ResponseBody
    public ApiResult getProxyWebSocketUrl(@RequestParam String agentIp) {
        if (Strings.isNullOrEmpty(agentIp)) {
            return ResultHelper.fail(-2, "no agent ip");
        }

        List<String> result = Lists.newArrayList();
        doGetWebSocketUrl(result, proxyService.getAllProxyUrls(), agentIp);

        if (!result.isEmpty()) {
            //status 为100是new proxy, 0是old proxy
            return ResultHelper.success(100, "new proxy", result.get(random.nextInt(result.size())));
        } else {
            return ResultHelper.fail(1, "no proxy for agent");
        }
    }

    private void doGetWebSocketUrl(List<String> result, List<String> proxyWebSocketUrls, final String agentIp) {
        for (String proxyWebSocketUrl : proxyWebSocketUrls) {
            Optional<ProxyInfo> optional = ProxyInfoParse.parseProxyInfo(proxyWebSocketUrl);
            if (!optional.isPresent()) {
                continue;
            }
            ProxyInfo proxyInfo = optional.get();
            String url = buildProxyAgentUrl(proxyInfo);
            if (existAgent(url, agentIp)) {
                result.add(buildWebsocketUrl(proxyInfo));
            }
        }
    }

    private String buildWebsocketUrl(ProxyInfo proxyInfo) {
        return SCHEMA + proxyInfo.getIp() + COLON + proxyInfo.getWebsocketPort() + PATH;
    }

    private String buildProxyAgentUrl(ProxyInfo proxyInfo) {
        return String.format(proxyAgent, proxyInfo.getIp(), proxyInfo.getTomcatPort());
    }

    private boolean existAgent(String url, @RequestParam String agentIp) {
        try {
            AsyncHttpClient.BoundRequestBuilder builder = httpClient.prepareGet(url);
            builder.addQueryParam("ip", agentIp);
            Response response = httpClient.executeRequest(builder.build()).get();
            if (response.getStatusCode() == 200) {
                ApiResult<AgentInfo> result = JacksonSerializer.deSerialize(response.getResponseBody("utf8"), AGENT_TYPE_REFERENCE);
                if (result.getStatus() == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("query exist agent error, agent ip [{}], url [{}]", agentIp, url, e);
        }
        return false;
    }


    private static class AgentInfo {
        private String ip;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }


}
