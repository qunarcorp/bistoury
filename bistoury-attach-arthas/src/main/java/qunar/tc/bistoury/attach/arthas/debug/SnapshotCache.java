package qunar.tc.bistoury.attach.arthas.debug;

import qunar.tc.bistoury.common.Snapshot;

/**
 * @author zhenyu.nie created on 2018 2018/9/21 17:00
 */
public interface SnapshotCache {

    Snapshot getSnapshot(String id);

    void remove(String id);
}
