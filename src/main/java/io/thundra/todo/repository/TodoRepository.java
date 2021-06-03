package io.thundra.todo.repository;

import io.thundra.todo.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author tolgatakir
 */
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    List<TodoEntity> findByCompletedIsTrue();
}
