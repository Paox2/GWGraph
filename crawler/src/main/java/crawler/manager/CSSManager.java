package crawler.manager;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import crawler.Constant.CSSType;
import crawler.Constant.NetworkConst;
import crawler.entity.CSSCodeBlock;
import crawler.entity.CSSRule;
import crawler.entity.HTMLElement;
import crawler.exception.NodeExtractionException;
import crawler.util.Logger;
import crawler.util.Random;
import crawler.util.Reader;
import crawler.util.URLResolver;
import lombok.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;

import static crawler.Constant.CSSType.*;

/**
 * Functions to create CSS and build connections.
 */
public class CSSManager {
    private String url;

    /**
     * Basic CSS collection.
     */
    private Map<String, CSSCodeBlock> cssCodeBlockMap;
    private Map<String, CSSRule> cssRuleMap;

    /**
     * Ids of all internal css code block.
     */
    private List<String> internalCSSBlocks;

    /**
     * Ids of all inline css code block.
     */
    private List<String> inlineCSSBlocks;

    /**
     * Ids of all external css code block.
     */
    private List<String> externalCSSBlocks;

    /**
     * Map the cssSelector to css styles.
     */
    private Map<String, List<String>> cssSelectorStyleMap;

    /**
     * Map the external css link to the css block.
     */
    private Map<String, List<String>> externalLinkCodeBlockMap;

    /**
     * The list of style id which contains external links.
     */
    private List<String> rulesContainLinks;

    /**
     * No Args Constructor
     */
    CSSManager() {
        cssCodeBlockMap = new HashMap<>();
        cssRuleMap = new HashMap<>();
        internalCSSBlocks = new ArrayList<>();
        inlineCSSBlocks = new ArrayList<>();
        externalCSSBlocks = new ArrayList<>();
        cssSelectorStyleMap = new HashMap<>();
        externalLinkCodeBlockMap = new HashMap<>();
        rulesContainLinks = new ArrayList<>();
    }

    /**
     * Get all css code blocks.
     *
     * @return - All CSS Code Blocks
     */
    public List<CSSCodeBlock> getAllCodeBlocks() {
        return new ArrayList<>(cssCodeBlockMap.values());
    }

    /**
     *
     * @param ids
     * @return - A list of blocks with require id.
     */
    public List<CSSCodeBlock> getCodeBlocksByIds(@NonNull List<String> ids) {
        List<CSSCodeBlock> blocks = new ArrayList<>();
        for (String id : ids) {
            CSSCodeBlock block = cssCodeBlockMap.get(id);
            if (block == null) {
                Logger.getInstance().warning("CSS Block does not found by id: " + id);
            } else {
                blocks.add(block);
            }
        }
        return blocks;
    }

    /**
     * Get all links of external css code blocks.
     *
     * @return
     */
    public List<String> getExternalCSSLinks() {
        return new ArrayList<>(externalLinkCodeBlockMap.keySet());
    }

    /**
     * Get the mapping of the location of external resource to external css code blocks. The value of map is a list since
     * two external css can have the same href attribute, although it is rarely appeared in real document.
     *
     * @return
     */
    public Map<String, List<String>> getLinkCSSCodeBlockMapping() {
        return externalLinkCodeBlockMap;
    }

    /**
     * Get all css rules.
     *
     * @return - All CSS Code Rules
     */
    public List<CSSRule> getAllRules() {
        return new ArrayList<>(cssRuleMap.values());
    }

    /**
     * Get the css rules which contains outbound requests.
     *
     * @return The list of css rules contains link.
     */
    public List<CSSRule> getRulesContainLinks() {
        List<CSSRule> rules = new ArrayList<>();
        for (String id : rulesContainLinks) {
            CSSRule rule = cssRuleMap.get(id);
            if (rule == null) {
                Logger.getInstance().warning("CSS rules does not found by id: " + id);
            } else {
                rules.add(rule);
            }
        }
        return rules;
    }

    /**
     * Find css rules by ids.
     *
     * @param ids
     * @return - A list of rules with required id.
     */
    public List<CSSRule> findRulesById(@NonNull List<String> ids) {
        List<CSSRule> rules = new ArrayList<>();
        for (String id : ids) {
            CSSRule rule = cssRuleMap.get(id);
            if (rule == null) {
                Logger.getInstance().warning("CSS rules does not found by id: " + id);
            } else {
                rules.add(rule);
            }
        }
        return rules;
    }

