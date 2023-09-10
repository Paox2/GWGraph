package graph.builder;

import graph.builder.entity.edge.Edge;
import graph.builder.entity.node.Node;
import graph.builder.util.Logger;
import graph.builder.vo.EdgeFilter;
import graph.builder.vo.KatzCentralityParam;
import lombok.NonNull;

import java.util.*;

/**
 * Collection of feature extraction algorithm.
 */
public class FeatureExtraction {
    /**
     * Find the in degree for node in the view.
     *
     * @param view - The view contains node which need to find the in degree.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @return - In degree of the node. Return -1 if the node does not exist in the view.
     */
    public static int inDegree(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter) {
        if (view.findNodeById(node.getId()) == null) {
            Logger.getInstance().warning("Cannot find the in degree for node " + node.getId() +
                    " since node is not found in view " + view.getViewId());
            return -1;
        }

        edgeFilter.buildTypeSet();

        List<String> inIdList = node.getInList();
        int result = 0;

        for (String inId : inIdList) {
            Edge edge = view.findEdgeById(inId);
            if (edge != null && view.findNodeById(edge.getFromNodeId()) != null &&
                    edgeFilter.contains(edge.getEdgeType())) {
                result++;
            }
        }

        return result;
    }

    /**
     * Find the out degree for node in the view.
     *
     * @param view - The view contains node which need to find the out degree.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @return - Out degree of the node. Return -1 if the node does not exist in the view.
     */
    public static int outDegree(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter) {
        if (view.findNodeById(node.getId()) == null) {
            Logger.getInstance().warning("Cannot find the out degree for node " + node.getId() +
                    " since node is not found in view " + view.getViewId());
            return -1;
        }

        edgeFilter.buildTypeSet();

        List<String> outIdList = node.getOutList();
        int result = 0;

        for (String outId : outIdList) {
            Edge edge = view.findEdgeById(outId);
            if (edge != null && view.findNodeById(edge.getToNodeId()) != null &&
                    edgeFilter.contains(edge.getEdgeType())) {
                result++;
            }
        }

        return result;
    }

    /**
     * Find the number of neighbors for node in the view.
     *
     * @param view - The view contains the aim node.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @return - The number of neighbours for node. Return -1 if the node does not exist in the view.
     */
    public static int neighbourCount(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter) {
        List<Node> neighbours = neighbor(view, node, edgeFilter);

        return neighbours == null ? -1 : neighbours.size();
    }

