package graph.builder.manager;

import graph.builder.View;
import graph.builder.common.NodeType;
import graph.builder.entity.edge.Edge;
import graph.builder.entity.node.Node;
import graph.builder.util.Random;
import graph.builder.vo.EdgeFilter;
import graph.builder.vo.LouvainCDParam;

import java.util.*;

/**
 * Louvain Propagation Algorithm Implementation.
 */
public class LouvainCD {

    /**
     * total edge weight.
     */
    private final double m;

    /**
     * The node will be assigned to community and the community will be seen as a new node. This is the map
     * from original node (input) to the new node which include this original node.
     */
    private Map<String, List<Node>> nodeBelongMap;

    private final double convergence;
    private final int maxAggregationTimes;
    private final int maxInnerIteration;
    private final Map<String, Double> edgeWeights;

    private Map<String, Integer> nodeCommunityMap;
    private Map<String, Node> nodeMap;
    private Map<String, Edge> edgeMap;

    /**
     * No Args Constructor.
     *
     * @param view - Aim view.
     * @param edgeFilter - Edge filter.
     * @param param - Parameter for algorithm.
     */
    LouvainCD(View view, EdgeFilter edgeFilter, LouvainCDParam param) {
        edgeFilter.buildTypeSet();

        this.convergence = param.getConvergence();
        this.maxAggregationTimes = param.getMaxAggregationTimes();
        this.maxInnerIteration = param.getMaxInnerIteration();
        this.edgeWeights = param.getWeights();

        nodeCommunityMap = new HashMap<>();

        nodeMap = new HashMap<>();
        edgeMap = new HashMap<>();
        nodeBelongMap = new HashMap<>();

        for (Node node : view.getAllNode()) {
            nodeMap.put(node.getId(), node);
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(node);
            nodeBelongMap.put(node.getId(), nodeList);
        }

        for (Edge edge : view.getAllEdge()) {
            if (!edgeFilter.contains(edge.getEdgeType())) {
                continue;
            }

            if (nodeMap.containsKey(edge.getFromNodeId()) && nodeMap.containsKey(edge.getToNodeId())) {
                edgeMap.put(edge.getId(), edge);
                double edgeWeight = edgeWeights.get(edge.getEdgeType());
                Node fromNode = nodeMap.get(edge.getFromNodeId());
                Node toNode = nodeMap.get(edge.getFromNodeId());
            }
        }

        m = getTotalWeight(edgeMap);
    }

    /**
     * Get the community distribution.
     *
     * @return - The label for each node.
     */
    Map<Node, Integer> getCommunities() {
        Map<Node, Integer> result = new HashMap<>();

        for (Map.Entry<String, List<Node>> entry : nodeBelongMap.entrySet()) {
            String newNodeId = entry.getKey();
            Integer nodeCommunity = nodeCommunityMap.get(newNodeId);
            for (Node oriNode : entry.getValue()) {
                result.put(oriNode, nodeCommunity);
            }
        }

        return result;
    }

    /**
     * Louvain algorithm for community detection.
     * Outer loop: Check the difference of the total modularity before and after inner loop. Stop when difference less
     * than convergence or
     */
    void detectCommunity() {

        initialCommunity();
        boolean improvement = true;
        int iteration = 0;

        double previousModularity = computeTotalModularity();

        while (improvement && iteration < maxAggregationTimes) {
            iteration++;

            // Community detection.
            improvement = performCommunityDetection();
            double newModularity = computeTotalModularity();
            if (Math.abs(newModularity - previousModularity) < convergence) {
                break;
            }
            previousModularity = newModularity;

            if (improvement) {
                aggregateGraph();
            }
        }
    }

    /**
     * Assign each node a community.
     */
    private void initialCommunity() {
        int communityNumber = 0;
        for (Node node : nodeMap.values()) {
            nodeCommunityMap.put(node.getId(), communityNumber);
            communityNumber++;
        }
    }

