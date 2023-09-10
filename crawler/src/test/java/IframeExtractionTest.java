import crawler.NodeService;
import crawler.common.ExtractionOptions;
import crawler.exception.NodeExtractionException;
import crawler.util.Writer;
import org.junit.Assert;
import org.junit.Test;

public class IframeExtractionTest {


    @Test
    public void testIframeExtraction() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(0);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addIframeExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            Writer.writeAllToFile("src/test/resources/wiki/Iframe.txt", nodeService.getNodeManager(), false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testIframeExtraction2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(1000);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addIframeExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());
            Assert.assertNotNull(nodeService.getNodeManager().getIFrameManagers());

            Writer.writeAllToFile("src/test/resources/polymer/Iframe.txt", nodeService.getNodeManager(), false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testIframeExtraction3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(0);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addIframeExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());

            Writer.writeAllToFile("src/test/resources/w3school/Iframe.txt", nodeService.getNodeManager(), false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }
}