    /**
     * Find the neighbors of node in the view.
     *
     * @param view - The view contains the aim node.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @return - The neighbours of node. Return null if node does not in the view.
     */
    public static List<Node> neighbor(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter) {
        if (view.findNodeById(node.getId()) == null) {
            Logger.getInstance().warning("Cannot find the neighbor number for node " + node.getId() +
                    " since node is not found in view " + view.getViewId());
            return null;
        }
        edgeFilter.buildTypeSet();

        List<Node> neighbors = new ArrayList<>();

        List<String> inIdList = node.getInList();
        for (String inId : inIdList) {
            Edge edge = view.findEdgeById(inId);
            if (edge == null || !edgeFilter.contains(edge.getEdgeType())) {
                continue;
            }

            Node neighbor = view.findNodeById(edge.getFromNodeId());
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }

        List<String> outIdList = node.getOutList();
        for (String outId : outIdList) {
            Edge edge = view.findEdgeById(outId);
            if (edge == null || !edgeFilter.contains(edge.getEdgeType())) {
                continue;
            }

            Node neighbor = view.findNodeById(edge.getToNodeId());
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    /**
     * Get the number of nodes that can be reached from this node. (If the graph contains loop, node itself is also
     * count in the ascendants).
     *
     * @param view - The view contains the aim node.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @param maxDistance - The max distance that can be reached from this node. Set to max integer value if input less than or equal to 0.
     * @return - The number of ascendants of this node. Return -1 if the node does not exist in the view.
     */
    public static int ascendants(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter, int maxDistance) {
        if (view.findNodeById(node.getId()) == null) {
            Logger.getInstance().warning("Cannot count the ascendants for node " + node.getId() +
                    " since node is not found in view " + view.getViewId());
            return -1;
        }

        edgeFilter.buildTypeSet();

        if (maxDistance <= 0) {
            maxDistance = Integer.MAX_VALUE;
        }

        Map<String, Integer> distances = new HashMap<>();
        // Visited set can also be the ascendant id sets.
        Set<String> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        distances.put(node.getId(), 0);
        queue.add(node);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            int currentDistance = distances.get(current.getId());
            if (currentDistance >= maxDistance) {
                continue;
            }

            List<Node> ascendantNodeList = ascendantNodes(view, current, edgeFilter);

            if (ascendantNodeList == null) {
                continue;
            }

            for (Node ascendantNode : ascendantNodeList) {
                if (!visited.contains(ascendantNode.getId())) {
                    distances.put(ascendantNode.getId(), currentDistance+1);
                    visited.add(ascendantNode.getId());
                    queue.add(ascendantNode);
                }
            }
        }

        return visited.size();
    }

    /**
     * Find the ascendant nodes for the specific node in the view with distance is 1.
     *
     * @param view - The view contains the aim node.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @return - The directly ascendant nodes of aim node. Return null if aim node does not in the view.
     */
    public static List<Node> ascendantNodes(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter) {
        if (view.findNodeById(node.getId()) == null) {
            return null;
        }

        edgeFilter.buildTypeSet();

        List<Node> ascendantNodes = new ArrayList<>();
        List<String> ascendantEdgeId = node.getInList();
        for (String edgeId : ascendantEdgeId) {
            Edge edge = view.findEdgeById(edgeId);
            if (edge == null || !edgeFilter.contains(edge.getEdgeType())) {
                continue;
            }

            Node ascendantNode = view.findNodeById(edge.getFromNodeId());
            if (ascendantNode != null) {
                ascendantNodes.add(ascendantNode);
            }
        }

        return ascendantNodes;
    }

    /**
     * Get the number of nodes that can reach this node. (If the graph contains loop, node itself is also
     * count in the ascendants).
     *
     * @param view - The view contains the aim node.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @param maxDistance - The max distance that can reach this node. Set to max integer value if input less than or equal to 0.
     * @return - The number of descendants of this node. Return -1 if the node does not exist in the view.
     */
    public static int descendants(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter, int maxDistance) {
        if (view.findNodeById(node.getId()) == null) {
            Logger.getInstance().warning("Cannot count the descendants for node " + node.getId() +
                    " since node is not found in view " + view.getViewId());
            return -1;
        }

        edgeFilter.buildTypeSet();

        if (maxDistance <= 0) {
            maxDistance = Integer.MAX_VALUE;
        }

        Map<String, Integer> distances = new HashMap<>();
        // Visited set can also be the descendant id sets.
        Set<String> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        distances.put(node.getId(), 0);
        queue.add(node);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            int currentDistance = distances.get(current.getId());
            if (currentDistance >= maxDistance) {
                continue;
            }

            List<Node> descendantNodeList = descendantNodes(view, current, edgeFilter);

            if (descendantNodeList == null) {
                continue;
            }

            for (Node descendantNode : descendantNodeList) {
                if (!visited.contains(descendantNode.getId())) {
                    distances.put(descendantNode.getId(), currentDistance+1);
                    visited.add(descendantNode.getId());
                    queue.add(descendantNode);
                }
            }
        }

        return visited.size();
    }

    /**
     * Find the descendant nodes for the specific node in the view with distance is 1.
     *
     * @param view - The view contains the aim node.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @return - The directly descendant nodes of aim node. Return null if aim node does not in the view.
     */
    public static List<Node> descendantNodes(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter) {
        if (view.findNodeById(node.getId()) == null) {
            return null;
        }

        edgeFilter.buildTypeSet();

        List<Node> descendantNodes = new ArrayList<>();
        List<String> descendantEdgeId = node.getOutList();
        for (String edgeId : descendantEdgeId) {
            Edge edge = view.findEdgeById(edgeId);
            if (edge == null || !edgeFilter.contains(edge.getEdgeType())) {
                continue;
            }

            Node descendantNode = view.findNodeById(edge.getToNodeId());
            if (descendantNode != null) {
                descendantNodes.add(descendantNode);
            }
        }

        return descendantNodes;
    }

