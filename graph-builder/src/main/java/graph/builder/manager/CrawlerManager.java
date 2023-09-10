package graph.builder.manager;

import crawler.NodeService;
import crawler.common.ExtractionOptions;
import crawler.entity.*;
import crawler.exception.NodeExtractionException;
import crawler.manager.*;
import crawler.util.Pair;
import graph.builder.Graph;
import graph.builder.View;
import graph.builder.common.EdgeType;
import graph.builder.common.NodeOptions;
import graph.builder.common.NodeType;
import graph.builder.constant.CSSRuleType;
import graph.builder.constant.CSSType;
import graph.builder.constant.ScriptType;
import graph.builder.entity.edge.Edge;
import graph.builder.entity.node.*;
import graph.builder.exception.GraphBuilderException;
import graph.builder.util.Logger;

import java.util.*;

/**
 * Interacts with the built-in crawler to fetch web elements at a given URL and create a view into the graph.
 */
public class CrawlerManager {

    /**
     * Call crawler to complete the extraction of nodes.
     *
     * @param url
     * @param waitTime
     * @param options
     * @param graph
     * @throws GraphBuilderException
     */
    public static void graphBuilding(String url, long waitTime, NodeOptions options, Graph graph) throws GraphBuilderException {
        if (url == null) {
            throw new GraphBuilderException("Incorrect URL");
        }

        NodeService service = new NodeService();
        try {
            service.setURL(url);
            service.setWaitTime(waitTime);
            service.setOptions(optionsTransfer(options));
            service.nodeExtraction();
            service.close();
        } catch (NodeExtractionException e) {
            throw new GraphBuilderException("Error detect when extract node" + e.getMessage());
        }

        NodeExtractionManager manager = service.getNodeManager();
        viewExtraction(manager, graph, options);

        for (View view : graph.getViews()) {
            view.connectionEdge();
        }
    }

    /**
     * Build the view for each frame.
     *  @param manager
     * @param graph
     * @param options
     */
    private static void viewExtraction(NodeExtractionManager manager, Graph graph, NodeOptions options) {
        View mainView = graph.createView();
        nodeExtraction(manager.getHtmlManager(), manager.getCssManager(),
                manager.getScriptManager(), manager.getNetworkRequestManager(), mainView, options);

        iframeExtraction(manager.getIFrameManagers(), mainView, graph, options);
    }

    /**
     * Process the extraction of iframes.
     *  @param iFrameManagers
     * @param parentView
     * @param graph
     * @param options
     */
    private static void iframeExtraction(Map<String, IFrameManager> iFrameManagers, View parentView, Graph graph, NodeOptions options) {
        for (Map.Entry<String, IFrameManager> entry : iFrameManagers.entrySet()) {
            String parentNodeId = entry.getKey();
            IFrameManager iframe = entry.getValue();

            IframeNode iframeNode = new IframeNode();
            iframeNode.setId(iframe.getId());
            iframeNode.setViewId(iframe.getId());
            if (parentView.addIframe(iframeNode) == null) {
                Logger.getInstance().info("Fail to add the iframe node into view. Iframe node id: " + iframeNode.getId());
                continue;
            }

            View view = graph.createView(iframe.getId());
            view.setParentViewId(parentView.getViewId());
            view.setParentNodeId(parentNodeId);
            nodeExtraction(iframe.getHtmlManager(), iframe.getCssManager(), iframe.getScriptManager(),
                    iframe.getNetworkRequestManager(), view, options);


            iframeExtraction(iframe.getIFrameManagers(), view, graph, options);
        }
    }

    /**
     * Process the extraction of node inside the view.
     *  @param htmlManager
     * @param cssManager
     * @param scriptManager
     * @param networkRequestManager
     * @param view
     * @param options
     */
    private static void nodeExtraction(HTMLManager htmlManager, CSSManager cssManager, ScriptManager scriptManager,
                                       NetworkRequestManager networkRequestManager, View view, NodeOptions options) {
        if (options.htmlExtraction()) {
            htmlNodeExtraction(htmlManager, view);
        }

        if (options.cssExtraction()) {
            cssNodeExtraction(cssManager, view);
        }

        if (options.scriptExtraction()) {
            scriptNodeExtraction(scriptManager, view);
        }

        if (options.networkExtraction()) {
            networkNodeExtraction(networkRequestManager, view);
        }

//        checkExternalResourceConnection(view);
    }

