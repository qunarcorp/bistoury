package qunar.tc.bistoury.instrument.client.util;

import org.objectweb.asm.Type;

/**
 * @author: leix.xie
 * @date: 2019/1/4 9:35
 * @describe：
 */
public class DescDeal {
    public static String generateNewName(final String name) {
        return "$$qmonitor$" + name.replace('<', '_').replace('>', '_') + "$generated";
    }

    public static String getSimplifyMethodDesc(String desc) {
        StringBuilder sb = new StringBuilder();
        Type methodType = Type.getMethodType(desc);
        Type[] types = methodType.getArgumentTypes();
        for (Type type : types) {
            sb.append(getName(type.getClassName()));
            sb.append(",");
        }
        String result = sb.toString();
        if (result.endsWith(",")) {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static String getName(String className) {
        int index = className.lastIndexOf(".") + 1;
        return className.substring(index);
    }
}
