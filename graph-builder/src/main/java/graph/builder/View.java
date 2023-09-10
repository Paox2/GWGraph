package graph.builder;

import graph.builder.common.NodeType;
import graph.builder.entity.edge.Edge;
import graph.builder.entity.node.*;
import graph.builder.util.Logger;
import graph.builder.util.Random;
import graph.builder.vo.EdgeFilter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is not a node but represents a view. A view can contain various other types of nodes.
 * This view mainly represents individual frames and their contents.
 */
public class View {
    /**
     * view id.
     */
    private String id;

    /**
     * The id of parent view (frame).
     */
    private String parentViewId;

    /**
     * The id of the node in parent view (frame), which points to this view.
     */
    private String parentNodeId;

    /**
     *
     */
    private Node rootHTMLNode;

    /**
     * Edge id to edge
     */
    private Map<String, Edge> edgeMap;

    private Map<String, Node> nodeMap;

    private Map<String, HTMLNode> htmlNodeMap;
    private Map<String, CSSNode> cssNodeMap;
    private Map<String, CSSRuleNode> cssRuleMap;
    private Map<String, ScriptNode> scriptNodeMap;
    private Map<String, NetworkNode> networkNodeMap;
    private Map<String, IframeNode> iframeNodeMap;

    /**
     * No args constructor.
     */
    public View() {
        edgeMap = new HashMap<>();

        nodeMap = new HashMap<>();

        htmlNodeMap = new HashMap<>();
        cssNodeMap = new HashMap<>();
        cssRuleMap = new HashMap<>();
        scriptNodeMap = new HashMap<>();
        networkNodeMap = new HashMap<>();
        iframeNodeMap = new HashMap<>();
    }

    /**
     * Get view id.
     *
     * @return
     */
    public String getViewId() {
        return id;
    }

    /**
     * Get parent view id.
     *
     * @return
     */
    public String getParentViewId() {
        return parentViewId;
    }

    /**
     * Get the HTML node ID in the parent view which points to this view.
     *
     * @return
     */
    public String getParentNodeId() {
        return parentNodeId;
    }

    /**
     *
     * @return
     */
    public Node getRootHTMLNode() {
        return rootHTMLNode;
    }

    public Node setRootHTMLNode(@NonNull Node root) {
        if (!root.getNodeType().equals(NodeType.HTML)) {
            return null;
        }

        if (root.getId() == null) {
            root.setId(Random.generateId());
        }

        if (nodeMap.containsKey(root.getId())) {
            Logger.getInstance().warning("Node id exists.");
            return null;
        }

        this.rootHTMLNode = root;
        return root;
    }

    /**
     * Get all nodes in the view.
     *
     * @return
     */
    public List<Node> getAllNode() {
        return new ArrayList<>(nodeMap.values());
    }

    /**
     * Get all edges in the view.
     *
     * @return
     */
    public List<Edge> getAllEdge() {
        return new ArrayList<>(edgeMap.values());
    }

    /**
     * Get all html nodes in the view.
     *
     * @return
     */
    public List<HTMLNode> getAllHTMLNodes() {
        return new ArrayList<>(htmlNodeMap.values());
    }

    /**
     * Get all css nodes in the view.
     *
     * @return
     */
    public List<CSSNode> getAllCSSNodes() {
        return new ArrayList<>(cssNodeMap.values());
    }

    /**
     * Get all css rules in the view.
     *
     * @return
     */
    public List<CSSRuleNode> getAllCSSRules() {
        return new ArrayList<>(cssRuleMap.values());
    }

    /**
     * Get all script nodes in the view.
     *
     * @return
     */
    public List<ScriptNode> getAllScriptNodes() {
        return new ArrayList<>(scriptNodeMap.values());
    }

    /**
     * Get all network nodes in the view.
     *
     * @return
     */
    public List<NetworkNode> getAllNetworkNodes() {
        return new ArrayList<>(networkNodeMap.values());
    }

    /**
     * Get all iframe in the current VIEW. Each iframe corresponds to a view in the graph.
     *
     * @return
     */
    public List<IframeNode> getAllIframeNodes() {
        return new ArrayList<>(iframeNodeMap.values());
    }

    /**
     *
     * @param id
     */
    public void setId(@NonNull String id) {
        this.id = id;
    }

    /**
     * Set the id of parent frame.
     *
     * @param id
     */
    public void setParentViewId(@NonNull String id) {
        this.parentViewId = id;
    }

    /**
     * The child frame should be included in a HTML 'Iframe' tag in the parent view.
     * This method is to set the id of this node.
     *
     * @param id
     */
    public void setParentNodeId(@NonNull String id) {
        this.parentNodeId = id;
    }

