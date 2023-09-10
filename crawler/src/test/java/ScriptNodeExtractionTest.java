import crawler.NodeService;
import crawler.common.ExtractionOptions;
import crawler.exception.NodeExtractionException;
import crawler.util.Writer;
import org.junit.Assert;
import org.junit.Test;

public class ScriptNodeExtractionTest {

    @Test
    public void testScriptNodeExtraction() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(0);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addScriptExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getScriptManager());

            Writer.writeScriptNodeToFile("src/test/resources/wiki/ScriptExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testScriptNodeExtraction2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(0);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addScriptExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getScriptManager());

            Writer.writeScriptNodeToFile("src/test/resources/polymer/ScriptExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testScriptNodeExtraction3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(0);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addScriptExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getScriptManager());

            Writer.writeScriptNodeToFile("src/test/resources/w3school/ScriptExtraction.txt", nodeService.getNodeManager());

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testScriptContentCorrection() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(1000);
            nodeService.setURL(commonLink.url);


            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addScriptExtraction();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getScriptManager());
            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeAllToFile("src/test/resources/wiki/ScriptContentCorrection.txt", nodeService.getNodeManager(), false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testScriptContentCorrection2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(1000);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addScriptExtraction();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getScriptManager());
            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeAllToFile("src/test/resources/polymer/ScriptContentCorrection.txt", nodeService.getNodeManager(),  false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testScriptContentCorrection3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(1000);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addScriptExtraction();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getScriptManager());
            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeAllToFile("src/test/resources/w3school/ScriptContentCorrection.txt", nodeService.getNodeManager(),  false, options);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

}
