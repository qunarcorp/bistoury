package qunar.tc.bistoury.serverside.bean;

/**
 * @author leix.xie
 * @date 2019/7/2 16:03
 * @describe
 */
public class ApiResult<T> {

    private Integer status;

    private String message;

    private T data;

    public ApiResult() {
    }

    public ApiResult(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "{" +
                "\"status\":" + status +
                ", \"message\":\"" + message + '\"' +
                ", \"data\":" + data +
                ",\"token\":\"dd\"}";
    }
}