    /**
     * Compute the total modularity for the view.
     *
     * @return - Total modularity.
     */
    private double computeTotalModularity() {
        double q = 0.0;

        for (Node node : nodeMap.values()) {
            int fromSideCommunity = nodeCommunityMap.get(node.getId());

            for (String edgeId : node.getOutList()) {
                Edge edge = edgeMap.get(edgeId);
                Node toSide = nodeMap.get(edge.getToNodeId());
                int toSideCommunity = nodeCommunityMap.get(toSide.getId());

                int delta = fromSideCommunity == toSideCommunity ? 1 : 0;
                q += (edgeWeights.get(edge.getEdgeType()) - (getSurroundWeight(node) * getSurroundWeight(toSide)) / (2.0 * m)) * delta;
            }
        }

        return q / (2.0 * m);
    }

    /**
     * Get the total surround edge weight of node.
     *
     * @param node
     * @return
     */
    private double getSurroundWeight(Node node) {
        double weight = 0.0;
        for (String outId : node.getOutList()) {
            Edge outEdge = edgeMap.get(outId);
            weight += edgeWeights.get(outEdge.getEdgeType());
        }
        for (String inId : node.getInList()) {
            Edge inEdge = edgeMap.get(inId);
            weight += edgeWeights.get(inEdge.getEdgeType());
        }

        return weight;
    }

    /**
     * Inner loop:
     *
     * @return
     */
    private boolean performCommunityDetection() {
        boolean hasChanged = true;
        int iteration = 0;

        while (hasChanged && iteration <= maxInnerIteration) {
            iteration++;
            hasChanged = false;

            for (Node node : nodeMap.values()) {
                int bestCommunity = findBestCommunity(node);
                if (bestCommunity != nodeCommunityMap.get(node.getId())) {
                    nodeCommunityMap.put(node.getId(), bestCommunity);
                    hasChanged = true;
                }
            }
        }

        return hasChanged;
    }

    /**
     * Find the best community for node where modularity increase max.
     *
     * @param node
     * @return
     */
    private int findBestCommunity(Node node) {
        int currentCommunity = nodeCommunityMap.get(node.getId());

        double ki = getSurroundWeight(node);
        double currentModularityContribution = getModularityContribution(node, currentCommunity, ki);

        int bestCommunity = currentCommunity;
        double bestModularityGain = 0.0;

        Set<Integer> neighboringCommunities = new HashSet<>();
        for (String outEdgeId : node.getOutList()) {
            String toNodeId = edgeMap.get(outEdgeId).getToNodeId();
            Node toNode = nodeMap.get(toNodeId);
            neighboringCommunities.add(nodeCommunityMap.get(toNode.getId()));
        }
        for (String inEdgeId : node.getOutList()) {
            String fromNodeId = edgeMap.get(inEdgeId).getFromNodeId();
            Node fromNode = nodeMap.get(fromNodeId);
            neighboringCommunities.add(nodeCommunityMap.get(fromNode.getId()));
        }

        for (int community : neighboringCommunities) {
            double modularityGain = getModularityContribution(node, community, ki) - currentModularityContribution;
            if (modularityGain > bestModularityGain) {
                bestModularityGain = modularityGain;
                bestCommunity = community;
            }
        }
        return bestCommunity;
    }

    /**
     * Calculate the modularity after the node becoming the member of community.
     *
     * @param node - Node.
     * @param community - The community node add in.
     * @param ki - Total edge weight surround node.
     * @return - The modularity value after node added in community
     */
    private double getModularityContribution(Node node, int community, double ki) {
        double ki_in = getWeightToCommunity(node, community);
        double sigma_tot = getCommunityWeight(community);
        double sigma_in = getInnerCommunityWeight(community);

        return (sigma_in + 2 * ki_in) / (2 * m) - Math.pow((sigma_tot + ki) / (2 * m), 2);
    }

