package crawler.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Css Node Class
 */
@Data
public class CSSRule {
    /**
     * Basic Content
     */
    private String id;
    private String ruleType;
    private String text;
    private byte isDeleted;

    /**
     * The id of css code block it belongs to.
     */
    private String belongTo;

    /**
     * For css style rules, it contains a selector for the html elements it applies to
     */
    private String selector;

    /**
     * External links in url();
     */
    private List<String> externalLinks;

    /**
     * The html elements it applies to.
     */
    private List<String> applyTo;

    /**
     * No args Constructor.
     */
    public CSSRule() {
        applyTo = new ArrayList<>();
        externalLinks = new ArrayList<>();
        isDeleted = (byte) 0;
    }

    /**
     * Add html id into apply to list.
     *
     * @param id
     */
    public void addApplyToHTML(String id) {
        applyTo.add(id);
    }

}
