package qunar.tc.bistoury.proxy.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.agile.LocalHost;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.util.ResultHelper;

import javax.annotation.PostConstruct;

/**
 * @author leix.xie
 * @date 2019/5/23 16:22
 * @describe
 */
@Controller
public class ProxyConfigForAgentGetController {

    private ProxyConfig proxyConfig;

    @PostConstruct
    public void init() {
        DynamicConfig dynamicConfig = DynamicConfigLoader.load("global.properties");

        proxyConfig = new ProxyConfig(
                LocalHost.getLocalHost(),
                dynamicConfig.getInt("agent.newport", -1),
                dynamicConfig.getInt("heartbeatSec", 30));
    }

    @ResponseBody
    @RequestMapping("/proxy/config/foragent")
    public ApiResult getProxyConfig() {
        return ResultHelper.success(proxyConfig);
    }

    private static class ProxyConfig {

        private final String ip;

        private final int port;

        private final int heartbeatSec;

        private ProxyConfig(String ip, int port, int heartbeatSec) {
            this.ip = ip;
            this.port = port;
            this.heartbeatSec = heartbeatSec;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public int getHeartbeatSec() {
            return heartbeatSec;
        }

        @Override
        public String toString() {
            return "ProxyConfig{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", heartbeatSec=" + heartbeatSec +
                    '}';
        }
    }
}
