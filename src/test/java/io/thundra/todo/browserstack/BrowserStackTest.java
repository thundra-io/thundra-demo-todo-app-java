package io.thundra.todo.browserstack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnabledIfEnvironmentVariable(named = "THUNDRA_BROWSERSTACK_ENABLED", matches = "true")
public class BrowserStackTest extends BrowserStackInitializer {

    private void addTodo(String message) {
        WebElement input = this.browserDriver.findElementByClassName("new-todo");

        if (input == null)
            failTest("Unable to find input element");

        else {
            input.sendKeys(message);
            input.sendKeys(Keys.ENTER);
        }

        waitToRefresh(   );
    }

    private List<WebElement> getTodoList(String content) {
        List<WebElement> elements = this.browserDriver.findElementsByTagName("li");
        if (elements.size() < 4)
            return new ArrayList<>();

        return elements.stream().filter(x -> {
            List<WebElement> test = x.findElements(By.tagName("label"));
            Optional<WebElement> label = test.stream().findFirst();
            return label.map(webElement -> webElement.getText().contains(content)).orElse(false);
        }).collect(Collectors.toList());
    }

    @Test
    @DisplayName("Test Open Todo Page")
    @Order(1)
    public void testOpenTodoPage() {

        this.browserDriver.get(testUrl);
        List<WebElement> elements = this.browserDriver.findElementsByTagName("h1");

        try {
            assertEquals("Thundra Java Todo Demo", this.browserDriver.getTitle());
            assertEquals(elements.size(), elements.stream().filter(x -> x.getText().equals("todos")).count());
        } catch (AssertionError e) {
            failTest("Test Todo Page could not be opened!: " + e.getMessage());
        }

        passed();
    }


    @Test
    @DisplayName("Test Add Todo")
    @Order(2)
    public void testAddTodo() {

        this.browserDriver.get(testUrl);
        this.addTodo("Test Todo");

        boolean hasTestTodo = !this.getTodoList("Test Todo").isEmpty();
        boolean hasAnyTodo = !this.browserDriver.findElementByClassName("todo-count")
                .getText().equals("0 Tasks");

        if (!hasAnyTodo || !hasTestTodo)
            failTest("Todo could not be added!");

        passed();
    }


    @Test
    @DisplayName("Test Delete Todo")
    @Order(3)
    public void testDeleteTodo() {
        this.browserDriver.get(testUrl);
        this.addTodo("Test Todo");

        List<WebElement> todoList = this.getTodoList("Test Todo");

        if (todoList.isEmpty())
            failTest("Test Todo could not be added!");


        for (WebElement testElement : todoList) {
            testElement.click();
            WebElement destroy = testElement.findElement(By.className("destroy"));
            destroy.click();
        }

        waitToRefresh();

        boolean isDeletedTestTodo = this.getTodoList("Test Todo").isEmpty();

        if (!isDeletedTestTodo)
            failTest("Test Todo could not be deleted!");

        passed();
    }

    @Test
    @DisplayName("Test Duplicate Todo")
    @Order(4)
    public void testDuplicateTodo() {
        this.browserDriver.get(testUrl);
        this.addTodo("Test Todo");

        List<WebElement> todoList = this.getTodoList("Test Todo");

        int previousSize = todoList.size();

        if (todoList.isEmpty())
            failTest("Test Todo could not be added!");

        for (WebElement testElement : todoList) {
            testElement.click();
            WebElement duplicate = testElement.findElement(By.className("duplicate"));
            duplicate.click();
        }

        waitToRefresh();
        todoList = this.getTodoList("Test Todo");
        int currentSize = todoList.size();

        if (previousSize >= currentSize)
            failTest("Test Todo could not be duplicated!");

        passed();
    }

    @Test
    @DisplayName("Test Edit Todo")
    @Order(5)
    public void testEditTodo() {

        this.browserDriver.get(testUrl);
        this.addTodo("Test Todo");

        List<WebElement> todoList = this.getTodoList("Test Todo");

        if (todoList.isEmpty())
            failTest("Test Todo could not be added!");

        Actions action = new Actions(this.browserDriver);

        for (WebElement testElement : todoList) {

            action.doubleClick(testElement).perform();

            WebElement edit = testElement.findElement(By.className("edit"));
            edit.clear();
            edit.sendKeys("Test Todo Edited");
            edit.sendKeys(Keys.ENTER);

            waitToRefresh();
        }

        todoList = this.getTodoList("Test Todo Edited");
        if (todoList.isEmpty())
            failTest("Test Todo could not be edited!");

        passed();

    }


    @Test
    @DisplayName("Test Mark Todo as Completed")
    @Order(6)
    public void testMarkTodo() {
        this.browserDriver.get(testUrl);
        this.addTodo("Test Todo");

        List<WebElement> todoList = this.getTodoList("Test Todo");

        for (WebElement testElement : todoList) {
            Optional<WebElement> completedToggle = testElement.findElements(By.className("toggle")).stream().findFirst();
            completedToggle.ifPresent(WebElement::click);
            waitToRefresh();
        }

        todoList = this.getTodoList("Test Todo");

        for (WebElement completedTodo : todoList) {
            if (!completedTodo.getAttribute("class").equals("completed"))
                failTest("Test Todo could not be marked as completed!");
        }

        passed();
    }

    @Test
    @DisplayName("Test Clear All Completed Todos")
    @Order(7)
    public void testClearCompletedTodo() {
        this.browserDriver.get(testUrl);

        this.addTodo("Test Todo 1");
        this.addTodo("Test Todo 2");
        this.addTodo("Test Todo 3");
        this.addTodo("Test Todo 4");

        List<WebElement> testTodoList = this.getTodoList("Test Todo");

        if (testTodoList.isEmpty())
            failTest("Test Todos could not be added!");

        for (WebElement testElement : testTodoList) {
            Optional<WebElement> completedToggle = testElement.findElements(By.className("toggle")).stream().findFirst();
            completedToggle.ifPresent(WebElement::click);
            waitToRefresh();
        }


        WebElement completeTodoButton = browserDriver.findElement(By.className("clear-completed"));
        completeTodoButton.click();

        waitToRefresh();

        List<WebElement> clearedTodoList = this.getTodoList("Test Todo");

        if (!clearedTodoList.isEmpty())
            failTest("Test Todos could not be cleared!");

        passed();
    }

}
