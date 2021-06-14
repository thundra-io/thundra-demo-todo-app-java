package io.thundra.todo.selenium;

import io.thundra.todo.ContextInitializedTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author tolgatakir
 */
@Testcontainers
class TodoSeleniumTest extends ContextInitializedTest {

    @Container
    private final BrowserWebDriverContainer chrome = new BrowserWebDriverContainer()
            .withCapabilities(new ChromeOptions());

    @Test
    void testOpenTodoPage() {
        RemoteWebDriver driver = chrome.getWebDriver();
        driver.get("http://host.testcontainers.internal:" + localPort);
        List<WebElement> elements = driver.findElementsByTagName("h1");
        assertThat(elements).extracting("text").containsOnly("todos");
    }
}
