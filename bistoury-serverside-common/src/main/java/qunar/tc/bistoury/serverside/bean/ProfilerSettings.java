package qunar.tc.bistoury.serverside.bean;

public class ProfilerSettings {

    private String appCode;

    private int duration;

    private int frequency;

    private int mode;

    private String command;

    public ProfilerSettings(String appCode) {
        this.appCode = appCode;
    }

    public ProfilerSettings(String appCode, int duration, int frequency, int mode, String command) {
        this.appCode = appCode;
        this.duration = duration;
        this.frequency = frequency;
        this.mode = mode;
        this.command = command;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
