package qunar.tc.bistoury.remoting.netty;

import com.google.common.util.concurrent.ListenableFuture;
import qunar.tc.bistoury.agent.common.job.ResponseJobStore;

/**
 * @author zhenyu.nie created on 2019 2019/10/30 15:51
 */
public class DefaultRunningTask implements RunnableTask {

    private final ResponseJobStore jobStore;

    private final Task task;

    public DefaultRunningTask(ResponseJobStore jobStore, Task task) {
        this.jobStore = jobStore;
        this.task = task;
    }

    @Override
    public String getId() {
        return task.getId();
    }

    @Override
    public long getMaxRunningMs() {
        return task.getMaxRunningMs();
    }

    @Override
    public final ListenableFuture<Integer> execute() {
        start();
        return task.getResultFuture();
    }

    @Override
    public final void cancel() {
        stop();
    }

    @Override
    public void pause() {
        jobStore.pause(getId());
    }

    @Override
    public void resume() {
        jobStore.resume(getId());
    }

    private void start() {
        jobStore.submit(task.createJob());
    }

    private void stop() {
        jobStore.stop(getId());
    }
}
