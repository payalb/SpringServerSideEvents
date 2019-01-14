package com.example.demo.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.demo.dto.Temperature;

@Component
public class TemperatureSensor {

	@Autowired ApplicationEventPublisher publisher;
	 private final ScheduledExecutorService executor =               // (3)
	           Executors.newScheduledThreadPool(1);
	@PostConstruct
	public void startProcessing() {
		executor.schedule(()-> this.probe(),1, TimeUnit.SECONDS);
	}
	public void probe() {
		publisher.publishEvent(new Temperature(Math.random()));
		executor
        .schedule(this::probe, 5000, TimeUnit.MILLISECONDS);
	}
}
