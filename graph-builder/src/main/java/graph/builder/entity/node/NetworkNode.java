package graph.builder.entity.node;

import crawler.entity.NetworkRequest;
import graph.builder.common.NodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Network node in the graph represents a node in the external network,
 * with different urls for different network nodes.
 * Different network methods for the same network node are the contains in one
 */
@Setter
@Getter
public class NetworkNode extends Node {
    /**
     * The url of this network point.
     */
    private String url;

    /**
     * A list of HTTP message
     */
    private List<Message> messageList;

    /**
     * If the network node is a potential request.
     */
    private byte isPotential;

    /**
     * It represents HTTP request and response messages
     * corresponding to different methods and possibly different headers
     */
    @Setter
    @Getter
    @NoArgsConstructor
    public class Message {
        private String method;
        private long timeStamp;
        private Map<String, String> requestHeaders;
        private String requestBody;
        private int responseStatus;
        private Map<String, String> responseHeaders;
        private String responseBody;
    }

    /**
     * No args constructor.
     */
    public NetworkNode() {
        super("", NodeType.NETWORK, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
        messageList = new ArrayList<>();
    }

    /**
     * Transfer from the data comes from crawler.
     *
     * @param request - Network request from crawler.
     */
    public void transferFrom(NetworkRequest request) {
        this.id = request.getId();
        this.url = request.getUrl();
        this.isPotential = (byte) 1;

        for (NetworkRequest.HTTPMessage httpMessage : request.getHttpMessages()) {
            Message message = new Message();
            message.setMethod(request.getMethod());
            message.setTimeStamp(httpMessage.getTimestamp());
            message.setRequestHeaders(httpMessage.getRequestHeaders());
            message.setRequestBody(httpMessage.getRequestBody());
            message.setResponseStatus(httpMessage.getResponseStatus());
            message.setResponseHeaders(httpMessage.getResponseHeaders());
            message.setResponseBody(httpMessage.getResponseBody());
        }
    }

    /**
     * Search the http message with specific method.
     *
     * @param networkMethod - Network method such as GET and POST.
     * @return
     * @see graph.builder.constant.NetworkConst
     */
    public List<Message> searchMessageByMethod(String networkMethod) {
        List<Message> result = new ArrayList<>();

        if (networkMethod == null) {
            return result;
        }
        for (Message message : messageList) {
            if (message.getMethod().equals(networkMethod)) {
                result.add(message);
            }
        }
        return result;
    }

    /**
     *
     * @param messages
     */
    public void addMessages(List<Message> messages) {
        messageList.addAll(messages);
    }

    /**
     *
     * @param isPotential
     */
    public void setIsPotential(boolean isPotential) {
        this.isPotential = isPotential ? (byte) 1 : (byte) 0;
    };


    /**
     * Check if the node is deleted.
     *
     * @return - boolean
     */
    public boolean isPotential() {
        return isPotential == (byte) 1;
    }
}
