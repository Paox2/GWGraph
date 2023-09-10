import crawler.NodeService;
import crawler.Constant.CSSType;
import crawler.common.ExtractionOptions;
import crawler.entity.CSSCodeBlock;
import crawler.entity.CSSRule;
import crawler.exception.NodeExtractionException;
import crawler.manager.CSSManager;
import crawler.util.Writer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CSSNodeExtractionTest {
    @Test
    public void testCSSNodeExtraction() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/wiki/CSSExtraction.txt", nodeService.getNodeManager(), false);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void testCSSNodeExtraction2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/polymer/CSSExtraction.txt", nodeService.getNodeManager(), false);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCSSNodeExtraction3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/w3school/CSSExtraction.txt", nodeService.getNodeManager(), false);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCSSNodeExtractionWithDels() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/wiki/CSSExtractionWithDels.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCSSNodeExtractionWithDels2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);


            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/polymer/CSSExtractionWithDels.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCSSNodeExtractionWithDels3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);


            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/w3school/CSSExtractionWithDels.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCSSContentCorrection() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());
            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeCSSNodeToFile("src/test/resources/wiki/CSSContentCorrection.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCSSContentCorrection2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());
            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeCSSNodeToFile("src/test/resources/polymer/CSSContentCorrection.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCSSContentCorrection3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());
            Assert.assertNotNull(nodeService.getNodeManager().getNetworkRequestManager());

            Writer.writeCSSNodeToFile("src/test/resources/w3school/CSSContentCorrection.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCssHTMLConnectCorrection() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addCssExtraction();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());
            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/wiki/CSSHTMLConnect.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCssHTMLConnectCorrection2() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url2);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addCssExtraction();
            options.addNetworkRequestExtraction();


            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());
            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/polymer/CSSHTMLConnect.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCssHTMLConnectCorrection3() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addHtmlExtraction();
            options.addCssExtraction();
            options.addNetworkRequestExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getHtmlManager());
            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            Writer.writeCSSNodeToFile("src/test/resources/w3school/CSSHTMLConnect.txt", nodeService.getNodeManager(), true);

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetSyleInsideBlock() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            CSSManager manager = nodeService.getNodeManager().getCssManager();
            List<CSSCodeBlock> blockList = manager.getAllCodeBlocks();
            for (CSSCodeBlock block : blockList) {
                List<CSSRule> styles = manager.getRulesInsideBlock(block);
                for (CSSRule style : styles) {
                    Assert.assertEquals(block.getId(), style.getBelongTo());
                }

                Assert.assertEquals(styles.size(), block.getInsideCSSRules().size());
            }

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetBlocksByType() {
        try {
            NodeService nodeService = new NodeService();

            nodeService.setWaitTime(10);
            nodeService.setURL(commonLink.url3);

            ExtractionOptions options = new ExtractionOptions();
            options.allRemove();
            options.addCssExtraction();

            nodeService.setOptions(options);

            nodeService.nodeExtraction();

            Assert.assertNotNull(nodeService.getNodeManager().getCssManager());

            CSSManager manager = nodeService.getNodeManager().getCssManager();
            List<CSSCodeBlock> blockList = manager.getCSSBlocksByType(CSSType.INLINE);
            for (CSSCodeBlock block : blockList) {
                Assert.assertEquals(CSSType.INLINE, block.getType());
            }

            blockList = manager.getCSSBlocksByType(CSSType.INTERNAL);
            for (CSSCodeBlock block : blockList) {
                Assert.assertEquals(CSSType.INTERNAL, block.getType());
            }

            blockList = manager.getCSSBlocksByType(CSSType.EXTERNAL);
            for (CSSCodeBlock block : blockList) {
                Assert.assertEquals(CSSType.EXTERNAL, block.getType());
            }

            nodeService.close();
        } catch (NodeExtractionException e) {
            System.out.println(e.getMessage());
        }
    }

}
