package qunar.tc.bistoury.clientside.common.monitor;

import java.util.List;

/**
 * @author: leix.xie
 * @date: 2019/1/8 16:43
 * @describe：
 */
public class MetricsSnapshot {
    private String name;
    private Long timestamp;
    private List<MetricsData> metricsData;

    public MetricsSnapshot() {

    }

    public MetricsSnapshot(String name, Long timestamp, List<MetricsData> metricsData) {
        this.name = name;
        this.timestamp = timestamp;
        this.metricsData = metricsData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<MetricsData> getMetricsData() {
        return metricsData;
    }

    public void setMetricsData(List<MetricsData> metricsData) {
        this.metricsData = metricsData;
    }
}
