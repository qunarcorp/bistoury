package qunar.tc.bistoury.attach.arthas.debug;

import qunar.tc.bistoury.common.Snapshot;

/**
 * @author zhenyu.nie created on 2018 2018/11/29 14:19
 */
public interface RemoveListener {

    void remove(String breakpointId, Snapshot snapshot);
}
