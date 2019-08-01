package qunar.tc.decompiler.jdk827;

import com.google.common.base.Function;

import java.util.Map;
import java.util.Objects;

/**
 * @author: leix.xie
 * @date: 2019/4/18 11:12
 * @describe：
 */
public class Map827 {
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = map.get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                map.put(key, newValue);
                return newValue;
            }
        }
        return v;
    }

    public static <K, V> boolean remove(Map<K, V> map, Object key, Object value) {
        Object curValue = map.get(key);
        if (!Objects.equals(curValue, value) ||
                (curValue == null && !map.containsKey(key))) {
            return false;
        }
        map.remove(key);
        return true;
    }

    public static <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
        V v = map.get(key);
        if (v == null) {
            v = map.put(key, value);
        }

        return v;
    }
}
