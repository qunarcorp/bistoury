package qunar.tc.bistoury.proxy.web.controller;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnection;
import qunar.tc.bistoury.proxy.communicate.agent.AgentConnectionStore;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author leix.xie
 * @date 2019/6/21 12:00
 * @describe
 */
@Controller
@RequestMapping("/proxy/agent/version")
public class AgentVersionController {
    @Autowired
    private AgentConnectionStore agentConnectionStore;

    @ResponseBody
    @RequestMapping("detail")
    public ApiResult getAllAgentConnection() {
        return ResultHelper.success(agentConnectionStore.getAgentConnection());
    }

    @ResponseBody
    @RequestMapping("search")
    public ApiResult getAgentConnection(@RequestParam("agentId") final String agentId) {
        Map<String, AgentConnection> connection = agentConnectionStore.searchConnection(agentId);
        if (!CollectionUtils.isEmpty(connection)) {
            return ResultHelper.success(connection);
        }
        return ResultHelper.fail(-1, "没有查询到 " + agentId + " 相关版本信息");
    }

    @ResponseBody
    @RequestMapping("abstract")
    public ApiResult count() {
        Map<String, AgentConnection> agentConnection = agentConnectionStore.getAgentConnection();
        Map<Integer, List<AgentConnection>> collect = agentConnection.values().stream().collect(Collectors.groupingBy(AgentConnection::getVersion));
        ImmutableSortedMap<Integer, Integer> result = ImmutableSortedMap.<Integer, Integer>naturalOrder().putAll(Maps.transformValues(collect, List::size)).build();
        return ResultHelper.success(result);
    }
}
