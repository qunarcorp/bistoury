package qunar.tc.bistoury.agent.common.job;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhenyu.nie created on 2019 2019/10/16 19:29
 */
public class DefaultResponseJobStore implements ResponseJobStore {

    private static final Logger logger = LoggerFactory.getLogger(DefaultResponseJobStore.class);

    private final ConcurrentMap<String, PausedJob> jobs = Maps.newConcurrentMap();

    private final Set<String> pausedJobs = Sets.newConcurrentHashSet();

    private volatile boolean writable = true;

    private volatile CountDownLatch latch = new CountDownLatch(0);

    private boolean isClosed = false;

    @Override
    public void submit(ContinueResponseJob job) {
        PausedJob old;
        PausedJob pausedJob;

        synchronized (this) {
            if (isClosed) {
                job.error(new IllegalStateException("job store closed"));
                return;
            }

            pausedJob = new PausedJob(job);
            old = jobs.putIfAbsent(pausedJob.getJob().getId(), pausedJob);
        }

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
    public void close() {
        synchronized (this) {
            if (isClosed) {
                return;
            }

            logger.info("close job store");
            setWritable(false);
            isClosed = true;
        }

        for (PausedJob pausedJob : jobs.values()) {
            pausedJob.stop();
        }
    }

    @Override
    public synchronized void setWritable(boolean writable) {
        if (isClosed || this.writable == writable) {
            return;
        }

        logger.info("change writable to {}", writable);
        this.writable = writable;
        if (writable) {
            latch.countDown();
        } else {
            latch = new CountDownLatch(1);
        }
    }

    private class PausedJob {

        private final ContinueResponseJob job;

        private final ListeningExecutorService executor;

        private boolean paused = false;

        private boolean stopped = false;

        private ListenableFuture<?> finishFuture;

        private PausedJob(ContinueResponseJob job) {
            this.job = new WrappedJob(job);
            this.executor = job.getExecutor();
        }

        public ContinueResponseJob getJob() {
            return job;
        }

        public synchronized void start() {
            if (stopped) {
                return;
            }

            this.finishFuture = executor.submit(new JobRunner(this));
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
                this.finishFuture = executor.submit(new JobRunner(this));
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
            job.cancel();
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
            logger.debug("run job {}", job.getJob().getId());
            try {
                if (job.isStopped()) {
                    return;
                }

                job.getJob().init();
                doRun();
            } catch (Throwable t) {
                logger.debug("error job {}", job.getJob().getId(), t);
                if (!job.isStopped()) {
                    job.getJob().error(t);
                }
            }
        }

        private void doRun() throws Exception {
            while (true) {
                latch.await();
                if (job.isStopped() || job.doPausedIfNeed()) {
                    logger.debug("stop or paused job {}", job.getJob().getId());
                    return;
                }

                boolean end = job.getJob().doResponse();
                if (end) {
                    logger.debug("finish job {}", job.getJob().getId());
                    if (!job.isStopped()) {
                        job.getJob().finish();
                    }
                    return;
                }
            }
        }
    }

    private static class WrappedJob extends ForwardContinueResponseJob {

        private final ContinueResponseJob delegate;

        private boolean init = false;

        private boolean clear = false;

        public WrappedJob(ContinueResponseJob delegate) {
            this.delegate = delegate;
        }

        @Override
        protected ContinueResponseJob delegate() {
            return delegate;
        }

        @Override
        public synchronized void init() throws Exception {
            if (!init && !clear) {
                init = true;
                logger.debug("job init {}", getId());
                super.init();
            }
        }

        @Override
        public synchronized void clear() {
            if (!clear) {
                clear = true;
                logger.debug("job clear {}", getId());
                try {
                    super.clear();
                } catch (Exception e) {
                    logger.error("job clear error, {}", getId());
                }
            }
        }

        @Override
        public void finish() throws Exception {
            logger.debug("job finish {}", delegate.getId());
            clear();
            super.finish();
        }

        @Override
        public void error(Throwable t) {
            logger.debug("job error {}", delegate.getId(), t);
            clear();
            super.error(t);
        }

        @Override
        public void cancel() {
            logger.debug("job cancel {}", delegate.getId());
            clear();
            super.cancel();
        }
    }
}
