package crawler.manager;

import crawler.Constant.CSSType;
import crawler.Constant.EntityType;
import crawler.Constant.ScriptType;
import crawler.common.ExtractionOptions;
import crawler.entity.*;
import crawler.util.Logger;
import crawler.util.Pair;
import crawler.util.Random;
import lombok.NonNull;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static crawler.Constant.NetworkConst.METHOD_GET;

/**
 * Functions to catch network requests and analyze them.
 */
public class NetworkRequestManager {

    /**
     * Basic Content
     */
    private Map<String, NetworkRequest> networkRequestMap;

    /**
     * Map the url to all request for this url.
     */
    private Map<String, List<String>> urlRequestMap;

    /**
     * The network requests when page load.
     */
    private List<String> requestOnLoad;

    /**
     * The potential requests has not been triggered.
     */
    private List<String> potentialRequest;

    /**
     * No args construct.
     */
    NetworkRequestManager() {
        networkRequestMap = new HashMap<>();
        urlRequestMap = new HashMap<>();
        requestOnLoad = new ArrayList<>();
        potentialRequest = new ArrayList<>();
    }

    /**
     * Network request capture.
     *
     * @param proxy
     */
    void networkRequestAnalyze(BrowserMobProxy proxy){
        captureRequestsOnPageLoad(proxy);
    }

    /**
     * Correct the wrong content of external css/script caused by wrong encoding.
     *
     * @param cssManager
     * @param scriptManager
     */
    void externalResourceContentCorrect(CSSManager cssManager, ScriptManager scriptManager) {
        externalCSSContentCorrection(cssManager);
        externalScriptContentCorrection(scriptManager);
    }

    /**
     * Match the html/css/script element with the network request.
     * In addition, create potential network request which may not be loaded when page loading.
     *
     * @param htmlManager
     * @param cssManager
     * @param scriptManager
     */
    void elementMatching(HTMLManager htmlManager, CSSManager cssManager, ScriptManager scriptManager, ExtractionOptions options) {
        externalCSSContentMatching(cssManager);
        externalScriptMatching(scriptManager);

        externalHTMLContentMapping(htmlManager, options);
    }

    /**
     * Capture the network request when page loading.
     * Eliminate duplicate network requests.
     * Now the duplicate judgement is based on url, method.
     *
     * @param proxy
     */
    private void captureRequestsOnPageLoad(BrowserMobProxy proxy) {
        Har har = proxy.getHar();
        processHarEntry(har.getLog().getEntries());
    }

    List<NetworkRequest> processHarEntry(List<HarEntry> entries) {
        List<NetworkRequest> relatedRequest = new ArrayList<>();
        for (HarEntry entry : entries) {
            // check same url
            List<String> requestIds = urlRequestMap.get(entry.getRequest().getUrl());
            if (requestIds == null || requestIds.isEmpty()) {
                NetworkRequest request = new NetworkRequest();
                request.transferFrom(entry);
                saveRequest(request, true);
                relatedRequest.add(request);
                continue;
            }

            // check same network method
            NetworkRequest requestWithSameMethod = null;
            for (String requestId : requestIds) {
                NetworkRequest request = networkRequestMap.get(requestId);
                if (request == null) {
                    Logger.getInstance().warning("Cannot find match request with id (" + requestId + ")" +
                            " which is extracted from list of network request with url: " + entry.getRequest().getUrl());
                    continue;
                }

                if (request.getMethod() == null) {
                    Logger.getInstance().warning("Request method is null for request: " + requestId);
                    continue;
                }

                if (request.getMethod().equals(entry.getRequest().getMethod())) {
                    requestWithSameMethod = request;
                    break;
                }
            }

            if (requestWithSameMethod != null) {
                requestWithSameMethod.addMessage(entry);
                relatedRequest.add(requestWithSameMethod);
            } else {
                NetworkRequest request = new NetworkRequest();
                request.transferFrom(entry);
                saveRequest(request, true);
                relatedRequest.add(request);
            }
        }
        return relatedRequest;
    }