    /**
     * Process the extraction of html node inside the view.
     *
     * @param htmlManager
     * @param view
     */
    private static void htmlNodeExtraction(HTMLManager htmlManager, View view) {
        for (HTMLElement element : htmlManager.getAllElement()) {
            HTMLNode node = new HTMLNode();
            node.transferFrom(element);
            if (view.addHTMLNode(node) == null) {
                Logger.getInstance().info("Fail to add the html node into view. HTML node id: " + node.getId());
                continue;
            }

            if (element.getParent() != null && !element.getParent().equals("")) {
                Edge edge = new Edge();
                edge.setFrom(element.getParent(), NodeType.HTML);
                edge.setTo(element.getId(), NodeType.HTML);
                edge.setEdgeType(EdgeType.PARENT_CHILD_RELATION);
                if (view.addEdgeUncheckExistence(edge) == null) {
                    Logger.getInstance().info("Fail to add the edge into view. Edge id: " + edge.getId() +
                            ". Edge type: " + EdgeType.PARENT_CHILD_RELATION);
                }
            }

            if (element.getShadowRoot() != null) {
                Edge edge = new Edge();
                edge.setFrom(element.getId(), NodeType.HTML);
                edge.setTo(element.getShadowRoot(), NodeType.HTML);
                edge.setEdgeType(EdgeType.SHADOW_HOST);
                if (view.addEdgeUncheckExistence(edge) == null) {
                    Logger.getInstance().info("Fail to add the edge into view. Edge id: " + edge.getId() +
                            ". Edge type: " + EdgeType.SHADOW_HOST);
                }
            }

            if (element.getRelatedIframeId() != null && element.getAttributes().get("src") == null) {
                Edge edge = new Edge();
                edge.setFrom(element.getId(), NodeType.HTML);
                edge.setTo(element.getRelatedIframeId(), NodeType.IFRAME);
                edge.setEdgeType(EdgeType.IFRAME_CONTAINER);
                if (view.addEdgeUncheckExistence(edge) == null) {
                    Logger.getInstance().info("Fail to add the edge into view. Edge id: " + edge.getId() +
                            ". Edge type: " + EdgeType.IFRAME_CONTAINER);
                }
            }
        }
    }

    /**
     * Process the extraction of css node and css rule node inside the view.
     *
     * @param cssManager
     * @param view
     */
    private static void cssNodeExtraction(CSSManager cssManager, View view) {
        cssRuleExtraction(cssManager.getAllRules(), view);
        cssCodeBlockExtraction(cssManager.getAllCodeBlocks(), view);
    }

    /**
     * Process the extraction of css node inside the view.
     *
     * @param allCodeBlocks
     * @param view
     */
    private static void cssCodeBlockExtraction(List<CSSCodeBlock> allCodeBlocks, View view) {
        for (CSSCodeBlock block : allCodeBlocks) {
            CSSNode node = new CSSNode();
            node.transferFrom(block);
            if (view.addCSSNode(node) == null) {
                Logger.getInstance().info("Fail to add the css node into view. CSS node id: " + node.getId());
                continue;
            }

            for (String ruleId : block.getInsideCSSRules()) {
                Edge edge = new Edge();
                edge.setFrom(block.getId(), NodeType.CSS);
                edge.setTo(ruleId, NodeType.CSS_RULE);
                edge.setEdgeType(EdgeType.CSS_RULE_CONTAINER);
                if (view.addEdgeUncheckExistence(edge) == null) {
                    Logger.getInstance().info("Fail to add the edge into view. Edge id: " + edge.getId() +
                            ". Edge type: " + EdgeType.CSS_RULE_CONTAINER);
                }
            }

            if (block.getType().equals(CSSType.EXTERNAL)) {
                continue;
            }

            if (block.getRelatedHTMLId() != null) {
                Edge edge = new Edge();
                edge.setFrom(block.getRelatedHTMLId(), NodeType.HTML);
                edge.setTo(block.getId(), NodeType.CSS);
                edge.setEdgeType(EdgeType.CONTAINS);
                if (view.addEdgeUncheckExistence(edge) == null) {
                    Logger.getInstance().info("Fail to add the edge into view. Edge id: " + edge.getId() +
                            ". Edge type: " + EdgeType.CONTAINS);
                }
            }
        }
    }

