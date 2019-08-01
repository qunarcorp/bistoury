package qunar.tc.bistoury.agent.common.util;

/**
 * @author: leix.xie
 * @date: 2019/1/7 15:17
 * @describe：
 */
public class Response {
    private String type;
    private int status;
    private String message;
    private Object data;

    public Response(String type, int status, String message) {
        this.type = type;
        this.status = status;
        this.message = message;
    }

    public Response(String type, int status, Object data) {
        this.type = type;
        this.status = status;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
