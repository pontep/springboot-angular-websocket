package com.pontep.springangularsocket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pontep.springangularsocket.model.Todo;
import com.pontep.springangularsocket.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Controller
@Slf4j
public class WebSocketController {

    public List<Todo> todoList = new ArrayList<>();

    private final SimpMessagingTemplate template;

    @Autowired
    WebSocketController(SimpMessagingTemplate template){
        this.template = template;
    }

    @Autowired
    private TodoRepository todoRepository;

    @CrossOrigin(origins = "http://localhost:4200")
    @MessageMapping("/send/message")
    public void sendMessage(String msg){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
//            Todo todo = objectMapper.readValue(msg, Todo.class);
            log.info("sendMessage: {}", msg);
            Todo _todo = new Todo();
            _todo.setTitle(msg);
            this.todoRepository.saveAndFlush(_todo);
//        เพื่อตามไม่ทันเพื่อน
            this.todoList.add(_todo);
            this.template.convertAndSend("/message",  _todo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/get/message")
    public ResponseEntity<List<Todo>> getMessage(){
        log.info("getMessage");
//        System.out.println(message);
//        this.template.getMessageConverter().toString();
//        this.template.convertAndSend("/message",  message);
        return ResponseEntity.ok().body(this.todoList);
    }

    public void toggleCompleted(Todo todo){
        this.template.convertAndSend("/message",  todo);
        this.todoList.forEach(t -> {
            if(t.getId() == todo.getId()){
                t.setCompleted(todo.isCompleted());
            }
        });
    }
}