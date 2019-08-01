package qunar.tc.bistoury.remoting.netty;

import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.List;

/**
 * @author zhenyu.nie created on 2019 2019/5/28 16:52
 */
public interface Processor<T> {

    List<Integer> types();

    void process(RemotingHeader header, T command, ResponseHandler handler);
}
