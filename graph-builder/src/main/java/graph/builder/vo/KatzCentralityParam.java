package graph.builder.vo;

import lombok.Data;

/**
 * Parameters for Katz Centrality Algorithm.
 */
@Data
public class KatzCentralityParam {
    /**
     * Attenuation factor, always a value equal to or less than 1/max eigenvalue
     */
    double alpha = 0.01;

    /**
     * Scalar or dictionary.
     */
    double beta = 1.0;

    /**
     * Max iterations.
     */
    int max_iteration = 1000;

    /**
     * Error tolerance used to check convergence in power method iteration.
     */
    double tol = 1.0e-6;

    /**
     * Since the graph is directed, we provide the position of neighbour nodes that should be considered to effect the
     * calculation of the feature of current node.
     *
     * 0 - only consider the ascendants side node.
     * 1 - only consider the descendants side node.
     * 2 - consider all neighbours of node to calculate the centrality. (Same as undirected graph)
     */
    int neighborConsideration = 2;
}
