package qunar.tc.bistoury.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zhenyu.nie created on 2018 2018/10/9 11:07
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonResult<T> {

    private int status;
    private String message;
    private T data;

    public JsonResult() {
    }

    @JsonCreator
    public JsonResult(@JsonProperty("status") int status, @JsonProperty("message") String message, @JsonProperty("data") T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isOK() {
        return status == 0;
    }
}

