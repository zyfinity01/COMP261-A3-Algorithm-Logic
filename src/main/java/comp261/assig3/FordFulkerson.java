package comp261.assig3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import javafx.util.Pair;

// helper class that does not need local memory

public class FordFulkerson {
    // class members
    // Augmentation paths and the corresponding bottleneck flows
    private static ArrayList<Pair<ArrayList<Node>, Double>> augmentationPaths;
    // residual graph
    private static double[][] residualGraph;

    // constructor
    public FordFulkerson() {
        augmentationPaths = null;
        residualGraph = null;
    }

    // find maximum flow value
    public static double calcMaxflows(Graph graph, Node source, Node sink) {

        double flow = 0;

        return flow;
    }

    // TODO:Use BFS to find an augmentation path from s to t
    // add the augmentation path found to the arraylist of augmentation paths
    // return bottleneck flow
    public static double bfs(Node s, Node t, Node[] parent) {

        return 0;
    }

    // TODO: For each flow identified by bfs() build the path from source to sink
    // Add this path to the Array list of augmentation paths: augmentationPaths
    // along with the corresponding flow
    public static void flowPath(Node s, Node t, Node[] parent, double new_flow) {

        ArrayList<Node> augmentationPath = new ArrayList<Node>();

        // TODO: find the augmentation path identified by the graph traversal algorithm
        // and add it to the list of augmentation paths

        augmentationPaths.add(new Pair<ArrayList<Node>, Double>(augmentationPath, new_flow));

    }

    // getter for augmentation paths
    public static ArrayList<Pair<ArrayList<Node>, Double>> getAugmentationPaths() {
        return augmentationPaths;
    }

    // TODO: find min-cut - as a set of sets and the corresponding cut-capacity
    public static Pair<Pair<HashSet<Node>, HashSet<Node>>, Double> minCut_s_t(Graph graph, Node source, Node sink) {

        Pair<Pair<HashSet<Node>, HashSet<Node>>, Double> minCutwithSets = null;

        return minCutwithSets;
    }

    // TODO: Use dfs to find set of nodes connected to s
    private static void dfs(Graph graph, Node s) {
    }
}
