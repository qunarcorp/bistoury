/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
