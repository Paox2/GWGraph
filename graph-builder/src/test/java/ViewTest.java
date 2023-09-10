import graph.builder.Graph;
import graph.builder.View;
import graph.builder.common.EdgeType;
import graph.builder.common.NodeType;
import graph.builder.entity.edge.Edge;
import graph.builder.entity.node.*;
import graph.builder.util.Random;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ViewTest {

    @Test
    public void testNormalOp() {
        Graph graph = new Graph();
        View view = graph.createView();
        HTMLNode htmlNode = new HTMLNode();
        htmlNode.setTagName("h");
        String htmlId = view.addHTMLNode(htmlNode);
        String cssId = view.addCSSNode(new CSSNode());
        String scriptId = view.addScriptNode(new ScriptNode());
        String cssRuleId = view.addCSSRuleNode(new CSSRuleNode());
        NetworkNode networkNode = new NetworkNode();
        networkNode.setUrl("www.test.com");
        String networkId = view.addNetworkNode(networkNode);

        Assert.assertNotNull(view.getViewId());
        Assert.assertNotNull(view.findNodeById(htmlId));
        Assert.assertNotNull(view.findNodeById(cssId));
        Assert.assertNotNull(view.findNodeById(cssRuleId));
        Assert.assertNotNull(view.findNodeById(scriptId));
        Assert.assertNotNull(view.findNodeById(networkId));
        Assert.assertNotNull(view.findHTMLNodeByTag("h"));
        Assert.assertNotNull(view.findNetworkNodeByURL(networkNode.getUrl()));

        Assert.assertEquals(1, view.getAllCSSNodes().size());
        Assert.assertEquals(1, view.getAllScriptNodes().size());
        Assert.assertEquals(1, view.getAllHTMLNodes().size());
        Assert.assertEquals(1, view.getAllNetworkNodes().size());
        Assert.assertEquals(1, view.getAllCSSRules().size());

        Edge edge = view.addEdge( htmlId, NodeType.HTML,networkId, NodeType.NETWORK, EdgeType.NETWORK_REQUEST);

        Assert.assertNotNull(view.findEdgeById(edge.getId()));
        Assert.assertEquals(1, htmlNode.getOutList().size());
        Assert.assertEquals(1, networkNode.getInList().size());


        Assert.assertEquals(1, view.findInEdgeForNode(networkNode).size());
        Assert.assertEquals(1, view.findOutEdgeForNode(htmlNode).size());
        Assert.assertEquals(edge, view.findInEdgeForNode(networkNode).get(0));
        Assert.assertEquals(edge, view.findOutEdgeForNode(htmlNode).get(0));
        Assert.assertEquals(0, view.findInEdgeForNode(networkNode, EdgeType.APPLY_TO).size());
        Assert.assertEquals(1, view.findInEdgeForNode(networkNode, EdgeType.NETWORK_REQUEST).size());

        view.deleteNetworkNodeByURL(networkNode.getUrl());
        Assert.assertEquals(0, view.getAllNetworkNodes().size());
        Assert.assertEquals(0, view.getAllEdge().size());

        Edge wrongEdge = new Edge();
        edge.setFrom(htmlId, NodeType.HTML);
        edge.setTo(Random.generateId(), NodeType.NETWORK);
        edge.setEdgeType(EdgeType.NETWORK_REQUEST);
        view.addEdgeUncheckExistence(edge);
        Assert.assertEquals(1, view.getAllEdge().size());
        List<Edge> deletedEdge = view.connectionEdge();
        Assert.assertEquals(1, deletedEdge.size());
        Assert.assertEquals(edge, deletedEdge.get(0));
        Assert.assertEquals(0, htmlNode.getOutList().size());
    }
}
