package qunar.tc.bistoury.magic.classes;

/**
 * @author zhenyu.nie created on 2018 2018/11/30 14:19
 */
public class MagicUtils {

    private static final ThreadLocal<Boolean> magicFlagThreadLocal = new ThreadLocal<>();

    public static void setMagicFlag() {
        magicFlagThreadLocal.set(true);
    }

    public static void removeMagicFlag() {
        magicFlagThreadLocal.remove();
    }

    public static boolean needMagic() {
        Boolean needMagic = magicFlagThreadLocal.get();
        return needMagic != null && needMagic;
    }
}
