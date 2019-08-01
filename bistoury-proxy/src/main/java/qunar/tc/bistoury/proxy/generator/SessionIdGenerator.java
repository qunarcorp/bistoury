package qunar.tc.bistoury.proxy.generator;

import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.agile.LocalHost;
import qunar.tc.bistoury.serverside.util.ServerManager;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leix.xie
 * @date 2019/5/13 14:32
 * @describe
 */
@Service
public class SessionIdGenerator implements IdGenerator {
    private static final String SPLITTER = ".";
    private static final int[] codex = {2, 3, 5, 6, 8, 9, 19, 11, 12, 14, 15, 17, 18};
    private static final AtomicInteger AUTO_INCREMENT_ID = new AtomicInteger(1);
    private static final String LOCAL_HOST = LocalHost.getLocalHost();
    private static final int PID = ServerManager.getPid();

    public String generateId() {
        StringBuilder sb = new StringBuilder(45);
        long time = System.currentTimeMillis();
        String ts = new Timestamp(time).toString();

        for (int idx : codex)
            sb.append(ts.charAt(idx));
        sb.append(SPLITTER).append(LOCAL_HOST);
        sb.append(SPLITTER).append(PID);
        sb.append(SPLITTER).append(AUTO_INCREMENT_ID.getAndIncrement());
        return sb.toString();
    }
}