    /**
     * Get the edge weight between the node and the community.
     *
     * @param node - node
     * @param community - community
     * @return - Edge weight between node and community.
     */
    private double getWeightToCommunity(Node node, int community) {
        double weight = 0.0;
        for (String outId : node.getOutList()) {
            Edge outEdge = edgeMap.get(outId);
            Node toSideNode = nodeMap.get(outEdge.getToNodeId());
            if (nodeCommunityMap.get(toSideNode.getId()) == community) {
                weight += edgeWeights.get(outEdge.getEdgeType());
            }
        }
        for (String inId : node.getInList()) {
            Edge inEdge = edgeMap.get(inId);
            Node fromSideNode = nodeMap.get(inEdge.getFromNodeId());
            if (nodeCommunityMap.get(fromSideNode.getId()) == community) {
                weight += edgeWeights.get(inEdge.getEdgeType());
            }
        }

        return weight;
    }

    /**
     * Calculate edge weight inside the community + connected to other community.
     *
     * @param community - community
     * @return - Total edge weight.
     */
    private double getCommunityWeight(int community) {
        double weight = 0.0;
        for (Edge edge : edgeMap.values()) {
            Node from = nodeMap.get(edge.getFromNodeId());
            Node to = nodeMap.get(edge.getToNodeId());

            if (nodeCommunityMap.get(from.getId()) == community || nodeCommunityMap.get(to.getId()) == community) {
                weight += edgeWeights.get(edge.getEdgeType());
            }
        }

        return weight;
    }

    /**
     * Calculate edge weight inside the community
     *
     * @param community - community
     * @return - Total edge weight.
     */
    private double getInnerCommunityWeight(int community) {
        double weight = 0.0;
        for (Edge edge : edgeMap.values()) {
            Node from = nodeMap.get(edge.getFromNodeId());
            Node to = nodeMap.get(edge.getToNodeId());

            if (nodeCommunityMap.get(from.getId()) == community && nodeCommunityMap.get(to.getId()) == community) {
                weight += edgeWeights.get(edge.getEdgeType());
            }
        }

        return weight;
    }

    /**
     * Get total weight for edge map.
     *
     * @param edges
     * @return
     */
    private double getTotalWeight(Map<String, Edge> edges) {
        return edges.values().stream().mapToDouble(edge -> edgeWeights.get(edge.getEdgeType())).sum();
    }

    private void aggregateGraph() {
        // community to node id
        Map<Integer, Node> communityNewNodeMap = new HashMap<>();
        // node id to community
        Map<String, Integer> newNodeCommunityMap = new HashMap<>();
        // new node id to the original node belongs to this node.
        Map<String, List<Node>> newNodeBelongMap = new HashMap<>();

        Map<String, String> oldNodeNewNodeMap = new HashMap<>();

        for (Node node : nodeMap.values()) {
            Node newNode = communityNewNodeMap.get(nodeCommunityMap.get(node.getId()));
            // check if the community has been created by other members in the community.
            if (newNode == null) {
                newNode = new Node(Random.generateId(), NodeType.HTML, new ArrayList<>(), new ArrayList<>());

                communityNewNodeMap.put(nodeCommunityMap.get(node.getId()), newNode);
                newNodeCommunityMap.put(newNode.getId(), nodeCommunityMap.get(node.getId()));
                newNodeBelongMap.put(newNode.getId(), new ArrayList<>());
            }

            oldNodeNewNodeMap.put(node.getId(), newNode.getId());

            newNodeBelongMap.get(newNode.getId()).addAll(nodeBelongMap.get(node.getId()));
        }

        this.nodeMap = new HashMap<>();
        for (Node node : communityNewNodeMap.values()) {
            this.nodeMap.put(node.getId(), node);
        }

        for (Edge edge : edgeMap.values()) {
            String newNodeIdFrom = oldNodeNewNodeMap.get(edge.getFromNodeId());
            String newNodeIdTo = oldNodeNewNodeMap.get(edge.getToNodeId());
            edge.setFromNodeId(newNodeIdFrom);
            edge.setToNodeId(newNodeIdTo);
        }

        this.nodeCommunityMap = newNodeCommunityMap;
        this.nodeBelongMap = newNodeBelongMap;
    }
}
