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

package qunar.tc.bistoury.remoting.command;

import qunar.tc.bistoury.common.URLCoder;

/**
 * @author leix.xie
 * @date 2019/5/27 10:55
 * @describe
 */
public class DecompilerCommand {
    private String className;
    private String classPath;

    public String getClassName() {
        return URLCoder.decode(className);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPath() {
        return URLCoder.decode(classPath);
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    @Override
    public String toString() {
        return "DecompilerCommand{" +
                "className='" + className + '\'' +
                ", classPath='" + classPath + '\'' +
                '}';
    }
}
