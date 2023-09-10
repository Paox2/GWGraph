import crawler.NodeService;
import crawler.common.ExtractionOptions;
import crawler.exception.NodeExtractionException;
import crawler.util.Writer;
import org.junit.Assert;
import org.junit.Test;

public class NetworkNodeExtractionTest {

    @Test
    public void testNetworkNodeExtraction() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(1);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeNetworkNodeToFile("src/test/resources/wiki/NetworkRequestExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testNetworkNodeExtraction2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeNetworkNodeToFile("src/test/resources/polymer/NetworkRequestExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testNetworkNodeExtraction3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeNetworkNodeToFile("src/test/resources/w3school/NetworkRequestExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testPotentialNetworkRequestGeneration() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(1);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeNetworkNodeToFile("src/test/resources/wiki/PotentialNetworkRequest.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testPotentialNetworkRequestGeneration2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeNetworkNodeToFile("src/test/resources/polymer/PotentialNetworkRequest.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testPotentialNetworkRequestGeneration3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeNetworkNodeToFile("src/test/resources/w3school/PotentialNetworkRequest.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

}