    /**
     * Find node by id.
     *
     * @param id
     * @return
     */
    public Node findNodeById(@NonNull String id) {
        return nodeMap.get(id);
    }

    /**
     * Add html node into the view.
     *
     * @return - The id of html node, if the id already exists, return null.
     */
    public String addHTMLNode(@NonNull HTMLNode htmlNode) {
        if (htmlNode.getId() == null || htmlNode.getId().equals("")) {
            htmlNode.setId(Random.generateId());
        }

        if (htmlNodeMap.containsKey(htmlNode.getId())) {
            return null;
        }

        nodeMap.put(htmlNode.getId(), htmlNode);
        htmlNodeMap.put(htmlNode.getId(), htmlNode);
        return htmlNode.getId();
    }

    /**
     * Find html node by tag name.
     *
     * @return - The html node with specific tag name,
     */
    public List<HTMLNode> findHTMLNodeByTag(@NonNull String tagName) {
        List<HTMLNode> nodeList = new ArrayList<>();
        if (tagName.equals("")) {
            return nodeList;
        }

        for (HTMLNode htmlNode : htmlNodeMap.values()) {
            if (htmlNode.getTagName() != null && htmlNode.getTagName().equals(tagName)) {
                nodeList.add(htmlNode);
            }
        }

        return nodeList;
    }

    /**
     * Delete HTML node by id.
     *
     * @param id
     * @return - deleted html node if remove success, otherwise return null.
     */
    public HTMLNode deleteHTMLNodeById(@NonNull String id) {
        HTMLNode node = htmlNodeMap.remove(id);
        if (node != null) {
            nodeMap.remove(id);
            breakConnection(node);
        }

        return node;
    }

    /**
     * Add css node into the view.
     *
     * @return - The id of css node, if the id already exists, return null.
     */
    public String addCSSNode(@NonNull CSSNode cssNode) {
        if (cssNode.getId() == null || cssNode.getId().equals("")) {
            cssNode.setId(Random.generateId());
        }

        if (cssNodeMap.containsKey(cssNode.getId())) {
            return null;
        }

        nodeMap.put(cssNode.getId(), cssNode);
        cssNodeMap.put(cssNode.getId(), cssNode);
        return cssNode.getId();
    }

    /**
     * Delete css node by id.
     *
     * @param id
     * @return - deleted css node if remove success, otherwise return null.
     */
    public CSSNode deleteCSSNodeById(@NonNull String id) {
        CSSNode node = cssNodeMap.remove(id);
        if (node != null) {
            nodeMap.remove(id);
            breakConnection(node);
        }

        return node;
    }

    /**
     * Add css rule into the view.
     *
     * @return - The id of css rule, if the id already exists, return null.
     */
    public String addCSSRuleNode(@NonNull CSSRuleNode cssRuleNode) {
        if (cssRuleNode.getId() == null || cssRuleNode.getId().equals("")) {
            cssRuleNode.setId(Random.generateId());
        }

        if (cssRuleMap.containsKey(cssRuleNode.getId())) {
            return null;
        }

        nodeMap.put(cssRuleNode.getId(), cssRuleNode);
        cssRuleMap.put(cssRuleNode.getId(), cssRuleNode);
        return cssRuleNode.getId();
    }

    /**
     * Delete css rule node by id.
     *
     * @param id
     * @return - deleted css rule node if remove success, otherwise return null.
     */
    public CSSRuleNode deleteCSSRuleNodeById(@NonNull String id) {
        CSSRuleNode node = cssRuleMap.remove(id);
        if (node != null) {
            nodeMap.remove(id);
            breakConnection(node);
        }

        return node;
    }

    /**
     * Add script node into the view.
     *
     * @return - The id of script node, if the id already exists, return null.
     */
    public String addScriptNode(@NonNull ScriptNode scriptNode) {
        if (scriptNode.getId() == null || scriptNode.getId().equals("")) {
            scriptNode.setId(Random.generateId());
        }

        if (scriptNodeMap.containsKey(scriptNode.getId())) {
            return null;
        }

        nodeMap.put(scriptNode.getId(), scriptNode);
        scriptNodeMap.put(scriptNode.getId(), scriptNode);
        return scriptNode.getId();
    }

    /**
     * Delete script node by id.
     *
     * @param id
     * @return - deleted script node if remove success, otherwise return null.
     */
    public ScriptNode deleteScriptNodeById(@NonNull String id) {
        ScriptNode node = scriptNodeMap.remove(id);
        if (node != null) {
            nodeMap.remove(id);
            breakConnection(node);
        }

        return node;
    }

