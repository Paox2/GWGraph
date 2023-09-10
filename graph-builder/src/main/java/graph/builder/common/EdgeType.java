package graph.builder.common;

/**
 * Edge type.
 */
public class EdgeType {
    /**
     * - From HTML node to HTML node
     *
     * Parent-child relationship in DOM tree, from parent to child
     */
    public static final String PARENT_CHILD_RELATION = "Parent-Child";

    /**
     * - From HTML node to internal/inline Script/CSS node
     */
    public static final String CONTAINS = "Contains";

    /**
     * - From CSS node to its rules.
     */
    public static final String CSS_RULE_CONTAINER = "CSS Rule Container";

    /**
     * - From HTML/CSS/SCRIPT node to Network node
     *
     * HTML request external css/script, HTML has an external link.
     * Script request external resource, access external resource, etc.
     */
    public static final String NETWORK_REQUEST = "Network Request";

    /**
     * - From Network node to HTML/Script node
     *
     * Represent the resource/or response such as external CSS/Script, Script (based on the request).
     */
    public static final String NETWORK_RESPONSE = "Network Response";

    /**
     * - From HTML node to HTML node
     *
     * Contains a parent-child relationship but more to represent a DOM change caused by javascript in DOM tree
     */
    public static final String DOM_CHANGE = "DOM Change";

    /**
     * - From CSS Rule to HTML node
     *
     * CSS style apply on HTML node.
     */
    public static final String APPLY_TO = "Apply To";

    /**
     * - From Shadow Host to Shadow Tree
     *
     * DOM elements can be shadow host which points to a shadow tree with a hidden set of DOM nodes.
     */
    public static final String SHADOW_HOST = "Shadow Host";

    /**
     * - From Iframe Node (one type of HTML node) to Another View
     *
     * In this graph, each iframe can be seen as a small view, and the main frame is the main view.
     * The view is separate from each other except link from parent view to its child iframe.
     */
    public static final String IFRAME_CONTAINER = "Iframe Container";

    /**
     * - From HTML/Script node to Script node
     *
     * Not at this time
     */
    public static final String FUNCTION_CALL = "Function Call";

}