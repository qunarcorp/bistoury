package qunar.tc.bistoury.proxy.communicate.ui;

import io.netty.channel.Channel;
import qunar.tc.bistoury.proxy.communicate.Connection;

/**
 * @author zhenyu.nie created on 2019 2019/5/13 14:18
 */
public interface UiConnection extends Connection {

    Channel getChannel();
}
