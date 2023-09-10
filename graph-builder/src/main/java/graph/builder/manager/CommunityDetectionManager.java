package graph.builder.manager;

import graph.builder.View;
import graph.builder.entity.node.Node;
import graph.builder.vo.EdgeFilter;
import graph.builder.vo.LabelPropagationCDParam;
import graph.builder.vo.LouvainCDParam;
import lombok.NonNull;

import java.util.Map;

/**
 * Community Detection Algorithm Collection
 */
public class CommunityDetectionManager {
    /**
     * Label propagations algorithm. A simple and efficient community detection algorithm based on node label propagation.
     * The community detection algorithm are regardless the direct.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter.
     * @param param - Parameter for algorithm.
     * @return - The label for each node.
     */
    protected static Map<Node, Integer> lpa(@NonNull View view, @NonNull EdgeFilter edgeFilter, @NonNull LabelPropagationCDParam param) {
        LabelPropagationCD lpa = new LabelPropagationCD(view, edgeFilter, param);
        lpa.detectCommunity();
        return lpa.getCommunities();
    }

    /**
     * Louvain algorithm for community detection.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter.
     * @param param - Parameter for algorithm.
     * @return - The community index for each node.
     */
    protected static Map<Node, Integer> louvain(@NonNull View view, @NonNull EdgeFilter edgeFilter, @NonNull LouvainCDParam param) {
        LouvainCD louvainCD = new LouvainCD(view, edgeFilter, param);
        louvainCD.detectCommunity();
        return louvainCD.getCommunities();
    }

}
