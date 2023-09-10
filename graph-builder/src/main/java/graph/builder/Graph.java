package graph.builder;

import graph.builder.common.NodeOptions;
import graph.builder.exception.GraphBuilderException;
import graph.builder.manager.CrawlerManager;
import graph.builder.util.Random;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * API Graph service
 */
public class Graph {
    private List<View> viewList;

    /**
     * No args constructor.
     */
    public Graph() {
        viewList = new ArrayList<>();
    }

    /**
     * Use the default webpage crawler.
     *
     * @param url
     * @throws GraphBuilderException
     */
    public void useCrawler(@NonNull String url) throws GraphBuilderException {
        useCrawler(url, 5, new NodeOptions());
    }

    /**
     * Use the default webpage crawler.
     *
     * @param url
     * @param waitTime
     * @throws GraphBuilderException
     */
    public void useCrawler(@NonNull String url, long waitTime) throws GraphBuilderException {
        useCrawler(url, waitTime, new NodeOptions());
    }

    /**
     * Use the default webpage crawler.
     *
     * @param url
     * @param waitTime
     * @param options
     * @throws GraphBuilderException
     */
    public void useCrawler(@NonNull String url, long waitTime, @NonNull NodeOptions options) throws GraphBuilderException {
        CrawlerManager.graphBuilding(url, waitTime, options, this);
    }

    /**
     * Create and return the view.
     *
     * @return -
     */
    public View createView() {
        String id = Random.generateId();
        return createView(id);
    }

    /**
     * Create and return the view.
     *
     * @param id - view id.
     * @return
     */
    public View createView(String id) {
        View view = new View();
        view.setId(id);
        viewList.add(view);
        return view;
    }

    public List<View> getViews() {
        return viewList;
    }

    /**
     * Get the default content of the webpage.
     *
     * @return
     */
    public View getMainView() {
        return viewList.get(0);
    }

    /**
     * Find the view by view id.
     *
     * @param id
     * @return Return null if the view does not exists.
     */
    public View findViewById(@NonNull String id) {
        View view = null;

        for (View v : viewList) {
            if (v.getViewId().equals(id)) {
                view = v;
            }
        }

        return view;
    }

    /**
     * Find the view by the html node Id in the parent view.
     *
     * @param htmlNodeId
     * @return Return null if the view does not exists.
     */
    public View findViewByRelatedNodeId(@NonNull String htmlNodeId) {
        View view = null;

        for (View v : viewList) {
            if (v.getParentNodeId().equals(htmlNodeId)) {
                view = v;
            }
        }

        return view;
    }

}
