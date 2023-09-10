package crawler.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * URL resolver utility to get absolute url from base and relative url
 */
public class URLResolver {
    public static String resolve(String baseUrl, String relativeUrl) throws URISyntaxException {
        URI baseUri = new URI(baseUrl);
        URI resolvedUri = baseUri.resolve(relativeUrl);
        return resolvedUri.toString();
    }
}
