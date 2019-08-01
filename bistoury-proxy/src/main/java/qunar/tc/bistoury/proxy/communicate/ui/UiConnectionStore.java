package qunar.tc.bistoury.proxy.communicate.ui;

import com.google.common.base.Optional;
import io.netty.channel.Channel;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 11:38
 */
public interface UiConnectionStore {

    UiConnection register(Channel channel);

    Optional<UiConnection> getConnection(Channel channel);
}
