package qunar.tc.bistoury.commands.job;

/**
 * @author zhenyu.nie created on 2019 2019/10/23 16:46
 */
public abstract class ForwardContinueResponseJob implements ContinueResponseJob {

    protected ForwardContinueResponseJob() {

    }

    protected abstract ContinueResponseJob delegate();

    @Override
    public String getId() {
        return delegate().getId();
    }

    @Override
    public void init() throws Exception {
        delegate().init();
    }

    @Override
    public boolean doResponse() throws Exception {
        return delegate().doResponse();
    }

    @Override
    public void clear() {
        delegate().clear();
    }

    @Override
    public void finish() throws Exception {
        delegate().finish();
    }

    @Override
    public void error(Throwable t) {
        delegate().error(t);
    }
}
