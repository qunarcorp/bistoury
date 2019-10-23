package qunar.tc.bistoury.commands.job;

/**
 * @author zhenyu.nie created on 2019 2019/10/16 19:20
 */
public interface ContinueResponseJob {

    String getId();

    void init();

    boolean doResponse() throws Exception;

    void finish();

    void error(Throwable t);
}