    /**
     * Find css rules by css rule type (style, media...).
     *
     * @param type - Rule type.
     * @return - A list of rules of a specific rule type
     */
    public List<CSSRule> findRulesByRuleType(@NonNull String type) {
        List<CSSRule> rules = new ArrayList<>();

        for (CSSRule rule : rules) {
            if (rule.getRuleType().equals(type)) {
                rules.add(rule);
            }
        }

        return rules;
    }

    /**
     * Find css rules by css rule types (style, media...).
     *
     * @param types - Rule type.
     * @return - A list of css rules for certain types.
     */
    public List<CSSRule> findRulesByTypes(@NonNull List<String> types) {
        List<CSSRule> rules = new ArrayList<>();
        for (Map.Entry<String, CSSRule> ruleEntry : cssRuleMap.entrySet()) {
            if (types.contains(ruleEntry.getValue().getRuleType())) {
                rules.add(ruleEntry.getValue());
            }
        }
        return rules;
    }

    /**
     * Get the css rules inside the specific css code block.
     *
     * @param block
     * @return - a list of css rules inside certain block
     */
    public List<CSSRule> getRulesInsideBlock(@NonNull CSSCodeBlock block) {
        List<CSSRule> rules = new ArrayList<>();
        for (String id : block.getInsideCSSRules()) {
            CSSRule rule = cssRuleMap.get(id);
            if (rule == null) {
                Logger.getInstance().warning("CSS rule does not found by id: " + id + "  inside block: " + block.getId());
            } else {
                rules.add(rule);
            }
        }
        return rules;
    }

    /**
     * Get the css code block by css type (inline/internal/external).
     *
     * @param type - external/internal/inline css code
     * @return - all specific type css
     */
    public List<CSSCodeBlock> getCSSBlocksByType(@NonNull String type) {
        List<CSSCodeBlock> blocks = new ArrayList<>();
        List<String> ids;
        if (type.equals(CSSType.INTERNAL)) {
            ids = internalCSSBlocks;
        } else if (type.equals(CSSType.EXTERNAL)) {
            ids = externalCSSBlocks;
        } else if (type.equals(CSSType.INLINE)) {
            ids = inlineCSSBlocks;
        } else {
            Logger.getInstance().error("Wrong css types.");
            return blocks;
        }

        for (String id : ids) {
            CSSCodeBlock block = cssCodeBlockMap.get(id);
            if (block == null) {
                Logger.getInstance().warning("Cannot find matching css code block for id (" + id + ") " +
                        "which is found in " + type + " block list.");
            } else {
                blocks.add(block);
            }
        }
        return blocks;
    }

    /**
     * Get the css rules by its container's (code block) css type (inline/internal/external).
     *
     * @param type
     * @return - A list of rules of a specific css type
     */
    public List<CSSRule> getRulesByCSSType(@NonNull String type) {
        List<CSSRule> rules = new ArrayList<>();
        List<String> blockIds;
        if (type.equals(CSSType.INTERNAL)) {
            blockIds = internalCSSBlocks;
        } else if (type.equals(CSSType.EXTERNAL)) {
            blockIds = externalCSSBlocks;
        } else if (type.equals(CSSType.INLINE)) {
            blockIds = inlineCSSBlocks;
        } else {
            Logger.getInstance().error("Wrong css types: " + type);
            return rules;
        }

        for (String blockId : blockIds) {
            CSSCodeBlock block = cssCodeBlockMap.get(blockId);
            if (block == null) {
                Logger.getInstance().warning("Cannot find matching css code block for id (" + blockId + ") " +
                        "which is found in " + type + " block list.");
                continue;
            }

            for (String ruleId : block.getInsideCSSRules()) {
                CSSRule rule = cssRuleMap.get(ruleId);
                if (rule == null) {
                    Logger.getInstance().warning("Cannot find matching css rules for id (" + ruleId + ") " +
                            "which is found in block " + blockId);
                    continue;
                }
                rules.add(rule);
            }
        }
        return rules;
    }

