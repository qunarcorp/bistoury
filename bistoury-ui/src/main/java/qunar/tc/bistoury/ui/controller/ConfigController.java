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
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.ProxyService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Controller
@Component
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    private static final String SCHEMA = "ws://";
    private static final String PATH = "/ws";

    private static final AsyncHttpClient httpClient = AsyncHttpClientHolder.getInstance();

    private static final TypeReference<ApiResult<AgentInfo>> AGENT_TYPE_REFERENCE = new TypeReference<ApiResult<AgentInfo>>() {
    };

    private static final Random random = new Random(System.currentTimeMillis());

    private String proxyAgent;

    @Resource
    private ProxyService proxyService;

    @PostConstruct
    public void init() {
        DynamicConfigLoader.load("config.properties").addListener(conf -> proxyAgent = conf.getString("agent.proxy"));
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
            String proxyIp = proxyWebSocketUrl.substring(0, proxyWebSocketUrl.indexOf(':'));

            String url = buildProxyAgentUrl(proxyIp);
            if (existAgent(url, agentIp)) {
                result.add(SCHEMA + proxyWebSocketUrl + PATH);
            }
        }
    }

    private String buildProxyAgentUrl(String proxyIp) {
        return String.format(proxyAgent, proxyIp);
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