    /**
     * Capture the network request when page loading.
     * Eliminate duplicate network requests.
     * Now the duplicate judgement is based on url, method.
     *
     * @param entries
     */
    void addNewRequests(List<HarEntry> entries, String sender, String senderType, String receiver, String receiverType, boolean isLoaded) {
        for (HarEntry entry : entries) {
            // check same url
            List<String> requestIds = urlRequestMap.get(entry.getRequest().getUrl());
            if (requestIds == null || requestIds.isEmpty()) {
                NetworkRequest request = new NetworkRequest();
                request.transferFrom(entry);
                request.addRequestFlow(sender, senderType, receiver, receiverType, isLoaded);
                saveRequest(request, false);
                continue;
            }

            // check same network method
            NetworkRequest requestWithSameMethod = null;
            for (String requestId : requestIds) {
                NetworkRequest request = networkRequestMap.get(requestId);
                if (request == null) {
                    Logger.getInstance().warning("Cannot find match request with id (" + requestId + ")" +
                            " which is extracted from list of network request with url: " + entry.getRequest().getUrl());
                    continue;
                }

                if (request.getMethod() == null) {
                    Logger.getInstance().warning("Request method is null for request: " + requestId);
                    continue;
                }

                if (request.getMethod().equals(entry.getRequest().getMethod())) {
                    requestWithSameMethod = request;
                    break;
                }
            }

            if (requestWithSameMethod != null) {
                requestWithSameMethod.addMessage(entry);
                requestWithSameMethod.addRequestFlow(sender, senderType, receiver, receiverType, isLoaded);
            } else {
                NetworkRequest request = new NetworkRequest();
                request.transferFrom(entry);
                request.addRequestFlow(sender, senderType, receiver, receiverType, isLoaded);
                saveRequest(request, false);
            }
        }
    }

    /**
     *
     * @return - All active/potential network request
     */
    public List<NetworkRequest> getAllNetworkRequests() {
        return new ArrayList<>(networkRequestMap.values());
    }

    /**
     *
     * @return - The network requests when page load.
     */
    public List<NetworkRequest> getNetworkRequestsOnPageLoad() {
        List<NetworkRequest> networkRequestList = new ArrayList<>();
        for (String id : requestOnLoad) {
            NetworkRequest request = networkRequestMap.get(id);
            if (request != null) {
                networkRequestList.add(request);
            } else {
                Logger.getInstance().warning("Cannot find a match network request for id (" + id + ")" +
                        " which is extracted from the list of network requests on load.");
            }
        }
        return networkRequestList;
    }

    /**
     *
     * @return - The potential requests has not been triggered.
     */
    public List<NetworkRequest> getPotentialNetworkRequests() {
        List<NetworkRequest> networkRequestList = new ArrayList<>();
        for (String id : potentialRequest) {
            NetworkRequest request = networkRequestMap.get(id);
            if (request != null) {
                networkRequestList.add(request);
            } else {
                Logger.getInstance().warning("Cannot find a match network request for id (" + id + ")" +
                        " which is extracted from the list of potential network requests.");
            }
        }
        return networkRequestList;
    }

    /**
     * Save the request.
     *
     * @param request
     * @param onLoad
     */
    private void saveRequest(NetworkRequest request, boolean onLoad) {
        if (onLoad) {
            requestOnLoad.add(request.getId());
        } else {
            potentialRequest.add(request.getId());
        }

        networkRequestMap.put(request.getId(), request);

        if (urlRequestMap.get(request.getUrl()) != null) {
            urlRequestMap.get(request.getUrl()).add(request.getId());
        } else {
            List<String> ids = new ArrayList<>();
            ids.add(request.getId());
            urlRequestMap.put(request.getUrl(), ids);
        }
    }

    /**
     * Correct the external css content based on the network response from same url.
     * The content maybe fail to extract due to wrong encoding system, this function is to use response from network request
     * to correct that.
     *
     * @param cssManager
     */
    private void externalCSSContentCorrection(CSSManager cssManager) {
        if (cssManager == null) {
            return;
        }

        for (String url : cssManager.getExternalCSSLinks()) {
            NetworkRequest matchRequest = getRequestByFilter(url, METHOD_GET);

            if (matchRequest == null) {
                Logger.getInstance().info("External css is not loaded for url: " + url);
                continue;
            }

            String matchDeclarations = null;
            for (NetworkRequest.HTTPMessage message : matchRequest.getHttpMessages()) {
                if (message.getResponseBody() != null && !message.getResponseBody().isEmpty()) {
                    matchDeclarations = message.getResponseBody();
                }
            }

            if (matchDeclarations == null) {
                Logger.getInstance().info("Cannot do the external css correction for url (" + url + ")" +
                        " since the network request does not exists.");
                continue;
            }

            for (CSSCodeBlock block : cssManager.getExternalCSSBlockByLink(url)) {
                // If the block contains styles, the extraction for this external css is succeed.
                // Currently, the content need to be corrected is because selecting the wrong encoding and cause css parser ail to work.
                // Therefore, if it contains css style which means the encoding is correct and the content is corrent.
                if (!matchDeclarations.isEmpty()) {
//                if (block.getInsideCSSRules().isEmpty()) {
                    cssManager.externalCssContentCorrection(block, matchDeclarations);
                }
            }
        }
    }

