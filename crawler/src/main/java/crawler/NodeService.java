package crawler;


import crawler.common.ExtractionOptions;
import crawler.exception.NodeExtractionException;
import crawler.manager.NodeExtractionManager;
import crawler.util.Driver;
import lombok.NoArgsConstructor;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.emulation.Emulation;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static crawler.Constant.Observer.*;

/**
 * All service for external users.
 */
@NoArgsConstructor
public class NodeService {
    private String url;
    private NodeExtractionManager nodeManager;
    private JavascriptExecutor js;
    private ChromeDriver driver;
    private BrowserMobProxy proxy;
    private DevTools devTools;
    private ExtractionOptions options;
    private long waitTime = 5;
    private boolean docOpen = false;

    /**
     * Set URL
     *
     * @param url
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     * Set the maximum wait time for loading page.
     *
     * @param waitTime
     * @throws NodeExtractionException
     */
    public void setWaitTime(long waitTime) throws NodeExtractionException {
        if (waitTime < 0) {
            throw new NodeExtractionException("The minimum wait time is zero.");
        }
        this.waitTime = waitTime;
    }

    /**
     * Set extraction options.
     *
     * @param options
     * @throws NodeExtractionException
     */
    public void setOptions(ExtractionOptions options) throws NodeExtractionException {
        if (docOpen) {
            throw new NodeExtractionException("Cannot set extraction options after opening document.");
        }
        this.options = options;
    }

    /**
     * Get the web driver.
     *
     * @return
     */
    public WebDriver getDriver(){
        return driver;
    }

    /**
     * Get the Javascript Executor.
     *
     * @return
     */
    public JavascriptExecutor getJs(){
        return js;
    }

    /**
     *
     * @return
     */
    public NodeExtractionManager getNodeManager() {
        return nodeManager;
    }

    /**
     * Open the document of node based on website link given before
     *
     * @throws NodeExtractionException
     */
    private void openDocument() throws NodeExtractionException {
        if (url == null) {
            throw new NodeExtractionException("Incorrect URL");
        }

        if (this.options == null) {
            docOpen = true;
            this.options = new ExtractionOptions();
        }

        // capture network request
        proxy = new BrowserMobProxyServer();

        proxy.start(0);

        Driver driverManager = new Driver();
        try {
            driver = driverManager.driverSetUp(proxy);
            js = (JavascriptExecutor) driver;

            devTools = driver.getDevTools();
            devTools.createSession();

            // If user wants to extract the script element,
            // it needs to disable the script execution first to simulate the action of the script afterwards.
            if (options.scriptExtraction()) {
                devTools.send(Emulation.setScriptExecutionDisabled(true));
            }

            // enable detailed HAR capture
            proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
            proxy.newHar();

            driver.get(url);

            // waiting for a while for the page to load.
            if (waitTime != 0) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState=='complete';"));
            }

            if (options.scriptExtraction()) {
                devTools.send(Emulation.setScriptExecutionDisabled(false));
                js.executeScript(DELAY_SCRIPT);
            }
            js.executeScript(ELEMENT_OBSERVER);

        } catch (Exception e) {
            throw new NodeExtractionException("Fail to build the connection with the website: " + url);
        }
    }

    /**
     * Close the document.
     */
    public void close() {
        driver.quit();
        proxy.stop();
    }

    /**
     * Start extraction tasks.
     *
     * @throws NodeExtractionException
     */
    public void nodeExtraction() throws NodeExtractionException {
        openDocument();
        nodeManager = new NodeExtractionManager();
        nodeManager.nodeExtraction(options, driver, proxy, js, devTools);
    }
}
