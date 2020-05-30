package qunar.tc.bistoury.common.profiler.method;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cai.wen created on 2019/10/17 10:59
 */
public class MethodCache {

    private static final Map<MethodInfo, Integer> infoCache = new ConcurrentHashMap<>();

    private static final AtomicInteger INFO_ID_GENERATOR = new AtomicInteger(0);

    public static int getMethodTagId(MethodInfo methodInfo) {
        Integer id = infoCache.get(methodInfo);
        if (id == null) {
            id = INFO_ID_GENERATOR.incrementAndGet();
            infoCache.put(methodInfo, id);
        }
        return id;
    }

    public static Map<Integer, MethodInfo> getIdToInfoMapping() {
        Map<Integer, MethodInfo> idMap = Maps.newHashMapWithExpectedSize(infoCache.size());
        for (Map.Entry<MethodInfo, Integer> entry : infoCache.entrySet()) {
            idMap.put(entry.getValue(), entry.getKey());
        }
        return idMap;
    }
}
