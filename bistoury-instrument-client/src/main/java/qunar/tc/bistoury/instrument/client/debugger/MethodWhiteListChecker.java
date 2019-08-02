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

package qunar.tc.bistoury.instrument.client.debugger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhaohui.yu
 * 8/10/17
 */
public class MethodWhiteListChecker {
    private static Set<String> WHITE_LIST = new HashSet<>();

    static {
        WHITE_LIST.add("equals");
        WHITE_LIST.add("length");
        WHITE_LIST.add("valueOf");
        WHITE_LIST.add("toString");
        WHITE_LIST.add("hashCode");
        WHITE_LIST.add("compareTo");
        WHITE_LIST.add("size");
        WHITE_LIST.add("count");
    }

    public static void check(String methodName) {
        if (!WHITE_LIST.contains(methodName)) {
            throw new RuntimeException("不允许执行自定义方法");
        }
    }
}
