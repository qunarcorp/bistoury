package qunar.tc.bistoury.common;

/**
 * @author zhenyu.nie created on 2018 2018/11/26 15:44
 */
public class CodeProcessResponse<T> {

    private String id;

    private int code;

    private T data;

    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CodeProcessResponse{" +
                "id='" + id + '\'' +
                ", code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
