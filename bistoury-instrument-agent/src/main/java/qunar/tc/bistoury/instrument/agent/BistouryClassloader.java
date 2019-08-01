package qunar.tc.bistoury.instrument.agent;

import sun.misc.CompoundEnumeration;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

/**
 * @author zhenyu.nie created on 2018 2018/11/19 19:45
 */
public class BistouryClassloader extends URLClassLoader {

    private ClassLoader magicClassLoader;

    private Method isMagicClassMethod;

    private final ClassLoader resourceSearchParent = getSystemClassLoader().getParent();

    public BistouryClassloader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
    }

    public void setMagicClassSetting(ClassLoader magicClassLoader, Method isMagicClassMethod) {
        this.magicClassLoader = magicClassLoader;
        this.isMagicClassMethod = isMagicClassMethod;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return getResolvedClass(loadedClass, resolve);
        }

        // 优先从parent（SystemClassLoader）里加载系统类和spy类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.")
                || name.startsWith("java.")
                || name.startsWith("qunar.tc.bistoury.instrument.spy."))) {
            return super.loadClass(name, resolve);
        }

        Class<?> magicClass = loadFromMagic(name);
        if (magicClass != null) {
            return getResolvedClass(magicClass, resolve);
        }

        try {
            Class<?> theClass = findClass(name);
            return getResolvedClass(theClass, resolve);
        } catch (Exception e) {
            // ignore
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public URL getResource(String name) {
        URL url = resourceSearchParent.getResource(name);
        if (url != null) {
            return url;
        }
        return findResource(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];
        tmp[0] = resourceSearchParent.getResources(name);
        tmp[1] = findResources(name);
        return new CompoundEnumeration<>(tmp);
    }

    private Class<?> getResolvedClass(Class<?> loadedClass, boolean resolve) {
        if (resolve) {
            resolveClass(loadedClass);
        }
        return loadedClass;
    }

    private Class<?> loadFromMagic(String name) {
        try {
            if (magicClassLoader != null && isMagicClass(name)) {
                return magicClassLoader.loadClass(name);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isMagicClass(String name) throws Throwable {
        return name != null && isMagicClassMethod != null
                && (boolean) isMagicClassMethod.invoke(null, name);
    }
}
