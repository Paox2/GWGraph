package crawler.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Css Code Block, may contains multiple css style.
 */
@Data
public class CSSCodeBlock {
    /**
     * Basic info.
     */
    private String id;
    private String type;
    private String unprocessContent;
    private byte isDeleted;

    /**
     * Block content generated from external network response. The src is the link of this network request.
     */
    private String src;

    /**
     * The html element contains this inline/internal css block or contains the request to apply this external resource.
     */
    private String relatedHTMLId;

    /**
     * All inside css rules.
     *
     * @see CSSRule
     */
    private List<String> insideCSSRules;

    /**
     * No args Constructor.
     */
    public CSSCodeBlock() {
        insideCSSRules = new ArrayList<>();
        isDeleted = (byte) 0;
    }

    /**
     * Add css style into the list.
     */
    public void addCSSRule(CSSRule rule) {
        insideCSSRules.add(rule.getId());
    }

    /**
     * Remove all css styles in this block
     */
    public void removeAllRules() {
        insideCSSRules.clear();
    }

    /**
     *
     * @return - Number of rules inside this block
     */
    public int countRules() {
        return insideCSSRules.size();
    }
}
