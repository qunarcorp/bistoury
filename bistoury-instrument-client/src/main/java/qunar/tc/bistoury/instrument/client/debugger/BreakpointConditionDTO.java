package qunar.tc.bistoury.instrument.client.debugger;

import java.util.Map;

/**
 * 判断断点条件是否为真时使用的rootObject
 * 持有运行时类的上下文。同时也变相规定了程序所接受的断点条件的编写规范。
 */
class BreakpointConditionDTO {
    private Map<String, Object> localVariables;
    private Map<String, Object> fields;
    private Map<String, Object> staticFields;

    public Map<String, Object> getLocalVariables() {
        return localVariables;
    }

    public void setLocalVariables(Map<String, Object> localVariables) {
        this.localVariables = localVariables;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(Map<String, Object> staticFields) {
        this.staticFields = staticFields;
    }

    @Override
    public String toString() {
        return "BreakpointConditionDTO{" +
                "localVariables=" + localVariables +
                ", fields=" + fields +
                ", staticFields=" + staticFields +
                '}';
    }
}
