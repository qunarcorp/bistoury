package qunar.tc.bistoury.proxy.communicate.ui.handler.encryption;

import qunar.tc.bistoury.proxy.communicate.ui.RequestData;

import java.io.IOException;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 15:52
 */
public interface RequestEncryption {
    RequestData<String> decrypt(String in) throws IOException;
}
