package com.taobao.arthas.core.shell.system.impl;

import com.taobao.arthas.core.GlobalOptions;
import com.taobao.arthas.core.shell.cli.CliToken;
import com.taobao.arthas.core.shell.handlers.Handler;
import com.taobao.arthas.core.shell.impl.ShellImpl;
import com.taobao.arthas.core.shell.system.Job;
import com.taobao.arthas.core.util.LogUtil;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.common.NamedThreadFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhenyu.nie created on 2018 2018/11/21 16:10
 * 因为类可见性的问题，从arthas copy出来
 */
public class QGlobalJobControllerImpl extends QJobControllerImpl {

    private static final Logger logger = LogUtil.getArthasLogger();

    // 改成executor实现并设置在cancel时remove，防止内存在timeout时间内不清理
    private final ScheduledThreadPoolExecutor timeoutExecutor = initTimeoutExecutor();

    private final Map<Integer, ScheduledFuture<?>> jobTimeoutTaskMap = new HashMap<>();

    private ScheduledThreadPoolExecutor initTimeoutExecutor() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("qjob-timeout", true));
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }

    @Override
    public void close(final Handler<Void> completionHandler) {
        if (completionHandler != null) {
            completionHandler.handle(null);
        }
    }

    @Override
    public void close() {
        timeoutExecutor.shutdownNow();
        jobTimeoutTaskMap.clear();
        for (Job job : jobs()) {
            job.terminate();
        }
    }

    @Override
    public boolean removeJob(int id) {
        ScheduledFuture<?> jobTimeoutTask = jobTimeoutTaskMap.remove(id);
        if (jobTimeoutTask != null) {
            jobTimeoutTask.cancel(true);
        }
        return super.removeJob(id);
    }

    @Override
    public Job createJob(InternalCommandManager commandManager, List<CliToken> tokens, ShellImpl shell) {
        final Job job = super.createJob(commandManager, tokens, shell);

        /*
         * 达到超时时间将会停止job
         */
        ScheduledFuture<?> timoutTaskFuture = timeoutExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                job.terminate();
            }
        }, getJobTimeoutInSecond(), TimeUnit.SECONDS);

        jobTimeoutTaskMap.put(job.id(), timoutTaskFuture);
        Date timeoutDate = new Date(System.currentTimeMillis() + (getJobTimeoutInSecond() * 1000));
        job.setTimeoutDate(timeoutDate);

        return job;
    }

    private long getJobTimeoutInSecond() {
        long result = -1;
        String jobTimeoutConfig = GlobalOptions.jobTimeout.trim();
        try {
            char unit = jobTimeoutConfig.charAt(jobTimeoutConfig.length() - 1);
            String duration = jobTimeoutConfig.substring(0, jobTimeoutConfig.length() - 1);
            switch (unit) {
                case 'h':
                    result = TimeUnit.HOURS.toSeconds(Long.parseLong(duration));
                    break;
                case 'd':
                    result = TimeUnit.DAYS.toSeconds(Long.parseLong(duration));
                    break;
                case 'm':
                    result = TimeUnit.MINUTES.toSeconds(Long.parseLong(duration));
                    break;
                case 's':
                    result = Long.parseLong(duration);
                    break;
                default:
                    result = Long.parseLong(jobTimeoutConfig);
                    break;
            }
        } catch (Exception e) {
        }

        if (result < 0) {
            // 如果设置的属性有错误，那么使用默认的1天
            result = TimeUnit.DAYS.toSeconds(1);
            logger.warn("Configuration with job timeout " + jobTimeoutConfig + " is error, use 1d in default.");
        }
        return result;
    }
}
