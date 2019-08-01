package qunar.tc.bistoury.agent.common.pid.bean;

/**
 * @author: leix.xie
 * @date: 2019/3/13 17:16
 * @describe：
 */
public class Res<T> {
    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
