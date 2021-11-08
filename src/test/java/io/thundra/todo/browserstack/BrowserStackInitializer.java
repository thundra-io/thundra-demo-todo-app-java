package io.thundra.todo.browserstack;

import io.thundra.todo.ContextInitializedTest;
import com.browserstack.local.Local;

import org.junit.jupiter.api.*;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BrowserStackInitializer extends ContextInitializedTest {

    private final String userName = System.getenv("THUNDRA_BROWSERSTACK_USERNAME");
    private final String accessKey = System.getenv("THUNDRA_BROWSERSTACK_ACCESSKEY");
    private final String server = "hub-cloud.browserstack.com/wd/hub";

    private final String configurationFilePath = "src/test/resources/browserstack/config.json";

    private Map<String, Object> capabilitiesMap;

    private BrowserStackUtils utils;
    private URL browserStackUrl;


    protected RemoteWebDriver browserDriver;
    protected Local browserLocalAgent;

    protected String testUrl;

    @BeforeAll
    private void initialize() throws Exception {

        this.utils = BrowserStackUtils.create();

        this.browserStackUrl = new URL(String.format("https://%s:%s@%s",userName, accessKey, server));
        this.testUrl = "http://bs-local.com:" + localPort;

        Map<String, Object> config = utils.readConfigurationFromJSONFile(configurationFilePath);
        Object capabilitiesSource = config.get("capabilities");

        capabilitiesMap = utils.convertObject(capabilitiesSource, Map.class);

        Object localFlag = capabilitiesMap.get("browserstack.local");
        boolean isLocal =  localFlag != null && utils.convertObject(localFlag, Boolean.class);

        if(isLocal){

            Map<String, String> localOptions = new HashMap<>();
            localOptions.put("key", accessKey);

            this.browserLocalAgent = new Local();
            this.browserLocalAgent.start(localOptions);
        }
    }


    @BeforeEach
    private void initializeWebBrowser(TestInfo info) {
        this.capabilitiesMap.put("name", info.getDisplayName());
        this.browserDriver = new RemoteWebDriver(browserStackUrl, new DesiredCapabilities(capabilitiesMap));
    }

    @AfterEach
    private void tearDown() {
        if(this.browserDriver != null)
            this.browserDriver.quit();
    }


    protected void markTestResult(boolean isCompleted, String reason){
        String url = String.format("https://%s:%s@api.browserstack.com/automate/sessions/%s.json",
                userName, accessKey,this.browserDriver.getSessionId());

        utils.sendCompleteRequest(url, isCompleted, reason);
    }


    protected void passed(){
        this.utils.clearTodos(this.testUrl);
        waitToRefresh();
        markTestResult(true, "");
    }


    protected void failTest(String message) {
        markTestResult(false, message);
        fail(message);
    }


    protected void waitToRefresh(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }




}
