package graph.builder.entity.edge;

import graph.builder.common.EdgeType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Edge interface contains the generic variables and functions. The edge is directed.
 */
@Data
@AllArgsConstructor
public class Edge {

    /**
     * Id.
     */
    private String id;

    /**
     * Edge type.
     *
     * @see EdgeType
     */
    private String edgeType;

    /**
     * The id of the node which the edge starts from.
     */
    private String fromNodeId;

    /**
     * The type of the node which the edge starts from.
     */
    private String fromNodeType;

    /**
     * The id of the node which the edge points to.
     */
    private String toNodeId;

    /**
     * The type of the node which the edge points to.
     */
    private String toNodeType;

    /**
     * The this relationship is build when page load.
     */
    private byte onload;

    /**
     * The comment of the edge, the content is different for different edge type.
     * It could be the different of attribute
     */
    private Map<String, String> comment;

    /**
     * No args constructor.
     */
    public Edge() {
        onload = (byte) 1;
        comment = new HashMap<>();
    }

    /**
     * Set the information of the start side of the edge
     *
     * @param fromNodeId
     * @param fromNodeType
     */
    public void setFrom(String fromNodeId, String fromNodeType) {
        this.fromNodeId = fromNodeId;
        this.fromNodeType = fromNodeType;
    }

    /**
     * Set the information of the end side of the edge
     *
     * @param toNodeId
     * @param toNodeType
     */
    public void setTo(String toNodeId, String toNodeType) {
        this.toNodeId = toNodeId;
        this.toNodeType = toNodeType;
    }

    /**
     * For the network request, the request may be potential and not triggered when page loading,
     * these edges will be marked as not onload.
     *
     * @return
     */
    public boolean onLoad() {
        return onload == (byte) 1;
    }
}
 