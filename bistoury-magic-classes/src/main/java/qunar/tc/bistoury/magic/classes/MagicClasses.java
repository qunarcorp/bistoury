package qunar.tc.bistoury.magic.classes;

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
        Set<String> nameSet = new HashSet<>();
        nameSet.add("com.fasterxml.jackson.databind.ser.BeanSerializerFactory");
        nameSet.add("com.taobao.arthas.core.advisor.Enhancer");

        Set<String> namePrefixSet = new HashSet<>();
        for (String name : nameSet) {
            namePrefixSet.add(name + "$");
        }

        MAGIC_CLASS_NAME_SET = nameSet;
        MAGIC_CLASS_PREFIX_SET = namePrefixSet;
    }

    public static boolean isMagicClass(String name) {
        if (name == null || name.isEmpty()) {
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
