package qunar.tc.bistoury.agent.common.pid.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.pid.Jps;
import qunar.tc.bistoury.agent.common.pid.PidHandler;
import qunar.tc.bistoury.agent.common.pid.bean.JpsInfo;
import qunar.tc.bistoury.agent.common.pid.bean.Res;

import java.util.List;

/**
 * @author: leix.xie
 * @date: 2019/3/13 17:14
 * @describe：
 */
public class PidByJpsHandler extends AbstractPidHandler implements PidHandler {

    private static final Logger logger = LoggerFactory.getLogger(PidByJpsHandler.class);

    private static final Splitter SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();

    private static final int JPS_PID_INDEX = 0;
    private static final int JPS_CLASS_INDEX = 1;

    private static final String JPS_SYMBOL_CLASS = System.getProperty("bistoury.pid.handler.jps.symbol.class", "org.apache.catalina.startup.Bootstrap");

    @Override
    public int priority() {
        return Priority.FROM_JPS_PRIORITY;
    }

    @Override
    protected int doGetPid() {
        Res<List<String>> res = getJpsInfo();
        if (res.getCode() == 0) {
            ArrayListMultimap<String, JpsInfo> multimap = parseJpsInfo(res.getData());
            List<JpsInfo> jpsInfos = multimap.get(JPS_SYMBOL_CLASS);
            if (jpsInfos.size() > 0) {
                return jpsInfos.iterator().next().getPid();
            } else {
                return -1;
            }
        } else {
            logger.error(res.getMessage());
            return -1;
        }
    }

    private ArrayListMultimap<String, JpsInfo> parseJpsInfo(List<String> jpsInfos) {
        ArrayListMultimap<String, JpsInfo> multimap = ArrayListMultimap.create();
        for (String jpsInfo : jpsInfos) {
            List<String> list = SPLITTER.splitToList(jpsInfo);
            if (list.size() == 2) {
                final int pid = Integer.parseInt(list.get(JPS_PID_INDEX));
                final String clazz = list.get(JPS_CLASS_INDEX);
                multimap.put(clazz, new JpsInfo(pid, clazz));
            }
        }
        return multimap;
    }

    private Res<List<String>> getJpsInfo() {
        Res<List<String>> res = new Res<>();
        Jps.executeJps(new String[]{"-l"}, res);
        return res;
    }

}