    /**
     * Get external code blocks which location of resource is the input url. The return is a list since
     * two external css can have the same href attribute, although it is rarely appeared in real document.
     *
     * @param url
     * @return - The block maps to certain external css resource
     */
    public List<CSSCodeBlock> getExternalCSSBlockByLink(@NonNull String url) {
        List<String> blockIds = externalLinkCodeBlockMap.get(url);

        if (blockIds.isEmpty()) {
            return null;
        }

        List<CSSCodeBlock> blocks = new ArrayList<>();
        for (String id : blockIds) {
            CSSCodeBlock block = cssCodeBlockMap.get(id);
            if (block == null) {
                Logger.getInstance().warning("Cannot find matching css code block for id (" + id + ") " +
                        "which is found in external link map.");
                continue;
            }
            blocks.add(block);
        }


        return blocks;
    }

    /**
     * Find the css code block with input url and return the rules in it. If over one block is from this location, it
     * will only return all rules in the first code block to avoid duplicate rules since their content is same.
     *
     * @param url
     * @return - The rules extracted from url.
     */
    public List<CSSRule> getExternalCSSRulesByLink(@NonNull String url) {
        List<String> blockIds = externalLinkCodeBlockMap.get(url);

        if (blockIds.isEmpty()) {
            return null;
        }

        CSSCodeBlock block = cssCodeBlockMap.get(blockIds.get(0));

        if (block == null) {
            Logger.getInstance().warning("Cannot find matching css code block for id (" + block.getId() + ") " +
                    "which is found in external link map.");
            return null;
        }

        List<CSSRule> rules = new ArrayList<>();
        for (String id : block.getInsideCSSRules()) {
            CSSRule rule = cssRuleMap.get(id);
            if (rule == null) {
                Logger.getInstance().warning("Cannot find matching css rules for id (" + id + ") " +
                        "which is found in block: " + block.getId());
                continue;
            }

            rules.add(rule);
        }

        return rules;
    }

    /**
     * Get css style rules by selector.
     *
     * @param cssSelector
     * @return - All css style rules with certain css selector.
     */
    public List<CSSRule> getCSSStylesByCSSSelector(@NonNull String cssSelector) {
        List<CSSRule> rules = new ArrayList<>();

        List<String> ids = cssSelectorStyleMap.get(cssSelector);
        if (ids.isEmpty()) {
            return rules;
        }

        for (String id : ids) {
            CSSRule rule = cssRuleMap.get(id);
            if (rule == null) {
                Logger.getInstance().warning("Cannot find matching css rules for id (" + id + ") " +
                        "which is found in css selector mapping.");
                continue;
            }
            rules.add(rule);
        }
        return rules;
    }

    /**
     *
     * @param driver
     * @param htmlManager
     */
    void extractCSS(WebDriver driver, HTMLManager htmlManager) {
        url = driver.getCurrentUrl();

        if (htmlManager != null) {
            extractCSS(htmlManager);
        } else {
            extractCSS(driver);
        }
    }

    /**
     * Extract css using pre-extracted html elements.
     *
     * @param htmlManager
     */
    void extractCSS(HTMLManager htmlManager) {
        extractInlineCSS(htmlManager.getAllElement());

        List<HTMLElement> css = htmlManager.getCSS();
        List<HTMLElement> internal = new ArrayList<>();
        List<HTMLElement> external = new ArrayList<>();

        for (HTMLElement element : css) {
            if (element.getTagName().equals("style")) {
                internal.add(element);
            } else if (element.getTagName().equals("link")) {
                external.add(element);
            }
        }

        extractInternalCSS(internal);
        extractExternalCSS(external);
    }

    /**
     * Extract css using webdriver.
     *
     * @param driver
     */
    void extractCSS(WebDriver driver) {
        extractInternalCSS(driver);
        extractInlineCSS(driver);
        extractExternalCSS(driver);
    }

