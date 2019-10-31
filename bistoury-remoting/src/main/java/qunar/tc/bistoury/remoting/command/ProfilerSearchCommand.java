package qunar.tc.bistoury.remoting.command;

/**
 * @author cai.wen created on 2019/10/28 11:38
 */
public class ProfilerSearchCommand {

    private String id;

    public ProfilerSearchCommand() {
    }

    public ProfilerSearchCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
