package qunar.tc.decompiler.jdk827;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.util.Iterator;
import java.util.List;

/**
 * @author: leix.xie
 * @date: 2019/4/18 14:08
 * @describe：
 */
public class List827 {
    public static <T> void forEach(List<T> list, Function<T, Void> function) {
        for (T t : list) {
            function.apply(t);
        }
    }

    public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (!predicate.apply(t)) {
                iterator.remove();
            }
        }
        return list;
    }

    public static <T> boolean anyMatch(List<T> list, Predicate<? super T> predicate) {
        for (T t : list) {
            if (predicate.apply(t)) {
                return true;
            }
        }
        return false;
    }
}
