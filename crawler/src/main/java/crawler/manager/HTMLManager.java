package crawler.manager;

import crawler.Constant.EntityType;
import crawler.common.ExtractionOptions;
import crawler.entity.HTMLElement;
import crawler.entity.ScriptCodeBlock;
import crawler.exception.NodeExtractionException;
import crawler.util.Logger;
import crawler.util.Pair;
import crawler.util.Random;
import crawler.util.URLResolver;
import lombok.NonNull;
import org.openqa.selenium.*;

import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static crawler.Constant.ChangeOperation.*;
import static crawler.Constant.Interactable.EVENT;
import static crawler.Constant.NetworkConst.METHOD_GET;
import static crawler.Constant.NetworkConst.METHOD_POST;
import static crawler.Constant.TagName.SHADOW_ROOT_TAG_NAME;

/**
 * Functions to build the html element and its related connections.
 */
public class HTMLManager {
    private JavascriptExecutor js;
    private WebDriver driver;
    private String url;
    private ExtractionOptions options;

    /**
     * Basic.
     */
    private HTMLElement rootHTMLEle;
    private Map<String, HTMLElement> htmlElementMap;

    /**
     * shadow HTML
     */
    private boolean shadomDOM;
    private List<String> shadowHosts;

    /**
     * Lists used to hasten the search speed.
     * Trade of between speed and memory.
     */
    private List<String> cssTag;
    private List<String> scriptTag;
    private List<String> activeOutboundRequestEle;
    private List<String> passiveOutboundRequestEle;
    private List<String> interactableEle;
    private List<String> deletedEle;

    /**
     * For the analysis of other elements that help inside the crawler.
     */
    private Map<String, Pair<String, WebElement>> iframeMap;
    private Map<WebElement, HTMLElement> elementMap;

    /**
     * No args Constructor.
     */
    HTMLManager() {
        htmlElementMap = new HashMap<>();

        shadowHosts = new ArrayList<>();

        cssTag = new ArrayList<>();
        scriptTag = new ArrayList<>();
        activeOutboundRequestEle = new ArrayList<>();
        passiveOutboundRequestEle = new ArrayList<>();
        interactableEle = new ArrayList<>();
        deletedEle = new ArrayList<>();

        iframeMap = new HashMap<>();
        elementMap = new HashMap<>();
    }

    /**
     * The url of webpage which contains these html nodes.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the children of HTML elements with specific id.
     *
     * @param id
     * @return
     */
    public List<HTMLElement> getChildrenById(@NonNull String id) {
        HTMLElement element = htmlElementMap.get(id);
        List<HTMLElement> children = new ArrayList<>();
        for (String childId : element.getChildren()) {
            HTMLElement child = htmlElementMap.get(childId);
            if (child == null) {
                Logger.getInstance().info("Cannot find match html node for id (" + id + ") " +
                        "which is the child of " + id + ".");
            }
            children.add(child);
        }

        return children;
    }

    /**
     *
     * @param id - shadow host id.
     * @return
     */
    public HTMLElement findShadowRootByHostId(@NonNull String id) {
        HTMLElement element = htmlElementMap.get(id);
        if (element != null && element.getShadowRoot() != null) {
            HTMLElement shadowRoot = htmlElementMap.get(element.getShadowRoot());
            if (shadowRoot == null) {
                Logger.getInstance().warning("Cannot find shadow root with id: " + element.getShadowRoot());
                return null;
            }
            return shadowRoot;
        }

        return null;
    }

    /**
     *
     * @return - The root element in the DOM tree.
     */
    public HTMLElement getRootHTMLElement() {
        return rootHTMLEle;
    }

    /**
     * Find HTML elements by class name
     *
     * @param className
     * @return - A list of html element with specific classname
     */
    public List<HTMLElement> findHTMLElementByClass(@NonNull List<String> className) {
        List<HTMLElement> elementList = new ArrayList<>();
        for (Map.Entry<String, HTMLElement> entry : htmlElementMap.entrySet()) {
            String currClassName = entry.getValue().getClassNames();
            if (currClassName != null && className.contains(currClassName)) {
                elementList.add(entry.getValue());
            }
        }
        return elementList;
    }

    /**
     * Find HTML elements by tag name
     *
     * @param tagName - tag name in lower case.
     * @return - A list of html element with specific tag name
     */
    public List<HTMLElement> findHTMLElementByTagName(@NonNull List<String> tagName) {
        List<HTMLElement> elementList = new ArrayList<>();
        for (Map.Entry<String, HTMLElement> entry : htmlElementMap.entrySet()) {
            String currTagName = entry.getValue().getTagName();
            if (currTagName != null && tagName.contains(currTagName)) {
                elementList.add(entry.getValue());
            }
        }
        return elementList;
    }

    /**
     * Find HTML elements by id which is defined in html page.
     *
     * @param ids - A list of id which is defined in html page as an identifier of script accessing.
     * @return - A list of html element match id list.
     */
    public List<HTMLElement> findElementsByAttributeIds(@NonNull List<String> ids) {
        List<HTMLElement> elementList = new ArrayList<>();
        for (Map.Entry<String, HTMLElement> entry : htmlElementMap.entrySet()) {
            String currIdentifyId = entry.getValue().getIdentifyID();
            if (currIdentifyId != null && ids.contains(currIdentifyId)) {
                elementList.add(entry.getValue());
            }
        }
        return elementList;
    }

