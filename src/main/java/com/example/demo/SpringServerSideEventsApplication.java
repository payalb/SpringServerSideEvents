package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SpringServerSideEventsApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(SpringServerSideEventsApplication.class, args);
	}
	
	@Bean
	public AsyncTaskExecutor taskExecutor() {
		return  new SimpleAsyncTaskExecutor("MyThread-");
	}

	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setTaskExecutor(taskExecutor());
	}
}