    /**
     * Process the extraction of css rule node inside the view.
     *
     * @param allRules
     * @param view
     */
    private static void cssRuleExtraction(List<CSSRule> allRules, View view) {
        for (CSSRule rule : allRules) {
            CSSRuleNode node = new CSSRuleNode();
            node.transferFrom(rule);
            if (view.addCSSRuleNode(node) == null) {
                Logger.getInstance().info("Fail to add the css rule node into view. CSS rule node id: " + node.getId());
                continue;
            }

            if (!rule.getRuleType().equals(CSSRuleType.CSS_STYLE_RULE)) {
                continue;
            }

            for (String htmlId : rule.getApplyTo()) {
                Edge edge = new Edge();
                edge.setFrom(node.getId(), NodeType.CSS_RULE);
                edge.setTo(htmlId, NodeType.HTML);
                edge.setEdgeType(EdgeType.APPLY_TO);
                if (view.addEdgeUncheckExistence(edge) == null) {
                    Logger.getInstance().info("Fail to add the edge into view. Edge id: " + edge.getId() +
                            ". Edge type: " + EdgeType.APPLY_TO);
                }
            }
        }
    }

    /**
     * Process the extraction of script node inside the view.
     *
     * @param scriptManager
     * @param view
     */
    private static void scriptNodeExtraction(ScriptManager scriptManager, View view) {
        for (ScriptCodeBlock block : scriptManager.getAllBlocks()) {
            ScriptNode node = new ScriptNode();
            node.transferFrom(block);
            if (view.addScriptNode(node) == null) {
                Logger.getInstance().info("Fail to add the script node into view. Script node id: " + node.getId());
                continue;
            }

            for (Map.Entry<Pair<String, String>, List<ScriptCodeBlock.Effected>> interact : block.getInteraction().entrySet()) {
                String callerId = interact.getKey().getKey();
                String callerType = interact.getKey().getValue();
                String callerConnectEdgeId = null;

                if (callerId != null && callerType != null) {
                    Edge callerSideEdge = new Edge();
                    callerSideEdge.setFrom(callerId, callerType);
                    callerSideEdge.setTo(block.getId(), NodeType.SCRIPT);
                    callerSideEdge.setEdgeType(EdgeType.FUNCTION_CALL);
                    if (view.addEdgeUncheckExistence(callerSideEdge) == null) {
                        Logger.getInstance().info("Fail to add the edge into view. Edge id: " + callerSideEdge.getId() +
                                ". Edge type: " + EdgeType.FUNCTION_CALL);
                    }
                    callerConnectEdgeId = callerSideEdge.getId();
                }

                for (ScriptCodeBlock.Effected effect : interact.getValue()) {
                    String effectElementId = effect.getEffectElementId();
                    String effectElementType = effect.getEffectElementType();
                    Pair<String, List<String>> ops = effect.getOps();

                    Edge effectEdge = new Edge();
                    effectEdge.setFrom(block.getId(), NodeType.SCRIPT);
                    effectEdge.setTo(effectElementId, effectElementType);
                    effectEdge.setEdgeType(EdgeType.DOM_CHANGE);
                    Map<String, String> comment = new HashMap<>();
                    comment.put("operation", ops.getKey());
                    if (ops.getValue() != null && !ops.getValue().isEmpty()) {
                        comment.put("change", String.join(";", ops.getValue()));
                    }
                    if(callerConnectEdgeId != null) {
                        comment.put("callerEdgeId", callerConnectEdgeId);
                    }
                    effectEdge.setComment(comment);

                    if (view.addEdgeUncheckExistence(effectEdge) == null) {
                        Logger.getInstance().info("Fail to add the edge into view. Edge id: " + effectEdge.getId() +
                                ". Edge type: " + EdgeType.DOM_CHANGE);
                    }
                }
            }

            if (block.getType().equals(ScriptType.EXTERNAL)) {
                continue;
            }

            if (block.getRelatedHTMLId() != null) {
                Edge edge = new Edge();
                edge.setFrom(block.getRelatedHTMLId(), NodeType.HTML);
                edge.setTo(block.getId(), NodeType.SCRIPT);
                edge.setEdgeType(EdgeType.CONTAINS);
                if (view.addEdgeUncheckExistence(edge) == null) {
                    Logger.getInstance().info("Fail to add the edge into view. Edge id: " + edge.getId() +
                            ". Edge type: " + EdgeType.CONTAINS);
                }
            }
        }
    }

