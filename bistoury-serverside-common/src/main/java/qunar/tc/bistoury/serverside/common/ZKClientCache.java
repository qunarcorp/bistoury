package qunar.tc.bistoury.serverside.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/7/4 19:57
 * @describe
 */
public class ZKClientCache {
    private static final Logger logger = LoggerFactory.getLogger(ZKClientCache.class);
    private static final Map<String, ZKClient> CACHE = new HashMap<>();

    public synchronized static ZKClient get(String address) {
        logger.info("get zkclient for {}", address);
        ZKClient client = CACHE.get(address);
        if (client == null) {
            client = new ZKClient(address);
            CACHE.put(address, client);
        } else {
            client = CACHE.get(address);
        }
        client.incrementReference();
        return client;
    }

}
