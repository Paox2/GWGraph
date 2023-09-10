package graph.builder.entity.node;

import graph.builder.common.NodeType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Setter
@Getter
public class IframeNode extends Node {
    /**
     * Represent the corresponding child view id.
     */
    private String viewId;

    /**
     * No args constructor.
     */
    public IframeNode() {
        super("", NodeType.IFRAME, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    /**
     * Set the id of corresponding child view.
     *
     * @param viewId
     */
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }
}
