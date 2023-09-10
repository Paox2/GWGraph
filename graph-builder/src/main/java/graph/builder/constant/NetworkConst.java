package graph.builder.constant;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class NetworkConst {

    /**
     * HTML ATTRI which may contains external link
     */
    public static final List<String> HTML_ATTRI = Arrays.asList(
            "src",
            "href",
            "data",
            "poster",
            "action",
            "cite",
            "icon",
            "background",
//            "usemap",
            "profile",
            "ping",
            "manifest",
            "codebase",
            "code",
            "archive"
    );

    public static final List<String> HTML_ATTRI_GET = Arrays.asList(
            "src",
            "href",
            "icon",
            "data",
            "poster",
            "cite",
            "background",
            "usemap",
            "profile",
            "ping",
            "manifest",
            "codebase",
            "code",
            "archive"
    );

    public static final List<String> HTML_ATTRI_POST = Arrays.asList(
            "src", // it could be post in input
            "ping",
            "manifest"
    );

    public static final List<String> HTML_ATTRI_UNKNOW = Arrays.asList(
            "action" // depend on the method attribute
    );

    /**
     * CSS declaration
     */
    public static final Pattern CSS_EXTERNAL_LINK_VALUE = Pattern.compile("url\\(['\"]?(.+?)['\"]?\\)");


    /**
     * Network Request Type
     */
    public static final List<String> NETWORK_REQUEST_METHOD = Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "HEAD",
            "OPTIONS",
            "CONNECT",
            "TRACE",
            "PATCH"
    );

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_CONNECT = "CONNECT";
    public static final String METHOD_TRACE = "TRACE";
    public static final String METHOD_PATCH = "PATCH";
}
