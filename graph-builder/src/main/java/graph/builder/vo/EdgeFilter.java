package graph.builder.vo;

import graph.builder.common.EdgeType;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Edge type filter.
 */
@Data
public class EdgeFilter {
    private boolean parentChildRelationship;
    private boolean contains;
    private boolean networkRequest;
    private boolean networkResponse;
    private boolean domChange;
    private boolean applyTo;
    private boolean shadowHost;
    private boolean iframeContainer;
    private boolean cssContainer;
    private boolean functionCall;

    private Set<String> typeList;

    /**
     * No args constructor. The apply to relationship is false by default. Other types is true.
     */
    public EdgeFilter() {
        parentChildRelationship = true;
        contains = true;
        networkRequest = true;
        networkResponse = true;
        domChange = true;
        shadowHost = true;
        iframeContainer = true;
        functionCall = true;
        cssContainer = true;
        applyTo = false;
        typeList = new HashSet<>();
    }

    /**
     * Remove all filters in type set.
     */
    public void resetTypeSet() {
        typeList.clear();
    }


    /**
     * Build the set which includes all allowed edge type.
     */
    public void buildTypeSet() {
        if (!typeList.isEmpty()) {
            return;
        }

        if (parentChildRelationship) {
            typeList.add(EdgeType.PARENT_CHILD_RELATION);
        }

        if (contains) {
            typeList.add(EdgeType.CONTAINS);
        }

        if (networkRequest) {
            typeList.add(EdgeType.NETWORK_REQUEST);
        }

        if (networkResponse) {
            typeList.add(EdgeType.NETWORK_RESPONSE);
        }

        if (domChange) {
            typeList.add(EdgeType.DOM_CHANGE);
        }

        if (applyTo) {
            typeList.add(EdgeType.APPLY_TO);
        }

        if (shadowHost) {
            typeList.add(EdgeType.SHADOW_HOST);
        }

        if (iframeContainer) {
            typeList.add(EdgeType.IFRAME_CONTAINER);
        }

        if (functionCall) {
            typeList.add(EdgeType.FUNCTION_CALL);
        }

        if (cssContainer) {
            typeList.add(EdgeType.CSS_RULE_CONTAINER);
        }
    }

    /**
     * Check if type is contains in typeList.
     *
     * @param edgeType
     * @return
     */
    public boolean contains(String edgeType) {
        return typeList.contains(edgeType);
    }
}