    /**
     * Process the extraction of network node inside the view.
     *
     * @param networkRequestManager
     * @param view
     */
    private static void networkNodeExtraction(NetworkRequestManager networkRequestManager, View view) {
        for (NetworkRequest request : networkRequestManager.getAllNetworkRequests()) {
            // filter out the network request caused by redirect
            if (request.getRequestFlows().isEmpty()) {
                continue;
            }

            NetworkNode node = new NetworkNode();
            node.transferFrom(request);
            if (networkRequestManager.getNetworkRequestsOnPageLoad().contains(request)) {
                node.setIsPotential(false);
            }
            String savedId = view.addNetworkNode(node);

            if (savedId == null) {
                Logger.getInstance().info("Fail to add the script node into view. Network node id: " + node.getId());
                continue;
            }

            for (NetworkRequest.RequestFlow flow : request.getRequestFlows()) {
                Pair<String, String> sender = flow.getSender();
                Pair<String, String> receiver = flow.getReceiver();
                Edge senderEdge = new Edge();
                senderEdge.setFrom(sender.getKey(), sender.getValue());
                senderEdge.setTo(savedId, NodeType.NETWORK);
                senderEdge.setEdgeType(EdgeType.NETWORK_REQUEST);
                senderEdge.setOnload(flow.isLoaded() ? (byte) 1: (byte) 0);
                if (view.addEdgeUncheckExistence(senderEdge) == null) {
                    Logger.getInstance().info("Fail to add the edge into view. Edge id: " + senderEdge.getId() +
                            ". Edge type: " + EdgeType.NETWORK_REQUEST);
                }
                String sendSideEdgeId = senderEdge.getId();

                if (receiver.getKey() != null && receiver.getKey() != null) {
                    Edge receiverEdge = new Edge();
                    receiverEdge.setFrom(savedId, NodeType.NETWORK);
                    receiverEdge.setTo(receiver.getKey(), receiver.getValue());
                    receiverEdge.setEdgeType(EdgeType.NETWORK_RESPONSE);
                    receiverEdge.setOnload(flow.isLoaded() ? (byte) 1 : (byte) 0);
                    Map<String, String> comment = new HashMap<>();
                    comment.put("senderEdgeId", sendSideEdgeId);
                    receiverEdge.setComment(comment);
                    if (view.addEdgeUncheckExistence(receiverEdge) == null) {
                        Logger.getInstance().info("Fail to add the edge into view. Edge id: " + receiverEdge.getId() +
                                ". Edge type: " + EdgeType.NETWORK_RESPONSE);
                    }
                }
            }
        }
    }


    /**
     * Build the extractionOptions from node options
     *
     * @param options
     * @return
     */
    private static ExtractionOptions optionsTransfer(NodeOptions options) {
        ExtractionOptions extractionOptions = new ExtractionOptions();
        if (options.htmlExtraction())  {
            extractionOptions.addHtmlExtraction();
        } else {
            extractionOptions.removeHtmlExtraction();
        }

        if (options.cssExtraction()) {
            extractionOptions.addCssExtraction();
        } else {
            extractionOptions.removeCssExtraction();
        }

        if (options.scriptExtraction()) {
            extractionOptions.addScriptExtraction();
        } else {
            extractionOptions.removeScriptExtraction();
        }

        if (options.networkExtraction()) {
            extractionOptions.addNetworkRequestExtraction();
        } else {
            extractionOptions.removeNetworkRequestExtraction();
        }

        if (options.iframeExtraction()) {
            extractionOptions.addIframeExtraction();
        } else {
            extractionOptions.removeIframeExtraction();
        }

        if (options.shadowDOMExtraction()) {
            extractionOptions.allowShadowDOMExtraction();
        } else {
            extractionOptions.removeShadowDOMExtraction();
        }

        return extractionOptions;
    }

}