    /**
     * Correct the external css content if the content is incorrect.
     * The content maybe fail to extract due to wrong encoding system, this function is to use response from network request
     * to correct that.
     *
     * @param scriptManager
     */
    private void externalScriptContentCorrection(ScriptManager scriptManager) {
        if (scriptManager == null) {
            return;
        }

        for (String url : scriptManager.getExternalScriptLinks()) {
            NetworkRequest matchRequest = getRequestByFilter(url, METHOD_GET);

            if (matchRequest == null) {
                Logger.getInstance().info("External script is not loaded for url: " + url);
                continue;
            }

            String matchContent = null;
            for (NetworkRequest.HTTPMessage message : matchRequest.getHttpMessages()) {
                if (message.getResponseBody() != null && !message.getResponseBody().isEmpty()) {
                    matchContent = message.getResponseBody();
                }
            }

            if (matchContent == null) {
                Logger.getInstance().info("Cannot do the external script correction for url (" + url + ")" +
                        " since the network request does not exists.");
                continue;
            }

            for (ScriptCodeBlock block : scriptManager.getExternalScriptBlockByLink(url)) {
                scriptManager.externalScriptContentCorrection(block, matchContent);
            }
        }
    }

    /**
     * Get the network request which has not find the caller yet.
     *
     * @return
     */
    public List<NetworkRequest> getRequestsWithoutConnect() {
        List<NetworkRequest> requests = new ArrayList<>();
        for (NetworkRequest request : networkRequestMap.values()) {
            if (request.getRequestFlows().isEmpty()) {
                requests.add(request);
            }
        }
        return requests;
    }

    /**
     * Match the external link inside the html to network requests.
     *
     * @param htmlManager
     * @param options
     */
    void externalHTMLContentMapping(HTMLManager htmlManager, ExtractionOptions options) {
        if (htmlManager == null) {
            return;
        }

        List<HTMLElement> actives = new ArrayList<>();
        // Filter the external css and script tag. This is done in CSSContentMapping and ScriptContentMapping.
        for (HTMLElement active : htmlManager.getElementsWithActiveOutboundRequest()) {
            if (active.getTagName().equals("script") && options.scriptExtraction()) {
                continue;
            }

            if (options.cssExtraction()) {
                if (active.getTagName().equals("style")) {
                    continue;
                }

                if (active.getTagName().equals("link") && active.getAttributes().get("rel") != null &&
                        active.getAttributes().get("rel").equals("stylesheet")) {
                    continue;
                }
            }

            actives.add(active);
        }
        activeHTMLRequestAnalyze(actives);


        List<HTMLElement> passives = htmlManager.getElementsWithPassiveOutboundRequest();
        passiveHTMLRequestAnalyze(passives);
    }

    /**
     * Parses and matches html attributes that actively generate outbound requests to the corresponding network request.
     *
     * @param elementList
     */
    private void activeHTMLRequestAnalyze(List<HTMLElement> elementList) {
        for (HTMLElement element : elementList) {
            List<Pair<String, String>> activeOutboundRequests = element.getActiveOutboundRequest();
            if (activeOutboundRequests.isEmpty()) {
                Logger.getInstance().warning("HTML element placed in active outbound requests list " +
                        "without active outbound request");
            }

            String receiverId = null;
            String receiverType = null;

            if (element.getTagName().equals("iframe")) {
                receiverId = element.getRelatedIframeId();
                if (receiverId == null) {
                    Logger.getInstance().warning("Iframe html element does not contains related iframe Id.");
                } else {
                    receiverType = EntityType.IFRAME;
                }
            }

            for (Pair<String, String> outboundRequest : activeOutboundRequests) {
                String url = outboundRequest.getKey();
                String method = outboundRequest.getValue();

                NetworkRequest matchRequest = getRequestByFilter(url, method);

                if (matchRequest == null) {
                    createNewRequest(url, element.getId(), EntityType.HTML, receiverId, receiverType, method);
                    continue;
                }

                matchRequest.addRequestFlow(element.getId(), EntityType.HTML, receiverId, receiverType);
            }
        }
    }

