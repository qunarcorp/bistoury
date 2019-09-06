package qunar.tc.bistoury.magic.classes;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * 所有需要修改代码的类都应该注册在这里
 *
 * @author zhenyu.nie created on 2018 2018/11/30 14:02
 */
public class MagicClasses {

    private static final Set<String> MAGIC_CLASS_NAME_SET = ImmutableSet.of(
            "com.fasterxml.jackson.databind.ser.BeanSerializerFactory",
            "com.taobao.arthas.core.advisor.Enhancer"
    );

    private static final Set<String> MAGIC_CLASS_PREFIX_SET = ImmutableSet.of(
            "com.taobao.arthas.core.advisor.Enhancer$"
    );

    public static boolean isMagicClass(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return false;
        }

        if (MAGIC_CLASS_NAME_SET.contains(name)) {
            return true;
        }

        for (String prefix : MAGIC_CLASS_PREFIX_SET) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }
}
