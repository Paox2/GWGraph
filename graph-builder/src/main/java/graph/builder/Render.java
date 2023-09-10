package graph.builder;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import graph.builder.common.EdgeType;
import graph.builder.common.NodeType;
import graph.builder.entity.edge.Edge;
import graph.builder.entity.node.CSSRuleNode;
import graph.builder.entity.node.HTMLNode;
import graph.builder.entity.node.NetworkNode;
import graph.builder.entity.node.Node;
import graph.builder.util.Logger;
import graph.builder.vo.EdgeFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Render {
    private static final int nodeWidth = 30;
    private static final int nodeHeight = 30;

    private static final String htmlNodeStyle = "shape=ellipse;strokeColor=none;fillColor=yellow;";
    private static final String shadowHtmlStyle = "shape=ellipse;strokeColor=none;fillColor=lightyellow;";
    private static final String cssNodeStyle = "shape=ellipse;strokeColor=none;fillColor=green;";
    private static final String cssRuleStyle = "shape=ellipse;strokeColor=none;fillColor=lightgreen;";
    private static final String networkStyle = "shape=ellipse;strokeColor=none;fillColor=blue;";
    private static final String potentialNetworkStyle = "shape=ellipse;strokeColor=none;fillColor=lightblue;";
    private static final String scriptNodeStyle = "shape=ellipse;strokeColor=none;fillColor=red;";
    private static final String iframeStyle = "shape=rectangle;strokeColor=none;fillColor=purple;";


    private static final String unknownNodeStyle = "shape=ellipse;strokeColor=black;fillColor=white;";


    private static final String edgeBlack = "endArrow=classic;strokeColor=black;";
    private static final String edgeGray = "endArrow=classic;strokeColor=lightgray;";

    public static void renderViewToImage(View view, String imageFormat, String filePath, EdgeFilter filter) {
        filter.buildTypeSet();
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        Map<String, Object> vertexMap = insertVertex(parent, graph, view.getAllNode());
        insertEdge(parent, graph, vertexMap, view.getAllEdge(), filter);

        graph.getModel().endUpdate();
        mxIGraphLayout layout = new mxOrganicLayout(graph);
//        mxIGraphLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(graph.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
        try {
            ImageIO.write(image, imageFormat, new File(filePath));
        } catch (IOException e) {
            Logger.getInstance().error("Fail to write image to file");
        }
    }

    private static void insertEdge(Object parent, mxGraph graph, Map<String, Object> vertexMap, List<Edge> edgeList, EdgeFilter filter) {
        for (Edge edge : edgeList) {
            if (!filter.contains(edge.getEdgeType())) {
                continue;
            }

            Object from = vertexMap.get(edge.getFromNodeId());
            Object to = vertexMap.get(edge.getToNodeId());

            String style = "";
            if (edge.onLoad()) {
                style = edgeBlack;
            } else {
                style = edgeGray;
            }

            String value = "";
            if (edge.getEdgeType().equals(EdgeType.DOM_CHANGE)) {
                value = edge.getComment().get("operation");
            }

            graph.insertEdge(parent, edge.getId(), value, from, to, style);
        }
    }

    private static Map<String, Object> insertVertex(Object parent, mxGraph graph, List<Node> nodeList) {
        Map<String, Object> vertexMap = new HashMap<>();
        for (Node node : nodeList) {
            String value = "";
            String style = "";
            switch (node.getNodeType()) {
                case NodeType.HTML:
                    HTMLNode htmlNode = (HTMLNode) node;
                    value = htmlNode.getTagName();
                    if (htmlNode.isShadow()) {
                        style = shadowHtmlStyle;
                    } else {
                        style = htmlNodeStyle;
                    }
                    break;
                case NodeType.CSS:
                    value = "CSS";
                    style = cssNodeStyle;
                    break;
                case NodeType.CSS_RULE:
                    value = ((CSSRuleNode) node).getRuleType();
                    style = cssRuleStyle;
                    break;
                case NodeType.SCRIPT:
                    value = "Script";
                    style = scriptNodeStyle;
                    break;
                case NodeType.NETWORK:
                    NetworkNode networkNode = (NetworkNode) node;
//                    value = networkNode.getUrl();
                    value = "Network";
                    if (networkNode.isPotential()) {
                        style = potentialNetworkStyle;
                    } else {
                        style = networkStyle;
                    }
                    break;
                case NodeType.IFRAME:
                    value = "Iframe";
                    style = iframeStyle;
                    break;
                default:
                    value = "unknown";
                    style = unknownNodeStyle;
            }

            Object vertex = graph.insertVertex(parent, node.getId(), value,
                    0, 0, nodeWidth, nodeHeight, style);
            vertexMap.put(node.getId(), vertex);
        }
        return vertexMap;
    }

}
