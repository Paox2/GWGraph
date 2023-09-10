import crawler.NodeService;
import crawler.common.ExtractionOptions;
import crawler.entity.HTMLElement;
import crawler.exception.NodeExtractionException;
import crawler.util.Writer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HTMLExtractionTest {

    @Test
    public void testHTMLNodeExtraction() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            Writer.writeHTMLElementToFile("src/test/resources/wiki/HTMLExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testHTMLNodeExtraction2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            Writer.writeHTMLElementToFile("src/test/resources/polymer/HTMLExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testHTMLNodeExtraction3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            Writer.writeHTMLElementToFile("src/test/resources/w3school/HTMLExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testHTMLNodeExtractionWithShadowDOM() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.allowShadowDOMExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            Writer.writeHTMLElementToFile("src/test/resources/wiki/ShadowDOMExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testHTMLNodeExtractionWithShadowDOM2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.allowShadowDOMExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            Writer.writeHTMLElementToFile("src/test/resources/polymer/ShadowDOMExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testHTMLNodeExtractionWithShadowDOM3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.allowShadowDOMExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            Writer.writeHTMLElementToFile("src/test/resources/w3school/ShadowDOMExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCssTagList() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.allowShadowDOMExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            List<HTMLElement> elementList = nodeService.getNodeManager().getHtmlManager().getCSS();
            for (HTMLElement element : elementList) {
                Assert.assertEquals("style", element.getTagName());
            }

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testScriptTagList() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.allowShadowDOMExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            List<HTMLElement> elementList = nodeService.getNodeManager().getHtmlManager().getScript();
            for (HTMLElement element : elementList) {
                Assert.assertEquals("script", element.getTagName());
            }

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testFindElementByClassList() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.allowShadowDOMExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            List<String> classNames = new ArrayList<>();
            classNames.add("level-1");
            List<HTMLElement> elementList = nodeService.getNodeManager().getHtmlManager().findHTMLElementByClass(classNames);
            for (HTMLElement element : elementList) {
                Assert.assertEquals("level-1", element.getClassNames());
            }

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testFindElementByTagList() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.allowShadowDOMExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            List<String> tags = new ArrayList<>();
            tags.add("div");
            tags.add("script");

            List<HTMLElement> elementList = nodeService.getNodeManager().getHtmlManager().findHTMLElementByTagName(tags);
            for (HTMLElement element : elementList) {
                Assert.assertTrue("Tag name should be in the collection.", tags.contains(element.getTagName()));
            }

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }
}
