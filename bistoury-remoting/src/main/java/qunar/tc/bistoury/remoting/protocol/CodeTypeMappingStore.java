package qunar.tc.bistoury.remoting.protocol;

/**
 * @author zhenyu.nie created on 2019 2019/5/23 20:03
 */
public interface CodeTypeMappingStore {

    Class<?> getMappingType(int code);
}
