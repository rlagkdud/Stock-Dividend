package com.dayone.config;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        // 스레드 수는 (사용되는 cpu 코어 수) * 2 또는 (사용되는 cpu 코어 수)+1
        int n = Runtime.getRuntime().availableProcessors();
        threadPool.setPoolSize(n+1);
        threadPool.initialize();

        taskRegistrar.setTaskScheduler(threadPool); // 스케쥴러에서 우리가 설정해준 스레드풀 사용하게 됨
    }
}
