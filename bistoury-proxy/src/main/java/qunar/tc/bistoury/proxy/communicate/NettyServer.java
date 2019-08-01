package qunar.tc.bistoury.proxy.communicate;

/**
 * @author zhenyu.nie created on 2019 2019/7/19 17:00
 */
public interface NettyServer {

    void start();

    boolean isActive();

    void stop();
}
