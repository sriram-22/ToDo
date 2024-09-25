package com.example.ToDo.repo;

import com.example.ToDo.model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoItemRepo extends JpaRepository<ToDoItem, Integer> {
    Optional<ToDoItem> findByUserIdAndTitle(int userId, String title);
}
