package qunar.tc.bistoury.commands;

import com.google.common.util.concurrent.ListenableFuture;
import qunar.tc.bistoury.commands.job.ContinueResponseJob;
import qunar.tc.bistoury.commands.job.DefaultResponseJobStore;
import qunar.tc.bistoury.commands.job.ResponseJobStore;
import qunar.tc.bistoury.remoting.netty.Task;

/**
 * @author zhenyu.nie created on 2019 2019/10/24 13:58
 */
public abstract class AbstractTask implements Task {

    private static final ResponseJobStore JOB_STORE = DefaultResponseJobStore.getInstance();

    protected abstract ContinueResponseJob createJob();

    protected abstract ListenableFuture<Integer> getResultFuture();

    @Override
    public final ListenableFuture<Integer> execute() {
        start();
        return getResultFuture();
    }

    @Override
    public final void cancel() {
        stop();
        getResultFuture().cancel(true);
    }

    protected void start() {
        JOB_STORE.submit(createJob());
    }

    @Override
    public void pause() {
        JOB_STORE.pause(getId());
    }

    @Override
    public void resume() {
        JOB_STORE.resume(getId());
    }

    protected void stop() {
        JOB_STORE.stop(getId());
    }
}
