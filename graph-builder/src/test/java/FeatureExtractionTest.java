import graph.builder.FeatureExtraction;
import graph.builder.View;
import graph.builder.common.EdgeType;
import graph.builder.entity.node.HTMLNode;
import graph.builder.entity.node.Node;
import graph.builder.vo.EdgeFilter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureExtractionTest {

    @Test
    public void testBetweennessCentrality() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);

        Map<String, Double> result = FeatureExtraction.betweennessCentrality(view, new EdgeFilter());

        Assert.assertEquals(0.0, result.get("1"), 0.001);
        Assert.assertEquals(3.0, result.get("2"), 0.001);
        Assert.assertEquals(1.0, result.get("3"), 0.001);
        Assert.assertEquals(1.0, result.get("4"), 0.001);
        Assert.assertEquals(0.0, result.get("5"), 0.001);


    }

    @Test
    public void testAscendants() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
            nodes.add(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);


        Assert.assertEquals(4, FeatureExtraction.ascendants(view, nodes.get(4), new EdgeFilter(), -1));
        Assert.assertEquals(3, FeatureExtraction.ascendants(view, nodes.get(4), new EdgeFilter(), 2));
        Assert.assertEquals(2, FeatureExtraction.ascendants(view, nodes.get(3), new EdgeFilter(), -1));
        Assert.assertEquals(2, FeatureExtraction.ascendants(view, nodes.get(2), new EdgeFilter(), -1));
        Assert.assertEquals(1, FeatureExtraction.ascendants(view, nodes.get(1), new EdgeFilter(), -1));
        Assert.assertEquals(0, FeatureExtraction.ascendants(view, nodes.get(0), new EdgeFilter(), -1));


        view.addEdge("5", "", "1", "", EdgeType.PARENT_CHILD_RELATION);

        Assert.assertEquals(5, FeatureExtraction.ascendants(view, nodes.get(4), new EdgeFilter(), -1));
        Assert.assertEquals(5, FeatureExtraction.ascendants(view, nodes.get(0), new EdgeFilter(), -1));
    }

    @Test
    public void testDescendants() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
            nodes.add(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);


        Assert.assertEquals(4, FeatureExtraction.descendants(view, nodes.get(0), new EdgeFilter(), -1));
        Assert.assertEquals(3, FeatureExtraction.descendants(view, nodes.get(0), new EdgeFilter(), 2));
        Assert.assertEquals(3, FeatureExtraction.descendants(view, nodes.get(1), new EdgeFilter(), -1));
        Assert.assertEquals(1, FeatureExtraction.descendants(view, nodes.get(2), new EdgeFilter(), -1));
        Assert.assertEquals(1, FeatureExtraction.descendants(view, nodes.get(3), new EdgeFilter(), -1));
        Assert.assertEquals(0, FeatureExtraction.descendants(view, nodes.get(4), new EdgeFilter(), -1));


        view.addEdge("5", "", "1", "", EdgeType.PARENT_CHILD_RELATION);

        Assert.assertEquals(5, FeatureExtraction.descendants(view, nodes.get(4), new EdgeFilter(), -1));
        Assert.assertEquals(5, FeatureExtraction.descendants(view, nodes.get(0), new EdgeFilter(), -1));
    }

    @Test
    public void testDegree() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
            nodes.add(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);


        Assert.assertEquals(0, FeatureExtraction.inDegree(view, nodes.get(0), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.inDegree(view, nodes.get(1), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.inDegree(view, nodes.get(2), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.inDegree(view, nodes.get(3), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.inDegree(view, nodes.get(4), new EdgeFilter()));

        Assert.assertEquals(1, FeatureExtraction.outDegree(view, nodes.get(0), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.outDegree(view, nodes.get(1), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.outDegree(view, nodes.get(2), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.outDegree(view, nodes.get(3), new EdgeFilter()));
        Assert.assertEquals(0, FeatureExtraction.outDegree(view, nodes.get(4), new EdgeFilter()));


        view.addEdge("5", "", "1", "", EdgeType.PARENT_CHILD_RELATION);


        Assert.assertEquals(1, FeatureExtraction.inDegree(view, nodes.get(0), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.inDegree(view, nodes.get(1), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.inDegree(view, nodes.get(2), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.inDegree(view, nodes.get(3), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.inDegree(view, nodes.get(4), new EdgeFilter()));

        Assert.assertEquals(1, FeatureExtraction.outDegree(view, nodes.get(0), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.outDegree(view, nodes.get(1), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.outDegree(view, nodes.get(2), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.outDegree(view, nodes.get(3), new EdgeFilter()));
        Assert.assertEquals(1, FeatureExtraction.outDegree(view, nodes.get(4), new EdgeFilter()));

    }

    @Test
    public void testNeighbour() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
            nodes.add(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);


        Assert.assertEquals(1, FeatureExtraction.neighbourCount(view, nodes.get(0), new EdgeFilter()));
        Assert.assertEquals(3, FeatureExtraction.neighbourCount(view, nodes.get(1), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.neighbourCount(view, nodes.get(2), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.neighbourCount(view, nodes.get(3), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.neighbourCount(view, nodes.get(4), new EdgeFilter()));

        List<Node> node0Neighbour = FeatureExtraction.neighbor(view, nodes.get(0), new EdgeFilter());
        assert node0Neighbour != null;
        Assert.assertEquals(1, node0Neighbour.size());
        Assert.assertTrue(node0Neighbour.contains(nodes.get(1)));
        List<Node> node1Neighbour = FeatureExtraction.neighbor(view, nodes.get(1), new EdgeFilter());
        assert node1Neighbour != null;
        Assert.assertEquals(3, node1Neighbour.size());
        Assert.assertTrue(node1Neighbour.contains(nodes.get(0)));
        Assert.assertTrue(node1Neighbour.contains(nodes.get(2)));
        Assert.assertTrue(node1Neighbour.contains(nodes.get(3)));
        List<Node> node2Neighbour = FeatureExtraction.neighbor(view, nodes.get(2), new EdgeFilter());
        assert node2Neighbour != null;
        Assert.assertEquals(2, node2Neighbour.size());
        Assert.assertTrue(node2Neighbour.contains(nodes.get(1)));
        Assert.assertTrue(node2Neighbour.contains(nodes.get(4)));
        List<Node> node3Neighbour = FeatureExtraction.neighbor(view, nodes.get(3), new EdgeFilter());
        assert node3Neighbour != null;
        Assert.assertEquals(2, node3Neighbour.size());
        Assert.assertTrue(node3Neighbour.contains(nodes.get(1)));
        Assert.assertTrue(node3Neighbour.contains(nodes.get(4)));
        List<Node> node4Neighbour = FeatureExtraction.neighbor(view, nodes.get(4), new EdgeFilter());
        assert node4Neighbour != null;
        Assert.assertEquals(2, node4Neighbour.size());
        Assert.assertTrue(node4Neighbour.contains(nodes.get(2)));
        Assert.assertTrue(node4Neighbour.contains(nodes.get(3)));


        view.addEdge("5", "", "1", "", EdgeType.PARENT_CHILD_RELATION);


        Assert.assertEquals(2, FeatureExtraction.neighbourCount(view, nodes.get(0), new EdgeFilter()));
        Assert.assertEquals(3, FeatureExtraction.neighbourCount(view, nodes.get(1), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.neighbourCount(view, nodes.get(2), new EdgeFilter()));
        Assert.assertEquals(2, FeatureExtraction.neighbourCount(view, nodes.get(3), new EdgeFilter()));
        Assert.assertEquals(3, FeatureExtraction.neighbourCount(view, nodes.get(4), new EdgeFilter()));


        node0Neighbour = FeatureExtraction.neighbor(view, nodes.get(0), new EdgeFilter());
        assert node0Neighbour != null;
        Assert.assertEquals(2, node0Neighbour.size());
        Assert.assertTrue(node0Neighbour.contains(nodes.get(1)));
        Assert.assertTrue(node0Neighbour.contains(nodes.get(4)));
        node1Neighbour = FeatureExtraction.neighbor(view, nodes.get(1), new EdgeFilter());
        assert node1Neighbour != null;
        Assert.assertEquals(3, node1Neighbour.size());
        Assert.assertTrue(node1Neighbour.contains(nodes.get(0)));
        Assert.assertTrue(node1Neighbour.contains(nodes.get(2)));
        Assert.assertTrue(node1Neighbour.contains(nodes.get(3)));
        node2Neighbour = FeatureExtraction.neighbor(view, nodes.get(2), new EdgeFilter());
        assert node2Neighbour != null;
        Assert.assertEquals(2, node2Neighbour.size());
        Assert.assertTrue(node2Neighbour.contains(nodes.get(1)));
        Assert.assertTrue(node2Neighbour.contains(nodes.get(4)));
        node3Neighbour = FeatureExtraction.neighbor(view, nodes.get(3), new EdgeFilter());
        assert node3Neighbour != null;
        Assert.assertEquals(2, node3Neighbour.size());
        Assert.assertTrue(node3Neighbour.contains(nodes.get(1)));
        Assert.assertTrue(node3Neighbour.contains(nodes.get(4)));
        Assert.assertTrue(node3Neighbour.contains(nodes.get(4)));
        node4Neighbour = FeatureExtraction.neighbor(view, nodes.get(4), new EdgeFilter());
        assert node4Neighbour != null;
        Assert.assertEquals(3, node4Neighbour.size());
        Assert.assertTrue(node4Neighbour.contains(nodes.get(2)));
        Assert.assertTrue(node4Neighbour.contains(nodes.get(3)));
        Assert.assertTrue(node4Neighbour.contains(nodes.get(0)));
    }

    @Test
    public void testAscendantNodes() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
            nodes.add(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);


        List<Node> node0Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(0), new EdgeFilter());
        assert node0Neighbour != null;
        Assert.assertEquals(0, node0Neighbour.size());
        List<Node> node1Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(1), new EdgeFilter());
        assert node1Neighbour != null;
        Assert.assertEquals(1, node1Neighbour.size());
        Assert.assertTrue(node1Neighbour.contains(nodes.get(0)));
        List<Node> node2Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(2), new EdgeFilter());
        assert node2Neighbour != null;
        Assert.assertEquals(1, node2Neighbour.size());
        Assert.assertTrue(node2Neighbour.contains(nodes.get(1)));
        List<Node> node3Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(3), new EdgeFilter());
        assert node3Neighbour != null;
        Assert.assertEquals(1, node3Neighbour.size());
        Assert.assertTrue(node3Neighbour.contains(nodes.get(1)));
        List<Node> node4Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(4), new EdgeFilter());
        assert node4Neighbour != null;
        Assert.assertEquals(2, node4Neighbour.size());
        Assert.assertTrue(node4Neighbour.contains(nodes.get(2)));
        Assert.assertTrue(node4Neighbour.contains(nodes.get(3)));


        view.addEdge("5", "", "1", "", EdgeType.PARENT_CHILD_RELATION);


        node0Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(0), new EdgeFilter());
        assert node0Neighbour != null;
        Assert.assertEquals(1, node0Neighbour.size());
        Assert.assertTrue(node0Neighbour.contains(nodes.get(4)));
        node1Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(1), new EdgeFilter());
        assert node1Neighbour != null;
        Assert.assertEquals(1, node1Neighbour.size());
        Assert.assertTrue(node1Neighbour.contains(nodes.get(0)));
        node2Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(2), new EdgeFilter());
        assert node2Neighbour != null;
        Assert.assertEquals(1, node2Neighbour.size());
        Assert.assertTrue(node2Neighbour.contains(nodes.get(1)));
        node3Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(3), new EdgeFilter());
        assert node3Neighbour != null;
        Assert.assertEquals(1, node3Neighbour.size());
        Assert.assertTrue(node3Neighbour.contains(nodes.get(1)));
        node4Neighbour = FeatureExtraction.ascendantNodes(view, nodes.get(4), new EdgeFilter());
        assert node4Neighbour != null;
        Assert.assertEquals(2, node4Neighbour.size());
        Assert.assertTrue(node4Neighbour.contains(nodes.get(2)));
        Assert.assertTrue(node4Neighbour.contains(nodes.get(3)));
    }

    @Test
    public void testDescendantNodes() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
            nodes.add(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);


        List<Node> node0Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(0), new EdgeFilter());
        assert node0Neighbour != null;
        Assert.assertEquals(1, node0Neighbour.size());
        Assert.assertTrue(node0Neighbour.contains(nodes.get(1)));
        List<Node> node1Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(1), new EdgeFilter());
        assert node1Neighbour != null;
        Assert.assertEquals(2, node1Neighbour.size());
        Assert.assertTrue(node1Neighbour.contains(nodes.get(2)));
        Assert.assertTrue(node1Neighbour.contains(nodes.get(3)));
        List<Node> node2Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(2), new EdgeFilter());
        assert node2Neighbour != null;
        Assert.assertEquals(1, node2Neighbour.size());
        Assert.assertTrue(node2Neighbour.contains(nodes.get(4)));
        List<Node> node3Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(3), new EdgeFilter());
        assert node3Neighbour != null;
        Assert.assertEquals(1, node3Neighbour.size());
        Assert.assertTrue(node3Neighbour.contains(nodes.get(4)));
        List<Node> node4Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(4), new EdgeFilter());
        assert node4Neighbour != null;
        Assert.assertEquals(0, node4Neighbour.size());


        view.addEdge("5", "", "1", "", EdgeType.PARENT_CHILD_RELATION);


        node0Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(0), new EdgeFilter());
        assert node0Neighbour != null;
        Assert.assertEquals(1, node0Neighbour.size());
        Assert.assertTrue(node0Neighbour.contains(nodes.get(1)));
        node1Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(1), new EdgeFilter());
        assert node1Neighbour != null;
        Assert.assertEquals(2, node1Neighbour.size());
        Assert.assertTrue(node1Neighbour.contains(nodes.get(2)));
        Assert.assertTrue(node1Neighbour.contains(nodes.get(3)));
        node2Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(2), new EdgeFilter());
        assert node2Neighbour != null;
        Assert.assertEquals(1, node2Neighbour.size());
        Assert.assertTrue(node2Neighbour.contains(nodes.get(4)));
        node3Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(3), new EdgeFilter());
        assert node3Neighbour != null;
        Assert.assertEquals(1, node3Neighbour.size());
        Assert.assertTrue(node3Neighbour.contains(nodes.get(4)));
        node4Neighbour = FeatureExtraction.descendantNodes(view, nodes.get(4), new EdgeFilter());
        assert node4Neighbour != null;
        Assert.assertEquals(1, node4Neighbour.size());
        Assert.assertTrue(node4Neighbour.contains(nodes.get(0)));
    }

    @Test
    public void testSP() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
            nodes.add(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);


        Map<String, Integer> node0SP = FeatureExtraction.shortestPathFrom(view, nodes.get(0), new EdgeFilter(), 0);
        Assert.assertNotNull(node0SP);
        Assert.assertEquals(5, node0SP.size());
        Assert.assertEquals(Integer.valueOf(0), node0SP.get(nodes.get(0).getId()));
        Assert.assertEquals(Integer.valueOf(1), node0SP.get(nodes.get(1).getId()));
        Assert.assertEquals(Integer.valueOf(2), node0SP.get(nodes.get(2).getId()));
        Assert.assertEquals(Integer.valueOf(2), node0SP.get(nodes.get(3).getId()));
        Assert.assertEquals(Integer.valueOf(3), node0SP.get(nodes.get(4).getId()));
        Map<String, Integer> node1SP = FeatureExtraction.shortestPathFrom(view, nodes.get(1), new EdgeFilter(), 0);
        Assert.assertNotNull(node1SP);
        Assert.assertEquals(4, node1SP.size());
        Assert.assertEquals(Integer.valueOf(0), node1SP.get(nodes.get(1).getId()));
        Assert.assertEquals(Integer.valueOf(1), node1SP.get(nodes.get(2).getId()));
        Assert.assertEquals(Integer.valueOf(1), node1SP.get(nodes.get(3).getId()));
        Assert.assertEquals(Integer.valueOf(2), node1SP.get(nodes.get(4).getId()));

        view.addEdge("5", "", "1", "", EdgeType.PARENT_CHILD_RELATION);

        node0SP = FeatureExtraction.shortestPathFrom(view, nodes.get(0), new EdgeFilter(), 0);
        Assert.assertNotNull(node0SP);
        Assert.assertEquals(5, node0SP.size());
        Assert.assertEquals(Integer.valueOf(0), node0SP.get(nodes.get(0).getId()));
        Assert.assertEquals(Integer.valueOf(1), node0SP.get(nodes.get(1).getId()));
        Assert.assertEquals(Integer.valueOf(2), node0SP.get(nodes.get(2).getId()));
        Assert.assertEquals(Integer.valueOf(2), node0SP.get(nodes.get(3).getId()));
        Assert.assertEquals(Integer.valueOf(3), node0SP.get(nodes.get(4).getId()));
        node1SP = FeatureExtraction.shortestPathFrom(view, nodes.get(1), new EdgeFilter(), 0);
        Assert.assertNotNull(node1SP);
        Assert.assertEquals(5, node1SP.size());
        Assert.assertEquals(Integer.valueOf(3), node1SP.get(nodes.get(0).getId()));
        Assert.assertEquals(Integer.valueOf(0), node1SP.get(nodes.get(1).getId()));
        Assert.assertEquals(Integer.valueOf(1), node1SP.get(nodes.get(2).getId()));
        Assert.assertEquals(Integer.valueOf(1), node1SP.get(nodes.get(3).getId()));
        Assert.assertEquals(Integer.valueOf(2), node1SP.get(nodes.get(4).getId()));
    }

    @Test
    public void testEccentricity() {
        View view = new View();

        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HTMLNode node = new HTMLNode();
            node.setId(String.valueOf(i));
            view.addHTMLNode(node);
            nodes.add(node);
        }

        view.addEdge("1", "", "2", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "3", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("2", "", "4", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("3", "", "5", "", EdgeType.PARENT_CHILD_RELATION);
        view.addEdge("4", "", "5", "", EdgeType.PARENT_CHILD_RELATION);


        double node0e = FeatureExtraction.eccentricity(view, nodes.get(0), new EdgeFilter());
        Assert.assertEquals(3.0, node0e, 0.00001);
        double node1e = FeatureExtraction.eccentricity(view, nodes.get(1), new EdgeFilter());
        Assert.assertEquals(2.0, node1e, 0.00001);

        view.addEdge("5", "", "1", "", EdgeType.PARENT_CHILD_RELATION);

        node0e = FeatureExtraction.eccentricity(view, nodes.get(0), new EdgeFilter());
        Assert.assertEquals(3.0, node0e, 0.00001);
        node1e = FeatureExtraction.eccentricity(view, nodes.get(1), new EdgeFilter());
        Assert.assertEquals(3.0, node1e, 0.00001);
    }

}
