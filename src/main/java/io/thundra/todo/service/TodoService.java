package io.thundra.todo.service;

import io.thundra.todo.model.Todo;

import java.util.List;

/**
 * @author tolgatakir
 */
public interface TodoService {
    List<Todo> findTodos();

    Todo addTodo(Todo todo);

    Todo update(Long id, Todo todo);

    void deleteTodo(Long id);

    Todo duplicateTodo(Long id);

    void clearCompletedTodo();
}
