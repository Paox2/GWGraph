package crawler.util;

import crawler.Constant.CSSType;
import crawler.common.ExtractionOptions;
import crawler.entity.*;
import crawler.exception.NodeExtractionException;
import crawler.manager.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Writer {

    public static String writeAllToFile(String fileName, NodeExtractionManager extractionManager, boolean includeDeclaration, ExtractionOptions options) throws NodeExtractionException {
        StringBuilder nodeString = new StringBuilder("");

        nodeString.append("------------------------- Base URL : ").append(extractionManager.getCurrentUrl()).append("-------------------------\n\n\n");

        if (options.htmlExtraction()) {
            nodeString.append("HTML Node: \n\n");
            nodeString.append(writeHTMLElementAsString(extractionManager.getHtmlManager()));
        }

        if (options.cssExtraction()) {
            nodeString.append("\n\n\n\n\nCSS Node: \n\n");
            nodeString.append(writeCSSAsString(extractionManager.getCssManager(), includeDeclaration));
        }

        if (options.networkRequestExtraction()) {
            nodeString.append("\n\n\n\n\nNetwork Node: \n\n");
            nodeString.append(writeNetworkNodeAsString(extractionManager.getNetworkRequestManager()));
        }

        if (options.scriptExtraction()) {
            nodeString.append("\n\n\n\n\nScript Node: \n\n");
            nodeString.append(writeScriptNodeAsString(extractionManager.getScriptManager()));
        }

        if (options.iframeExtraction() && options.htmlExtraction()) {
            nodeString.append("\n\n\n\n\nIframe Content: \n\n");
            nodeString.append(writeIframesAsString(extractionManager.getIFrameManagers(), options, includeDeclaration));
        }
        
        String finalString = nodeString.toString();
        writeStringToFile(fileName, finalString);

        return finalString;
    }

    public static String writeHTMLElementToFile(String fileName, NodeExtractionManager extractionManager) throws NodeExtractionException {
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append("------------------------- Base URL : ").append(extractionManager.getCurrentUrl()).append("-------------------------\n\n\n");
        HTMLManager htmlManager =  extractionManager.getHtmlManager();
        String result = writeHTMLElementAsString(htmlManager);
        result = baseUrl.toString() + result;
        writeStringToFile(fileName, result);
        return result;
    }

    public static String writeCSSNodeToFile(String fileName, NodeExtractionManager extractionManager, boolean includeDeclaration) throws NodeExtractionException {
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append("------------------------- Base URL : ").append(extractionManager.getCurrentUrl()).append("-------------------------\n\n\n");
        CSSManager cssNodeManager =  extractionManager.getCssManager();
        String nodeString = writeCSSAsString(cssNodeManager, includeDeclaration);
        nodeString = baseUrl.toString() + nodeString;
        writeStringToFile(fileName, nodeString);
        return nodeString;
    }

    public static String writeNetworkNodeToFile(String fileName, NodeExtractionManager nodeManager) throws NodeExtractionException {
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append("------------------------- Base URL : ").append(nodeManager.getCurrentUrl()).append("-------------------------\n\n\n");
        NetworkRequestManager networkNodeManager =  nodeManager.getNetworkRequestManager();
        String nodeString = writeNetworkNodeAsString(networkNodeManager);
        nodeString = baseUrl.toString() + nodeString;
        writeStringToFile(fileName, nodeString);
        return nodeString;
    }

    public static String writeScriptNodeToFile(String fileName, NodeExtractionManager nodeManager) throws NodeExtractionException {
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append("------------------------- Base URL : ").append(nodeManager.getCurrentUrl()).append("-------------------------\n\n\n");
        ScriptManager scriptNodeManager =  nodeManager.getScriptManager();
        String nodeString = writeScriptNodeAsString(scriptNodeManager);
        nodeString = baseUrl.toString() + nodeString;
        writeStringToFile(fileName, nodeString);
        return nodeString;
    }

    private static String writeHTMLElementAsString(HTMLManager htmlManager) {
        StringBuilder eleString = new StringBuilder("");
        for (HTMLElement element : htmlManager.getAllElement()) {
            eleString.append("Unique Id: ").append(element.getId()).append("\n");
            eleString.append("Id: ").append(element.getIdentifyID()).append("\n");
            eleString.append("Tag Name: ").append(element.getTagName()).append("\n");
            eleString.append("Class Name: ").append(element.getClassNames()).append("\n");
            eleString.append("Parent: ").append(element.getParent()).append("\n");
            eleString.append("Shadow Root: ").append(element.getShadowRoot()).append("\n");
            eleString.append("Shadow Host: ").append(element.getShadowHost()).append("\n");
            eleString.append("Active Outbound Request: ").append("\n");
            for (Pair<String, String> entry : element.getActiveOutboundRequest()) {
                eleString.append("  ").append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
            }
            eleString.append("Passive Outbound Request: ").append("\n");
            for (Pair<String, String> entry : element.getPassiveOutboundRequest()) {
                eleString.append("  ").append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
            }

            eleString.append("Attributes:").append("\n");
            for (Map.Entry<String, String> attribute : element.getAttributes().entrySet()) {
                eleString.append("  ").append(attribute.getKey()).append(": ").append(attribute.getValue()).append("\n");
            }
            eleString.append("\n\n");
        }
        return eleString.toString();
    }

    private static String writeCSSAsString(CSSManager cssManager, boolean includeDeclaration) {
        StringBuilder result = new StringBuilder("");

        result.append("\n\n\n---------------------Inline CSS: ------------------------\n\n\n");
        for (CSSCodeBlock block : cssManager.getCSSBlocksByType(CSSType.INLINE)) {
            result.append("Block Id: ").append(block.getId()).append("\n");
            result.append("Src: ").append(block.getSrc()).append("\n");
            result.append("Related html node: ").append(block.getRelatedHTMLId()).append("\n");
            result.append("Type: ").append(block.getType()).append("\n");

            result.append("Inside CSS Styles: ").append("\n");
            for (CSSRule rule : cssManager.getRulesInsideBlock(block)) {
                result.append("    Rule Id: ").append(rule.getId()).append("\n");
                result.append("    Rule Type: ").append(rule.getRuleType()).append("\n");
                result.append("    Selector:").append(rule.getSelector()).append("\n");
                result.append("    Belong to: ").append(rule.getBelongTo()).append("\n");

                result.append("    Apply to: ").append("\n");
                for (String applyTo : rule.getApplyTo()) {
                    result.append("      ").append(applyTo).append("\n");
                }

                result.append("    Related Link: ").append("\n");
                for (String link : rule.getExternalLinks()) {
                    result.append("        ").append(link).append("\n");
                }

                if (includeDeclaration) {
                    result.append("    Content:").append("\n").append("      ").append(rule.getText()).append("\n");
                }
                result.append("\n");
            }
            result.append("\n\n");
        }

        result.append("\n\n\n---------------------Internal CSS: ------------------------\n\n\n");
        for (CSSCodeBlock block : cssManager.getCSSBlocksByType(CSSType.INTERNAL)) {
            result.append("Block Id: ").append(block.getId()).append("\n");
            result.append("Src: ").append(block.getSrc()).append("\n");
            result.append("Related html node: ").append(block.getRelatedHTMLId()).append("\n");
            result.append("Type: ").append(block.getType()).append("\n");

            result.append("Inside CSS Styles: ").append("\n");
            for (CSSRule rule : cssManager.getRulesInsideBlock(block)) {
                result.append("    Rule Id: ").append(rule.getId()).append("\n");
                result.append("    Rule Type: ").append(rule.getRuleType()).append("\n");
                result.append("    Selector:").append(rule.getSelector()).append("\n");
                result.append("    Belong to: ").append(rule.getBelongTo()).append("\n");

                result.append("    Apply to: ").append("\n");
                for (String applyTo : rule.getApplyTo()) {
                    result.append("      ").append(applyTo).append("\n");
                }

                result.append("    Related Link: ").append("\n");
                for (String link : rule.getExternalLinks()) {
                    result.append("       ").append(link).append("\n");
                }

                if (includeDeclaration) {
                    result.append("    Content:").append("\n").append("      ").append(rule.getText()).append("\n");
                }
                result.append("\n");
            }
            result.append("\n\n");
        }

        result.append("\n\n\n---------------------External CSS: ------------------------\n\n\n");
        for (CSSCodeBlock block : cssManager.getCSSBlocksByType(CSSType.EXTERNAL)) {
            result.append("Block Id: ").append(block.getId()).append("\n");
            result.append("Src: ").append(block.getSrc()).append("\n");
            result.append("Related html node: ").append(block.getRelatedHTMLId()).append("\n");
            result.append("Type: ").append(block.getType()).append("\n");

            result.append("Inside CSS Styles: ").append("\n");
            for (CSSRule rule : cssManager.getRulesInsideBlock(block)) {
                result.append("    Rule Id: ").append(rule.getId()).append("\n");
                result.append("    Rule Type: ").append(rule.getRuleType()).append("\n");
                result.append("    Selector:").append(rule.getSelector()).append("\n");
                result.append("    Belong to: ").append(rule.getBelongTo()).append("\n");

                result.append("    Apply to: ").append("\n");
                for (String applyTo : rule.getApplyTo()) {
                    result.append("      ").append(applyTo).append("\n");
                }

                result.append("    Related Link: ").append("\n");
                for (String link : rule.getExternalLinks()) {
                    result.append("        ").append(link).append("\n");
                }

                if (includeDeclaration) {
                    result.append("    Content:").append("\n").append("      ").append(rule.getText()).append("\n");
                }
                result.append("\n");
            }
            result.append("\n\n");
        }
        return result.toString();
    }

    private static String writeNetworkNodeAsString(NetworkRequestManager networkNodeManager) {
        StringBuilder nodeString = new StringBuilder("");

        for (NetworkRequest node : networkNodeManager.getAllNetworkRequests()) {
            nodeString.append("Unique Id: ").append(node.getId()).append("\n");
            nodeString.append("URL: ").append(node.getUrl()).append("\n");
            nodeString.append("Method: ").append(node.getMethod()).append("\n");

            nodeString.append("Request Flows:").append("\n");
            for (NetworkRequest.RequestFlow flow : node.getRequestFlows()) {
                nodeString.append("  ").append(flow.toString()).append("\n");
            }

            nodeString.append("HTTP Messages:").append("\n");
            for (NetworkRequest.HTTPMessage message : node.getHttpMessages()) {
                nodeString.append("-----------------").append("\n");
//                nodeString.append(message.toString());
//                nodeString.append("-----------------").append("\n");
            }
            nodeString.append("\n\n\n");
        }
        return nodeString.toString();
    }

    private static String writeScriptNodeAsString(ScriptManager scriptNodeManager) {
        StringBuilder nodeString = new StringBuilder("");
        for (ScriptCodeBlock node : scriptNodeManager.getAllBlocks()) {
            nodeString.append("Unique Id: ").append(node.getId()).append("\n");
            nodeString.append("Related html node: ").append(node.getRelatedHTMLId()).append("\n");
            nodeString.append("Type: ").append(node.getType()).append("\n");
            nodeString.append("Src: ").append(node.getSrc()).append("\n");
            nodeString.append("Async: ").append(node.getAsync()).append("\n");
            nodeString.append("Defer: ").append(node.getDefer()).append("\n");
            nodeString.append("Content: ").append(node.getContent()).append("\n");
            nodeString.append("\n\n\n");
        }
        return nodeString.toString();
    }

    private static void writeStringToFile(String fileName, String nodeString) throws NodeExtractionException {
        File file = new File(fileName);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(nodeString);
            bufferedWriter.close();
        } catch (IOException e) {
            throw new NodeExtractionException("Fail to write stuff into the file: " + fileName);
        }
    }

    private static String writeIframesAsString(Map<String, IFrameManager> iFrameManagers, ExtractionOptions options, boolean includeDeclaration) {
        StringBuilder iframesScript = new StringBuilder("");
        for (Map.Entry<String, IFrameManager> iFrameManagerEntry : iFrameManagers.entrySet()) {
            iframesScript.append("\n\n\n\n------------------------- Base URL : ").append(iFrameManagerEntry.getValue().getCurrentUrl()).append("-------------------------\n\n\n");
            iframesScript.append("Iframe ID: ").append(iFrameManagerEntry.getKey()).append("\n\n");
            iframesScript.append(writeIframeAsString(iFrameManagerEntry.getKey(), iFrameManagerEntry.getValue(), options, includeDeclaration));
        }

        return iframesScript.toString();
    }

    private static String writeIframeAsString(String iframeId, IFrameManager iframe, ExtractionOptions options, boolean includeDeclaration) {
        StringBuilder iframeScript = new StringBuilder("");

        if (options.htmlExtraction()) {
            iframeScript.append("\n\nHTML Node In Iframe:").append(iframeId).append("\n");
            iframeScript.append(writeHTMLElementAsString(iframe.getHtmlManager()));
        }

        if (options.cssExtraction()) {
            iframeScript.append("\n\nCSS Node In Iframe:").append(iframeId).append("\n");
            iframeScript.append(writeCSSAsString(iframe.getCssManager(), includeDeclaration));
        }

        if (options.networkRequestExtraction()) {
            iframeScript.append("\n\nNetwork Node In Iframe:").append(iframeId).append("\n");
            iframeScript.append(writeNetworkNodeAsString(iframe.getNetworkRequestManager()));
        }

        if (options.scriptExtraction()) {
            iframeScript.append("\n\nScript Node In Iframe:").append(iframeId).append("\n");
            iframeScript.append(writeScriptNodeAsString(iframe.getScriptManager()));
        }

        if (options.iframeExtraction() && options.htmlExtraction()) {
            iframeScript.append("\n\nIframe Content In Iframe:").append(iframeId).append("\n");
            iframeScript.append(writeIframesAsString(iframe.getIFrameManagers(), options, includeDeclaration));
        }

        return iframeScript.toString();
    }
}
