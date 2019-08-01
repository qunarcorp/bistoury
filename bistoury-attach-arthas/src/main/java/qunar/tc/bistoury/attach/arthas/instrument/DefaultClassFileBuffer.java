package qunar.tc.bistoury.attach.arthas.instrument;

import com.taobao.arthas.core.advisor.Enhancer;
import qunar.tc.bistoury.instrument.client.common.ClassFileBuffer;

import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author zhenyu.nie created on 2019 2019/2/19 15:47
 */
public class DefaultClassFileBuffer implements ClassFileBuffer {

    private static final ClassFileBuffer INSTANCE = new DefaultClassFileBuffer();

    private final Map<Class<?>, byte[]> classBytesCache;

    private DefaultClassFileBuffer() {
        classBytesCache = Enhancer.classBytesCache;

    }

    public static ClassFileBuffer getInstance() {
        return INSTANCE;
    }

    @Override
    public byte[] getClassBuffer(Class clazz, byte[] defaultBuffer) {
        byte[] bytes = classBytesCache.get(clazz);
        if (bytes != null && bytes.length > 0) {
            return bytes;
        }
        return defaultBuffer;
    }

    @Override
    public void setClassBuffer(Class clazz, byte[] buffer) {
        classBytesCache.put(clazz, buffer);
    }

    @Override
    public Lock getLock() {
        return Enhancer.lock;
    }

    @Override
    public void destroy() {
        classBytesCache.clear();
    }
}
