package comp261.assig3;

/*
Simple Edge class for the graph

*/
// store the edges from the loaded file
public class Edge {
    // links directly to the nodes
    private Node from;
    private Node to;
    // also saves ids in case edges were loaded before nodes, or nodes are reloaded
    // but you want to keep the same edge connections
    private int fromId;
    private int toId;

    private double weight;

    private String vehicleType;

    private double originalWeight;

    // Node constructor
    public Edge(Node from, Node to, double weight) {
        this.from = from;
        this.fromId = from.getId();
        this.to = to;
        this.toId = to.getId();
        this.weight = weight;
        this.originalWeight = weight;
    }

    // Nodes without the weight assume a weight of 1
    public Edge(Node from, Node to) {
        this(from, to, 1.0);
    }

    // int constructor
    public Edge(int from, int to, double weight) {
        this.fromId = from;
        this.from = null; // the nodes might not exist yet
        this.toId = to;
        this.to = null; // the nodes might not exist yet
        this.weight = weight;
    }

    // getters
    public Node getFrom() {
        return from;
    }

    public int getFromId() {
        return fromId;
    }

    public Node getTo() {
        return to;
    }

    public int getToId() {
        return toId;
    }

    public double getWeight() {
        return weight;
    }

    public String getVehicleType(){
        return vehicleType;
    }

    // setter for completion
    
    public void setWeight(double weightMultiplier){
        this.weight = originalWeight*weightMultiplier;
    }

    public void setVehicleType(String s){
        this.vehicleType = s;
    }

}
