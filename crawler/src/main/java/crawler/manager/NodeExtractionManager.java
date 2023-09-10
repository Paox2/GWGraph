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

/**
 * The extraction of all elements and their relationships are managed by this class.
 */
public class NodeExtractionManager {
    private String url;
    private HTMLManager htmlManager;
    private CSSManager cssManager;
    private ScriptManager scriptManager;
    private NetworkRequestManager networkRequestManager;

    /**
     * Maps html element Id (its containers) to iframe.
     */
    private Map<String, IFrameManager> iFrameManagers;

    /**
     * No args constructor.
     */
    public NodeExtractionManager() {
        iFrameManagers = new HashMap<>();
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

    /**
     * Main function to extract the elements based on the options
     *
     * @param options
     * @param driver
     * @param proxy
     * @param js
     * @param devTools
     * @throws NodeExtractionException
     *
     * @see ExtractionOptions
     */
    public void nodeExtraction(ExtractionOptions options, ChromeDriver driver, BrowserMobProxy proxy, JavascriptExecutor js, DevTools devTools) throws NodeExtractionException {
        url = driver.getCurrentUrl();

        nodeExtraction(options, js, driver, proxy);
        nodeProcess(options, js);

        if (options.iframeExtraction()) {
            iFrameManagers = new HashMap<>();
            if (options.htmlExtraction()) {
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

                    driver.switchTo().defaultContent();
                }
            } else {
                throw new NodeExtractionException("Need to open up extraction of html nodes before extracting iframes");
            }
        }
    }

    /**
     * Do the content correction and matching for html, css, script and network element.
     *
     * @param options
     * @throws NodeExtractionException
     */
    private void nodeProcess(ExtractionOptions options, JavascriptExecutor js) throws NodeExtractionException {
        networkRequestManager.externalResourceContentCorrect(cssManager, scriptManager);

        if (options.scriptExtraction()) {
            scriptManager.connectionBuilding(htmlManager, cssManager, networkRequestManager);
            networkRequestManager.externalResourceContentCorrect(cssManager, scriptManager);
        }

        if (options.networkRequestExtraction()) {
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

        networkRequestManager = new NetworkRequestManager();
        networkRequestManager.networkRequestAnalyze(proxy);
    }
}
