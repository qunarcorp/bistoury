package qunar.tc.bistoury.commands.arthas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhenyu.nie created on 2018 2018/10/16 17:04
 */
public class ArthasEntity {

    private static final Logger logger = LoggerFactory.getLogger(ArthasEntity.class);

    private volatile int pid;

    public ArthasEntity(int pid) {
        this.pid = pid;
    }

    public void start() {
        try {
            ArthasStarter.start(pid);
        } catch (Exception e) {
            logger.error("start arthas error, pid [{}]", pid, e);
            throw new RuntimeException("start arthas error, pid [" + pid + "]," + e.getMessage());
        }
    }

    public int getPid() {
        return pid;
    }
}
