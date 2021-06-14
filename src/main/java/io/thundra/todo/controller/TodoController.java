package io.thundra.todo.controller;

import io.thundra.todo.model.Todo;
import io.thundra.todo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author tolgatakir
 */
@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> findTodos() {
        List<Todo> todos = service.findTodos();
        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<Todo> addTodo(@Valid @RequestBody Todo request) {
        Todo todo = service.addTodo(request);
        return ResponseEntity.ok(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody Todo request) {
        Todo todo = service.updateTodo(id, request);
        return ResponseEntity.ok(todo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        service.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Todo> duplicateTodo(@PathVariable Long id) {
        Todo todo = service.duplicateTodo(id);
        return ResponseEntity.ok(todo);
    }

    @PostMapping("/clear-completed")
    public ResponseEntity<Void> clearCompletedTodo() {
        service.clearCompletedTodo();
        return ResponseEntity.ok().build();
    }
}