    /**
     * Add network node into the view. The input node may be placed in an existing network node if their url is the same
     * and return the id of which node its content placed.
     * Also, if this is a new url but the id input has existed,
     * the result will be null.
     *
     * @return - The id of saved network node, the id maybe changed if the network node with same url has exists.
     */
    public String addNetworkNode(@NonNull NetworkNode networkNode) {
        if (networkNode.getId() == null || networkNode.getId().equals("")) {
            networkNode.setId(Random.generateId());
        }

        NetworkNode exist = findNetworkNodeByURL(networkNode.getUrl());

        if (exist != null) {
            exist.addMessages(networkNode.getMessageList());
            return exist.getId();
        } else {
            if (networkNodeMap.containsKey(networkNode.getId())) {
                return null;
            }

            nodeMap.put(networkNode.getId(), networkNode);
            networkNodeMap.put(networkNode.getId(), networkNode);
        }
        return networkNode.getId();
    }

    /**
     * Find the network node using URL.
     *
     * @param url
     * @return
     */
    public NetworkNode findNetworkNodeByURL(@NonNull String url) {
        for (NetworkNode node : networkNodeMap.values()) {
            if (node.getUrl().equals(url)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Delete network node by id.
     *
     * @param id
     * @return - deleted network node if remove success, otherwise return null.
     */
    public NetworkNode deleteNetworkNodeById(@NonNull String id) {
        NetworkNode node = networkNodeMap.remove(id);
        if (node != null) {
            nodeMap.remove(node.getUrl());
            breakConnection(node);
        }

        return node;
    }

    /**
     * Delete network node by url.
     *
     * @param url
     * @return - deleted network node if remove success, otherwise return null.
     */
    public NetworkNode deleteNetworkNodeByURL(@NonNull String url) {
        NetworkNode node = findNetworkNodeByURL(url);
        if (node != null) {
            networkNodeMap.remove(node.getId());
            nodeMap.remove(node.getId());
            breakConnection(node);
        }

        return node;
    }

    /**
     * Add a iframe node in the view which is related to a child view.
     *
     * @param iframeNode
     * @return
     */
    public String addIframe(@NonNull IframeNode iframeNode) {
        if (iframeNode.getId() == null || iframeNode.getId().equals("")) {
            iframeNode.setId(Random.generateId());
        }

        if (iframeNodeMap.containsKey(iframeNode.getId())) {
            return null;
        }

        nodeMap.put(iframeNode.getId(), iframeNode);
        iframeNodeMap.put(iframeNode.getId(), iframeNode);
        return iframeNode.getId();
    }

    /**
     * Delete iframe node by id.
     *
     * @param id
     * @return - deleted network node if remove success, otherwise return null.
     */
    public IframeNode deleteIframeNodeById(@NonNull String id) {
        if (id == null) {
            return null;
        }

        IframeNode node = iframeNodeMap.remove(id);
        if (node != null) {
            nodeMap.remove(id);
            breakConnection(node);
        }
        return node;
    }

    /**
     * Delete the in-edge and out-edges for the node.
     *
     * @param node - Aim node.
     * @return - Deleted edges.
     */
    public List<Edge> breakConnection(Node node) {
        List<Edge> deleted = new ArrayList<>();
        if (node.getInList() != null && !node.getInList().isEmpty()) {
            deleted.addAll(deleteEdgesById(node.getInList()));
        }

        if (node.getOutList() != null && !node.getOutList().isEmpty()) {
            deleted.addAll(deleteEdgesById(node.getOutList()));
        }

        return deleted;
    }

    /**
     * Add edge into the view.
     *
     * @param edge
     * @return - Return edge id if add successes, otherwise return null.
     */
    public String addEdge(@NonNull Edge edge) {
        if (edge.getFromNodeId() == null || edge.getToNodeId() == null) {
            return null;
        }

        // Make sure the node in two side has already added in the view.
        if (!nodeMap.containsKey(edge.getFromNodeId()) || !nodeMap.containsKey(edge.getToNodeId())) {
            return null;
        }

        if (edge.getId() == null || edge.getId().equals("")) {
            edge.setId(Random.generateId());
        }

        if (edgeMap.containsKey(edge.getId())) {
            return null;
        }

        nodeMap.get(edge.getFromNodeId()).addOutEdge(edge);
        nodeMap.get(edge.getToNodeId()).addInEdge(edge);

        edgeMap.put(edge.getId(), edge);

        return edge.getId();
    }

    /**
     * Add new edge into the view.
     *
     * @param fromNodeId
     * @param fromType
     * @param toNodeId
     * @param toType
     * @return - Return edge.
     */
    public Edge addEdge(@NonNull String fromNodeId, @NonNull String fromType, @NonNull String toNodeId, @NonNull String toType, @NonNull String edgeType) {
        // Make sure the node in two side has already added in the view.
        if (!nodeMap.containsKey(fromNodeId) || !nodeMap.containsKey(toNodeId)) {
            return null;
        }

        Edge edge = new Edge();
        edge.setId(Random.generateId());
        edge.setEdgeType(edgeType);
        edge.setFrom(fromNodeId, fromType);
        edge.setTo(toNodeId, toType);

        edgeMap.put(edge.getId(), edge);

        nodeMap.get(fromNodeId).addOutEdge(edge);
        nodeMap.get(toNodeId).addInEdge(edge);

        return edge;
    }

    /**
     * Unrecommended use, no consistence check. Add edge into the view without check if the nodes on the two sides of edge exist. Be careful to use since it may cause inconsistent.
     * If use this method, please use with connectionEdge() which will check if the node exists at edge's two side.
     *
     * @param edge
     * @return - Return edge id if add successes, otherwise return null.
     */
    public String addEdgeUncheckExistence(@NonNull Edge edge) {
        if (edge.getFromNodeId() == null || edge.getToNodeId() == null) {
            return null;
        }

        if (edge.getId() == null) {
            edge.setId(Random.generateId());
        }

        if (edgeMap.containsKey(edge.getId())) {
            return null;
        }

        edgeMap.put(edge.getId(), edge);

        return edge.getId();
    }

    /**
     * Delete edge by id.
     *
     * @param id
     * @return - deleted and return the edge if exists, otherwise return null.
     */
    public Edge deleteEdgeById(@NonNull String id) {
        Edge edge = edgeMap.remove(id);
        if (edge != null) {
            Node from = findNodeById(edge.getFromNodeId());
            if (from != null) {
                from.removeOutEdge(edge);
            }
            Node to = findNodeById(edge.getToNodeId());
            if (to != null) {
                to.removeInEdge(edge);
            }
        }

        return edge;
    }

    /**
     * Delete edge by id.
     *
     * @param ids
     * @return - deleted and return the edge if exists, otherwise return null.
     */
    public List<Edge> deleteEdgesById(@NonNull List<String> ids) {
        List<Edge> deleteList = new ArrayList<>();
        for (String id : ids) {
            Edge delete = deleteEdgeById(id);
            if (delete != null) {
                deleteList.add(delete);
            }
        }

        return deleteList;
    }

    /**
     * Find edge by id.
     *
     * @param id
     * @return
     */
    public Edge findEdgeById(@NonNull String id) {
        return edgeMap.get(id);
    }

    /**
     * Verify the edge exists and edge type meets the requirement of filter.
     *
     * @param id
     * @param filter
     * @return
     */
    public boolean edgeTypeVerify(@NonNull String id, @NonNull EdgeFilter filter) {
        Edge edge = findEdgeById(id);
        return edge != null && filter.contains(edge.getEdgeType());
    }

    /**
     Find the node on the to-side of the edge where the edge id is specified .
     *
     * @param id
     * @return
     */
    public Node findToSideForEdge(@NonNull String id) {
        Edge edge = findEdgeById(id);
        if(edge == null) {
            return null;
        }

        Node toSide = findNodeById(edge.getToNodeId());
        if (toSide == null) {
            return null;
        }
        return toSide;
    }

    /**
     * Find the edge that the to-side is the input node with a specific edge type.
     *
     * @param node
     * @return
     */
    public List<Edge> findInEdgeForNode(@NonNull Node node) {
        List<Edge> edgeList = new ArrayList<>();
        for (String edgeId : node.getInList()) {
            Edge in = edgeMap.get(edgeId);
            if (in == null) {
                Logger.getInstance().error("Fail to find edge for id: " + edgeId +
                        " which is the in-edge for node " + node.getId() + ".");
                continue;
            }
            edgeList.add(in);
        }
        return edgeList;
    }

    /**
     * Find all the edges surrounding the input node.
     *
     * @param node
     * @return
     */
    public List<Edge> findAllEdgeForNode(@NonNull Node node) {
        List<Edge> edgeList = new ArrayList<>();
        for (String edgeId : node.getInList()) {
            Edge in = edgeMap.get(edgeId);
            if (in == null) {
                Logger.getInstance().error("Fail to find edge for id: " + edgeId +
                        " which is the in-edge for node " + node.getId() + ".");
                continue;
            }
            edgeList.add(in);
        }

        for (String edgeId : node.getOutList()) {
            Edge in = edgeMap.get(edgeId);
            if (in == null) {
                Logger.getInstance().error("Fail to find edge for id: " + edgeId +
                        " which is the out-edge for node " + node.getId() + ".");
                continue;
            }
            edgeList.add(in);
        }
        return edgeList;
    }

    /**
     * Find all the edges surrounding the input node with a specific type.
     *
     * @param node
     * @param egdeType
     * @return
     */
    public List<Edge> findAllEdgeForNode(@NonNull Node node, @NonNull String egdeType) {
        List<Edge> edgeList = new ArrayList<>();
        for (String edgeId : node.getInList()) {
            Edge in = edgeMap.get(edgeId);
            if (in == null) {
                Logger.getInstance().error("Fail to find edge for id: " + edgeId +
                        " which is the in-edge for node " + node.getId() + ".");
                continue;
            }

            if (in.getEdgeType().equals(egdeType)) {
                edgeList.add(in);
            }
            edgeList.add(in);
        }

        for (String edgeId : node.getOutList()) {
            Edge out = edgeMap.get(edgeId);
            if (out == null) {
                Logger.getInstance().error("Fail to find edge for id: " + edgeId +
                        " which is the out-edge for node " + node.getId() + ".");
                continue;
            }

            if (out.getEdgeType().equals(egdeType)) {
                edgeList.add(out);
            }
            edgeList.add(out);
        }
        return edgeList;
    }


    /**
     * Find the edge that the to-side is the input node with a specific  edge type.
     *
     * @param node
     * @param egdeType
     * @return
     */
    public List<Edge> findInEdgeForNode(@NonNull Node node, @NonNull String egdeType) {
        List<Edge> edgeList = new ArrayList<>();
        for (String edgeId : node.getInList()) {
            Edge in = edgeMap.get(edgeId);
            if (in == null) {
                Logger.getInstance().error("Fail to find edge for id: " + edgeId +
                        " which is the in-edge for node " + node.getId() + ".");
                continue;
            }

            if (in.getEdgeType().equals(egdeType)) {
                edgeList.add(in);
            }
        }
        return edgeList;
    }

    /**
     * Find the edge that the from side is the input node.
     *
     * @param node
     * @return
     */
    public List<Edge> findOutEdgeForNode(@NonNull Node node) {
        List<Edge> edgeList = new ArrayList<>();
        for (String edgeId : node.getOutList()) {
            Edge out = edgeMap.get(edgeId);
            if (out == null) {
                Logger.getInstance().error("Fail to find edge for id: " + edgeId +
                        " which is the out-edge for node " + node.getId() + ".");
                continue;
            }
            edgeList.add(out);
        }
        return edgeList;
    }

    /**
     * Find the edge that the from side is the input node with specify edge type..
     *
     * @param node
     * @param egdeType
     * @return
     */
    public List<Edge> findOutEdgeForNode(@NonNull Node node, @NonNull String egdeType) {
        List<Edge> edgeList = new ArrayList<>();
        for (String edgeId : node.getOutList()) {
            Edge out = edgeMap.get(edgeId);
            if (out == null) {
                Logger.getInstance().error("Fail to find edge for id: " + edgeId +
                        " which is the out-edge for node " + node.getId() + ".");
                continue;
            }

            if (out.getEdgeType().equals(egdeType)) {
                edgeList.add(out);
            }
        }
        return edgeList;
    }

    /**
     * Ensure that all edges in the graph exist twice in the node's associated edgeList
     * Clear the missing edges if the node in one side of edge is missing.
     *
     * @return - The edge fail to find its connected node.
     */
    public List<Edge> connectionEdge() {
        List<Edge> failList = new ArrayList<>();
        for (Edge edge : edgeMap.values()) {
            Node from = findNodeById(edge.getFromNodeId());
            Node to = findNodeById(edge.getToNodeId());

            if (from == null || to == null) {
                failList.add(edge);
                Logger.getInstance().info("Edge (" + edge.getId() + " - " + edge.getEdgeType() + ") fail to build the connect.");

                if (from != null) {
                    from.removeOutEdge(edge);
                }

                if (to != null) {
                    to.removeOutEdge(edge);
                }
                continue;
            }

            from.addOutEdge(edge);
            to.addInEdge(edge);
        }

        for (Edge failEdge : failList) {
            edgeMap.remove(failEdge.getId());
        }

        return failList;
    }
}