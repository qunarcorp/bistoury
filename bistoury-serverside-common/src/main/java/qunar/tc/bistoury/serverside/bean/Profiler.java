package qunar.tc.bistoury.serverside.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * @author cai.wen created on 2019/10/29 20:39
 */
public class Profiler {

    private long id;

    private String profilerId;

    private String operator;

    private String appCode;

    private String agentId;

    private int pid;

    private Timestamp startTime;

    private Timestamp updateTime;

    private State state;

    private int duration;

    private int interval;

    private Mode mode;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfilerId() {
        return profilerId;
    }

    public void setProfilerId(String profilerId) {
        this.profilerId = profilerId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public enum State {

        start(0), stop(1), ready(2), analyzed(3), error(4);

        public final int code;

        State(int code) {
            this.code = code;
        }

        public static State fromCode(int code) {
            for (State state : values()) {
                if (state.code == code) {
                    return state;
                }
            }
            throw new IllegalArgumentException("no code found in State.");
        }
    }

    public enum Mode {

        sampler(1), async_sampler(0);

        public final int code;

        Mode(int code) {
            this.code = code;
        }

        public static Mode fromCode(int code) {
            for (Mode mode : values()) {
                if (mode.code == code) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("no code found in Mode.");
        }
    }
}
