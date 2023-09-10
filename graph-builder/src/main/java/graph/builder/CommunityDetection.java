package graph.builder;

import graph.builder.entity.node.Node;
import graph.builder.manager.CommunityDetectionManager;
import graph.builder.vo.EdgeFilter;
import graph.builder.vo.LabelPropagationCDParam;
import graph.builder.vo.LouvainCDParam;
import lombok.NonNull;

import java.util.Map;

/**
 * Community Detection Cluster.
 */
public class CommunityDetection extends CommunityDetectionManager {
    /**
     * Label propagations algorithm. A simple and efficient community detection algorithm based on node label propagation.
     * The community detection algorithm are regardless the direct.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter.
     * @param param - Parameter for algorithm.
     * @return - The label for each node.
     */
    public static Map<Node, Integer> labelPropagationAlgorithm(@NonNull View view, @NonNull EdgeFilter edgeFilter, @NonNull LabelPropagationCDParam param) {
        return lpa(view, edgeFilter, param);
    }

    /**
     * Louvain algorithm for community detection.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter.
     * @param param - Parameter for algorithm.
     * @return - The community index for each node.
     */
    public static Map<Node, Integer> LouvainAlgorithm(@NonNull View view, @NonNull EdgeFilter edgeFilter, @NonNull LouvainCDParam param) {
        return louvain(view, edgeFilter, param);
    }

}
