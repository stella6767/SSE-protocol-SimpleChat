package com.example.reactorex02;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

@CrossOrigin
@RestController
public class TestController2 {

	Sinks.Many<String> sink;
	
	//mulitcast()는 새로 들어온 데이터만 응답받음 hot 시퀀스(시퀀스=스트림)
	//replay() 기존 데이터 + 새로운 데이터 응답    cold 시퀀스
	
	public TestController2() {
		this.sink = Sinks.many().multicast().onBackpressureBuffer();
	}
	
	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE) //분간은 힘들지만, 제이슨 배열이다.
	public Flux<Integer> findAll() {
		return Flux.just(1,2,3,4,5,6).log();
	}
	
//	@GetMapping("/send")
//	public void send(@RequestBody Chat chat) {
//		System.out.println("채팅 보낸 거 캐치: "+chat);
//		
//		String user = chat.getUser();
//		String comment = chat.getComment();
//		
//		EmitResult result = sink.tryEmitNext(user+": "+comment);
//	}
	
	@PostMapping("/send")
	public EmitResult send(@RequestBody Chat chat) {
		System.out.println("채팅 보낸 거 캐치: "+chat);
		
		String user = chat.getUser();
		String comment = chat.getComment();
		
		EmitResult result = sink.tryEmitNext(user+": "+comment);
		
		return result;
	}
	
	
	@GetMapping("/send/{room}")
	public void sendRoom(@PathVariable String room) { //채팅방 만들거면 이렇게 문자열로 걸러서
		sink.tryEmitNext(room+"=Hello World");
	}
	
	//data:실제값\n\n
	@GetMapping(value="/sse")//, produces = MediaType.TEXT_EVENT_STREAM_VALUE
	public Flux<ServerSentEvent<String>> sse() { //ServerSentEvent의 ContentType은 text event stream
		return sink.asFlux().map(e->ServerSentEvent.builder(e).build()).doOnCancel(()->{
			System.out.println("SSE 종료됨");
			sink.asFlux().blockLast();
		}); //구독
	}
	
}
