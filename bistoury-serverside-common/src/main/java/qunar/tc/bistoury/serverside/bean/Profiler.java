package qunar.tc.bistoury.serverside.bean;

import java.util.Date;

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

    private Date startTime;

    private Date updateTime;

    private State state;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public static enum State {

        start(0),  stop(1);

        private final int code;

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
}
