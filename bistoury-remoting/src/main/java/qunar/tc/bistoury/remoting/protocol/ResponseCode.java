package qunar.tc.bistoury.remoting.protocol;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/5/30 15:02
 * @describe
 */
public enum ResponseCode {
    RESP_TYPE_HEARTBEAT(0, -2),
    RESP_TYPE_EXCEPTION(-1, -1),
    RESP_TYPE_CONTENT(-2, 1),
    RESP_TYPE_SINGLE_END(-3, 2),
    RESP_TYPE_ALL_END(-4, 3);

    private int code;
    private int oldCode;
    private static final Map<Integer, ResponseCode> oldCodeMap = new HashMap<>();
    private static final Map<Integer, ResponseCode> codeMap = new HashMap<>();

    static {
        for (ResponseCode value : ResponseCode.values()) {
            codeMap.put(value.getCode(), value);
            oldCodeMap.put(value.getOldCode(), value);
        }
    }

    ResponseCode(int code, int oldCode) {
        this.code = code;
        this.oldCode = oldCode;
    }

    public static Optional<ResponseCode> valueOfOldCode(int oldCode) {
        ResponseCode responseCode = oldCodeMap.get(oldCode);
        if (responseCode == null) {
            return Optional.absent();
        }
        return Optional.of(responseCode);
    }

    public static Optional<ResponseCode> valueOfCode(int code) {
        ResponseCode responseCode = codeMap.get(code);
        if (responseCode == null) {
            return Optional.absent();
        }
        return Optional.of(responseCode);
    }

    public int getCode() {
        return code;
    }

    public int getOldCode() {
        return oldCode;
    }
}
