package qunar.tc.bistoury.instrument.client.profiler.async;

/**
 * @author cai.wen created on 2019/11/12 11:05
 */
public class ProfilerCommand {

    private ProfilerAction action;

    private String event;

    private Long interval;

    private String file;

    //是否根据线程id区分线程栈
    private boolean threads;

    public String getRealCommand() {
        StringBuilder command = new StringBuilder();

        // start - start profiling
        // stop - stop profiling
        command.append(action).append(',');

        if (this.event != null) {
            command.append("event=").append(this.event).append(',');
        }
        if (this.file != null) {
            command.append("file=").append(this.file).append(',');
        }
        if (this.interval != null) {
            command.append("interval=").append(this.interval).append(',');
        }
        if (this.threads) {
            command.append("threads").append(',');
        }

        return command.toString();
    }

    public ProfilerAction getAction() {
        return action;
    }

    public void setAction(ProfilerAction action) {
        this.action = action;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isThreads() {
        return threads;
    }

    public void setThreads(boolean threads) {
        this.threads = threads;
    }

    public enum ProfilerAction {
        start, stop
    }
}
