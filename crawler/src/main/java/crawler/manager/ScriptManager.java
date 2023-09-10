package crawler.manager;

import crawler.Constant.EntityType;
import crawler.Constant.ScriptType;
import crawler.entity.HTMLElement;
import crawler.entity.NetworkRequest;
import crawler.entity.ScriptCodeBlock;
import crawler.exception.NodeExtractionException;
import crawler.util.Logger;
import crawler.util.Random;
import crawler.util.Reader;
import crawler.util.URLResolver;
import lombok.NonNull;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.Har;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains functions to extract script elements and analyzing.
 */
public class ScriptManager {
    private String url;
    private JavascriptExecutor jsExecutor;
    private BrowserMobProxy proxy;

    /**
     * Basic Script collection.
     */
    private Map<String, ScriptCodeBlock> scriptCodeBlockMap;

    /**
     * Ids of all internal script code block.
     */
    private List<String> internalScriptBlocks;

    /**
     * Ids of all external script code block
     */
    private List<String> externalScriptBlocks;

    /**
     * Map the external script link to the script block.
     */
    private Map<String, List<String>> externalLinkCodeBlockMap;

    /**
     * Defer run script.
     * Not just maintain a way to fast find defer running script code block, but to represent the order of script loaded.
     */
    private List<String> deferRunScript;

    /**
     * Instant run script
     * Not just maintain a way to fast find instant running script code block, but to represent the order of script loaded.
     */
    private List<String> instantRunScript;


    /**
     * No args constructor
     */
    ScriptManager() {
        scriptCodeBlockMap = new HashMap<>();
        internalScriptBlocks = new ArrayList<>();
        externalScriptBlocks = new ArrayList<>();
        externalLinkCodeBlockMap = new HashMap<>();

        instantRunScript = new ArrayList<>();
        deferRunScript = new ArrayList<>();
    }

    /**
     * Get all script code blocks
     *
     * @return - All script code blocks
     */
    public List<ScriptCodeBlock> getAllBlocks() {
        return new ArrayList<>(scriptCodeBlockMap.values());
    }

    /**
     * Get script by id.
     *
     * @param ids
     * @return - A list of script code blocks with require id.
     */
    public List<ScriptCodeBlock> getScriptBlocksByIds(@NonNull List<String> ids) {
        List<ScriptCodeBlock> blocks = new ArrayList<>();
        for (String id : ids) {
            ScriptCodeBlock block = scriptCodeBlockMap.get(id);
            if (block == null) {
                Logger.getInstance().warning("Script Block does not found by id: " + id);
            } else {
                blocks.add(block);
            }
        }
        return blocks;
    }

    /**
     * Get script code by type (Inline/Internal/External).
     *
     * @return - all specific type script code blocks
     */
    public List<ScriptCodeBlock> getScriptBlocksByType(@NonNull String type) {
        List<ScriptCodeBlock> blocks = new ArrayList<>();
        List<String> ids;
        if (type.equals(ScriptType.INTERNAL)) {
            ids = internalScriptBlocks;
        } else if (type.equals(ScriptType.EXTERNAL)) {
            ids = externalScriptBlocks;
        } else {
            Logger.getInstance().error("Wrong Script types.");
            return blocks;
        }

        for (String id : ids) {
            ScriptCodeBlock block = scriptCodeBlockMap.get(id);
            if (block == null) {
                Logger.getInstance().warning("The script code block exists in " + type + " block list " +
                        "but cannot find in all" + id);
            } else {
                blocks.add(block);
            }
        }
        return blocks;
    }

    /**
     * Get external script by its location.
     *
     * @param url
     * @return - The blocks require certain external css resource
     */
    public List<ScriptCodeBlock> getExternalScriptBlockByLink(@NonNull String url) {
        List<String> blockIds = externalLinkCodeBlockMap.get(url);

        List<ScriptCodeBlock> scriptCodeBlocks = new ArrayList<>();

        if (blockIds == null || blockIds.isEmpty()) {
            return scriptCodeBlocks;
        }

        for (String blockId : blockIds) {
            ScriptCodeBlock block = scriptCodeBlockMap.get(blockId);

            if (block == null) {
                Logger.getInstance().error("Block exists in external link map but cannot find in map: " + blockId);
            } else {
                scriptCodeBlocks.add(block);
            }
        }

        return scriptCodeBlocks;
    }

