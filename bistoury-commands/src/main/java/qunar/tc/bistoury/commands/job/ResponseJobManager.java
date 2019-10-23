package qunar.tc.bistoury.commands.job;

import qunar.tc.bistoury.agent.common.WritableListener;

/**
 * @author zhenyu.nie created on 2019 2019/10/16 17:57
 */
public interface ResponseJobManager extends WritableListener {

    void submit(ContinueResponseJob job);

    void pause(String id);

    void resume(String id);

    void stop(String id);
}
