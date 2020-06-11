package com.quartz.job;

import org.quartz.Scheduler;

public interface QuartzService {

    /**
     * 创建任务
     * @param scheduler
     * @param quartzTask
     * @return
     */
    void createJob(Scheduler scheduler, QuartzTask quartzTask);

    /**
     * 修改定时任务
     * @param scheduler
     * @param quartzTask
     */
    void updateJob(Scheduler scheduler, QuartzTask quartzTask);

    /**
     * 暂停
     * @param scheduler
     * @param jobName
     * @param group
     */
    void pauseJob(Scheduler scheduler, String jobName, String group);

    /**
     * 恢复
     * @param scheduler
     * @param jobName
     * @param group
     */
    void resumeJob(Scheduler scheduler, String jobName, String group);
}
