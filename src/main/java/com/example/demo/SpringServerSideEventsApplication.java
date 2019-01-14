package com.example.demo;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class SpringServerSideEventsApplication implements AsyncConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(SpringServerSideEventsApplication.class, args);
	}
	
	/*@Bean
	public AsyncTaskExecutor taskExecutor() {
		return  new SimpleAsyncTaskExecutor("MyThread-");
	}

	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setTaskExecutor(taskExecutor());
	}*/
	   @Override
	   public Executor getAsyncExecutor() {                              // (3)
	      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();// (4)
	      executor.setCorePoolSize(2);
	      executor.setMaxPoolSize(100);
	      executor.setQueueCapacity(5);                                  // (5) 
	      executor.initialize();
	      return executor;
	   }

	   @Override
	   public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler(){
	      return new SimpleAsyncUncaughtExceptionHandler();              // (6)
	   }
}

