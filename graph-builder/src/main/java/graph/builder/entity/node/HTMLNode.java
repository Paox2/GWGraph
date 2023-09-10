package graph.builder.entity.node;

import crawler.entity.HTMLElement;
import graph.builder.common.NodeType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * HTML node entity in the graph.
 */
@Setter
@Getter
public class HTMLNode extends Node {
    /**
     * The id in html tag.
     */
    private String identifyID;

    /**
     * Tag name.
     */
    private String tagName;

    /**
     * The text content of a node and its descendants.
     */
    private String textualContent;

    /**
     * Class name.
     */
    private String classNames;

    /**
     * The attributes of the HTML element.
     */
    private Map<String, String> attributes;

    /**
     * Inner HTML, expose the textual content and also html code.
     */
    private String innerHTML;

    /**
     * Used to determine if the node is from the html document itself or if it was created later.
     */
    private byte isInitialNode;

    /**
     * If this node is an element in shadow tree.
     */
    private byte isShadow;

    /**
     * If the node is deleted.
     */
    private byte isDeleted;

    /**
     * The depth of node in DOM tree.
     */
    private int depth;

    /**
     * No args constructor.
     */
    public HTMLNode() {
        super("", NodeType.HTML, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    /**
     * Transfer from the data comes from crawler.
     *
     * @param html
     */
    public void transferFrom(HTMLElement html) {
        this.id = html.getId();
        this.identifyID = html.getIdentifyID();
        this.tagName = html.getTagName();
        this.textualContent = html.getTextualContent();
        this.classNames = html.getClassNames();
        this.attributes = html.getAttributes();
        this.innerHTML = html.getInnerHTML();
        this.isInitialNode = html.getInitialNode();
        this.isDeleted = html.getIsDeleted();
        this.depth = html.getDepth();
        this.isShadow = html.getShadowHost() == null ? (byte) 0 : (byte) 1;
    }

    /**
     * Check if the node is deleted.
     *
     * @return - boolean
     */
    public boolean isDelete() {
        return isDeleted == (byte) 1;
    }

    /**
     * Check if the node is in the shadow tree.
     *
     * @return - boolean
     */
    public boolean isShadow() {
        return isShadow == (byte) 1;
    }
}