    /**
     * Parses and matches html attributes that passively generate outbound requests to the corresponding network request.
     *
     * @param elementList
     */
    private void passiveHTMLRequestAnalyze(List<HTMLElement> elementList) {
        for (HTMLElement element : elementList) {
            List<Pair<String, String>> passiveOutBoundRequests = element.getPassiveOutboundRequest();
            if (passiveOutBoundRequests.isEmpty()) {
                Logger.getInstance().warning("HTML element placed in passive outbound requests list " +
                        "without passive outbound request");
            }

            for (Pair<String, String> outboundRequest : passiveOutBoundRequests) {
                String url = outboundRequest.getKey();
                String method = outboundRequest.getValue();

                NetworkRequest matchRequest = getRequestByFilter(url, method);

                if (matchRequest == null) {
                    createNewRequest(url, element.getId(), EntityType.HTML, method);
                    continue;
                }

                matchRequest.addRequestFlow(element.getId(), EntityType.HTML, false);
            }
        }
    }

    /**
     * Match the external css request with network request or create potential requests
     *
     * @param cssManager
     */
    private void externalCSSContentMatching(CSSManager cssManager) {
        if (cssManager == null) {
            return;
        }

        externalCSSBlockMatching(cssManager);
        externalCSSRuleMatching(cssManager);
    }

    /**
     * Match the link in css style with network request which can be used to find the connection between
     * inline/internal/external css rule  --contain link--> external fonts/image....
     *
     * @param cssManager
     */
    private void externalCSSRuleMatching(CSSManager cssManager) {
        for (CSSRule rule : cssManager.getRulesContainLinks()) {
            List<String> links = rule.getExternalLinks();
            for (String link : links) {
                NetworkRequest matchRequest = getRequestByFilter(link, METHOD_GET);

                // Create a new request or add this flow to an existing request based on the match
                if (matchRequest == null) {
                    createNewRequest(link, rule.getId(), EntityType.CSSRULE, METHOD_GET);
                    continue;
                }

                matchRequest.addRequestFlow(rule.getId(), EntityType.CSSRULE);
            }
        }
    }

    /**
     * Match the external css request with network request which can be used to find the connection between
     * html element --(create)-> network request --(response)-> external css content
     *
     * @param cssManager
     */
    void externalCSSBlockMatching(CSSManager cssManager) {
        CSSBlockMatching(cssManager.getCSSBlocksByType(CSSType.EXTERNAL));
    }

    /**
     * Match the external css request with network request which can be used to find the connection between
     * html element --(create)-> network request --(response)-> external css content
     *
     * @param blocks
     */
    void CSSBlockMatching(List<CSSCodeBlock> blocks) {
        for (CSSCodeBlock block : blocks) {
            String src = block.getSrc();

            NetworkRequest matchRequest = getRequestByFilter(src, METHOD_GET, block.getUnprocessContent());

            // Create a new request or add this flow to an existing request based on the match
            if (matchRequest == null) {
                createNewRequest(src, block.getRelatedHTMLId(), EntityType.HTML, block.getId(), EntityType.CSS, METHOD_GET, true);
                continue;
            }

            matchRequest.addRequestFlow(block.getRelatedHTMLId(), EntityType.HTML, block.getId(), EntityType.CSS);
        }
    }

    /**
     * Match the external script request with network request or create potential requests
     *
     * @param scriptManager
     */
    void externalScriptMatching(ScriptManager scriptManager) {
        if (scriptManager == null) {
            return;
        }

        externalScriptCodeBlockMatching(scriptManager);
    }

    /**
     * Match the external script request with network request which can be used to find the connection between
     * html element --(create)-> network request --(response)-> external script content
     *
     * @param scriptManager
     */
    void externalScriptCodeBlockMatching(ScriptManager scriptManager) {
        for (ScriptCodeBlock block : scriptManager.getScriptBlocksByType(ScriptType.EXTERNAL)) {
            String src = block.getSrc();

            NetworkRequest matchRequest = getRequestByFilter(src, METHOD_GET, block.getContent());

            // Create a new request or add this flow to an existing request based on the match
            if (matchRequest == null) {
                createNewRequest(src, block.getRelatedHTMLId(), EntityType.HTML, block.getId(), EntityType.SCRIPT, METHOD_GET, true);
                continue;
            }

            matchRequest.addRequestFlow(block.getRelatedHTMLId(), EntityType.HTML, block.getId(), EntityType.SCRIPT);
        }
    }

