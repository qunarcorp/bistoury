package qunar.tc.bistoury.instrument.client.common;

import java.util.concurrent.locks.Lock;

/**
 * @author zhenyu.nie created on 2019 2019/2/19 15:46
 */
public interface ClassFileBuffer {

    byte[] getClassBuffer(Class clazz, byte[] defaultBuffer);

    void setClassBuffer(Class clazz, byte[] buffer);

    Lock getLock();

    void destroy();
}
