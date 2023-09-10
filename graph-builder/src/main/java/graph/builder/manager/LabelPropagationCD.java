package graph.builder.manager;

import graph.builder.FeatureExtraction;
import graph.builder.View;
import graph.builder.entity.node.Node;
import graph.builder.vo.EdgeFilter;
import graph.builder.vo.LabelPropagationCDParam;

import java.util.*;

/**
 * Label Propagation Algorithm Implementation.
 */
class LabelPropagationCD{
    private final View view;
    private final EdgeFilter edgeFilter;

    private final double convergence;
    private final int maxIterations;
    private final boolean shuffle;
    private final double inDegreeWeight;
    private final double outDegreeWeight;
    private final Map<Node, Integer> nodeLabelMap;

    /**
     * Constructor.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter.
     * @param param - Parameter for algorithm.
     */
    LabelPropagationCD(View view, EdgeFilter edgeFilter, LabelPropagationCDParam param) {
        this.view = view;
        this.edgeFilter = edgeFilter;

        this.convergence = param.getConvergence();
        this.maxIterations = param.getMaxIteration();
        this.shuffle = param.isShuffle();
        this.inDegreeWeight = param.getInDegreeWeight();
        this.outDegreeWeight = param.getOutDegreeWeight();

        this.nodeLabelMap = new HashMap<>();
    }

    /**
     * Get the community distribution.
     *
     * @return - The label for each node.
     */
    Map<Node, Integer> getCommunities() {
        return nodeLabelMap;
    }

    /**
     * Label propagations algorithm. A simple and efficient community detection algorithm based on node label propagation.
     * The community detection algorithm are regardless the direct.
     */
    void detectCommunity() {
        edgeFilter.buildTypeSet();
        List<Node> nodeList = view.getAllNode();
        // Assign unique label for each node.
        for (Node node : nodeList) {
            nodeLabelMap.put(node, node.hashCode());
        }

        double nodeSize = nodeList.size();
        double labelsChanged = 1.0;
        int iterations = 0;

        while ((labelsChanged / nodeSize) > convergence && iterations < maxIterations) {
            labelsChanged = 0.0;
            iterations++;

            // shuffle to increase randomness.
            List<Node> processList = new ArrayList<>(nodeList);
            if (shuffle) {
                processList = new ArrayList<>(nodeList);
                Collections.shuffle(processList);
            }

            // propagation.
            for (Node node : processList) {
                Map<Integer, Double> labelCounts = neighbourLabelCount(node);

                // find the most frequently label
                double maxCount = -1.0;
                int dominantLabel = nodeLabelMap.get(node);
                for (Map.Entry<Integer, Double> entry : labelCounts.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        dominantLabel = entry.getKey();
                        maxCount = entry.getValue();
                    }
                }

                if (dominantLabel != nodeLabelMap.get(node)) {
                    nodeLabelMap.put(node,  dominantLabel);
                    labelsChanged++;
                }
            }
        }
    }

    /**
     * Count the occurrence of label in node's neighbours.
     *
     * @param node - Aim node.
     * @return - Label Count.
     */
    private Map<Integer, Double> neighbourLabelCount(Node node) {
        List<Node> ascendantNodes = FeatureExtraction.ascendantNodes(view, node, edgeFilter);
        List<Node> descendantNodes = FeatureExtraction.descendantNodes(view, node, edgeFilter);
        Map<Integer, Double> labelCounts = new HashMap<>();

        if (ascendantNodes != null) {
            for (Node ascendant : ascendantNodes) {
                Integer neighborLabel = nodeLabelMap.get(ascendant);
                labelCounts.put(neighborLabel,
                        labelCounts.getOrDefault(neighborLabel, 0.0) + inDegreeWeight);
            }
        }

        if (descendantNodes != null) {
            for (Node descendant : descendantNodes) {
                Integer neighborLabel = nodeLabelMap.get(descendant);
                labelCounts.put(neighborLabel,
                        labelCounts.getOrDefault(neighborLabel, 0.0) + outDegreeWeight);
            }
        }

        return labelCounts;
    }
}
