package crawler.entity;

import crawler.util.Pair;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTML node class.
 */
@Data
public class HTMLElement {
    private String id;

    /**
     * Basic content.
     */
    private String identifyID;
    private String tagName;  // The tag name of the HTML element.
    private String textualContent;
    private String classNames;
    private Map<String, String> attributes;  // The attributes of the HTML element.
    private String innerHTML;

    /**
     * If html element is a iframe, then this contains the related iframe id.
     */
    private String relatedIframeId;

    /**
     * The interactive event name such as onclick.
     */
    private List<String> interactive;

    /**
     * Contains all possible active requests to the external world within the current element
     * consisting of <url, method>.
     */
    private List<Pair<String, String>> activeOutboundRequest;

    /**
     * Contains all possible passive requests to the external world within the current element
     * consisting of <url, method>.
     */
    private List<Pair<String, String>> passiveOutboundRequest;

    /**
     * Related to shadow tree.
     */
    private byte initialNode;
    private byte isDeleted;
    private String shadowRoot;
    private String shadowHost;

    /**
     * About the structure of DOM tree
     */
    private String parent;
    private int depth;

    /**
     * Id list for the parent-child relationship inside the DOM tree.
     */
    private List<String> children;


    /**
     * No args constructor
     */
    public HTMLElement() {
        attributes = new HashMap<>();
        children = new ArrayList<>();
        activeOutboundRequest = new ArrayList<>();
        passiveOutboundRequest = new ArrayList<>();
        interactive = new ArrayList<>();
    }
}