    /**
     *
     * @return - All shadow hosts
     */
    public List<HTMLElement> getAllShadowHosts() {
        List<HTMLElement> elementList = new ArrayList<>();
        for (String id : shadowHosts) {
            HTMLElement element = htmlElementMap.get(id);
            if (element != null) {
                elementList.add(element);
            } else {
                Logger.getInstance().error("Shadow host cannot be found with id: " + id);
            }
        }
        return elementList;
    }

    /**
     * Get all html element contains interactive attribute.
     *
     * @return - All interactive html elements
     */
    public List<HTMLElement> getInteractiveElement() {
        List<HTMLElement> elementList = new ArrayList<>();
        for (String id : interactableEle) {
            HTMLElement element = htmlElementMap.get(id);
            if (element != null) {
                elementList.add(element);
            } else {
                Logger.getInstance().error("Interactive element cannot be found with id: " + id);
            }
        }
        return elementList;
    }


    /**
     *
     * @return - All deleted html elements
     */
    public List<HTMLElement> getDeletedElement() {
        List<HTMLElement> elementList = new ArrayList<>();
        for (String id : deletedEle) {
            HTMLElement element = htmlElementMap.get(id);
            if (element != null) {
                elementList.add(element);
            } else {
                Logger.getInstance().error("Deleted element cannot be found with id: " + id);
            }
        }
        return elementList;
    }

    /**
     * Get the iframe maps, <html id, <iframe id, iframe WebElement>>
     *
     * @return
     */
    Map<String, Pair<String, WebElement>> getIframeMap() {
        return iframeMap;
    }

    /**
     * Get the element map, htmlElement - webElement
     *
     * @return
     */
    Map<WebElement, HTMLElement> getElementMap() {
        return elementMap;
    }

    /**
     * Find the extracted html element from web element.
     *
     * @param webElement
     * @return
     */
    HTMLElement findHTMLFromWebElement(WebElement webElement) {
        return elementMap.get(webElement);
    }

    /**
     * Find HTML elements by ids.
     *
     * @param ids
     * @return
     */
    public List<HTMLElement> findElementsByIds(@NonNull List<String> ids) {
        List<HTMLElement> htmlElements = new ArrayList<>();
        for (String id : ids) {
            htmlElements.add(htmlElementMap.get(id));
        }
        return htmlElements;
    }

    /**
     * Find HTML elements by id.
     *
     * @param id
     * @return
     */
    public HTMLElement findElementsById(@NonNull String id) {
        return htmlElementMap.get(id);
    }

    /**
     *
     * @param cssSelector
     * @return
     */
    public List<HTMLElement> findElementsBySelector(@NonNull String cssSelector) {
        List<HTMLElement> htmlElements = new ArrayList<>();
        try {
            List<WebElement> webElements = driver.findElements(By.cssSelector(cssSelector));
            for (WebElement webElement : webElements) {
                HTMLElement htmlElement = elementMap.get(webElement);
                if(htmlElement == null) {
                    continue;
                }
            }
        } catch (Exception e) {
            Logger.getInstance().warning("Cannot find match html element for css selector: " + cssSelector);
        }
        return htmlElements;
    }

    /**
     * Get all elements with css tag.
     *
     * @return
     */
    public List<HTMLElement> getCSS() {
        List<HTMLElement> htmlElements = new ArrayList<>();
        for (String id : cssTag) {
            HTMLElement element = htmlElementMap.get(id);
            if (element == null) {
                Logger.getInstance().warning("Cannot find match html element for id (" + id + ")" +
                        " which is extracted from css list.");
            } else {
                htmlElements.add(htmlElementMap.get(id));
            }
        }
        return htmlElements;
    }

    /**
     * Get all elements with script tag.
     *
     * @return
     */
    public List<HTMLElement> getScript() {
        List<HTMLElement> htmlElements = new ArrayList<>();
        for (String id : scriptTag) {
            HTMLElement element = htmlElementMap.get(id);
            if (element == null) {
                Logger.getInstance().warning("Cannot find match html element for id (" + id + ")" +
                        " which is extracted from script list.");
            } else {
                htmlElements.add(htmlElementMap.get(id));
            }
        }
        return htmlElements;
    }

    /**
     * Get all elements with external link.
     *
     * @return
     */
    public List<HTMLElement> getElementsWithOutboundRequest() {
        List<HTMLElement> elementList = getElementsWithActiveOutboundRequest();
        elementList.addAll(getElementsWithPassiveOutboundRequest());
        return elementList;
    }

    /**
     * Get all elements with active outbound request.
     *
     * @return
     */
    public List<HTMLElement> getElementsWithActiveOutboundRequest() {
        List<HTMLElement> htmlElements = new ArrayList<>();
        for (String id : activeOutboundRequestEle) {
            HTMLElement element = htmlElementMap.get(id);
            if (element == null) {
                Logger.getInstance().warning("Cannot find match html element for id (" + id + ")" +
                        " which is extracted from active outbound request list.");
            } else {
                htmlElements.add(htmlElementMap.get(id));
            }
        }
        return htmlElements;
    }