    /**
     * Find the shortest path length from input start node to all reachable nodes in the view.
     *
     * @param view - The view contains start node.
     * @param start - Start node.
     * @param edgeFilter - Edge filter.
     * @param direction - 0 for only descendants, 1 for only ascendants, others for two direction.
     * @return - The shortest path length from start node to other reachable nodes (Id) in the view. Return null if start node does not in the view.
     */
    public static Map<String, Integer> shortestPathFrom(@NonNull View view, @NonNull Node start, @NonNull EdgeFilter edgeFilter, @NonNull int direction) {
        if (view.findNodeById(start.getId()) == null) {
            Logger.getInstance().warning("Cannot find the shorted path length from start node " + start.getId() +
                    " since start node is not found in view " + view.getViewId());
            return null;
        }

        edgeFilter.buildTypeSet();
        Map<String, Integer> distances = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.add(start);
        distances.put(start.getId(), 0);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentDist = distances.get(current.getId());
            List<Node> neighbourNodeList = new ArrayList<>();

            if (direction != 0) {
                List<Node> ascendantNodes = ascendantNodes(view, current, edgeFilter);
                if (ascendantNodes != null) {
                    neighbourNodeList.addAll(ascendantNodes);
                }
            }

            if (direction != 1) {
                List<Node> descendantNodeList = descendantNodes(view, current, edgeFilter);
                if (descendantNodeList != null) {
                    neighbourNodeList.addAll(descendantNodeList);
                }
            }


            for (Node neighbourNode : neighbourNodeList) {
                if (!visited.contains(neighbourNode)) {
                    queue.add(neighbourNode);
                    visited.add(neighbourNode);
                    distances.put(neighbourNode.getId(), currentDist + 1);
                }
            }
        }

