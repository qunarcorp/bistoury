package qunar.tc.bistoury.agent.common.job;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

/**
 * @author zhenyu.nie created on 2019 2019/11/4 11:06
 */
public class TestJob {

    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);

    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(
            Executors.newSingleThreadExecutor());

    private static ResponseJobStore jobStore = new DefaultResponseJobStore();

    public static void main(String[] args) throws Exception {
        final String id = "xx";
        final Job job = new Job(id);
        jobStore.submit(job);

        Thread.sleep(2000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    jobStore.pause(id);
                    Thread.sleep(5000);
                    jobStore.resume(id);
                    Thread.sleep(5000);
                    jobStore.setWritable(false);
                    Thread.sleep(2000);
                    jobStore.stop(id);
                    Thread.sleep(5000);
                    jobStore.resume(id);
                } catch (Exception e) {

                }
            }
        }).start();
        System.in.read();
        System.out.println(jobStore);
        jobStore.submit(new Job("11"));
        System.in.read();
        System.out.println(jobStore);
        System.in.read();
        jobStore.setWritable(true);
        Thread.sleep(5000);
        System.out.println(jobStore);
        System.in.read();
        jobStore.setWritable(false);
        Thread.sleep(5000);
        System.out.println(jobStore);
        System.in.read();
        jobStore.setWritable(true);
        System.in.read();
        System.out.println(jobStore);
        System.in.read();
    }

    private static class Job implements ContinueResponseJob {

        private final String id;

        private int i = 15;

        private Job(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void init() throws Exception {
            logger.info("init");
        }

        @Override
        public boolean doResponse() throws Exception {
            if (i-- > 0) {
                logger.info("response");
                Thread.sleep(1500);
                return false;
            }
            return true;
        }

        @Override
        public void clear() {
            logger.info("clear");
        }

        @Override
        public void finish() throws Exception {
            logger.info("finish");
        }

        @Override
        public void error(Throwable t) {
            logger.info("error {}", t.getClass().getName());
        }

        @Override
        public void cancel() {
            logger.info("cancel");
        }

        @Override
        public ListeningExecutorService getExecutor() {
            return executor;
        }

        @Override
        public String toString() {
            return "Job{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}