    /**
     * Get all elements with passive outbound request.
     *
     * @return
     */
    public List<HTMLElement> getElementsWithPassiveOutboundRequest() {
        List<HTMLElement> htmlElements = new ArrayList<>();
        for (String id : passiveOutboundRequestEle) {
            HTMLElement element = htmlElementMap.get(id);
            if (element == null) {
                Logger.getInstance().warning("Cannot find match html element for id (" + id + ")" +
                        " which is extracted from passive outbound request list.");
            } else {
                htmlElements.add(htmlElementMap.get(id));
            }
        }
        return htmlElements;
    }

    /**
     * Get all Elements
     *
     * @return
     */
    public List<HTMLElement> getAllElement() {
        return new ArrayList<>(htmlElementMap.values());
    }

    /**
     * Main function calls to parse the HTML document.
     * @param js
     * @param driver
     * @throws NodeExtractionException
     */
    void parseHTMLDocument(JavascriptExecutor js, WebDriver driver) throws NodeExtractionException {
        parseHTMLDocument(js, driver, true);
    }

    /**
     * Main function calls to parse the HTML document.
     * @param js
     * @param driver
     * @param allowShadowDOM
     * @throws NodeExtractionException
     */
    void parseHTMLDocument(JavascriptExecutor js, WebDriver driver, boolean allowShadowDOM) throws NodeExtractionException {
        shadomDOM = allowShadowDOM;
        this.driver = driver;
        this.js = js;
        this.url = driver.getCurrentUrl();
        execExtractor();
    }

    /**
     * Execute the extractor.
     *
     * @throws NodeExtractionException
     */
    private void execExtractor() throws NodeExtractionException {
        WebElement rootElement = (WebElement) js.executeScript("return document.documentElement");
        rootHTMLEle = new HTMLElement();
        extractHTMLEle(rootElement, null, 0, rootHTMLEle);
    }

    /**
     * Extract the HTML Node in the DOM tree.
     *
     * @param element
     * @param parent
     * @param depth
     * @param htmlEle
     * @throws NodeExtractionException
     */
    private void extractHTMLEle(WebElement element, HTMLElement parent, int depth, HTMLElement htmlEle) throws NodeExtractionException {
        convertWebElementToHTMLEle(element, parent, depth, htmlEle);

        saveElement(element, htmlEle);

        // recursive for all children
        List<String> children = new ArrayList<>();
        htmlEle.setChildren(children);
        List<WebElement> childElements = element.findElements(By.xpath("./*"));
        for (WebElement childElement : childElements) {
            HTMLElement childHtmlEle = new HTMLElement();
            extractHTMLEle(childElement, htmlEle, depth+1, childHtmlEle);
            children.add(childHtmlEle.getId());
        }

        if (shadomDOM && js.executeScript("return arguments[0].shadowRoot", element) != null) {
            HTMLElement shadowRoot = processShadowRoot(element, htmlEle, depth);
            shadowHosts.add(htmlEle.getId());
            htmlEle.setShadowRoot(shadowRoot.getId());
        }

    }

    /**
     * Extract the shadow root connected to the shadow host.
     *
     * @param element
     * @param shadowHost
     * @param depth
     * @return
     * @throws NodeExtractionException
     */
    private HTMLElement processShadowRoot(WebElement element, HTMLElement shadowHost, int depth) throws NodeExtractionException {
        HTMLElement shadowRoot = new HTMLElement();

        String id = Random.generateId();
        shadowRoot.setId(id);
        shadowRoot.setInitialNode((byte) 1);
        shadowRoot.setIsDeleted((byte) 0);
        shadowRoot.setTagName(SHADOW_ROOT_TAG_NAME);
        shadowRoot.setDepth(depth);
        shadowRoot.setClassNames(SHADOW_ROOT_TAG_NAME);
        shadowRoot.setTextualContent(SHADOW_ROOT_TAG_NAME);
        shadowRoot.setIdentifyID(id);
        shadowRoot.setAttributes(new HashMap<>());

        Object shadowRootObj = js.executeScript("return arguments[0].shadowRoot", element);

        shadowRoot.setShadowHost(shadowHost.getId());

        // Chromium browsers before v96
        if (shadowRootObj instanceof WebElement) {
            WebElement shadowDOM = (WebElement) shadowRootObj;
            extractShadowEles(shadowDOM, depth+1, shadowRoot);
        }  else if (shadowRootObj instanceof SearchContext){
            // Chromium browsers after v96
            SearchContext shadowDOM = (SearchContext) shadowRootObj;
            extractShadowEles(shadowDOM, depth+1, shadowRoot);
        } else {
            throw new NodeExtractionException("Cannot read shadow DOM under current browser version.");
        }

        saveElement(null, shadowRoot);

        return shadowRoot;
    }

    /**
     * For the chromium version after v96, the shadow root is a SearchContext class.
     *
     * @param shadowDOM
     * @param depth
     * @param shadowRoot
     * @throws NodeExtractionException
     */
    private void extractShadowEles(SearchContext shadowDOM, int depth, HTMLElement shadowRoot) throws NodeExtractionException {
        List<String> children = new ArrayList<>();
        shadowRoot.setChildren(children);

        List<WebElement> elements = shadowDOM.findElements(By.cssSelector("*"));
        for (WebElement element : elements) {
            HTMLElement childShadowEle = new HTMLElement();
            extractShadowEles(element, shadowRoot, depth+1, childShadowEle);
            children.add(childShadowEle.getId());
        }
    }

