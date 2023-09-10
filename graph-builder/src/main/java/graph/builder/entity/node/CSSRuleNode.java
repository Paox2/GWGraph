package graph.builder.entity.node;

import crawler.entity.CSSRule;
import graph.builder.common.NodeType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * CSS rule node.
 */
@Setter
@Getter
public class CSSRuleNode extends Node {
    /**
     * Rule type
     *
     * @see graph.builder.constant.CSSRuleType
     */
    private String ruleType;

    /**
     * Content
     */
    private String text;

    /**
     * Only for style type.
     */
    private String selector;

    /**
     * If the node is deleted.
     */
    private byte isDeleted;

    /**
     * No args constructor
     */
    public CSSRuleNode() {
        super("", NodeType.CSS_RULE, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    /**
     * Transfer from the data comes from crawler.
     *
     * @param rule
     */
    public void transferFrom(CSSRule rule) {
        this.id = rule.getId();
        this.ruleType = rule.getRuleType();
        this.text = rule.getText();
        this.selector = rule.getSelector();
        this.isDeleted = rule.getIsDeleted();
    }

    /**
     * Check if the node is deleted.
     *
     * @return - boolean
     */
    public boolean isDeleted() {
        return isDeleted == (byte) 1;
    }
}
