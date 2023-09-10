package graph.builder.entity.node;

import crawler.entity.ScriptCodeBlock;
import graph.builder.common.NodeType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * CSS node in the graph.
 */
@Setter
@Getter
public class ScriptNode extends Node {
    /**
     * Script Type
     * @see graph.builder.constant.ScriptType
     */
    private String scriptType;

    /**
     * Script Content
     */
    private String content;

    /**
     * If the script is loaded async.
     */
    private byte async;

    /**
     * If the script is defer loaded.
     */
    private byte defer;

    /**
     * No args constructor.
     */
    public ScriptNode() {
        super("", NodeType.SCRIPT, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    /**
     * Transfer from the data comes from crawler.
     *
     * @param node
     */
    public void transferFrom(ScriptCodeBlock node) {
        this.id = node.getId();
        this.scriptType = node.getType();
        this.content = node.getContent();
        this.async = node.getAsync();
        this.defer = node.getDefer();
    }
}