    /**
     * The all links of external script.
     *
     * @return
     */
    public List<String> getExternalScriptLinks() {
        return new ArrayList<>(externalLinkCodeBlockMap.keySet());
    }

    /**
     * Get the mapping of the location of external resource to external script code blocks. The value of map is a list since
     * two external script can have the same src attribute, although it is rarely appeared in real document.
     *
     * @return
     */
    public Map<String, List<String>> getLinkScriptCodeBlockMapping() {
        return externalLinkCodeBlockMap;
    }

    /**
     * Get the defer running scripts in their running orders.
     *
     * @return - List of defer running script code block
     */
    public List<ScriptCodeBlock> findDeferScript() {
        List<ScriptCodeBlock> blocks = new ArrayList<>();
        for (String id : deferRunScript) {
            ScriptCodeBlock block = scriptCodeBlockMap.get(id);

            if (block == null) {
                Logger.getInstance().warning("Cannot find match script code block for id (" + id + ")" +
                        " which is extracted from defer running script list.");
                continue;
            }
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Get the instant running scripts in their running orders.
     *
     * @return - List of instant running script code block
     */
    public List<ScriptCodeBlock> findInstantScript() {
        List<ScriptCodeBlock> blocks = new ArrayList<>();
        for (String id : instantRunScript) {
            ScriptCodeBlock block = scriptCodeBlockMap.get(id);

            if (block == null) {
                Logger.getInstance().warning("Cannot find match script code block for id (" + id + ")" +
                        " which is extracted from instant running script list.");
                continue;
            }
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * @param driver
     * @param htmlManager
     * @param proxy
     */
    void extractScript(WebDriver driver, HTMLManager htmlManager, BrowserMobProxy proxy) {
        this.url = driver.getCurrentUrl();
        this.proxy = proxy;
        this.jsExecutor = (JavascriptExecutor) driver;

        if (htmlManager == null) {
            scriptAnalyze(driver);
        } else {
            scriptAnalyze(htmlManager.getScript());
        }
    }

    /**
     * Extract all script using html elements which is extracted before.
     *
     * @param htmlElements
     */
    void scriptAnalyze(List<HTMLElement> htmlElements) {
        for (HTMLElement element : htmlElements) {
            String src = element.getAttributes().get("src");
            String type = (src == null || src.isEmpty()) ? ScriptType.INTERNAL : ScriptType.EXTERNAL;
            byte async = element.getAttributes().get("async") != null ? (byte) 1 : (byte) 0;
            byte defer = element.getAttributes().get("defer") != null ? (byte) 1 : (byte) 0;

            String scriptContent;
            if (src == null) {
                scriptContent = element.getInnerHTML();
            } else {
                scriptContent = Reader.readStringFromURL(src);
            }

            ScriptCodeBlock scriptCodeBlock = new ScriptCodeBlock();
            scriptCodeBlock.setSrc(src);
            scriptCodeBlock.setType(type);
            scriptCodeBlock.setAsync(async);
            scriptCodeBlock.setDefer(defer);
            scriptCodeBlock.setContent(scriptContent);
            scriptCodeBlock.setRelatedHTMLId(element.getId());

            String id = Random.generateId();
            scriptCodeBlock.setId(id);

            saveScript(scriptCodeBlock);
        }
    }

    /**
     * Extract all script using selenium webdriver.
     *
     * @param driver
     */
    void scriptAnalyze(WebDriver driver) {

        List<WebElement> jsScriptElements = driver.findElements(By.tagName("script"));

        for (WebElement scriptElement : jsScriptElements) {
            String src = scriptElement.getAttribute("src");
            String type = (scriptElement.getAttribute("src") == null || scriptElement.getAttribute("src").isEmpty())
                    ? ScriptType.INTERNAL : ScriptType.EXTERNAL;
            byte async = scriptElement.getAttribute("async") != null ? (byte) 1 : (byte) 0;
            byte defer = scriptElement.getAttribute("defer") != null ? (byte) 1 : (byte) 0;

            String scriptContent;
            String relatedHTMLId = "";

            if (type.equals(ScriptType.EXTERNAL)) {

                scriptContent = Reader.readStringFromURL(src);
            } else {
                scriptContent = scriptElement.getAttribute("innerHTML");
                src = "";
            }

            // create ScriptNode here and populate the attributes
            ScriptCodeBlock scriptCodeBlock = new ScriptCodeBlock();
            scriptCodeBlock.setSrc(src);
            scriptCodeBlock.setType(type);
            scriptCodeBlock.setAsync(async);
            scriptCodeBlock.setDefer(defer);
            scriptCodeBlock.setContent(scriptContent);
            scriptCodeBlock.setRelatedHTMLId(relatedHTMLId);

            String id = Random.generateId();
            scriptCodeBlock.setId(id);

            saveScript(scriptCodeBlock);
        }
    }

    /**
     *
     * @param block
     */
    private void saveScript(ScriptCodeBlock block) {
        if (block.getType().equals(ScriptType.INTERNAL)) {
            internalScriptBlocks.add(block.getId());
        } else {
            externalScriptBlocks.add(block.getId());

            if (externalLinkCodeBlockMap.get(block.getSrc()) == null) {
                List<String> ids = new ArrayList<>();
                ids.add(block.getId());
                externalLinkCodeBlockMap.put(block.getSrc(), ids);
            } else {
                externalLinkCodeBlockMap.get(block.getSrc()).add(block.getId());
            }
        }

        if (block.getDefer() == (byte) 1) {
            deferRunScript.add(block.getId());
        } else {
            instantRunScript.add(block.getId());
        }

        // Add to your ScriptNode List or Map
        scriptCodeBlockMap.put(block.getId(), block);
    }

    /**
     * @param htmlManager
     * @param cssManager
     * @param networkRequestManager
     */
    void connectionBuilding(HTMLManager htmlManager, CSSManager cssManager, NetworkRequestManager networkRequestManager) throws NodeExtractionException {
        if (htmlManager != null || cssManager != null || networkRequestManager != null) {
            executeDefaultContent(htmlManager, cssManager, networkRequestManager);
        }
    }

    /**
     * Execute the default running content in script code block.
     *
     * @param htmlManager
     * @param cssManager
     * @param networkRequestManager
     * @throws NodeExtractionException
     */
    private void executeDefaultContent(HTMLManager htmlManager, CSSManager cssManager, NetworkRequestManager networkRequestManager) throws NodeExtractionException {
        List<String> orderScript = instantRunScript;
        orderScript.addAll(deferRunScript);
        scriptRunner(htmlManager, cssManager, networkRequestManager);
    }

    /**
     * Run the script using javascript executor. Collect and analyze the change and network request.
     *
     * @param htmlManager
     * @param cssManager
     * @param networkRequestManager
     * @throws NodeExtractionException
     */
    private void scriptRunner(HTMLManager htmlManager, CSSManager cssManager, NetworkRequestManager networkRequestManager) throws NodeExtractionException {

        if (networkRequestManager != null) {
            proxy.newHar();
        }

        List<String> mark = (List<String>) jsExecutor.executeScript("return window.executeNextScript();");

        // check if the script is executed or all script are finished.
        if (mark.get(2).equals("false")) {
            return;
        }

        String scriptType = mark.get(0);
        String matchContent = mark.get(1);

        // Find the related script.
        ScriptCodeBlock block = findScriptBlockByFilter(scriptType, matchContent);
        if (block == null) {
            Logger.getInstance().info("Fail to find match the script from javascript response to exist script." +
                    " Type:" + scriptType);
            block = new ScriptCodeBlock();
            if (scriptType.equals(ScriptType.INTERNAL)) {
                block.setType(ScriptType.INTERNAL);
                block.setContent(matchContent);
            } else {
                block.setType(ScriptType.EXTERNAL);
                block.setSrc(matchContent);
            }
            saveScript(block);
        }

        // wait script executing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // The node with path in list should be:
        // delete (from bottom to top) use the path from old script, add (from top to bottom) use the path from new script
        // change use the path from new script.
        List<Map<String, Object>> changes = (List<Map<String, Object>>) jsExecutor.executeScript("return window.compareElements();");

        if (networkRequestManager != null) {
            Har har = proxy.getHar();
            List <NetworkRequest> requests = networkRequestManager.processHarEntry(har.getLog().getEntries());
            for (NetworkRequest request : requests) {
                if (request.getUrl().equals(block.getSrc())) {
                    continue;
                }
                request.addRequestFlow(block.getId(), EntityType.SCRIPT, true);
            }
        }

        if (htmlManager != null) {
            for (Map<String, Object> change : changes) {
                // if it changes element's attribute, then 'effected' in block will contains
                // the attribute change <"attriName:oldValue:newValue">.
                // if it create element, then the 'effected' in block will
                // record the attribute list <"attriName::value">
                // this can be used to track the update of attribute for html node, and also the update/change
                // for css, script element
                htmlManager.changeElementForPath(change, cssManager, this, block);
            }
        }
        scriptRunner(htmlManager, cssManager, networkRequestManager);
    }

    /**
     *
     * @param scriptType
     * @param matchContent - Src/Content based on the External/Internal type
     * @return
     */
    public ScriptCodeBlock findScriptBlockByFilter(String scriptType, String matchContent) {
        ScriptCodeBlock result = null;
        if (scriptType.equalsIgnoreCase(ScriptType.EXTERNAL)) {
            if (!matchContent.startsWith("http")) {
                try {
                    matchContent = URLResolver.resolve(this.url, matchContent);

                } catch (URISyntaxException e) {
                    Logger.getInstance().warning("Fail to get the abs link from " + this.url + " and " + url +
                            "analyzing the link in script and observer matching.");
                }
            }

            for (ScriptCodeBlock block : getScriptBlocksByType(ScriptType.EXTERNAL)) {
                if (block.getSrc().equals(matchContent)) {
                    result = block;
                }
            }
        } else if (scriptType.equalsIgnoreCase(ScriptType.INTERNAL)) {
            for (ScriptCodeBlock block : getScriptBlocksByType(ScriptType.INTERNAL)) {
                if (block.getContent().equals(matchContent)) {
                    result = block;
                }
            }
        }
        return result;
    }

    /**
     * Find the html element which contains this script style.
     *
     * @param internalScript
     * @param scriptTagList
     * @return
     */
    private String findRelatedHTMLId(String internalScript, List<HTMLElement> scriptTagList) {
        if (scriptTagList.isEmpty()) {
            return "";
        }

        for (HTMLElement htmlNodeDO : scriptTagList) {
            if (internalScript.equals(htmlNodeDO.getInnerHTML())) {
                return htmlNodeDO.getId();
            }
        }

        return "";
    }

    /**
     * It can be fail to extract external script style from link caused by wrong encoding system. The response body
     * of network requests can be used to detect this failure and correct them.
     *
     * @param url
     * @param correctContent
     */
    void externalScriptContentCorrection(String url, String correctContent) {
        List<String> blockIds = externalLinkCodeBlockMap.get(url);

        if (blockIds != null && !blockIds.isEmpty()) {

            for (String blockId : blockIds) {
                ScriptCodeBlock block = scriptCodeBlockMap.get(blockId);

                if (block == null) {
                    Logger.getInstance().info("Try to fix the script script content in url: " + url +
                            " but does not find the script code block with correct content: " + correctContent);
                }
                block.setContent(correctContent);
            }
        } else {
            Logger.getInstance().info("Does not find match external script for url: " + url);
        }
    }

    /**
     * It can be fail to extract external script style from link caused by wrong encoding system. The response body
     * of network requests can be used to detect this failure and correct them.
     *
     * @param block
     * @param correctContent
     */
    void externalScriptContentCorrection(ScriptCodeBlock block, String correctContent) {
        block.setContent(correctContent);
    }

    /**
     * Mark the script block as deleted
     *
     * @param htmlId - The id of html element.
     */
    void deleteBlockByRelatedHTML(String htmlId) {
        ScriptCodeBlock block = null;
        for (ScriptCodeBlock cssBlock : scriptCodeBlockMap.values()) {
            if (cssBlock.getRelatedHTMLId().equals(htmlId)) {
                block = cssBlock;
                break;
            }
        }

        if (block == null) {
            Logger.getInstance().error("The block to be deleted does not exists " +
                    "which is searched by related html id: " + htmlId);
            return;
        }

        block.setIsDeleted((byte) 1);
    }
}
