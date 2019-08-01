package qunar.tc.bistoury.proxy.util;

import com.google.common.util.concurrent.FutureCallback;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 13:53
 */
public interface FutureSuccessCallBack<V> extends FutureCallback<V> {

    @Override
    default void onFailure(Throwable t) {
        // do nothing
    }
}
