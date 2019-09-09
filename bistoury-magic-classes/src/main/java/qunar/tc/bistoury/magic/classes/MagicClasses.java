package qunar.tc.bistoury.magic.classes;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

/**
 * 所有需要修改代码的类都应该注册在这里
 *
 * @author zhenyu.nie created on 2018 2018/11/30 14:02
 */
public class MagicClasses {

    private static final Set<String> MAGIC_CLASS_NAME_SET;

    private static final Set<String> MAGIC_CLASS_PREFIX_SET;

    static {
        MAGIC_CLASS_NAME_SET = new HashSet<>();
        MAGIC_CLASS_NAME_SET.add("com.fasterxml.jackson.databind.ser.BeanSerializerFactory");
        MAGIC_CLASS_NAME_SET.add("com.taobao.arthas.core.advisor.Enhancer");

        MAGIC_CLASS_PREFIX_SET = new HashSet<>();
        for (String name : MAGIC_CLASS_NAME_SET) {
            MAGIC_CLASS_PREFIX_SET.add(name + "$");
        }
    }

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
