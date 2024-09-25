package com.example.ToDo.controller;

import com.example.ToDo.dto.ToDoItemDto;
import com.example.ToDo.model.ToDoItem;
import com.example.ToDo.service.ToDoItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ToDoItemController {

    private final ToDoItemService service;

    public ToDoItemController(ToDoItemService service) {
        this.service = service;
    }

    @GetMapping("/todo")
    public ResponseEntity<List<ToDoItemDto>> getToDoItems() {
        return new ResponseEntity<>(service.getToDoItems(), HttpStatus.OK);
    }

    @GetMapping("/todo/{id}")
    public ResponseEntity<ToDoItemDto> getToDoItemById(@PathVariable int id) {
        ToDoItemDto response = service.getToDoItemById(id);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/todo")
    public ResponseEntity<?> addToDoItem(@RequestBody ToDoItemDto item) {
        System.out.println(item.getUserId()+" "+item.getTitle());
        System.out.println(item.toString());
        try {
            ToDoItemDto createdTask = service.addToDo(item);
            return new ResponseEntity<>(createdTask, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/todo")
    public ResponseEntity<?> updateToDoItem(@RequestBody ToDoItemDto item) {
        try {
            ToDoItemDto updateTask = service.updateToDoItem(item);
            return new ResponseEntity<>(updateTask, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/todo/{id}")
    public ResponseEntity<Void> deleteToDoItem(@PathVariable int id){
        service.deleteToDoItem(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
