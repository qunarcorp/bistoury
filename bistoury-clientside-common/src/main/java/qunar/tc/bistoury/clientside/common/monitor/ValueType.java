package qunar.tc.bistoury.clientside.common.monitor;


public enum ValueType {

    VALUE("数值"),
    //
    MIN("最小值"),
    //
    MAX("最大值"),
    //
    MEAN("平均值"),
    //
    STD("方差"),
    //
    P75(""),
    //
    P98(""),
    //
    MEAN_RATE("TPS"),
    //
    MIN_1("1分钟TPS"),
    //
    MIN_5("5分钟TPS"),
    //
    MIN_15("15分钟TPS");

    private final String text;

    ValueType(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}