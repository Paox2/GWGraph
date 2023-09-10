package graph.builder.entity.node;


import crawler.entity.CSSCodeBlock;
import graph.builder.common.NodeType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * CSS node entity in the graph.
 */
@Setter
@Getter
public class CSSNode extends Node {
    /**
     * CSS type: inline/internal/external.
     */
    private String cssType;

    /**
     * CSS code block (undivided into multiple rules)
     */
    private String unprocessedContent;

    /**
     * If the element is deleted.
     */
    private byte isDeleted;

    /**
     * No args constructor.
     */
    public CSSNode() {
        super("", NodeType.CSS, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    /**
     * Transfer from the data comes from crawler.
     *
     * @param css - CSS Code Block
     */
    public void transferFrom(CSSCodeBlock css) {
        this.id = css.getId();
        this.cssType = css.getType();
        this.isDeleted = css.getIsDeleted();
        this.unprocessedContent = css.getUnprocessContent();
    }

    /**
     * Check if the node is deleted.
     *
     * @return - boolean
     */
    public boolean isDelete() {
        return isDeleted == (byte) 1;
    }
}
