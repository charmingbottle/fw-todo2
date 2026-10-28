package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TodoService {

    private final TodoMapper todoMapper;

    public TodoService(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    public List<Todo> search(String keyword, String category, String order, boolean showCompleted,
            int limit, int offset) {
        return todoMapper.search(keyword, category, order, showCompleted, limit, offset);
    }

    public int count(String keyword, String category, boolean showCompleted) {
        return todoMapper.count(keyword, category, showCompleted);
    }

    public List<Todo> exportTodos(String keyword, String category, String order, boolean showCompleted) {
        return todoMapper.exportTodos(keyword, category, order, showCompleted);
    }

    public List<Todo> searchByDueDate(String keyword, String category, String order,
            LocalDate from, LocalDate to) {
        return todoMapper.searchByDueDate(keyword, category, order, from, to);
    }

    public Todo findById(Long id) {
        return todoMapper.findById(id);
    }

    public void create(Todo todo) {
        todoMapper.insert(todo);
        log.info("登録: id={}", todo.getId());
    }

    public void update(Todo todo) {
        Todo existingTodo = todoMapper.findById(todo.getId());
        boolean wasCompleted = existingTodo != null && Boolean.TRUE.equals(existingTodo.getCompleted());
        boolean isCompleted = Boolean.TRUE.equals(todo.getCompleted());
        if (!wasCompleted && isCompleted) {
            todo.setCompletedAt(LocalDateTime.now());
        } else if (wasCompleted && !isCompleted) {
            todo.setCompletedAt(null);
        } else if (existingTodo != null) {
            todo.setCompletedAt(existingTodo.getCompletedAt());
        }
        todoMapper.update(todo);
        log.info("編集: id={}", todo.getId());
    }

    public void togglePinned(Long id, boolean pinned) {
        todoMapper.updatePinned(id, pinned);
    }

    public void delete(Long id) {
        todoMapper.deleteById(id);
        log.info("削除: id={}", id);
    }
}
