package qunar.tc.bistoury.commands.qjtools.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 用于新起进程,失败调用 System.exit
 * Created by cai.wen on 18-12-17.
 */
public class ReflectUtil {
    public static void setField(Object target, Class<?> clazz, String name, Object value) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static Object getField(Object target, Class<?> clazz, String name) {

        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public static Object invokeMethod(Object target, Method method, Object... args) {
        method.setAccessible(true);
        try {
            return method.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}