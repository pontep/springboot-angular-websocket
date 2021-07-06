package com.pontep.springangularsocket.controller;

import com.pontep.springangularsocket.model.Todo;
import com.pontep.springangularsocket.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private WebSocketController webSocketController;

    @PostMapping("/todo")
    public ResponseEntity<?> toggleCompletedTodo(@RequestParam("id") Long id){
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Id is not exists."));
        todo.setCompleted(!todo.isCompleted());
        this.todoRepository.save(todo);
        webSocketController.toggleCompleted(todo);
        return ResponseEntity.created(null).build();
    }
}
