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
        log.info("sendMessage: {}", msg);
        // create new TodoObject.
        Todo _todo = new Todo();
        _todo.setTitle(msg);
        this.todoRepository.saveAndFlush(_todo);
        this.todoList.add(_todo); // update todoList.
        this.template.convertAndSend("/message",  _todo);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/get/todos")
    public ResponseEntity<List<Todo>> getAllTodos(){
        log.info("getMessage");
        return ResponseEntity.ok().body(this.todoList);
    }

    public void toggleCompleted(Todo todo){
        this.template.convertAndSend("/message",  todo); // for frontend.
        this.todoList.forEach(t -> { // for update todoList.
            if(t.getId().equals(todo.getId())){
                t.setCompleted(todo.isCompleted());
            }
        });
    }
}