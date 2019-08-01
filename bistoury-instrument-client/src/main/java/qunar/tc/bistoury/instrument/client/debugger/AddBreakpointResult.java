package qunar.tc.bistoury.instrument.client.debugger;

/**
 * @author zhenyu.nie created on 2018 2018/11/29 19:43
 */
public class AddBreakpointResult {

    private String id;

    private boolean newId;

    public AddBreakpointResult(String id, boolean newId) {
        this.id = id;
        this.newId = newId;
    }

    public String getId() {
        return id;
    }

    public boolean isNewId() {
        return newId;
    }
}
