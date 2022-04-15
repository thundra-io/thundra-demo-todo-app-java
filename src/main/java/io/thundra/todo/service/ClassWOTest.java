package io.thundra.todo.service;

import io.thundra.todo.entity.TodoEntity;
import io.thundra.todo.model.Todo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yusuferdem
 */
public class ClassWOTest {
    public String findString() {
        String str = "Hello World";
         str = str.substring(5);
        return str;
    }
}
