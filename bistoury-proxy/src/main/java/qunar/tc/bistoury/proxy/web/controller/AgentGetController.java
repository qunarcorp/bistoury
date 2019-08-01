package qunar.tc.bistoury.proxy.web.controller;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;

import java.util.Optional;

/**
 * @author leix.xie
 * @date 2019/5/23 12:03
 * @describe
 */
@Controller
public class AgentGetController {
    @Autowired
    private AgentConnectionStore agentConnectionStore;

    @ResponseBody
    @RequestMapping("/proxy/agent/get")
    public ApiResult getAgentInfo(@RequestParam final String ip) {
        if (Strings.isNullOrEmpty(ip)) {
            return ResultHelper.fail(-1, "argument error", null);
        }
        Optional<AgentConnection> connection = agentConnectionStore.getConnection(ip);
        if (connection.isPresent() && connection.get().isActive()) {
            AgentInfo agentInfo = new AgentInfo(ip);
            return ResultHelper.success(agentInfo);
        }

        return ResultHelper.fail(-1, "no agent", null);
    }

    private static class AgentInfo {
        private final String ip;

        public AgentInfo(String ip) {
            this.ip = ip;
        }

        public String getIp() {
            return ip;
        }
    }
}
