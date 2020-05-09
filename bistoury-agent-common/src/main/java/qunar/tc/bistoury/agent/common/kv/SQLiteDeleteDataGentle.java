package qunar.tc.bistoury.agent.common.kv;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author leix.xie
 * @date 2019/12/11 14:10
 * @describe
 */
public class SQLiteDeleteDataGentle {
    private static final Logger logger = LoggerFactory.getLogger(SQLiteDeleteDataGentle.class);
    private SQLiteStoreImpl sqLite;

    //Time to execute cleanup task, used for the 24-hour clock.
    private static final int EXECUTE_CLEAN_DATA_HOUR = 3;

    private static final ListeningScheduledExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("clean_sqlite_expire_data")));

    private static final MetaStore META_STORE = MetaStores.getMetaStore();

    public SQLiteDeleteDataGentle(SQLiteStoreImpl sqLite) {
        this.sqLite = sqLite;
    }

    public void start() {
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = new GregorianCalendar();
                if (calendar.get(Calendar.HOUR_OF_DAY) == EXECUTE_CLEAN_DATA_HOUR) {
                    long start = System.currentTimeMillis();
                    int delete = deleteGentle();
                    logger.info("finish delete expire data, count: {}, cost: {}", delete, System.currentTimeMillis() - start);
                }
            }
        }, 1, 1, TimeUnit.HOURS);
    }

    /**
     * 削峰
     *
     * @return
     */
    private int deleteGentle() {

        long expireTimestamp = System.currentTimeMillis();
        int count = 0;

        while (true) {
            int limit = META_STORE.getIntProperty("delete.each.query.limit", 10000);
            int slice = META_STORE.getIntProperty("delete.slice.size", 100);
            long sleepTime = META_STORE.getLongProperty("delete.sleep.ms", 100);

            //一次查询 $limit 条key
            List<String> keySet = sqLite.expireKey(expireTimestamp, limit);

            if (keySet.size() == 0) {
                break;
            }

            if (limit == slice) {
                //如果分片数量和查询总数一样，就直接删除，跳过分组逻辑
                count += doDelete(keySet);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                //将这个 $limit 条数据进行分组
                List<List<String>> partition = Lists.partition(keySet, slice);
                for (List<String> keys : partition) {
                    count += doDelete(keys);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            if (keySet.size() < limit) {
                break;
            }
        }
        return count;
    }

    private int doDelete(List<String> keys) {
        return sqLite.delete(keys);
    }

    public void destroy() {
        executorService.shutdownNow();
    }
}
