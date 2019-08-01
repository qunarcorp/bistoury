package qunar.tc.bistoury.remoting.netty;

import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.util.Set;

/**
 * @author zhenyu.nie created on 2019 2019/5/28 15:38
 */
public interface TaskFactory<T> {

    Set<Integer> codes();

    String name();

    Task create(RemotingHeader header, T command, ResponseHandler handler);
}
