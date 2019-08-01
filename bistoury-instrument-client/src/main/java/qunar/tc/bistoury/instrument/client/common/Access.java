package qunar.tc.bistoury.instrument.client.common;

/**
 * 修饰符计算，比如去除PUBLIC；添加PRIVATE；判断是否是ABSTRACT等等。
 *
 * @author Daniel Li
 * @since 30 March 2015
 */
public class Access {

    private int access;

    public static Access of(int access) {
        return new Access(access);
    }

    private Access(int access) {
        this.access = access;
    }

    public Access remove(int remove) {
        access &= ~remove;
        return this;
    }

    public Access add(int add) {
        access |= add;
        return this;
    }

    public boolean contain(int partAccess) {
        return (access & partAccess) != 0;
    }

    public int get() {
        return access;
    }
}
