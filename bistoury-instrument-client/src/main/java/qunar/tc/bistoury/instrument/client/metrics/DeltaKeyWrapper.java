package qunar.tc.bistoury.instrument.client.metrics;

public abstract class DeltaKeyWrapper<T> extends KeyWrapper<T> {

    protected boolean delta = false;
    protected boolean keep = false;

    public DeltaKeyWrapper(String name) {
        super(name);
    }

    /**
     * 只记录变化量
     */
    public DeltaKeyWrapper<T> delta() {
        this.delta = true;
        return this;
    }

    /**
     * 当两次检查数据一致时，维持变化量
     */
    public DeltaKeyWrapper<T> keep() {
        this.keep = true;
        return this;
    }
}
