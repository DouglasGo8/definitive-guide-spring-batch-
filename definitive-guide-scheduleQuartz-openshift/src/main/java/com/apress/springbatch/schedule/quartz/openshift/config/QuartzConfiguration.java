package com.apress.springbatch.schedule.quartz.openshift.config;

import com.apress.springbatch.schedule.quartz.openshift.service.BatchScheduledJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

/**
 * @author dbatista
 */
@Configuration
public class QuartzConfiguration {

    @Bean
    public JobDetail quartzJobDetail() {
        return JobBuilder.newJob(BatchScheduledJob.class)
                .storeDurably()
                .build();
    }

   /*@Bean
    public Trigger jobTrigger() {

        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5)
                .withRepeatCount(10);

        return TriggerBuilder.newTrigger()
                .forJob(quartzJobDetail())
                .withSchedule(scheduleBuilder)
                .build();
    }*/

    @Bean
    public CronTriggerFactoryBean jobTrigger() {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();

        cronTriggerFactoryBean.setJobDetail(quartzJobDetail());

        final String cronExpression = "0 00 18 ? * MON-FRI";

        //cronTriggerFactoryBean.setCronExpression("0 0/1 * 1/1 * ? *");
        cronTriggerFactoryBean.setCronExpression(cronExpression);

        return cronTriggerFactoryBean;


    }

}