    /**
     * Get network request by filter.
     * Because presence checking means that requests with the same url and the same method are put into one network request,
     * which is saved internally with a different httpMessage.
     * Therefore, joint query by url and method will only get one result.
     *
     * @param url
     * @param method
     * @param responseBody
     * @return
     */
    public NetworkRequest getRequestByFilter(@NonNull String url, @NonNull String method, @NonNull String responseBody) {
        List<String> requestIds = urlRequestMap.get(url);
        if (requestIds == null || requestIds.isEmpty()) {
            return null;
        }

        for (String requestId : requestIds) {
            NetworkRequest request = networkRequestMap.get(requestId);
            // Check for: request exists; request method match; content match
            if (request == null) {
                Logger.getInstance().warning("Network Request find in url map but cannot find in all, id: " + requestId);
                continue;
            }

            if (!request.getMethod().equals(method)) {
                continue;
            }

            for (NetworkRequest.HTTPMessage message : request.getHttpMessages()) {
                if (message.getResponseBody().equals(responseBody)) {
                    return request;
                }
            }
        }

        return null;
    }

    /**
     * Get network request by filter.
     * Because presence checking means that requests with the same url and the same method are put into one network request,
     * which is saved internally with a different httpMessage.
     * Therefore, joint query by url and method will only get one result.
     *
     * @param url
     * @param method
     * @return
     */
    public NetworkRequest getRequestByFilter(@NonNull String url, @NonNull String method) {
        NetworkRequest matchRequest = null;

        List<String> requestIds = urlRequestMap.get(url);
        if (requestIds == null || requestIds.isEmpty()) {
            return matchRequest;
        }

        for (String requestId : requestIds) {
            NetworkRequest request = networkRequestMap.get(requestId);
            // Check for: request exists; request method match;
            if (request == null) {
                Logger.getInstance().warning("Network Request find in url map but cannot find in all, id: " + requestId);
                continue;
            }

            if (request.getMethod().equals(method)) {
                matchRequest = request;
                break;
            }
        }

        return matchRequest;
    }

    /**
     *
     * @param src
     * @param sender
     * @param senderType
     * @param receiver
     * @param receiveType
     * @param method
     */
    private void createNewRequest(String src, String sender, String senderType,
                                  String receiver, String receiveType, String method) {
        NetworkRequest newRequest = new NetworkRequest();
        newRequest.setId(Random.generateId());
        newRequest.setUrl(src);
        newRequest.setMethod(method);
        newRequest.addRequestFlow(sender, senderType, receiver, receiveType, false);
        saveRequest(newRequest, false);
    }

    /**
     *
     * @param src
     * @param sender
     * @param senderType
     * @param method
     */
    private void createNewRequest(String src, String sender, String senderType, String method) {
        NetworkRequest newRequest = new NetworkRequest();
        newRequest.setId(Random.generateId());
        newRequest.setUrl(src);
        newRequest.setMethod(method);
        newRequest.addRequestFlow(sender, senderType, false);
        saveRequest(newRequest, false);
    }

    /**
     *
     * @param src
     * @param sender
     * @param senderType
     * @param receiver
     * @param receiveType
     * @param method
     * @param onLoaded
     */
    private void createNewRequest(String src, String sender, String senderType,
                                  String receiver, String receiveType, String method, boolean onLoaded) {
        NetworkRequest newRequest = new NetworkRequest();
        newRequest.setId(Random.generateId());
        newRequest.setUrl(src);
        newRequest.setMethod(method);
        newRequest.addRequestFlow(sender, senderType, receiver, receiveType, onLoaded);
        saveRequest(newRequest, onLoaded);
    }


    /**
     *
     * @param src
     * @param sender
     * @param senderType
     * @param method
     * @param onLoaded
     */
    private void createNewRequest(String src, String sender, String senderType, String method, boolean onLoaded) {
        NetworkRequest newRequest = new NetworkRequest();
        newRequest.setId(Random.generateId());
        newRequest.setUrl(src);
        newRequest.setMethod(method);
        newRequest.addRequestFlow(sender, senderType, onLoaded);
        saveRequest(newRequest, onLoaded);
    }
}
