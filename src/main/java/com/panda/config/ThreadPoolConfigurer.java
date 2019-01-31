package com.panda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author J.y
 */
@Configuration
public class ThreadPoolConfigurer {

	@Bean(name = "clientTaskPool")
	public ThreadPoolTaskExecutor clientTaskPool() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setKeepAliveSeconds(60);
		executor.setMaxPoolSize(Integer.MAX_VALUE);
		executor.setThreadNamePrefix("clientTaskPool");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}

	@Bean(name = "clientMessageTaskPool")
	public ThreadPoolTaskExecutor clientMessageTaskPool() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setKeepAliveSeconds(60);
		executor.setMaxPoolSize(Integer.MAX_VALUE);
		executor.setThreadNamePrefix("clientMessageTaskPool");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}
}