    /**
     * Extract inline css code without extract html element before
     *
     * @param driver
     * @return
     */
    List<CSSCodeBlock> extractInlineCSS(WebDriver driver) {
        List<CSSCodeBlock> blocks = new ArrayList<>();
        List<WebElement> inlineStyleElements = driver.findElements(By.cssSelector("[style]"));

        for (WebElement inlineStyleElement : inlineStyleElements) {
            String cssType = CSSType.INLINE;
            String text = inlineStyleElement.getAttribute("style");

            CSSCodeBlock block = createCSSCodeBlock(cssType, "", "", text);
            saveCSSBlock(cssType, block);

            CSSRule style = parseCSSRule(text, block.getId());
            saveRule(style, block);
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Extract inline css code based on the css which is extracted before.
     *
     * @param htmlElements
     * @return
     */
    List<CSSCodeBlock> extractInlineCSS(List<HTMLElement> htmlElements) {
        List<CSSCodeBlock> blocks = new ArrayList<>();
        for (HTMLElement element: htmlElements) {
            if (element.getAttributes().get("style") == null) {
                continue;
            }

            String cssType = CSSType.INLINE;
            String text = element.getAttributes().get("style");

            CSSCodeBlock block = createCSSCodeBlock(cssType, element.getId(), "", text);
            saveCSSBlock(cssType, block);

            CSSRule style = parseCSSRule(text, block.getId());
            style.addApplyToHTML(element.getId());
            saveRule(style, block);
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Extract inline css code based on the css which is extracted before.
     *
     * @param element
     * @return
     */
    CSSCodeBlock extractInlineCSS(HTMLElement element) {
        CSSCodeBlock blocks = null;
        if (element.getAttributes().get("style") == null) {
            return blocks;
        }

        String cssType = CSSType.INLINE;
        String text = element.getAttributes().get("style");

        CSSCodeBlock block = createCSSCodeBlock(cssType, element.getId(), "", text);
        saveCSSBlock(cssType, block);

        CSSRule style = parseCSSRule(text, block.getId());
        style.addApplyToHTML(element.getId());
        saveRule(style, block);
        return block;
    }

    /**
     * Extract external css code using webdriver.
     *
     * @param driver
     * @return
     */
    List<CSSCodeBlock> extractExternalCSS(WebDriver driver) {
        List<WebElement> linkElements = driver.findElements(By.cssSelector("link[rel=stylesheet]"));
        List<CSSCodeBlock> blocks = new ArrayList<>();

        for (WebElement link : linkElements) {
            String cssUrl = link.getAttribute("href");
            String cssType = CSSType.EXTERNAL;
            String text = Reader.readStringFromURL(cssUrl);

            CSSCodeBlock block = createCSSCodeBlock(cssType, "", cssUrl, text);
            saveCSSBlock(cssType, block);

            List<CSSRule> rules = parseCSSBlock(text, block.getId(), cssUrl);
            saveRules(rules, block);
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Extract external css code using webdriver.
     *
     * @param htmlElements
     * @return
     */
    List<CSSCodeBlock> extractExternalCSS(List<HTMLElement> htmlElements) {
        List<CSSCodeBlock> blocks = new ArrayList<>();
        for (HTMLElement element : htmlElements) {
            String cssUrl = element.getAttributes().get("href");
            String cssType = CSSType.EXTERNAL;
            String text = Reader.readStringFromURL(cssUrl);

            CSSCodeBlock block = createCSSCodeBlock(cssType, element.getId(), cssUrl, text);
            saveCSSBlock(cssType, block);

            List<CSSRule> rules = parseCSSBlock(text, block.getId(), cssUrl);
            saveRules(rules, block);
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Extract internal css code using htmlElements which are extracted before.
     *
     * @param internalCSSList - A list of HTML element which represents for internal css
     * @return
     */
    List<CSSCodeBlock> extractInternalCSS(List<HTMLElement> internalCSSList) {
        List<CSSCodeBlock> blocks = new ArrayList<>();
        for (HTMLElement element : internalCSSList) {
            String text = element.getInnerHTML();
            String cssType = CSSType.INTERNAL;

            CSSCodeBlock block = createCSSCodeBlock(cssType, element.getId(), "", text);
            saveCSSBlock(cssType, block);
            List<CSSRule> styles = parseCSSBlock(text, block.getId(), this.url);
            saveRules(styles, block);
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Extract internal css code using webdriver.
     *
     * @param driver
     * @return
     */
    List<CSSCodeBlock> extractInternalCSS(WebDriver driver) {
        List<CSSCodeBlock> blocks = new ArrayList<>();
        List<WebElement> internalStyleElements = driver.findElements(By.tagName("style"));
        for (WebElement style : internalStyleElements) {
            String text = style.getAttribute("innerHTML");
            String cssType = CSSType.INTERNAL;

            CSSCodeBlock block = createCSSCodeBlock(cssType, "", "", text);
            saveCSSBlock(cssType, block);

            List<CSSRule> rules = parseCSSBlock(text, block.getId(), this.url);
            saveRules(rules, block);
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Create css code block.
     *
     * @param cssType - Internal/Inline/External
     * @param relatedHTMLId - The HTML element id which contains this css code block.
     * @param src - For external css, the position it stores.
     * @param content - Unprocessing css code.
     * @return
     */
    private CSSCodeBlock createCSSCodeBlock(String cssType, String relatedHTMLId, String src, String content) {
        CSSCodeBlock block = new CSSCodeBlock();

        String blockId = Random.generateId();
        block.setId(blockId);
        block.setType(cssType);
        block.setRelatedHTMLId(relatedHTMLId);
        block.setSrc(src);

        content = content == null ? "" : content;
        block.setUnprocessContent(content);

        return block;
    }

    /**
     * Save the css code block into related storage.
     *
     * @param type - internal/external/inline
     * @param block
     */
    private void saveCSSBlock(String type, CSSCodeBlock block) {
        if (type.equals(CSSType.INTERNAL)) {
            internalCSSBlocks.add(block.getId());
        } else if (type.equals(CSSType.EXTERNAL)){
            externalCSSBlocks.add(block.getId());

            if (externalLinkCodeBlockMap.get(block.getSrc()) != null) {
                externalLinkCodeBlockMap.get(block.getSrc()).add(block.getId());
            } else {
                List<String> ids = new ArrayList<>();
                ids.add(block.getId());
                externalLinkCodeBlockMap.put(block.getSrc(), ids);
            }
        } else if (type.equals(CSSType.INLINE)){
            inlineCSSBlocks.add(block.getId());
        } else {
            Logger.getInstance().warning("Unrecognized css type: " + type);
            return;
        }

        cssCodeBlockMap.put(block.getId(), block);
    }

    /**
     * Save the css style list into list/map.
     *
     * @param cssStyles
     * @param block
     * @throws NodeExtractionException - unexpect css type
     */
    private void saveRules(List<CSSRule> cssStyles, CSSCodeBlock block) {
        for (CSSRule rule : cssStyles) {
            saveRule(rule, block);
        }
    }

    /**
     * Save the css rule into related storage.
     *
     * @param rule
     * @param block
     */
    private void saveRule(CSSRule rule, CSSCodeBlock block) {
        block.addCSSRule(rule);
        cssRuleMap.put(rule.getId(), rule);

        if (rule.getSelector() != null && !rule.getSelector().isEmpty()) {
            if (cssSelectorStyleMap.get(rule.getSelector()) == null) {
                List<String> ids = new ArrayList<>();
                ids.add(rule.getId());
                cssSelectorStyleMap.put(rule.getSelector(), ids);
            } else {
                cssSelectorStyleMap.get(rule.getSelector()).add(rule.getId());
            }
        }

        if (!rule.getExternalLinks().isEmpty()) {
            rulesContainLinks.add(rule.getId());
        }
    }

    /**
     * Find the external Links in css style
     */
    private List<String> findExternalLinksInRule(CSSRule rule) {
        return rule.getExternalLinks();
    }

    /**
     * Find the html element which contains this css style.
     *
     * @param internalCss
     * @param htmlStyleTag
     * @return
     */
    private HTMLElement findRelatedHTML(String internalCss, List<HTMLElement> htmlStyleTag) {
        for (HTMLElement htmlElement : htmlStyleTag) {
            if (internalCss.equals(htmlElement.getInnerHTML())) {
                return htmlElement;
            }
        }
        return null;
    }

    /**
     * Parse the css script.
     *
     * @param css - css content
     * @param cssUrl - css content storage location
     * @return
     */
    private List<CSSRule> parseCSSBlock(String css, String blockId, String cssUrl) {
        List<CSSRule> cssRuleList = new ArrayList<>();

        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
        CSSStyleSheet stylesheet = null;
        try {
            stylesheet = parser.parseStyleSheet(new InputSource(new StringReader(css)), null, null);
        } catch (IOException e) {
            Logger.getInstance().error("Fail to parse css content: " + css);
            return cssRuleList;
        }

        if (stylesheet == null) {
            Logger.getInstance().error("Fail to parse css content: " + css);
            return cssRuleList;
        }

        CSSRuleList ruleList = stylesheet.getCssRules();

        for (int i = 0; i < ruleList.getLength(); i++) {
            org.w3c.dom.css.CSSRule extractRule = ruleList.item(i);

            CSSRule rule = new CSSRule();
            String id = Random.generateId();
            String text = extractRule.getCssText();
            Set<String> links = new HashSet<>(getLinksInCSSText(text, cssUrl));

            String type = ruleTypeAnalyze(extractRule);
            String selector = "";

            if (type.equals(CSS_STYLE_RULE)) {
                CSSStyleRule styleRule = (CSSStyleRule) extractRule;
                selector = styleRule.getSelectorText();
            }

            rule.setId(id);
            rule.setBelongTo(blockId);
            rule.setText(text);
            rule.setSelector(selector);
            rule.setRuleType(type);
            rule.setExternalLinks(new ArrayList<>(links));

            cssRuleList.add(rule);
        }
        return cssRuleList;
    }

    /**
     *
     * @param extractRule - The rule parsed by CSSOMParser
     * @return - The type of the rules
     */
    private String ruleTypeAnalyze(org.w3c.dom.css.CSSRule extractRule) {
        switch (extractRule.getType()) {
            case (short) 1:
                return CSS_STYLE_RULE;
            case (short) 2:
                return CSS_CHARSET_RULE;
            case (short) 3:
                return CSS_IMPORT_RULE;
            case (short) 4:
                return CSS_MEDIA_RULE;
            case (short) 5:
                return CSS_FONT_FACE_RULE;
            case (short) 6:
                return CSS_PAGE_RULE;
            default:
                return CSS_UNKNOWN_RULE;
        }
    }

    /**
     * Only for inline css declarations.
     *
     * @param text
     * @param belongTo - the id the css code block it belongs to.
     * @return
     * @throws NodeExtractionException
     */
    private CSSRule parseCSSRule(String text, String belongTo) {
        CSSRule rule = new CSSRule();
        String id = Random.generateId();
        String selector = "";

        Set<String> links = new HashSet<>(getLinksInCSSText(text, this.url));

        rule.setId(id);
        rule.setBelongTo(belongTo);
        rule.setSelector(selector);
        rule.setText(text);
        rule.setRuleType(CSS_STYLE_RULE);
        rule.setExternalLinks(new ArrayList<>(links));

        return rule;
    }

    /**
     * Find all link from css text.
     *
     * @param originalText
     * @param baseUrl - the webpage which contains current css block
     * @return - Absolute links in css declarations.
     */
    private List<String> getLinksInCSSText(String originalText, String baseUrl) {
        List<String> links = new ArrayList<>();
        Matcher matcher = NetworkConst.CSS_EXTERNAL_LINK_VALUE.matcher(originalText);
        while (matcher.find()) {
            String url = matcher.group(1);
            if(url.startsWith("http")) {
                links.add(url);
                continue;
            }

            if (!baseUrl.isEmpty()) {
                try {
                    url = URLResolver.resolve(baseUrl, url);
                    links.add(url);
                } catch (Exception e) {
                    Logger.getInstance().warning("Fail to find absolute url from " + baseUrl + " and " + url);
                }
            } else {
                try {
                    url = URLResolver.resolve(this.url, url);
                    links.add(url);
                } catch (Exception e) {
                    Logger.getInstance().warning("Fail to find absolute url from " + this.url + " and " + url);
                }
            }
        }
        return links;
    }

    /**
     * It can be fail to extract external css style from link caused by wrong encoding system. The response body
     * of network requests can be used to detect this failure and correct them.
     *
     * @param url
     * @param correctContent
     */
    void externalCssContentCorrection(String url, String correctContent) {
        List<String> blockIds = externalLinkCodeBlockMap.get(url);

        if (blockIds != null && !blockIds.isEmpty()) {
            for (String blockId : blockIds) {
                CSSCodeBlock block = cssCodeBlockMap.get(blockId);

                // remove old styles
                removeRulesInsideBlock(block);

                // add new styles
                List<CSSRule> rules = parseCSSBlock(correctContent, blockId, url);
                saveRules(rules, block);
            }
        } else {
            Logger.getInstance().info("Does not find match external external css code block for url: " + url);
        }
    }

    /**
     * It can be fail to extract external css rules from link caused by wrong encoding system. The response body
     * of network requests can be used to detect this failure and correct it.
     *  @param block
     * @param correctContent
     */
    void externalCssContentCorrection(CSSCodeBlock block, String correctContent) {
        removeRulesInsideBlock(block);
        block.setUnprocessContent(correctContent);

        // add new styles
        List<CSSRule> rules = parseCSSBlock(correctContent, block.getId(), block.getSrc());
        saveRules(rules, block);
    }

    /**
     * Remove the rules inside the block and related storage.
     *
     * @param block
     */
    private void removeRulesInsideBlock(CSSCodeBlock block) {
        List<String> oldRuleIds = block.getInsideCSSRules();
        for (String oldRuleId : oldRuleIds) {
            if (cssRuleMap.get(oldRuleId).getRuleType().equals(CSS_STYLE_RULE)) {
                String styleSelector = cssRuleMap.get(oldRuleId).getSelector();
                cssSelectorStyleMap.get(styleSelector).remove(oldRuleId);
            }

            cssRuleMap.remove(oldRuleId);
        }

        rulesContainLinks.removeAll(oldRuleIds);
        block.removeAllRules();
    }

    /**
     * Match the css style rule to the html element based on css selector.
     *
     * @param js
     * @param htmlManager
     */
    void matchCssSelector(JavascriptExecutor js, HTMLManager htmlManager) {
        Map<String, HTMLElement> pathElementIdMap = htmlManager.buildPathMap();
        for(Map.Entry<String, List<String>> entry : cssSelectorStyleMap.entrySet()) {
            String cssSelector = entry.getKey();
            List<String> currStyleIds = entry.getValue();

            List<String> pathList = (List<String>) js.executeScript(
                    "return window.findHTMLNodeListByCSSSelector(arguments[0]);",
                    cssSelector);

            List<String> htmlIdLists = new ArrayList<>();
            for (String path : pathList) {
                htmlIdLists.add(pathElementIdMap.get(path).getId());
            }

            for (String id : currStyleIds) {
                CSSRule style = cssRuleMap.get(id);
                if (style == null) {
                    Logger.getInstance().warning("Cannot find matching css style rule for id (" + id + ") " +
                            "which is found css selector and style mapping.");
                    continue;
                }

                style.setApplyTo(htmlIdLists);
            }
        }
    }

    /**
     * Mark the css block and inside css rule as deleted
     *
     * @param htmlId - The id of html element.
     */
    void deleteBlockByRelatedHTML(String htmlId) {
        CSSCodeBlock block = null;
        for (CSSCodeBlock cssBlock : cssCodeBlockMap.values()) {
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
        List<CSSRule> rules = getRulesInsideBlock(block);
        for (CSSRule rule : rules) {
            rule.setIsDeleted((byte) 1);
        }
    }

    /**
     * Update the block information which is related to the html with 'id'.
     *
     * @param htmlId
     * @param newValue
     */
    void updateInlineCSSBlockForHTMLEle(String htmlId, String newValue) {
        CSSCodeBlock block = null;
        for (CSSCodeBlock cssBlock : cssCodeBlockMap.values()) {
            if (cssBlock.getRelatedHTMLId().equals(htmlId)) {
                block = cssBlock;
                break;
            }
        }

        if (block == null) {
            block = createCSSCodeBlock(INLINE, htmlId, "", newValue);
            saveCSSBlock(INLINE, block);
            CSSRule rule = parseCSSRule(newValue, block.getId());
            saveRule(rule, block);
            rule.addApplyToHTML(htmlId);
        } else {
            block.setUnprocessContent(newValue);
            String oldRuleId = block.getInsideCSSRules().get(0);
            CSSRule oldRule = cssRuleMap.get(oldRuleId);
            oldRule.setText(newValue);
        }
    }
}
