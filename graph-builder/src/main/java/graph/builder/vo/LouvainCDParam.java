package graph.builder.vo;

import graph.builder.common.EdgeType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Parameter for Louvain Community Detection
 */
@Data
public class LouvainCDParam {
    /**
     * The minimum total modularity changes to end the iteration.
     */
    double convergence;

    /**
     * Max aggregation times for outer loop.
     */
    int maxAggregationTimes;

    /**
     * Max iteration times for inner loop.
     */
    int maxInnerIteration;

    /**
     * Weight for different types of edge.
     */
    Map<String, Double> weights;

    /**
     * No args constructor.
     */
    public LouvainCDParam() {
        convergence = 0.00001;
        maxAggregationTimes = 3;
        maxInnerIteration = 100;

        weights = new HashMap<>();
        weights.put(EdgeType.PARENT_CHILD_RELATION, 1.0);
        weights.put(EdgeType.CONTAINS, 1.0);
        weights.put(EdgeType.CSS_RULE_CONTAINER, 1.0);
        weights.put(EdgeType.NETWORK_REQUEST, 1.0);
        weights.put(EdgeType.NETWORK_RESPONSE, 1.0);
        weights.put(EdgeType.DOM_CHANGE, 1.0);
        weights.put(EdgeType.APPLY_TO, 1.0);
        weights.put(EdgeType.SHADOW_HOST, 1.0);
        weights.put(EdgeType.IFRAME_CONTAINER, 1.0);
        weights.put(EdgeType.FUNCTION_CALL, 1.0);
    }

    /**
     * Update the edge weight for type.
     *
     * @param edgeType - Edge Type.
     * @param newWeight - New weight.
     * @return - Return 1 if update success, otherwise, return 0.
     */
    public int updateEdgeWeight(String edgeType, double newWeight) {
        if (!weights.containsKey(edgeType) || newWeight <= 0.0) {
            return 0;
        }

        weights.put(edgeType, newWeight);
        return 1;
    }

    /**
     * Get the edge weight for type.
     *
     * @param edgeType - Edge Type.
     * @return - Return a double greater than 0 is edge type is valid, otherwise return -1.
     */
    public double getEdgeWeight(String edgeType) {
        if (!weights.containsKey(edgeType)) {
            return -1.0;
        }

        return weights.get(edgeType);
    }
}