        return distances;
    }

    /**
     * Calculate maximum distance from start node to all other nodes.
     *
     * @param view - The view contains start node.
     * @param start - Start node.
     * @param edgeFilter - Edge filter.
     * @return - The maximum distance from start node to all other nodes. Return null if start node does not in the view.
     */
    public static double eccentricity(@NonNull View view, @NonNull Node start, @NonNull EdgeFilter edgeFilter) {
        if (view.findNodeById(start.getId()) == null) {
            Logger.getInstance().warning("Cannot calculate eccentricity from start node " + start.getId() +
                    " since start node is not found in view " + view.getViewId());
            return -1.0;
        }

        edgeFilter.buildTypeSet();

        Map<String, Integer> distances = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.add(start);
        distances.put(start.getId(), 0);
        visited.add(start);

        double maxDistance = 0.0;

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentDist = distances.get(current.getId());
            List<Node> neighbours = descendantNodes(view, current, edgeFilter);

            if (neighbours == null) {
                continue;
            }

            for (Node neighbour : neighbours) {
                if (!visited.contains(neighbour)) {
                    queue.add(neighbour);
                    visited.add(neighbour);
                    distances.put(neighbour.getId(), currentDist + 1);
                    if (currentDist + 1 > maxDistance) {
                        maxDistance = currentDist + 1;
                    }
                }
            }
        }

        return maxDistance;
    }

    /**
     * Calculate the mean degree of the neighbours of aim node in the view.
     *
     * @param view - The view contains aim node.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @param considerSide - 0 for only consider the in-edge, 1 for only consider the out-edge, other number means both.
     * @return - The mean degree of the neighbours of aim node, return -1 if aim node does not in the view
     */
    public static double meanConnectivityDegree(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter, int considerSide) {
        if (view.findNodeById(node.getId()) == null) {
            Logger.getInstance().warning("Cannot calculate the mean degree connectivity of node " + node.getId() +
                    " since node is not found in view " + view.getViewId());
            return -1;
        }
        edgeFilter.buildTypeSet();

        double total = 0.0;
        List<Node> neighbours = neighbor(view, node, edgeFilter);
        double numberOfNeighbour = neighbours.size();
        for (Node neighbour : neighbours) {
            if (considerSide != 0) {
                total += outDegree(view, node, edgeFilter);
            }

            if (considerSide != 1) {
                total += inDegree(view, node, edgeFilter);
            }
        }

        return total / numberOfNeighbour;
    }

    /**
     * Calculate the clustering coefficient for the aim node in the view.
     * Since the graph is directed, the function to calculate the coefficient will be changed:
     * the total possible edges between neighbors should be twice since we need to consider the two directions.
     *
     * @param view - The view contains aim node.
     * @param node - Aim node.
     * @param edgeFilter - Edge filter.
     * @return - The cluster coefficient of aim node. Return -1 if the node does not exist in the view.
     */
    public static Double clusterCoefficient(@NonNull View view, @NonNull Node node, @NonNull EdgeFilter edgeFilter) {
        if (view.findNodeById(node.getId()) == null) {
            Logger.getInstance().warning("Cannot calculate the cluster coefficient of node " + node.getId() +
                    " since node is not found in view " + view.getViewId());
            return null;
        }
        edgeFilter.buildTypeSet();

        List<Node> neighbors = neighbor(view, node, edgeFilter);
        if (neighbors == null || neighbors.size() < 2) {
            return 0.0;
        }

        Set<String> neighborIds = new HashSet<>();
        Set<String> edgeIdSet = new HashSet<>();
        for (Node neighbor : neighbors) {
            neighborIds.add(neighbor.getId());
            edgeIdSet.addAll(neighbor.getOutList());
            edgeIdSet.addAll(neighbor.getInList());
        }

        double existingEdgeBetweenNeighbors = 0.0;
        double totalPossibleEdgesBetweenNeighbors = neighbors.size() * (neighbors.size() - 1);

        for (String edgeId : edgeIdSet) {
            Edge edge = view.findEdgeById(edgeId);
            if (edge == null || !edgeFilter.contains(edge.getEdgeType())) {
                continue;
            }

            if (!edge.getFromNodeId().equals(edge.getToNodeId()) &&
                    neighborIds.contains(edge.getFromNodeId()) && neighborIds.contains(edge.getToNodeId())) {
                existingEdgeBetweenNeighbors++;
            }
        }

        return existingEdgeBetweenNeighbors / totalPossibleEdgesBetweenNeighbors;
    }

    /**
     * Calculate the closeness centrality for all the node in the view.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter.
     * @param direction - 0 for only descendants, 1 for only ascendants, others for two direction.
     * @return - The closeness centrality for all nodes in the view.
     */
    public static Map<String, Double> closenessCentrality(@NonNull View view, @NonNull EdgeFilter edgeFilter, @NonNull int direction) {
        edgeFilter.buildTypeSet();
        Map<String, Double> closenessMap = new HashMap<>();
        for (Node node : view.getAllNode()) {
            Map<String, Integer> reachableDist = shortestPathFrom(view, node,edgeFilter, direction);
            if (reachableDist == null) {
                continue;
            }

            reachableDist.remove(node.getId());
            double numPath = reachableDist.size();
            double countPathLength = 0.0;
            for(double length : reachableDist.values()) {
                countPathLength += length;
            }

            if (numPath == 0.0 || countPathLength == 0.0) {
                closenessMap.put(node.getId(), 0.0);
                continue;
            }

            double closeness = 1 / (countPathLength/numPath);

            closenessMap.put(node.getId(), closeness);
        }

        return closenessMap;
    }

    /**
     * Follow the Brandes algorithm which calculate the betweenness centrality for all nodes by cutting the big graph
     * into small chunks to improve the efficiency.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter.
     * @return - The betweenness centrality for all nodes in the view.
     */
    public static Map<String, Double> betweennessCentrality(@NonNull View view, @NonNull EdgeFilter edgeFilter) {
        edgeFilter.buildTypeSet();
        Map<String, Double> betweennessMap = new HashMap<>();
        for (Node node : view.getAllNode()) {
            betweennessMap.put(node.getId(), 0.0);
        }

        for (Node node : view.getAllNode()) {
            bfsBetweennessCal(node, view.getAllNode(), view, edgeFilter, betweennessMap);
        }

        return betweennessMap;
    }

    /**
     * Use BFS to calculate the shortest path from current start "node" to all its descendants. The unreachable nodes
     * does not contribute to the betweenness centrality calculation for this node.
     *
     * @param start
     * @param allNodes
     * @param view
     * @param edgeFilter
     * @param betweennessMap
     */
    private static void bfsBetweennessCal(Node start, List<Node> allNodes, View view, EdgeFilter edgeFilter, Map<String, Double> betweennessMap) {
        Queue<Node> queue = new LinkedList<>();
        Stack<Node> stack = new Stack<>();

        // Initial the predecessor list for each node. Set the distance for each node to -1 which means this node
        // has not been calculated. Sigma means the number of shortest path from start node to this node.
        Map<Node, List<Node>> predecessors = new HashMap<>();
        Map<Node, Integer> distance = new HashMap<>();
        Map<Node, Double> sigma = new HashMap<>();

        for (Node node : allNodes) {
            predecessors.put(node, new ArrayList<>());
            distance.put(node, -1);
            sigma.put(node, 0.0);
        }

        distance.put(start, 0);
        sigma.put(start, 1.0);
        queue.add(start);

        // bfs: go through start points to its descendants, add the unprocessed node to queue for processing later in bfs
        // order. Use stack to record the sequence to make sure popping the node from end to start.
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            stack.push(current);

            List<Node> descendantNodeList = descendantNodes(view, current, edgeFilter);

            if (descendantNodeList == null) {
                continue;
            }

            for (Node descendantNode : descendantNodeList) {
                // if neighbor hasn't been visited yet, add node to the queue for process and set the shortest distance
                // from start node to this child node to (current distance + 1).
                if (distance.get(descendantNode) < 0) {
                    queue.add(descendantNode);
                    distance.put(descendantNode, distance.get(current) + 1);
                }

                // If this descendant node has been processed and the shortest length is same as previous one,
                // it means this could also be another shortest path from current node to descendant node.
                // Add this node 'current' to the predecessors list for this descendant node.
                // Update the sigma: how many shortest path from start to 'current' means how many new shortest paths
                // from start -> 'current' -> 'descendant', add it to the number of shortest path found before.
                if (distance.get(descendantNode) == (distance.get(current) + 1)) {
                    sigma.put(descendantNode, sigma.get(descendantNode) + sigma.get(current));
                    predecessors.get(descendantNode).add(current);
                }
            }
        }

        // A component of cumulative mediated centrality.
        Map<Node, Double> delta = new HashMap<>();
        for (Node node : allNodes) {
            delta.put(node, 0.0);
        }

        // Go from end to start.
        while (!stack.isEmpty()) {
            Node w = stack.pop();
            for (Node v : predecessors.get(w)) {
                double coeff = (sigma.get(v) / sigma.get(w)) * (1.0 + delta.get(w));
                delta.put(v, delta.get(v) + coeff);
            }

            if (w != start) {
                betweennessMap.replace(w.getId(), betweennessMap.get(w.getId()) + delta.get(w));
            }
        }
    }

    /**
     * Calculate the katz centrality for each node.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter
     * @param param - Pre-set parameters.
     * @return - Katz centrality for each node.
     *
     * @see KatzCentralityParam
     */
    public static Map<String, Double> katzCentrality(@NonNull View view, @NonNull EdgeFilter edgeFilter, @NonNull KatzCentralityParam param) {
        double alpha = param.getAlpha();
        double beta = param.getBeta();
        int maxIteration = param.getMax_iteration();
        double tol = param.getTol();
        boolean careAscendant = true;
        boolean careDescendant = true;

        if (param.getNeighborConsideration() == 0) {
            careDescendant = false;
        } else if (param.getNeighborConsideration() == 1) {
            careAscendant = false;
        }

        Map<String, Double> oldCentrality = new HashMap<>();
        Map<String, Double> newCentrality = new HashMap<>();

        for (Node node : view.getAllNode()) {
            oldCentrality.put(node.getId(), beta);
        }

        for (int iteration = 0; iteration < maxIteration; iteration++) {
            for (Node node : view.getAllNode()) {
                double sum = 0;
                List<Node> neighborNodes = new ArrayList<>();

                if (careAscendant) {
                    List<Node> ascendantNodes = ascendantNodes(view, node, edgeFilter);
                    if (ascendantNodes != null) {
                        neighborNodes.addAll(ascendantNodes);
                    }
                }

                if (careDescendant) {
                    List<Node> descendantNodes = descendantNodes(view, node, edgeFilter);
                    if (descendantNodes != null) {
                        neighborNodes.addAll(descendantNodes);
                    }
                }

                for (Node neighbourNode : neighborNodes) {
                    sum += oldCentrality.get(neighbourNode.getId());
                }
                newCentrality.put(node.getId(), beta + alpha * sum);
            }

            if (converged(oldCentrality, newCentrality, tol)) {
                break;
            }

            oldCentrality.replaceAll((i, v) -> newCentrality.get(i));
        }
        return newCentrality;
    }

    /**
     * Check if the calculate result converge. The different between old value and new value less than tolerance threshold.
     *
     * @param oldValues - Nodes old value.
     * @param newValues - Nodes new value.
     * @param tol - Tolerance for difference.
     * @return
     */
    private static boolean converged(Map<String, Double> oldValues, Map<String, Double> newValues, double tol) {
        for (String nodeId : oldValues.keySet()) {
            if (Math.abs(oldValues.get(nodeId) - newValues.get(nodeId)) > tol) {
                return false;
            }
        }
        return true;
    }
}