    /**
     * For the chromium version before v96, the shadow root is a WebElement.
     *
     * @param shadowDOM
     * @param depth
     * @param shadowRoot
     * @throws NodeExtractionException
     */
    private void extractShadowEles(WebElement shadowDOM, int depth, HTMLElement shadowRoot) throws NodeExtractionException {
        List<String> children = new ArrayList<>();
        shadowRoot.setChildren(children);

        List<WebElement> elements = (List<WebElement>) js.executeScript("return arguments[0].querySelectorAll('*')", shadowDOM);
        for (WebElement element : elements) {
            HTMLElement childShadowEle = new HTMLElement();
            extractShadowEles(element, shadowRoot, depth+1, childShadowEle);
            children.add(childShadowEle.getId());
        }
    }

    /**
     * Extract the Elements inside the shadow tree.
     *
     * @param element
     * @param parent
     * @param depth
     * @param htmlEle
     * @throws NodeExtractionException
     */
    private void extractShadowEles(WebElement element, HTMLElement parent, int depth, HTMLElement htmlEle) throws NodeExtractionException {
        convertWebElementToHTMLEle(element, parent, depth, htmlEle);

        htmlEle.setShadowHost(parent.getShadowHost());
        saveElement(element, htmlEle);

        // recursive for all children
        List<String> children = new ArrayList<>();
        htmlEle.setChildren(children);
        List<WebElement> childrenInShadow = (List<WebElement>) js.executeScript("return arguments[0].querySelectorAll('*')", element);
        for (WebElement childElement : childrenInShadow) {
            HTMLElement childHtmlEle = new HTMLElement();
            extractShadowEles(childElement, htmlEle, depth + 1, childHtmlEle);
            children.add(childHtmlEle.getId());
        }

        if (js.executeScript("return arguments[0].shadowRoot", element) != null) {
            HTMLElement shadowRoot = processShadowRoot(element, htmlEle, depth);
            shadowHosts.add(htmlEle.getId());
            htmlEle.setShadowRoot(shadowRoot.getId());
        }

    }

    /**
     *
     * @param element
     * @param htmlElement
     */
    private void saveElement(WebElement element, HTMLElement htmlElement) {
        analyzeScript(htmlElement);
        analyzeCSS(htmlElement);

        htmlElementMap.put(htmlElement.getId(), htmlElement);

        if (element != null) {
            elementMap.put(element, htmlElement);
            if (element.getTagName().equals("iframe")) {
                iframeMap.put(htmlElement.getId(), new Pair<>(htmlElement.getRelatedIframeId(), element));
            }
        }


        if (!htmlElement.getInteractive().isEmpty()) {
            interactableEle.add(htmlElement.getId());
        }

        if (!htmlElement.getActiveOutboundRequest().isEmpty()) {
            activeOutboundRequestEle.add(htmlElement.getId());
        }

        if (!htmlElement.getPassiveOutboundRequest().isEmpty()) {
            passiveOutboundRequestEle.add(htmlElement.getId());
        }
    }

    /**
     *
     * @param element
     * @return - A list of URL - Method Pair.
     */
    public List<Pair<String, String>> getActiveOutboundRequest(@NonNull HTMLElement element) {
        List<Pair<String, String>> outboundRequests = element.getActiveOutboundRequest();
        return outboundRequests;
    }

    /**
     *
     * @param element
     * @return - A list of URL - Method Pair.
     */
    public List<Pair<String, String>> getPassiveOutboundRequest(@NonNull HTMLElement element) {
        List<Pair<String, String>> outboundRequests = element.getActiveOutboundRequest();
        return outboundRequests;
    }

    /**
     *
     * @param element
     * @return - A list of URL - Method Pair.
     */
    public List<Pair<String, String>> getAllOutboundRequest(@NonNull HTMLElement element) {
        List<Pair<String, String>> outboundRequests = element.getActiveOutboundRequest();
        outboundRequests.addAll(element.getPassiveOutboundRequest());
        return outboundRequests;
    }

    /**
     * If the tag name of Ele is css which means it is related to a internal css.
     *
     * @param htmlEle
     */
    private void analyzeCSS(HTMLElement htmlEle) {
        if (htmlEle.getTagName().equals("style") ||
                (htmlEle.getTagName().equals("link") && htmlEle.getAttributes().get("rel") != null &&
                        htmlEle.getAttributes().get("rel").equals("stylesheet"))) {
            cssTag.add(htmlEle.getId());
        }
    }

    /**
     * If the tag name of Ele is script which means it is related to a internal/external script.
     *
     * @param htmlEle
     */
    private void analyzeScript(HTMLElement htmlEle) {
        if (htmlEle.getTagName().equals("script")) {
            scriptTag.add(htmlEle.getId());
        }
    }

