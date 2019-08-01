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
