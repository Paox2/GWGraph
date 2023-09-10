package graph.builder.common;

/**
 * Entity type.
 */
public class NodeType {
    /**
     * HTML Node.
     */
    public static final String HTML = "HTML";

    /**
     * CSS Node, the whole block which may contains multiple css rules.
     */
    public static final String CSS = "CSS";

    /**
     * For single CSS rules which can be a @font or style apply to html element based on selector.
     */
    public static final String CSS_RULE = "CSS Rule";

    /**
     * Script code block.
     */
    public static final String SCRIPT = "Script";

    /**
     * Represent a single network node which could contains multiple flow with different network methods.
     */
    public static final String NETWORK = "Network";

    /**
     * Represent a iframe which refer to a view but does not show the inside content.
     */
    public static final String IFRAME = "iframe";
}
