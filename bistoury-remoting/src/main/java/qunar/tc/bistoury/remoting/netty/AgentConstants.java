package qunar.tc.bistoury.remoting.netty;

import io.netty.util.AttributeKey;

/**
 * @author sen.chai
 * @date 15-6-15
 */
public class AgentConstants {

    public static final String CHANNEL_REQUEST_ID_KEY = "CHANNEL_REQUEST_ID";

    /**
     * 存储requeset的id
     */
    public static final AttributeKey<String> attributeKey = AttributeKey.valueOf(CHANNEL_REQUEST_ID_KEY);

    public static final int VERSION = 10;
}

    