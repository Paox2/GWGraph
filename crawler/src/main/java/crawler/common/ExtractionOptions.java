package crawler.common;


import lombok.AllArgsConstructor;

/**
 * Provide options to the type of elements need to be extracted
 */
@AllArgsConstructor
public class ExtractionOptions {
    /**
     * Basic Structure
     */
    boolean html;
    boolean css;
    boolean script;
    boolean network;

    /**
     * Additional structure
     */
    boolean iframe;
    boolean shadowDOM;

    /**
     * No args constructor
     */
    public ExtractionOptions() {
        html = true;
        css = true;
        script = true;
        network = true;

        shadowDOM = false;
        iframe = false;
    }

    /**
     * Remove All extraction.
     */
    public void allRemove() {
        html = false;
        css = false;
        script = false;
        network = false;

        shadowDOM = false;
        iframe = false;
    }

    /**
     * Add All extraction
     */
    public void allAdd() {
        html = true;
        css = true;
        script = true;
        network = true;

        shadowDOM = true;
        iframe = true;
    }


    public void addHtmlExtraction() {
        html = true;
    }

    public void removeHtmlExtraction() {
        html = false;
    }

    public boolean htmlExtraction() {
        return html;
    }

    public void addCssExtraction() {
        css = true;
    }

    public void removeCssExtraction() {
        css = false;
    }

    public boolean cssExtraction() {
        return css;
    }

    public void addScriptExtraction() {
        script = true;
    }

    public void removeScriptExtraction() {
        script = false;
    }

    public boolean scriptExtraction() {
        return script;
    }

    public void addNetworkRequestExtraction() {
        network = true;
    }

    public void removeNetworkRequestExtraction() {
        network = false;
    }

    public boolean networkRequestExtraction() {
        return network;
    }

    public void addIframeExtraction() {
        iframe = true;
    }

    public void removeIframeExtraction() {
        iframe = false;
    }

    public boolean iframeExtraction() {
        return iframe;
    }

    public void allowShadowDOMExtraction() {
        shadowDOM = true;
    }

    public void removeShadowDOMExtraction() {
        shadowDOM = false;
    }

    public boolean shadowDOMExtraction() {
        return shadowDOM;
    }

}
