package qunar.tc.bistoury.commands.job;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.remoting.netty.AgentRemotingExecutor;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhenyu.nie created on 2019 2019/10/16 19:29
 */
public class DefaultResponseJobManager implements ResponseJobManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultResponseJobManager.class);

    private static final DefaultResponseJobManager INSTANCE = new DefaultResponseJobManager();

    private static final ListeningExecutorService EXECUTOR = AgentRemotingExecutor.getExecutor();

    public static ResponseJobManager getInstance() {
        return INSTANCE;
    }

    private final ConcurrentMap<String, PausedJob> jobs = Maps.newConcurrentMap();

    private final Set<String> pausedJobs = Sets.newConcurrentHashSet();

    private volatile boolean writable = true;

    private volatile CountDownLatch latch = new CountDownLatch(0);

    @Override
    public void submit(ContinueResponseJob job) {
        PausedJob pausedJob = new PausedJob(job);
        PausedJob old = jobs.putIfAbsent(pausedJob.getJob().getId(), pausedJob);
        if (old == null) {
            logger.debug("submit job {}", job.getId());
            pausedJob.start();
        }
    }

    @Override
    public void pause(String id) {
        logger.debug("pause job {}", id);
        PausedJob pausedJob = jobs.get(id);
        if (pausedJob != null) {
            pausedJob.paused();
        }
    }

    @Override
    public void resume(String id) {
        logger.debug("resume job {}", id);
        PausedJob pausedJob = jobs.get(id);
        if (pausedJob != null) {
            pausedJob.resume();
        }
    }

    @Override
    public void stop(String id) {
        logger.debug("stop job {}", id);
        PausedJob pausedJob = jobs.get(id);
        if (pausedJob != null) {
            pausedJob.stop();
        }
    }

    @Override
    public synchronized void setWritable(boolean writable) {
        if (this.writable == writable) {
            return;
        }

        logger.debug("change writable to {}", writable);
        this.writable = writable;
        if (writable) {
            latch.countDown();
        } else {
            latch = new CountDownLatch(1);
        }
    }

    private static class InitOnceJob extends ForwardContinueResponseJob {

        private final ContinueResponseJob delegate;

        private boolean init = false;

        public InitOnceJob(ContinueResponseJob delegate) {
            this.delegate = delegate;
        }

        @Override
        protected ContinueResponseJob delegate() {
            return delegate;
        }

        @Override
        public synchronized void init() {
            if (!init) {
                init = true;
                logger.debug("job init {}", delegate.getId());
                super.init();
            }
        }

        @Override
        public void finish() {
            logger.debug("job finish {}", delegate.getId());
            super.finish();
        }

        @Override
        public void error(Throwable t) {
            logger.debug("job error {}", delegate.getId());
            super.error(t);
        }
    }

    private class PausedJob {

        private final ContinueResponseJob job;

        private boolean paused = false;

        private boolean stopped = false;

        private ListenableFuture<?> finishFuture;

        private PausedJob(ContinueResponseJob job) {
            this.job = new InitOnceJob(job);
        }

        public ContinueResponseJob getJob() {
            return job;
        }

        public synchronized void start() {
            if (stopped) {
                return;
            }

            this.finishFuture = EXECUTOR.submit(new JobRunner(this));
        }

        public synchronized void paused() {
            if (!this.paused) {
                logger.debug("job paused {}", job.getId());
                this.paused = true;
            }
        }

        public synchronized void resume() {
            if (stopped || !this.paused) {
                return;
            }

            this.paused = false;
            boolean removed = pausedJobs.remove(job.getId());
            logger.debug("job resume {}, {}", removed, job.getId());
            if (removed) {
                this.finishFuture = EXECUTOR.submit(new JobRunner(this));
            }
        }

        public synchronized void stop() {
            if (stopped) {
                return;
            }

            logger.debug("job stop {}", job.getId());
            stopped = true;
            if (finishFuture != null) {
                finishFuture.cancel(true);
            }
            pausedJobs.remove(job.getId());
            jobs.remove(job.getId());
        }

        public synchronized boolean isStopped() {
            return stopped;
        }

        public synchronized boolean doPausedIfNeed() {
            if (stopped) {
                return true;
            }

            if (paused) {
                logger.debug("job do pause {}", job.getId());
                pausedJobs.add(job.getId());
                finishFuture = null;
                return true;
            }
            return false;
        }
    }

    private class JobRunner implements Runnable {

        private final PausedJob job;

        private JobRunner(PausedJob job) {
            this.job = job;
        }

        @Override
        public void run() {
            logger.debug("job start run {}", job.getJob().getId());
            try {
                if (job.isStopped()) {
                    return;
                }

                job.getJob().init();
                doRun();
            } catch (InterruptedException e) {
                // be cancelled
                logger.debug("job interrupted {}", job.getJob().getId());
            } catch (Throwable t) {
                job.getJob().error(t);
            }
        }

        private void doRun() throws Exception {
            while (true) {
                latch.await();
                if (job.isStopped() || job.doPausedIfNeed()) {
                    return;
                }

                boolean end = job.getJob().doResponse();
                if (end) {
                    job.getJob().finish();
                    return;
                }
            }
        }
    }
}
