package crawler.manager;

import crawler.common.ExtractionOptions;
import crawler.exception.NodeExtractionException;
import crawler.util.Pair;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.emulation.Emulation;

import java.util.HashMap;
import java.util.Map;

import static crawler.Constant.Observer.DELAY_SCRIPT;
import static crawler.Constant.Observer.ELEMENT_OBSERVER;

public class IFrameManager {
    private String id;
    private String url;
    private HTMLManager htmlManager;
    private CSSManager cssManager;
    private ScriptManager scriptManager;
    private NetworkRequestManager networkRequestManager;
    /**
     * <Related HTML element ID : Iframe web element>
     */
    private Map<String, IFrameManager> iFrameManagers;

    /**
     * No args constructor
     * @param id
     */
    public IFrameManager(String id) {
        this.id = id;
    }

    /**
     * Get the id of iframe.
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Get the html manager.
     *
     * @return
     */
    public HTMLManager getHtmlManager() {
        return htmlManager;
    }

    /**
     * Get the css manager.
     *
     * @return
     */
    public CSSManager getCssManager() {
        return cssManager;
    }

    /**
     * Get the script manager
     *
     * @return
     */
    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    /**
     * Get the iframe manager map.
     *
     * @return
     */
    public Map<String, IFrameManager> getIFrameManagers() {
        return iFrameManagers;
    }

    /**
     * Get the url for this content view (or iframe).
     *
     * @return
     */
    public NetworkRequestManager getNetworkRequestManager() {
        return networkRequestManager;
    }

    /**
     * Get the url for this content view (or iframe).
     *
     * @return
     */
    public String getCurrentUrl() {
        return url;
    }


    void extractIframeContent(ChromeDriver driver, ExtractionOptions options, BrowserMobProxy proxy, DevTools devTools) throws NodeExtractionException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        url = driver.getCurrentUrl();

        nodeExtraction(options, js, driver, proxy);
        nodeProcess(options, js);

        iFrameManagers = new HashMap<>();
        for (Map.Entry<String, Pair<String, WebElement>> iframe : htmlManager.getIframeMap().entrySet()) {
            Pair<String, WebElement> elementPair = iframe.getValue();

            if (options.scriptExtraction()) {
                devTools.send(Emulation.setScriptExecutionDisabled(true));
            }

            driver.switchTo().frame(elementPair.getValue());
            JavascriptExecutor newjs = (JavascriptExecutor) driver;

            if (options.scriptExtraction()) {
                devTools.send(Emulation.setScriptExecutionDisabled(false));
                newjs.executeScript(DELAY_SCRIPT);
                newjs.executeScript(ELEMENT_OBSERVER);
            }

            IFrameManager iFrameManager = new IFrameManager(elementPair.getKey());
            iFrameManager.extractIframeContent(driver, options, proxy, devTools);

            iFrameManagers.put(iframe.getKey(), iFrameManager);

            driver.switchTo().parentFrame();
        }
    }


    /**
     * Do the content correction and matching for html, css, script and network element.
     *
     * @param options
     * @throws NodeExtractionException
     */
    private void nodeProcess(ExtractionOptions options, JavascriptExecutor js) throws NodeExtractionException {
        if (options.networkRequestExtraction()) {
            networkRequestManager.externalResourceContentCorrect(cssManager, scriptManager);
        }

        if (options.scriptExtraction()) {
            scriptManager.connectionBuilding(htmlManager, cssManager, networkRequestManager);
        }

        if (options.networkRequestExtraction()) {
            networkRequestManager.externalResourceContentCorrect(cssManager, scriptManager);
            networkRequestManager.elementMatching(htmlManager, cssManager, scriptManager, options);
        }

        if (options.htmlExtraction() && options.cssExtraction()) {
            cssManager.matchCssSelector(js, htmlManager);
        }
    }

    /**
     * Extract the html, css, script and network request for webpage.
     *
     * @param options
     * @param js
     * @param driver
     * @param proxy
     * @throws NodeExtractionException
     */
    private void nodeExtraction(ExtractionOptions options, JavascriptExecutor js, ChromeDriver driver, BrowserMobProxy proxy) throws NodeExtractionException {
        if (options.htmlExtraction()) {
            htmlManager = new HTMLManager();
            htmlManager.parseHTMLDocument(js, driver, options.shadowDOMExtraction());
        }

        if (options.cssExtraction()) {
            cssManager = new CSSManager();
            cssManager.extractCSS(driver, htmlManager);
        }

        if (options.scriptExtraction()) {
            scriptManager = new ScriptManager();
            scriptManager.extractScript(driver, htmlManager, proxy);
        }

        if (options.networkRequestExtraction()) {
            networkRequestManager = new NetworkRequestManager();
            networkRequestManager.networkRequestAnalyze(proxy);
        }
    }
}
