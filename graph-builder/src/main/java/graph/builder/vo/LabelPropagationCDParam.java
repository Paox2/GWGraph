package graph.builder.vo;

import lombok.Data;

/**
 * Parameters for label propagation algorithm
 */
@Data
public class LabelPropagationCDParam {
    /**
     * Minimum ratio of nodes need to update the label to continue the iteration.
     */
    double convergence;

    /**
     * Max iterations
     */
    int maxIteration;

    /**
     * Whether or not the node list should be shuffled before propagate the label.
     */
    boolean shuffle;

    /**
     * The weight for the node at in side.
     */
    double inDegreeWeight;

    /**
     * The weight for the node at out side.
     */
    double outDegreeWeight;

    /**
     * No args constructor.
     */
    LabelPropagationCDParam() {
        convergence = 0.00001;
        maxIteration = 500;
        shuffle = true;
        inDegreeWeight = 1.0;
        outDegreeWeight = 1.0;
    }
}
