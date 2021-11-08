package io.thundra.todo.browserstack;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.thundra.todo.entity.TodoEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;

import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class BrowserStackUtils {
    private static final BrowserStackUtils instance = new BrowserStackUtils();

    public static BrowserStackUtils create() {
        return instance;
    }

    private final ObjectMapper parser;
    private CloseableHttpClient httpClient;

    private final Logger log = LoggerFactory.getLogger("BROWSERSTACK");

    public BrowserStackUtils() {
        parser = new ObjectMapper();
        httpClient = HttpClientBuilder.create().build();
    }

    public Map<String, Object> readConfigurationFromJSONFile(String filePath) {
        File file = new File(filePath);
        try {
            return parser.readValue(file, Map.class);
        } catch (IOException e) {
            log.error("Failed to read configuration file", e);
            return new HashMap<>();
        }
    }

    public <T> T convertObject(Object input, Class<T> type) {
        return parser.convertValue(input, type);
    }

    private List<TodoEntity> getTodos(String url) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpGet request = new HttpGet(uri);

        CloseableHttpResponse response = httpClient.execute(request);
        String responseString = new BasicResponseHandler().handleResponse(response);

        List<LinkedHashMap<Object, Object>> responseEntity = parser.readValue(responseString, List.class);
        List<TodoEntity> result = new ArrayList<>();
        for (LinkedHashMap<Object, Object> map : responseEntity) {
            result.add(convertObject(map, TodoEntity.class));
        }
        return result;
    }

    public void clearTodos(String url) {
        String listTodosUrl = url.concat("/todos");
        List<Long> idSet = new ArrayList<>();
        try {
            for (TodoEntity todo : getTodos(listTodosUrl)) {
                idSet.add(todo.getId());
            }
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to clear todos", e);
        }

        idSet.forEach(id -> {
            HttpDelete deleteRequest = new HttpDelete(url.concat("/todos/").concat(String.valueOf(id)));
            try {
                httpClient.execute(deleteRequest);
            } catch (IOException e) {
                log.error("Failed to delete todo from backend", e);
            }
        });

    }

    public void sendCompleteRequest(String url, boolean isCompleted, String reason) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            log.error("Failed to create URI", e);
            return;
        }
        HttpPut putRequest = new HttpPut(uri);

        ArrayList<NameValuePair> requestEntity = new ArrayList<>();
        requestEntity.add((new BasicNameValuePair("status", isCompleted ? "passed" : "failed")));
        requestEntity.add((new BasicNameValuePair("reason", reason)));

        try {
            putRequest.setEntity(new UrlEncodedFormEntity(requestEntity));
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to create request entity for Browserstack test result: ", e);
            return;
        }

        try {
            HttpClientBuilder.create().build().execute(putRequest);
        } catch (IOException e) {
            log.error("Failed to send request to Browserstack", e);
        }
    }

}
