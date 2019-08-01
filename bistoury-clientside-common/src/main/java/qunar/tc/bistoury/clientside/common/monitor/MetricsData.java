package qunar.tc.bistoury.clientside.common.monitor;

import java.util.Arrays;

/**
 * @author: leix.xie
 * @date: 2019/1/7 11:58
 * @describe：
 */
public class MetricsData {
    private String name;
    private int type;
    private float[] data;

    public MetricsData() {

    }

    public MetricsData(String name, int type, float[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float[] getData() {
        return data;
    }

    public void setData(float[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "name: " + name + ", type: " + type + ", data: " + Arrays.toString(data);
    }
}
