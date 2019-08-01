package qunar.tc.decompiler.jdk827;

import com.google.common.base.Predicate;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author: leix.xie
 * @date: 2019/4/18 14:13
 * @describe：
 */
public class Set827 {

    public static <T> boolean removeIf(Set<T> set, Predicate<? super T> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<T> each = set.iterator();
        while (each.hasNext()) {
            if (filter.apply(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
}
