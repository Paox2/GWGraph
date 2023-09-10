package demo;

import crawler.util.Logger;
import graph.builder.FeatureExtraction;
import graph.builder.Graph;
import graph.builder.View;
import graph.builder.common.NodeOptions;
import graph.builder.entity.edge.Edge;
import graph.builder.entity.node.Node;
import graph.builder.exception.GraphBuilderException;
import graph.builder.vo.EdgeFilter;
import graph.builder.vo.KatzCentralityParam;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.scoring.KatzCentrality;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class testJGraphT {

    private static View getResultMainViewFromGWGraph(String url) {
        NodeOptions options = new NodeOptions();
        options.allAdd();
        long waitTime = 10L;
        Graph graph = new Graph();

        try {
            graph.useCrawler(url, waitTime, options);
            List<View> viewList = graph.getViews();
            View mainView = viewList.get(0);
            return mainView;

        } catch (GraphBuilderException var11) {
            Logger.getInstance().error(var11.getMessage());
        }

        return null;
    }

    private static org.jgrapht.Graph<String, DefaultWeightedEdge> getDWJGraph() {
        org.jgrapht.Graph<String, DefaultWeightedEdge> jGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        return jGraph;
    }

    private static org.jgrapht.Graph<String, DefaultWeightedEdge> getDirectedJGraph() {
        org.jgrapht.Graph<String, DefaultWeightedEdge> jGraph = new DefaultDirectedGraph<>(DefaultWeightedEdge.class);
        return jGraph;
    }

    public static void main(String[] args) throws IOException {
//        String url = "https://www.wikipedia.org/";
        String url = "https://www.iana.org/domains/example";
        EdgeFilter edgeFilter = new EdgeFilter();
        edgeFilter.buildTypeSet();
        View view = getResultMainViewFromGWGraph(url);
        org.jgrapht.Graph<String, DefaultWeightedEdge> jDefaultWeightGraph = getDWJGraph();
        org.jgrapht.Graph<String, DefaultWeightedEdge> directedJGraph= getDirectedJGraph();
        insertNodeIntoJGraph(view, jDefaultWeightGraph, edgeFilter);
        insertNodeIntoJGraph(view, directedJGraph, edgeFilter);

//        betweennessCentralityCompare(view, directedJGraph, edgeFilter);
        closenessCentralityCompare(view, jDefaultWeightGraph, edgeFilter);
        katzCentralityCompare(view, jDefaultWeightGraph, edgeFilter);
    }

    private static void closenessCentralityCompare(View view, org.jgrapht.Graph<String, DefaultWeightedEdge> jGraph, EdgeFilter edgeFilter) throws IOException {
        List<Double> ourRes = new ArrayList<>();
        List<Double> jGraphRes = new ArrayList<>();

        Map<String, Double> ourBet = FeatureExtraction.closenessCentrality(view, edgeFilter, 2);

        ClosenessCentrality<String, DefaultWeightedEdge> closenessCentrality = new ClosenessCentrality<>(jGraph);

        for (Node node : view.getAllNode()) {
            ourRes.add(ourBet.get(node.getId()));
            jGraphRes.add(closenessCentrality.getVertexScore(node.getId()));
        }

        buildGraph(ourRes, jGraphRes, "Closeness");
    }


    private static void katzCentralityCompare(View view, org.jgrapht.Graph<String, DefaultWeightedEdge> jGraph, EdgeFilter edgeFilter) throws IOException {
        List<Double> ourRes = new ArrayList<>();
        List<Double> jGraphRes = new ArrayList<>();

        KatzCentralityParam param = new KatzCentralityParam();
        param.setAlpha(0.01D);
        param.setMax_iteration(100);
        param.setTol(1.0E-4D);
        param.setNeighborConsideration(2);
        Map<String, Double> ourBet = FeatureExtraction.katzCentrality(view, edgeFilter, param);

        KatzCentrality<String, DefaultWeightedEdge> katzCentrality = new KatzCentrality<>(jGraph);

        for (Node node : view.getAllNode()) {
            ourRes.add(ourBet.get(node.getId()));
            jGraphRes.add(katzCentrality.getVertexScore(node.getId()));
        }

        buildGraph(ourRes, jGraphRes, "Katz");
    }

    private static void betweennessCentralityCompare(View view, org.jgrapht.Graph<String, DefaultWeightedEdge> jGraph, EdgeFilter edgeFilter) throws IOException {
        List<Double> ourRes = new ArrayList<>();
        List<Double> jGraphRes = new ArrayList<>();

        Map<String, Double> ourBet = FeatureExtraction.betweennessCentrality(view, edgeFilter);

        BetweennessCentrality<String, DefaultWeightedEdge> betweennessCentrality = new BetweennessCentrality<>(jGraph);

        for (Node node : view.getAllNode()) {
            ourRes.add(ourBet.get(node.getId()));
            jGraphRes.add(betweennessCentrality.getVertexScore(node.getId()));
        }

        buildGraph(ourRes, jGraphRes, "Betweenness");
    }

    private static void buildGraph(List<Double> ourRes, List<Double> jGraphRes, String yLabel) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries ourSeries = new XYSeries("Our Results");
        XYSeries jgraphSeries = new XYSeries("JGraphT Results");

        for (int i = 0; i < ourRes.size(); i++) {
            ourSeries.add(i, ourRes.get(i));
            jgraphSeries.add(i, jGraphRes.get(i));
        }

        dataset.addSeries(ourSeries);
        dataset.addSeries(jgraphSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Comparison of " + yLabel + " Results",
                "Node",
                "yLabel",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        Color transparentColor1 = new Color(255, 0, 0, 200);
        renderer.setSeriesPaint(0, transparentColor1);
        renderer.setSeriesShapesVisible(1, false);
        Color transparentColor2 = new Color(0, 0, 255, 255);
        renderer.setSeriesPaint(1, transparentColor2);
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        ChartUtilities.saveChartAsPNG(new File(yLabel + ".png"), chart, 800, 600);

    }

    private static void insertNodeIntoJGraph(View view, org.jgrapht.Graph<String, DefaultWeightedEdge> jGraph, EdgeFilter edgeFilter) {
        for (Node node : view.getAllNode()) {
            String id = node.getId();
            jGraph.addVertex(id);
        }

        for (Edge edge : view.getAllEdge()) {
            if (!edgeFilter.contains(edge.getEdgeType())) {
                continue;
            }
            jGraph.addEdge(edge.getFromNodeId(), edge.getToNodeId());
        }
    }

}