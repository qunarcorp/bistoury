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

package qunar.tc.bistoury.attach.arthas.debug;

import java.util.Properties;

/**
 * @author: leix.xie
 * @date: 2019/3/29 11:18
 * @describe：
 */
public class ClassInfo {
    private String classPath;
    private String jarName;
    private Boolean maven = false;
    private Properties mavenInfo;

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public Boolean getMaven() {
        return maven;
    }

    public void setMaven(Boolean maven) {
        this.maven = maven;
    }

    public Properties getMavenInfo() {
        return mavenInfo;
    }

    public void setMavenInfo(Properties mavenInfo) {
        this.mavenInfo = mavenInfo;
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "classPath='" + classPath + '\'' +
                ", jarName='" + jarName + '\'' +
                ", maven=" + maven +
                ", mavenInfo=" + mavenInfo +
                '}';
    }
}