    /**
     * Transfer the WebElement from Selenium to our HTML Ele.
     *
     * @param element
     * @param parent
     * @param depth
     * @param htmlEle
     * @return
     */
    private String convertWebElementToHTMLEle(WebElement element, HTMLElement parent, int depth, HTMLElement htmlEle) {
        htmlEle.setTagName(element.getTagName());
        htmlEle.setClassNames(element.getAttribute("class"));
        htmlEle.setTextualContent(element.getText());  // Get the element's own text, excluding the text of its children
        htmlEle.setIdentifyID(element.getAttribute("id"));
        htmlEle.setInitialNode((byte) 1);
        htmlEle.setIsDeleted((byte) 0);
        htmlEle.setInnerHTML(element.getAttribute("innerHTML"));
        String id = Random.generateId();
        htmlEle.setId(id);

        if (htmlEle.getTagName().equals("iframe")) {
            htmlEle.setRelatedIframeId(Random.generateId());
        }

        if (parent != null) {
            htmlEle.setParent(parent.getId());
        }
        htmlEle.setDepth(depth);

        // Add all attributes
        Map<String, String> attributes = extractAttributes(element);
        htmlEle.setAttributes(attributes);
        if (htmlEle.getTagName().equals("script")) {
            if (htmlEle.getAttributes().get("data-original-src") != null) {
                htmlEle.getAttributes().put("src", htmlEle.getAttributes().get("data-original-src"));
                htmlEle.getAttributes().remove("data-original-src");
            }
            if (htmlEle.getAttributes().get("data-original-text") != null) {
                htmlEle.setInnerHTML(htmlEle.getAttributes().get("data-original-text"));
                htmlEle.getAttributes().remove("data-original-text");
            }
        }

        List<String> interactive = extractInteractive(htmlEle);
        htmlEle.setInteractive(interactive);


        List<Pair<String, String>> activeRequests = extractActiveOutboundRequest(element, htmlEle);
        htmlEle.setActiveOutboundRequest(activeRequests);

        List<Pair<String, String>> passiveRequests = extractPassiveOutboundRequest(element, htmlEle);
        htmlEle.setPassiveOutboundRequest(passiveRequests);

        return id;
    }

