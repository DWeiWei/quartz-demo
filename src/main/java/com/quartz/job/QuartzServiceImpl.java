package com.quartz.job;

import org.quartz.*;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class QuartzServiceImpl implements QuartzService {

    private final static Logger logger = LoggerFactory.getLogger(QuartzServiceImpl.class);

    @SuppressWarnings("unchecked")
    @Transactional
    public void createJob(Scheduler scheduler, QuartzTask quartzTask) {
        try {
            String jobName = quartzTask.getJobName();
            JobDetail jobDetail = buildJod(quartzTask);
            // 设置定时任务执行方式
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzTask.getCronExpression()).withMisfireHandlingInstructionDoNothing();
            // 构建触发器trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName).withSchedule(scheduleBuilder).build();
            // 创建定时任务
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.error("创建定时任务出错,{} ", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void updateJob(Scheduler scheduler,QuartzTask quartzTask){
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(quartzTask.getJobName(), quartzTask.getGroup());
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzTask.getCronExpression());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        }catch (SchedulerException e){
            logger.error(" 更新定时任务出错,{}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void pauseJob(Scheduler scheduler, String jobName, String group) {
        JobKey jobKey = JobKey.jobKey(jobName, group);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("暂停定时任务出错,{}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void resumeJob(Scheduler scheduler, String jobName, String group) {
        JobKey jobKey = JobKey.jobKey(jobName, group);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("恢复定时任务出错,{}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static JobDetail buildJod(QuartzTask quartzTask) {
        JobKey key = new JobKey(Key.createUniqueName(quartzTask.getGroup()), quartzTask.getGroup());
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("id", quartzTask.getId());
        jobDataMap.put("jobClass", quartzTask.getClass());
        jobDataMap.put("parameter", quartzTask.getParameter());
        jobDataMap.put("description", quartzTask.getDescription());
        jobDataMap.put("createTime", System.currentTimeMillis());
        return JobBuilder.newJob(QuartzTaskFactory.class)
                .withIdentity(key)
                .withDescription(quartzTask.getDescription())
                .storeDurably()
                .requestRecovery(Boolean.TRUE)
                .storeDurably(Boolean.FALSE)
                .setJobData(jobDataMap)
                .build();
    }
}
