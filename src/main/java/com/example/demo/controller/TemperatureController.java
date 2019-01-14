package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.example.demo.dto.Temperature;

//Our temperature sensor generates only one stream of events without regard to how many clients are listening. However, it also creates them when nobody listens. That may lead to a waste of resources, especially when creation actions are resource hungry. For example, our component may communicate with real hardware and reduce hardware lifespan at the same time.
// we allocate the thread pool to asynchronously broadcast temperature events. In the case of a genuinely asynchronous and reactive approach (framework), we wouldn't have to do this.
@Controller
public class TemperatureController {

/*	// ResponseBodyEmitter handles async responses.
	// While DeferredResult is used to produce a single result, a
	// ResponseBodyEmitter can be used to send multiple objects where each object is
	// written with a compatible HttpMessageConverter.
//Results can be emitted from threads which are not necessarily the Servlet Request Thread of the Servlet Container.
	@GetMapping("/ResponseBodyEmitterDemo")
	public ResponseEntity<ResponseBodyEmitter> getResponseOne() {
		ResponseBodyEmitter emitter = new ResponseBodyEmitter();
		ExecutorService service = Executors.newCachedThreadPool();
		System.out.println(Thread.currentThread().getName());
		service.execute(() -> {
			for (int i = 0; i < 100; i++) {
				try {
					emitter.send(i + "-", MediaType.TEXT_PLAIN);
					System.out.println(Thread.currentThread().getName());
				} catch (IOException e) {
					e.printStackTrace();
					emitter.completeWithError(e);
					return;
				}
			}
			emitter.complete();
		});
		// return emitter;
		return new ResponseEntity(emitter, HttpStatus.OK);
	}

//SseEmitter is used to send Server Sent Events to the Client. Server Sent Events has a fixed format and the response type for the result will be text/event-stream.	
	@GetMapping("/SseEmitterDemo")
	public ResponseEntity<SseEmitter> getResponseTwo() {
		SseEmitter emitter = new SseEmitter();
		ExecutorService service = Executors.newCachedThreadPool();
		System.out.println(Thread.currentThread().getName());
		service.execute(() -> {
			for (int i = 0; i < 100; i++) {
				try {
					emitter.send(i + "-", MediaType.TEXT_PLAIN);
					System.out.println(Thread.currentThread().getName());
				} catch (IOException e) {
					e.printStackTrace();
					emitter.completeWithError(e);
					return;
				}
			}
			emitter.complete();
		});
		// return emitter;
		return new ResponseEntity(emitter, HttpStatus.OK);
	}

	// we can use StreamingResponseBody to write directly to an OutputStream before
	// passing that written information back to the client using a ResponseEntity.
	// StreamingResponseBody is used to send raw unformatted data such as bytes to
	// the client asynchronously of the Servlet Thread.
	// ResponseBodyEmitter and SseEmitter has a method named complete to mark its
	// completion and StreamingResponseBody will complete when there is no more data
	// to send.
	@GetMapping("/StreamingResponseBodyDemo")
	public ResponseEntity<StreamingResponseBody> getResponseThree() {
		System.out.println(Thread.currentThread().getName());
		StreamingResponseBody body = out -> {
			for (int i = 0; i < 100; i++) {
				out.write(("" + i).getBytes());
				System.out.println(Thread.currentThread().getName());
			}
		};
		return new ResponseEntity(body, HttpStatus.OK);
	}*/
	// return emitter;

	private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

	@GetMapping("/temperature-stream")
	public SseEmitter events() {
		SseEmitter emitter = new SseEmitter(); // (4)
		clients.add(emitter); // (5)
		emitter.onTimeout(() -> clients.remove(emitter)); // (6)
		emitter.onCompletion(() -> clients.remove(emitter));
		return emitter;
	}

	// . The handleMessage() method receives a new temperature event and
	// asynchronously sends it to all clients in JSON format in parallel for each
	// event (13). Also, when sending to individual emitters, we track all failing
	// ones (14) and remove them from the list of the active clients (15). Such an
	// approach makes it possible to spot clients that are not operational anymore.
	// Unfortunately, SseEmitter does not provide any callback for handling errors,
	// and can be done by handling errors thrown by the send() method only.
	@Async // (9)
	@EventListener // (10)
	public void handleMessage(Temperature temperature) { // (11)
		List<SseEmitter> deadEmitters = new ArrayList<>(); // (12)
		clients.forEach(emitter -> {
			try {
				emitter.send(temperature, MediaType.APPLICATION_JSON); // (13)
			} catch (Exception ignore) {
				deadEmitters.add(emitter); // (14)
			}
		});
		clients.removeAll(deadEmitters); // (15)
	}
}