    private List<String> extractInteractive(HTMLElement htmlEle) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : htmlEle.getAttributes().entrySet()) {
            if (entry.getKey().startsWith("on") && EVENT.contains(entry.getKey())) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Find the active outbound requests contains in the attribute of current element.
     *
     * @param element
     * @param htmlEle
     */
    private List<Pair<String, String>> extractActiveOutboundRequest(WebElement element, HTMLElement htmlEle) {
        List<Pair<String, String>> activeRequests = new ArrayList<>();

        String tagName = htmlEle.getTagName();
        if (tagName.equals("link")) {
            if (element.getAttribute("href") != null && !element.getAttribute("href").equals("")) {
                String url = urlFullfill(element.getAttribute("href"));
                htmlEle.getAttributes().put("href", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (tagName.equals("img") || tagName.equals("source")) {
            if (element.getAttribute("src") != null && !element.getAttribute("src").equals("")) {
                String url = urlFullfill(element.getAttribute("src"));
                htmlEle.getAttributes().put("src", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
            if (element.getAttribute("srcset") != null && !element.getAttribute("srcset").equals("")) {
                List<String> urlList = urlSetFullfill(element.getAttribute("srcset"));
                StringBuilder newurl = new StringBuilder();
                for (String url : urlList) {
                    activeRequests.add(new Pair<>(url, METHOD_GET));
                    newurl.append(url).append(";");
                }
                htmlEle.getAttributes().put("srcset", newurl.toString());

            }
        } else if (tagName.equals("script")) {
            if (element.getAttribute("src") != null && !element.getAttribute("src").equals("")) {
                String url = urlFullfill(element.getAttribute("src"));
                htmlEle.getAttributes().put("src", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        }else if (tagName.equals("iframe") || tagName.equals("frame") ||
                tagName.equals("embed") || tagName.equals("audio") || tagName.equals("track")) {
            if (element.getAttribute("src") != null && !element.getAttribute("src").equals("")) {
                String url = urlFullfill(element.getAttribute("src"));
                htmlEle.getAttributes().put("src", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (tagName.equals("object")) {
            if (element.getAttribute("data") != null && !element.getAttribute("data").equals("")) {
                String url = urlFullfill(element.getAttribute("data"));
                htmlEle.getAttributes().put("data", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (tagName.equals("video")) {
            if (element.getAttribute("poster") != null && !element.getAttribute("poster").equals("")) {
                String url = urlFullfill(element.getAttribute("poster"));
                htmlEle.getAttributes().put("poster", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
            if (element.getAttribute("src") != null && !element.getAttribute("src").equals("")) {
                String url = urlFullfill(element.getAttribute("src"));
                htmlEle.getAttributes().put("src", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (tagName.equals("input")) {
            if (element.getAttribute("type") != null && element.getAttribute("type").equals("image")) {
                if (element.getAttribute("src") != null) {
                    String url = urlFullfill(element.getAttribute("src"));
                    htmlEle.getAttributes().put("src", url);
                    activeRequests.add(new Pair<>(url, METHOD_GET));
                }
            }
        } else if (tagName.equals("applet")) {
            if (element.getAttribute("code") != null && !element.getAttribute("code").equals("")) {
                String url = urlFullfill(element.getAttribute("code"));
                htmlEle.getAttributes().put("code", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
            if (element.getAttribute("archive") != null && !element.getAttribute("archive").equals("")) {
                String url = urlFullfill(element.getAttribute("archive"));
                htmlEle.getAttributes().put("archive", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (tagName.equals("body") || tagName.equals("table")) {
            if (element.getAttribute("background") != null && !element.getAttribute("background").equals("")) {
                String url = urlFullfill(element.getAttribute("background"));
                htmlEle.getAttributes().put("background", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (tagName.equals("head")) {
            if (element.getAttribute("profile") != null && !element.getAttribute("profile").equals("")) {
                String url = urlFullfill(element.getAttribute("profile"));
                htmlEle.getAttributes().put("profile", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (tagName.equals("meta")) {
            if (element.getAttribute("profile") != null && element.getAttribute("content") != null && !element.getAttribute("content").equals("") &&
                element.getAttribute("http-equiv") != null && element.getAttribute("http-equiv").equals("refresh")) {
                String url = urlFullfill(element.getAttribute("content"));
                htmlEle.getAttributes().put("content", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (tagName.equals("use") || tagName.equals("feImage") || tagName.equals("pattern")) {
            if (element.getAttribute("href") != null && !element.getAttribute("href").equals("")) {
                String url = urlFullfill(element.getAttribute("href"));
                htmlEle.getAttributes().put("href", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
            if (element.getAttribute("xlink:href") != null && !element.getAttribute("xlink:href").equals("")) {
                String url = urlFullfill(element.getAttribute("xlink:href"));
                htmlEle.getAttributes().put("xlink:href", url);
                activeRequests.add(new Pair<>(url, METHOD_GET));
            }
        }

        return activeRequests;
    }

    /**
     * Find the passive outbound requests contains in the attribute of current element.
     *
     * @param element
     */
    private List<Pair<String, String>> extractPassiveOutboundRequest(WebElement element, HTMLElement htmlEle) {
        List<Pair<String, String>> passiveRequests = new ArrayList<>();

        if (element.getTagName().equals("a") || element.getTagName().equals("area")) {
            if (element.getAttribute("href") != null && !element.getAttribute("href").equals("")) {
                String url = urlFullfill(element.getAttribute("href"));
                htmlEle.getAttributes().put("href", url);
                passiveRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (element.getTagName().equals("form")) {
            if (element.getAttribute("action") == null || element.getAttribute("action").equals("")) {
                return passiveRequests;
            }

            String url = urlFullfill(element.getAttribute("action"));
            htmlEle.getAttributes().put("action", url);
            if (element.getAttribute("method") != null &&
                    element.getAttribute("method").equalsIgnoreCase("POST")){
                passiveRequests.add(new Pair<>(url, METHOD_POST));
            } else {
                passiveRequests.add(new Pair<>(url, METHOD_GET));
            }
        } else if (element.getTagName().equals("blockquote") || element.getTagName().equals("q") ||
                element.getTagName().equals("del") || element.getTagName().equals("ins")) {
            if (element.getAttribute("cite") != null && !element.getAttribute("cite").equals("")) {
                String url = urlFullfill(element.getAttribute("cite"));
                htmlEle.getAttributes().put("cite", url);
                passiveRequests.add(new Pair<>(url, METHOD_GET));
            }
        }

        return passiveRequests;
    }

    /**
     * Convert url to absolute path.
     *
     * @param url - absolute url
     * @return
     */
    private String urlFullfill(String url) {
        if (!url.startsWith("http")) {
            try {
                url = URLResolver.resolve(this.url, url);
            } catch (URISyntaxException e) {
                Logger.getInstance().warning("Fail to get the abs link from " + this.url + " and " + url +
                        "analyzing the link in html attribute");
                return url;
            }
        }

        return url;
    }

    /**
     * Convert all urls to absolute paths.
     *
     * @param srcset - A set of absolute url
     * @return
     */
    private List<String> urlSetFullfill(String srcset) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile("([^\\s,]+)\\s*(\\d+w|\\d+x)?\\s*,?");
        Matcher matcher = pattern.matcher(srcset);

        while (matcher.find()) {
            urls.add(matcher.group(1));
        }

        List<String> results = new ArrayList<>();
        for (String url : urls) {
            if (!url.startsWith("http")) {
                try {
                    url = URLResolver.resolve(this.url, url);
                } catch (URISyntaxException e) {
                    Logger.getInstance().warning("Fail to get the abs link from " + this.url + " and " + url +
                            "analyzing the link in html attribute");
                }
                results.add(url);
            }
        }

        return results;
    }

    /**
     * Extract the attributes of the HTML Ele.
     *
     * @param element
     * @return
     */
    private Map<String, String> extractAttributes(WebElement element) {
        Map<String, String> map =  (Map<String, String>) js.executeScript(
                "var attrs = arguments[0].attributes; " +
                        "var items = {}; " +
                        "for (var index = 0; index < attrs.length; index++) " +
                        "{ items[attrs[index].name] = attrs[index].value };" +
                        " return items;",
                element);

        return new HashMap<>(map);
    }

    /**
     * Process the change of html elements caused by script including create, delete and change attribute.
     * Also create or modify the css code block if some content is changed.
     *
     * @param change
     * @param cssManager
     * @param scriptManager
     * @param block
     * @return - The id of changed html element
     * @throws NodeExtractionException
     */
    String changeElementForPath(Map<String, Object> change, CSSManager cssManager, ScriptManager scriptManager, ScriptCodeBlock block) throws NodeExtractionException {
        String type = (String) change.get("type");
        String path = (String) change.get("path");
        String op = (String) change.get("op");
        Object node = change.get("node");
        List<String> pathIndex = new ArrayList<>(Arrays.asList(path.split(">")));

        if (!shadomDOM && pathIndex.contains("shadowRoot")) {
            return "";
        }

        HTMLElement element = null;

        if (op.equals(DELETE)) {

            element = findElementByPath(pathIndex);
            if ((element.getTagName().equals("shadowRoot") && type.equals("node")) ||
                    (!element.getTagName().equals("shadowRoot") && type.equals("shadowRoot"))) {
                Logger.getInstance().error(element.getTagName() + " - " + type);
                throw new NodeExtractionException("The Javascript response node is not match with node extracted before");
            }

            processDeleteElement(element, cssManager, scriptManager, true);
            block.addDefaultInteraction(element.getId(), EntityType.HTML, op, null);

        } else if (op.equals(CREATE)) {

            element = new HTMLElement();
            List<String> attributeCreate = processAddElement(element, type, pathIndex, node, cssManager, scriptManager);
            block.addDefaultInteraction(element.getId(), EntityType.HTML, op, attributeCreate);

        } else if (op.equals(CHANGE)) {

            element = findElementByPath(pathIndex);
            if ((element.getTagName().equals("shadowRoot") && type.equals("node")) ||
                    (!element.getTagName().equals("shadowRoot") && type.equals("shadowRoot"))) {
                Logger.getInstance().error(element.getTagName() + " - " + type);
                throw new NodeExtractionException("The Javascript response node is not match with node extracted before");
            }

            if (type.equals("shadowRoot")) {
                Logger.getInstance().warning("Try to modify the attribute of shadow root.");
                return element.getId();
            }

            List<String> attributeChange = processChangeAttribute(element, (WebElement) node, cssManager);
            block.addDefaultInteraction(element.getId(), EntityType.HTML, op, attributeChange);

        } else {
            throw new NodeExtractionException("Unknown html change operation: " + op);
        }

        return element.getId();
    }

    /**
     * Process the change of htmlElement
     *
     * @param element
     * @param node
     * @param cssManager
     * @return
     */
    private List<String> processChangeAttribute(HTMLElement element, WebElement node, CSSManager cssManager) {
        List<String> attributeChangeList = new ArrayList<>();
        Map<String, String> oldAttributes = new HashMap<>(element.getAttributes());
        Map<String, String> newAttributes = extractAttributes(node);

        for (Map.Entry<String, String> newAttri : newAttributes.entrySet()) {
            String key = newAttri.getKey();
            if (oldAttributes.containsKey(key) && oldAttributes.get(key).equals(newAttri.getValue())) {
                oldAttributes.remove(key);
                continue;
            }

            String oldValue = oldAttributes.get(key);
            String newValue = newAttri.getValue();
            if (!oldAttributes.containsKey(key)) {
                attributeChangeList.add(key + "::" + newValue);
            } else if (!oldAttributes.get(key).equals(newAttri.getValue())) {
                attributeChangeList.add(key + ":" + oldValue + ":" + newValue);
                oldAttributes.remove(key);
            }

            if (key.equals("style") && cssManager != null) {
                cssManager.updateInlineCSSBlockForHTMLEle(element.getId(), newValue);
            }
        }

        // The remain attribute is deleted in the element.
        if (cssManager != null) {
            for (Map.Entry<String, String> oldAttri : oldAttributes.entrySet()) {
                if (oldAttri.getKey().equals("style")) {
                    cssManager.deleteBlockByRelatedHTML(element.getId());
                }
            }
        }

        if (!attributeChangeList.isEmpty()) {
            element.setAttributes(newAttributes);

            List<String> interactive = extractInteractive(element);
            element.setInteractive(interactive);

            List<Pair<String, String>> activeRequests = extractActiveOutboundRequest(node, element);
            element.setActiveOutboundRequest(activeRequests);

            List<Pair<String, String>> passiveRequests = extractPassiveOutboundRequest(node, element);
            element.setPassiveOutboundRequest(passiveRequests);
        }
        return attributeChangeList;
    }

    /**
     * Find the html element from path
     *
     * @param pathIndex - A list of int/"shadowRoot" which refer to the position of html element in dom
     *
     * @return
     */
    private HTMLElement findElementByPath(List<String> pathIndex) throws NodeExtractionException {
        pathIndex.remove(0);
        HTMLElement currentPos = rootHTMLEle;

        for (String index : pathIndex) {
            String id = "";
            if (index.equals("shadowRoot")) {
                id = currentPos.getShadowRoot();
            } else {
                id = currentPos.getChildren().get(Integer.parseInt(index));
            }
            currentPos = htmlElementMap.get(id);
            if (currentPos == null) {
                Logger.getInstance().error("Fail to find html element with id: " + id);
                throw new NodeExtractionException();
            }

        }

        return currentPos;
    }

    /**
     * From Map<key, value> to List<key:value>
     *
     * @return
     */
    private List<String> attributesCompress(Map<String, String> map) {
        List<String> attributes = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            attributes.add(entry.getKey() + "::" + entry.getValue());
        }

        return attributes;
    }

    /**
     * Analyze if there are new css/script.
     *
     * @param element
     * @param attributeList
     * @param cssManager
     * @param scriptManager
     */
    private void newElementAnalyzing(HTMLElement element, List<String> attributeList,
                                     CSSManager cssManager, ScriptManager scriptManager) {
        List<HTMLElement> elements = new ArrayList<>();
        elements.add(element);

        if (cssManager != null) {
            if (element.getTagName().equals("style")) {
                // if element is internal css block
                cssManager.extractInternalCSS(elements);
            } else if (element.getTagName().equals("link") && element.getAttributes().get("rel") != null &&
                    element.getAttributes().get("rel").equals("stylesheet")) {
                // if element is external css block
                cssManager.extractExternalCSS(elements);
            } else {
                for (String attri : attributeList) {
                    if (attri.startsWith("style")) {
                        cssManager.extractInlineCSS(elements);
                    }
                }
            }
        }

        if (scriptManager != null && element.getTagName().equals("script")) {
            scriptManager.scriptAnalyze(elements);
        }
    }

    /**
     *
     * @param element
     * @param type
     * @param pathIndex
     * @param node
     * @param cssManager
     * @return
     */
    private List<String> processAddElement(HTMLElement element, String type, List<String> pathIndex,
                                           Object node, CSSManager cssManager, ScriptManager scriptManager) throws NodeExtractionException {
        String index = pathIndex.remove(pathIndex.size()-1);
        HTMLElement upperElement = findElementByPath(pathIndex);

        if (type.equals("node")) {
            WebElement webElement;

            // process the exception that the node maybe invalid because of running time cause node is not available.
            try {
                webElement = (WebElement) node;
                convertWebElementToHTMLEle(webElement, upperElement, upperElement.getDepth() + 1, element);
            } catch (Exception e) {
                webElement = (WebElement) js.executeScript("return window.findNodeByPath(arguments[0])", pathIndex);
                convertWebElementToHTMLEle(webElement, upperElement, upperElement.getDepth() + 1, element);
            }
            upperElement.getChildren().add(Integer.parseInt(index), element.getId());
            saveElement(webElement, element);
        } else {
            String id = Random.generateId();
            element.setId(id);
            element.setInitialNode((byte) 0);
            element.setIsDeleted((byte) 0);
            element.setTagName(SHADOW_ROOT_TAG_NAME);
            element.setDepth(upperElement.getDepth()+1);
            element.setClassNames(SHADOW_ROOT_TAG_NAME);
            element.setTextualContent(SHADOW_ROOT_TAG_NAME);
            element.setIdentifyID(id);
            element.setAttributes(new HashMap<>());
            element.setShadowHost(upperElement.getId());
            upperElement.setShadowRoot(element.getId());
            saveElement(null, element);
        }

        List<String> attributeList = new ArrayList<>(attributesCompress(element.getAttributes()));
        newElementAnalyzing(element, attributeList, cssManager, scriptManager);
        return attributeList;
    }

    /**
     * Delete element from current DOM tree by marking the element and its children/shadow as deleted
     * and cut down the connection from its parent or shadow host.
     *  @param element
     * @param cssManager
     * @param scriptManager
     */
    private void processDeleteElement(HTMLElement element, CSSManager cssManager, ScriptManager scriptManager, boolean removeFromUpper) {
        if (removeFromUpper) {
            if (element.getTagName().equals("shadowRoot")) {
                String hostId = element.getShadowHost();
                HTMLElement host = htmlElementMap.get(hostId);
                if (host == null) {
                    Logger.getInstance().warning("Cannot find shadow host HTML element using id (" + hostId + ").");
                } else {
                    host.setShadowRoot(null);
                }
            } else {
                String parentId = element.getParent();
                HTMLElement parent = findElementsById(parentId);
                if (parent == null) {
                    Logger.getInstance().warning("Cannot find parent HTML element using id (" + parentId + ").");
                } else {
                    parent.getChildren().remove(element);
                }
            }
        }

        element.setIsDeleted((byte) 1);
        deletedEle.add(element.getId());

        // if it is css tag
        if (element.getTagName().equals("style") ||
                (element.getTagName().equals("link") && element.getAttributes().get("rel") != null &&
                element.getAttributes().get("rel").equals("stylesheet"))) {
            cssManager.deleteBlockByRelatedHTML(element.getId());
        } else if (element.getTagName().equals("script")) {
            scriptManager.deleteBlockByRelatedHTML(element.getId());
        }

        for (String child : element.getChildren()) {
            processDeleteElement(htmlElementMap.get(child), cssManager, scriptManager, false);
        }
        if (element.getShadowRoot() != null) {
            processDeleteElement(htmlElementMap.get(element.getShadowRoot()), cssManager, scriptManager, false);
        }
    }

    /**
     * Build the map from the path in DOM tree to html element id.
     * The path format : index from parent >' .... '>' shadow root '>' ....
     *
     * @return
     */
    public Map<String, HTMLElement> buildPathMap() {
        Map<String, HTMLElement> pathIdMap = new HashMap<>();
        buildPathForChildNode(rootHTMLEle, "0", pathIdMap);
        return pathIdMap;
    }

    /**
     * The helper function of buildPathMap().
     *
     * @param element
     * @param path
     * @param pathIdMap
     */
    private void buildPathForChildNode(HTMLElement element, String path, Map<String, HTMLElement> pathIdMap) {
        pathIdMap.put(path, element);

        int index = 0;
        for (String childId : element.getChildren()) {
            HTMLElement child = htmlElementMap.get(childId);
            if (child == null) {
                Logger.getInstance().warning("The child of html element " + element.getId() + "cannot be found.");
                continue;
            }

            String nextPath = path + ">" + index;
            index++;

            buildPathForChildNode(child, nextPath, pathIdMap);
        }

        if (element.getShadowRoot() != null) {
            HTMLElement shadowRoot = htmlElementMap.get(element.getShadowRoot());
            String nextPath = path + ">" + "shadowRoot";
            buildPathForChildNode(shadowRoot, nextPath, pathIdMap);
        }
    }
}
