package qunar.tc.bistoury.common;

/**
 * @author zhenyu.nie created on 2018 2018/11/28 19:49
 */
public class TypeResponse<T> {

    private String type;

    private CodeProcessResponse<T> data;

    public TypeResponse() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CodeProcessResponse<T> getData() {
        return data;
    }

    public void setData(CodeProcessResponse<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TypeResponse{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
