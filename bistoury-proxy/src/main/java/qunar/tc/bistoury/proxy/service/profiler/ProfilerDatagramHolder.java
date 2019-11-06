package qunar.tc.bistoury.proxy.service.profiler;

import qunar.tc.bistoury.remoting.protocol.Datagram;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cai.wen created on 2019/11/6 8:46
 */
public class ProfilerDatagramHolder {

    private final AtomicInteger time;

    private final Datagram datagram;

    private final String profilerId;

    private final String agentId;

    public ProfilerDatagramHolder(String agentId, String profilerId, Datagram datagram, int duration) {
        this.datagram = datagram;
        this.profilerId = profilerId;
        this.agentId = agentId;
        this.time = new AtomicInteger(-duration);
    }

    public Datagram getDatagram() {
        return datagram;
    }

    public String getProfilerId() {
        return profilerId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void decreaseTime(int delay) {
        time.addAndGet(delay);
    }

    public boolean isExpired() {
        return time.get() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfilerDatagramHolder that = (ProfilerDatagramHolder) o;
        return Objects.equals(datagram, that.datagram) &&
                Objects.equals(profilerId, that.profilerId) &&
                Objects.equals(agentId, that.agentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datagram, profilerId, agentId);
    }
}
