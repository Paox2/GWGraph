package crawler.entity;

import crawler.util.Pair;
import crawler.util.Random;
import lombok.*;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class NetworkRequest {
    /**
     * basic content
     */
    private String id;
    private String url;
    private String method;
    private List<HTTPMessage> httpMessages;
    private List<RequestFlow> requestFlows;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HTTPMessage {
        private String referer;;
        private long timestamp;
        private Map<String, String> requestHeaders;
        private String requestBody;
        private int responseStatus;
        private Map<String, String> responseHeaders;
        private String responseBody;

        public HTTPMessage(long timestamp, Map<String, String> requestHeaders, String requestBody
                , int responseStatus, Map<String, String> responseHeaders, String responseBody) {
            this.timestamp = timestamp;
            this.requestHeaders = requestHeaders;
            this.requestBody = requestBody;
            this.responseStatus = responseStatus;
            this.responseHeaders = responseHeaders;
            this.responseBody = responseBody;
        }

        public String toString() {
            StringBuilder string = new StringBuilder();
            string.append("Time: ").append(timestamp).append("\n");
            string.append("Referer: ").append(referer).append("\n");
            string.append("responseStatus: ").append(responseStatus).append("\n");
            string.append("Request Headers:").append("\n");
            for (Map.Entry<String, String> attribute : requestHeaders.entrySet()) {
                string.append(attribute.getKey()).append(": ").append(attribute.getValue()).append("\n");
            }
            string.append("Request Body: ").append(requestBody).append("\n");
            string.append("Response Headers:").append("\n");
            for (Map.Entry<String, String> attribute : responseHeaders.entrySet()) {
                string.append(attribute.getKey()).append(": ").append(attribute.getValue()).append("\n");
            }
            string.append("Response Body: ").append(responseBody).append("\n");
            return string.toString();
        }
    }

    /**
     * The request chain between network request and other entities.
     */
    public static class RequestFlow {
        String sender;
        String senderType;
        String receiver;
        String receiverType;
        private boolean loaded;

        /**
         *
         * @param sender
         * @param senderType
         */
        public RequestFlow(String sender, String senderType, Boolean loaded) {
            this.sender = sender;
            this.senderType = senderType;
            this.loaded = loaded;
        }

        /**
         *
         * @param sender
         * @param senderType
         * @param receiver
         * @param receiverType
         */
        public RequestFlow(String sender, String senderType, String receiver, String receiverType, Boolean loaded) {
            this.sender = sender;
            this.senderType = senderType;
            this.receiver = receiver;
            this.receiverType = receiverType;
            this.loaded = loaded;
        }

        /**
         * Get sender id-type pair.
         *
         * @return
         */
        public Pair<String, String> getSender() {
            return new Pair<>(sender, senderType);
        }

        /**
         * Get receiver id-type pair.
         *
         * @return
         */
        public Pair<String, String> getReceiver() {
            return new Pair<>(receiver, receiverType);
        }

        /**
         *  Whether the element receiving the message exists
         *
         * @return
         */
        public boolean hasReceiver() {
            return receiver == null;
        }

        /**
         * This represents whether the request is actively or passively triggered for access to this network resource.
         *
         * @return
         */
        public boolean isLoaded() {
            return loaded;
        }

        /**
         *
         * @return
         */
        public String toString() {
            return "From: " + sender + " " + senderType + "  To " + receiver + " " + receiverType + "  -  " + loaded;
        }
    }

    /**
     *
     * @return - Number of entities use this network request responses.
     *
     * @see RequestFlow - The request chain between network request and other entities.
     */
    public int countRequestFlows() {
        return requestFlows.size();
    }

    /**
     * Add relevant information on network exchanges
     * @param entry
     */
    public void addMessage(HarEntry entry) {
        long timestamp = entry.getStartedDateTime().getTime();
        String requestBody = "";
        Map<String, String> requestHeaders = new HashMap<>();
        String responseBody = "";
        Map<String, String> responseHeaders = new HashMap<>();
        int responseStatus = 0;

        if (entry.getRequest() != null) {
            if (entry.getRequest().getPostData() != null && entry.getRequest().getPostData().getText() != null) {
                requestBody = entry.getRequest().getPostData().getText();
            }

            List<HarNameValuePair> requestHeadersList = entry.getRequest().getHeaders();
            for (HarNameValuePair header : requestHeadersList) {
                requestHeaders.put(header.getName(), header.getValue());
            }
        }

        if (entry.getResponse() != null) {
            responseStatus = entry.getResponse().getStatus();
            if (entry.getResponse().getContent() != null && entry.getResponse().getContent().getText() != null) {
                responseBody = entry.getResponse().getContent().getText();
            }

            List<HarNameValuePair> responseHeadersList = entry.getResponse().getHeaders();
            for (HarNameValuePair header : responseHeadersList) {
                responseHeaders.put(header.getName(), header.getValue());
            }
        }

        this.httpMessages.add(new HTTPMessage(timestamp, requestHeaders, requestBody,
                responseStatus, responseHeaders, responseBody));
    }

    /**
     *
     * @param sender
     * @param senderType
     * @see RequestFlow - The request chian between network request and other entities.
     */
    public void addRequestFlow(String sender, String senderType) {
        boolean isLoaded = false;
        if (!httpMessages.isEmpty()) {
            isLoaded = true;
        }
        requestFlows.add(new RequestFlow(sender, senderType, isLoaded));
    }

    /**
     *
     * @param sender
     * @param senderType
     * @param isLoaded
     * @see RequestFlow - The request chian between network request and other entities.
     */
    public void addRequestFlow(String sender, String senderType, boolean isLoaded) {
        requestFlows.add(new RequestFlow(sender, senderType, isLoaded));
    }

    /**
     *
     * @param sender
     * @param senderType
     * @param receiver
     * @param receiverType
     * @see RequestFlow - The request chian between network request and other entities.
     */
    public void addRequestFlow(String sender, String senderType, String receiver, String receiverType) {
        boolean isLoaded = false;
        if (!httpMessages.isEmpty()) {
            isLoaded = true;
        }
        requestFlows.add(new RequestFlow(sender, senderType, receiver, receiverType, isLoaded));
    }

    /**
     *
     * @param sender
     * @param senderType
     * @param receiver
     * @param receiverType
     * @param isLoaded
     * @see RequestFlow - The request chian between network request and other entities.
     */
    public void addRequestFlow(String sender, String senderType, String receiver, String receiverType, boolean isLoaded) {
        requestFlows.add(new RequestFlow(sender, senderType, receiver, receiverType, isLoaded));
    }

    /**
     * Get the request flow for this network request (include sender, senderType, receiver, receiveType)
     *
     * @return
     * @see RequestFlow - The request chian between network request and other entities.
     */
    public List<RequestFlow> getRequestFlows() {
        return requestFlows;
    }

    /**
     * No args constructor
     */
    public NetworkRequest() {
        httpMessages = new ArrayList<>();
        requestFlows = new ArrayList<>();
    }


    /**
     * Transfer har entry to network request entity
     *
     * @param entry
     */
    public void transferFrom(HarEntry entry) {
        id = Random.generateId();
        url = entry.getRequest().getUrl();
        method = entry.getRequest().getMethod();
        addMessage(entry);
    }
}
