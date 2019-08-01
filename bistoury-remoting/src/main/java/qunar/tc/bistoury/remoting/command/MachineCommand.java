package qunar.tc.bistoury.remoting.command;

/**
 * @author zhenyu.nie created on 2019 2019/5/23 18:15
 */
public class MachineCommand {

    private String command;

    private String workDir;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    @Override
    public String toString() {
        return "MachineCommand{" +
                "command='" + command + '\'' +
                ", workDir='" + workDir + '\'' +
                '}';
    }
}
