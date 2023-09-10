import crawler.NodeService;
import crawler.common.ExtractionOptions;
import crawler.entity.HTMLElement;
import crawler.exception.NodeExtractionException;
import crawler.manager.HTMLManager;
import crawler.util.Logger;
import crawler.util.URLResolver;
import crawler.util.Writer;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class NodeExtractionTest {

    @Test
    public void testAll() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allAdd();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();


            Writer.writeAllToFile("src/test/resources/wiki/all.txt", nodeService.getNodeManager(), false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testAll2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allAdd();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();


            Writer.writeAllToFile("src/test/resources/polymer/all.txt", nodeService.getNodeManager(), false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testAll3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allAdd();
            options.removeShadowDOMExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();


            Writer.writeAllToFile("src/test/resources/w3school/all.txt", nodeService.getNodeManager(), false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests that the updates made to the html element by the node changes
     * returned by the script code are consistent with the elements of the html page
     */
    @Test
    public void testHTMLConsistAfterScriptRunning() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allAdd();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            int size = nodeService.getNodeManager().getHtmlManager().getAllElement().size();
            JavascriptExecutor executor = (JavascriptExecutor) (nodeService.getDriver());
            int expect = ((Long) executor.executeScript("function countElements(node) {\n" +
                    "    let count = 0;\n" +
                    "\n" +
                    "    count += 1;\n" +
                    "\n" +
                    "    Array.from(node.children).forEach(child => {\n" +
                            "        count += countElements(child);\n" +
                    "    });\n" +
                    "\n" +
                    "    if (node.shadowRoot) {\n" +
                    "        count += countElements(node.shadowRoot);\n" +
                    "    }\n" +
                    "\n" +
                    "    return count;\n" +
                    "}\n" +
                    "return countElements(document.documentElement);")).intValue();
            Long number = (Long) executor.executeScript("return window.numberOfElement;");

            Assert.assertEquals(expect, size);
            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testHTMLPositionCorrection() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allAdd();
            nodeService.setOptions(options);
            nodeService.nodeExtraction();

            JavascriptExecutor js = nodeService.getJs();
            HTMLManager htmlManager = nodeService.getNodeManager().getHtmlManager();
            Map<String, HTMLElement> map = htmlManager.buildPathMap();

            List<String> divPath = (List<String>) js.executeScript("return window.findHTMLNodeListByCSSSelector('div');");
            for (String path : divPath) {
                HTMLElement element = map.get(path);
                if (element == null) {
                    Logger.getInstance().error("Path: " + path + ". DIV.");
                    Assert.fail();
                }

                Assert.assertEquals("div", element.getTagName());
            }

            List<String> stylePath = (List<String>) js.executeScript("return window.findHTMLNodeListByCSSSelector('style');");
            for (String path : stylePath) {
                HTMLElement element = map.get(path);
                if (element == null) {
                    Logger.getInstance().error("Path: " + path + ". STYLE.");
                    Assert.fail();
                }

                Assert.assertEquals("style", element.getTagName());
            }

            List<String> aPath = (List<String>) js.executeScript("return window.findHTMLNodeListByCSSSelector('a');");
            for (String path : aPath) {
                HTMLElement element = map.get(path);
                if (element == null) {
                    Logger.getInstance().error("Path: " + path + ". A.");
                    Assert.fail();
                }

                Assert.assertEquals("a", element.getTagName());
            }

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testURLResolver() {
        String baseUrl = commonLink.url2;
        String uri = "//www.google-analytics.com/analytics.js";
        try {
            String result = URLResolver.resolve(baseUrl, uri);
            System.out.println(result);
        } catch (URISyntaxException e) {
            Assert.fail();
        }
    }
}
