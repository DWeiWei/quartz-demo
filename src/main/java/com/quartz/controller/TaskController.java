package com.quartz.controller;

import com.quartz.job.QuartzService;
import com.quartz.job.QuartzTask;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
public class TaskController {

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private QuartzService quartzService;

    @PostMapping(value = "/task")
    public void createTask(@RequestBody QuartzTask quartzTask) {
        quartzService.createJob(scheduler, quartzTask);
    }

    @PutMapping(value = "/task")
    public void updateJob(@RequestBody QuartzTask quartzTask) {
        quartzService.updateJob(scheduler, quartzTask);
    }

    @PostMapping(value = "/task/pause")
    public void pauseJob(@RequestParam("jobName") String jobName,
                         @RequestParam("group") String group) {
        quartzService.pauseJob(scheduler, jobName, group);
    }

    @PostMapping(value = "/task/resume")
    public void resumeJob(@RequestParam("jobName") String jobName,
                          @RequestParam("group") String group) {
        quartzService.resumeJob(scheduler, jobName, group);
    }

}
