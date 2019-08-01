package qunar.tc.bistoury.common;

/**
 * @author leix.xie
 * @date 2019-07-24 20:01
 * @describe
 */
public class Throwables {
    public static RuntimeException propagate(Throwable throwable) {
        com.google.common.base.Throwables.throwIfUnchecked(throwable);
        throw new RuntimeException(throwable);
    }
}
