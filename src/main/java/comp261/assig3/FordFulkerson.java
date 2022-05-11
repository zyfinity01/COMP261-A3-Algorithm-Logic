package comp261.assig3;

import java.util.ArrayDeque;
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
    private static boolean flag;
    private static String vehicleType;

    // constructor
    public FordFulkerson() {
        augmentationPaths = null;
        residualGraph = null;
    }



    // find maximum flow value
    public static double calcMaxflows(Graph graph, Node source, Node sink) {

        double flow = 0;

        int nodeListSize = graph.getNodeList().size(); // stores size

        residualGraph = new double[nodeListSize][nodeListSize];
        augmentationPaths = new ArrayList<Pair<ArrayList<Node>, Double>>();
                
        // residual graph = network graph. Lec13 - Edmonds Karp Alg slide
        for (int row = 0; row < nodeListSize; row++) {
            for (int col = 0; col < nodeListSize; col++) {

                residualGraph[row][col] = graph.getAdjacencyMatrix()[row][col];

                for (Edge edge : graph.getEdgeList()) {

                    if (edge.getVehicleType().equals("car")) {
                        edge.setWeight(2);
                        vehicleType = "car";
                    } else if (edge.getVehicleType().equals("moped")) {
                        edge.setWeight(4);
                        vehicleType = "moped";
                    } else if(edge.getVehicleType().equals("bus")){
                        edge.setWeight(1);
                        vehicleType = "bus";
                    }

                }

                for (Edge edge : graph.getEdgeList()) {

                    if (edge.getVehicleType().equals("car")) {
                        residualGraph[row][col] *= 2;
                        break;
                    } else if (edge.getVehicleType().equals("moped")) {
                        residualGraph[row][col] *= 4;
                        break;
                    } else {
                        residualGraph[row][col] = graph.getAdjacencyMatrix()[row][col];
                        break;
                    }

                }
                Main.graph = graph;
            }
        }

        int[] parent = new int[nodeListSize];

        do {
            flag = false;
            Queue<Node> queue = new ArrayDeque<Node>();
            queue.add(source);
            parent[source.getId()] = -2;

            for (int i = 0; i < nodeListSize; i++) {
                if (source.getId() != i) {
                    parent[i] = -1;
                }
            }

            flow = bfs(source, sink, parent, graph, flow, queue);

        } while (flag);
        //minCut_s_t(graph, source, sink);
       
        return flow;
    }

    // TODO:Use BFS to find an augmentation path from s to t
    // add the augmentation path found to the arraylist of augmentation paths
    // return bottleneck flow
    public static double bfs(Node source, Node sink, int[] parent, Graph graph, double flow, Queue<Node> queue) {
       ArrayList<Node> path = new ArrayList<Node>();

        while (!queue.isEmpty()) { // move this down to the proper method

            Node current = queue.poll();

            for (Node neighbour : current.getNeighbours()) {

                if (parent[neighbour.getId()] == -1 && (residualGraph[current.getId()][neighbour.getId()]) != 0) {

                    if (neighbour.getId() == sink.getId()) {
                        flag = true;
                    }

                    parent[neighbour.getId()] = current.getId();
                    queue.add(neighbour);

                }

            }

        }

        if (parent[sink.getId()] != -1) {

            double df = Double.MAX_VALUE;

            for (Node v = sink; v != source; v = graph.getNodeList().get(parent[v.getId()])) {

                Node u = graph.getNodeList().get(parent[v.getId()]);

                df = Math.min(df, residualGraph[u.getId()][v.getId()]);

            }

            for (Node v = sink; v != source; v = graph.getNodeList().get(parent[v.getId()])) {

                Node u = graph.getNodeList().get(parent[v.getId()]);

                residualGraph[u.getId()][v.getId()] = residualGraph[u.getId()][v.getId()] - df;
                residualGraph[v.getId()][u.getId()] = residualGraph[v.getId()][u.getId()] + df;

            }

            flow += df;
            flowPath(source, sink, parent, df, graph);
        }

        return flow;
    }

    // TODO: For each flow identified by bfs() build the path from source to sink
    // Add this path to the Array list of augmentation paths: augmentationPaths
    // along with the corresponding flow
    public static void flowPath(Node source, Node sink, int[] parent, double new_flow, Graph graph) {

        ArrayList<Node> augmentationPath = new ArrayList<Node>();

        int temp = sink.getId();
        Node tempNode = sink;

        while(temp > 0){
            
            tempNode = graph.getNodeList().get(temp);
            augmentationPath.add(0, tempNode);
            temp = parent[tempNode.getId()];

        }

        augmentationPath.add(0, source);
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
      // https://www.geeksforgeeks.org/minimum-cut-in-a-directed-graph/ - VERY USEFUL
        //calcMaxflows(graph, source, sink);
        Pair<Pair<HashSet<Node>, HashSet<Node>>, Double> minCutWithSets = null;

        double capacity = 0;

        HashSet<Node> aSet = new HashSet<Node>();
        HashSet<Node> bSet = new HashSet<Node>();

        boolean[] visited = new boolean[graph.getNodeList().size()];
        dfs(graph, source, visited);

        for (int i = 0; i < graph.getAdjacencyMatrix().length; i++) {

            for (int k = 0; k < graph.getAdjacencyMatrix().length; k++) {

                if (graph.getAdjacencyMatrix()[i][k] > 0 && visited[i] && !visited[k]) {

                    
                    aSet.add(graph.getNodeList().get(i));

                    
                    if (vehicleType.equals("car")) {
                        capacity += graph.getAdjacencyMatrix()[i][k]*2;
                    } else if (vehicleType.equals("moped")) {
                        capacity += graph.getAdjacencyMatrix()[i][k]*4;
                    } else if(vehicleType.equals("bus")){
                        capacity += graph.getAdjacencyMatrix()[i][k];
                    }

                }
            }
        }

        for (int k = 0; k < graph.getAdjacencyMatrix().length; k++) {
            if (!aSet.contains(graph.getNodeList().get(k))) {
                bSet.add(graph.getNodeList().get(k));
            }
        }

        // adding aSet and bSet to minCutWithSets
        minCutWithSets = new Pair<Pair<HashSet<Node>, HashSet<Node>>, Double>(
                new Pair<HashSet<Node>, HashSet<Node>>(aSet, bSet), capacity);

        return minCutWithSets;
    }

    // TODO: Use dfs to find set of nodes connected to s
    private static void dfs(Graph graph, Node s, boolean[] visited) {
        visited[s.getId()] = true;
        for (int i = 0; i < residualGraph.length; i++) {

            if (residualGraph[s.getId()][i] > 0 && !visited[i]) {
                dfs(graph, graph.getNodeList().get(i), visited);
            }

        }
    }
}
