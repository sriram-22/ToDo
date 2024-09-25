package com.example.ToDo.service;

import com.example.ToDo.dto.ToDoItemDto;
import com.example.ToDo.model.ToDoItem;
import com.example.ToDo.model.User;
import com.example.ToDo.repo.ToDoItemRepo;
import com.example.ToDo.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ToDoItemService {
    private final ToDoItemRepo toDoItemRepo;
    private final UserRepository userRepository;

    public ToDoItemService(UserRepository userRepository, ToDoItemRepo toDoItemRepo) {
        this.toDoItemRepo = toDoItemRepo;
        this.userRepository = userRepository;
    }

    public List<ToDoItemDto> getToDoItems() {
        List<ToDoItem> toDoItems = toDoItemRepo.findAll();
        return toDoItems.stream().map(this::convertToDto).collect(Collectors.toList());
        //return toDoItemRepo.findByUser_Id(0);

    }

    public ToDoItemDto getToDoItemById(int id) {
        Optional<ToDoItem> toDoItem = toDoItemRepo.findById(id);
        return toDoItem.map(this::convertToDto).orElse(null);
//        ToDoItem item = toDoItemRepo.findById(id).orElse(null);
//        return toDoItemRepo.findById(id).orElse(null);
    }

    public ToDoItemDto addToDo(ToDoItemDto item) {
        User user = userRepository.findById(item.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Optional<ToDoItem> existingTask = toDoItemRepo.findByUserIdAndTitle(user.getId(), item.getTitle());
        if (existingTask.isPresent()){
            throw new RuntimeException("Task with the same title already exists for this user.");
        }

        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setTitle(item.getTitle());
        toDoItem.setCategory(item.getCategory());
        toDoItem.setStatus(item.getStatus());
        toDoItem.setId(item.getId());
        toDoItem.setDescription(item.getDescription());
        toDoItem.setDueDate(item.getDueDate());
        toDoItem.setUser(user);

        ToDoItem savedItem = toDoItemRepo.save(toDoItem);

        return convertToDto(savedItem);
    }
    public ToDoItemDto updateToDoItem(ToDoItemDto item) {
        ToDoItem existingItem = toDoItemRepo.findById(item.getId()).orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(item.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Optional<ToDoItem> existingTask = toDoItemRepo.findByUserIdAndTitle(item.getUserId(), item.getTitle());
        if (existingTask.isPresent()){
            throw new RuntimeException("Task with the same title already exists for this user.");
        }

        existingItem.setStatus(item.getStatus());
        existingItem.setTitle(item.getTitle());
        existingItem.setCategory(item.getCategory());
        existingItem.setDescription(item.getDescription());
        existingItem.setDueDate(item.getDueDate());
        existingItem.setUser(userRepository.findById(item.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));

        ToDoItem updatedItem = toDoItemRepo.save(existingItem);
        return convertToDto(updatedItem);
    }

    public void deleteToDoItem(int id) {
        toDoItemRepo.deleteById(id);
    }

    private ToDoItemDto convertToDto(ToDoItem toDoitem){
        ToDoItemDto dto = new ToDoItemDto();
        dto.setId(toDoitem.getId());
        dto.setTitle(toDoitem.getTitle());
        dto.setDueDate(toDoitem.getDueDate());
        dto.setCategory(toDoitem.getCategory());
        dto.setStatus(toDoitem.getStatus());
        dto.setDescription(toDoitem.getDescription());
        dto.setUserId(toDoitem.getUser().getId());

        return dto;
    }
}
