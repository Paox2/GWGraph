import graph.builder.Graph;
import graph.builder.Render;
import graph.builder.View;
import graph.builder.common.NodeOptions;
import graph.builder.entity.node.HTMLNode;
import graph.builder.exception.GraphBuilderException;
import graph.builder.util.Logger;
import graph.builder.vo.EdgeFilter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GraphTest {
    @Test
    public void drawGraph() {
        NodeOptions options = new NodeOptions();
        options.allAdd();

        String url = commonLink.simpleWeb;
        long waitTime = 10;

        Graph graph = new Graph();
        try {
            graph.useCrawler(url, waitTime, options);

            List<View> viewList = graph.getViews();
            int id = 1;
            for (View view : viewList) {
                String filePath = "src/test/resources/simpleWeb/pureHTML-view" + id + ".png";
                Render.renderViewToImage(view, "PNG", filePath, new EdgeFilter());
                id += 1;
            }

        } catch (GraphBuilderException e) {
            Logger.getInstance().error(e.getMessage());
        }
    }

    @Test
    public void testGraphBuilding() {
        NodeOptions options = new NodeOptions();
        options.allAdd();

        String url = commonLink.simpleWeb;
        long waitTime = 10;

        Graph graph = new Graph();
        try {
            graph.useCrawler(url, waitTime, options);

            List<View> viewList = graph.getViews();
            View view = graph.getMainView();

            List<HTMLNode> iframeHtml = view.findHTMLNodeByTag("iframe");

            if (viewList.size() > 1) {
                Assert.assertTrue(iframeHtml.size() > 0);
            }

            for (HTMLNode iframe : iframeHtml) {
                Assert.assertNotNull(graph.findViewByRelatedNodeId(iframe.getId()));
            }
        } catch (GraphBuilderException e) {
            Logger.getInstance().error(e.getMessage());
        }
    }

    @Test
    public void testGraphBuilding2() {
        NodeOptions options = new NodeOptions();
        options.allAdd();

        String currentDirectory = System.getProperty("user.dir");

        String url = "file://" + currentDirectory + commonLink.exampleLink;
//        String url = "view-source:localhost:63342/generalGraph/graphBuilder/testPage.html";
        long waitTime = 10;

        Graph graph = new Graph();
        try {
            graph.useCrawler(url, waitTime, options);

            List<View> viewList = graph.getViews();
            int id = 1;
            for (View view : viewList) {
                String filePath = "src/test/resources/examplePage/example-view" + id + ".png";
                EdgeFilter edgeFilter = new EdgeFilter();
                edgeFilter.setApplyTo(true);
                Render.renderViewToImage(view, "PNG", filePath, edgeFilter);
                id += 1;
            }
        } catch (GraphBuilderException e) {
            Logger.getInstance().error(e.getMessage());
        }
    }
}
