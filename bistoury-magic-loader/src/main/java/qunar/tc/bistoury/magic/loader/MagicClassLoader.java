package qunar.tc.bistoury.magic.loader;

import qunar.tc.bistoury.magic.classes.MagicClasses;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author zhenyu.nie created on 2018 2018/11/30 13:44
 * MagicClassLaoder用来加载自己修改了依赖jar包代码的类
 */
public class MagicClassLoader extends URLClassLoader {

    public MagicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }

        if (MagicClasses.isMagicClass(name)) {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } else {
            return super.loadClass(name, resolve);
        }
    }
}
