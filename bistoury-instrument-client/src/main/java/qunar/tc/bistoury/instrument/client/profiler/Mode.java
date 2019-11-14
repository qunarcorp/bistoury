package qunar.tc.bistoury.instrument.client.profiler;

/**
 * @author cai.wen created on 2019/10/23 10:45
 */
public enum Mode {
    sampler(1), async_sampler(0);

    private final int code;

    Mode(int code) {
        this.code = code;
    }

    public static Mode codeOf(int code) {
        for (Mode mode : values()) {
            if (mode.code == code) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid mode code: " + code);
    }
}
