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
