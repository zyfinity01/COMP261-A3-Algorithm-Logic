package comp261.assig3;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javafx.event.*;

/*
Graph controller associated with FXML file and managing the graph and the view

@author: Simon McCallum, Github Copilot
@author: Jyoti Sahni - modificcations for flow network problem
*/

public class GraphController {

    // names from the items defined in the FXML file
    @FXML
    private VBox vbox; // parent of the canvas

    @FXML
    private TextField searchtext;
    @FXML
    private Label nodeDisplay;

    @FXML
    private Button load;
    @FXML
    private Button quit;
    @FXML
    private Button up;
    @FXML
    private Button down;
    @FXML
    private Button left;
    @FXML
    private Button right;

    // added for car/moped/bus
    @FXML
    private CheckBox car_ch;
    @FXML
    private CheckBox moped_ch;
    @FXML
    private CheckBox bus_ch;

    @FXML
    private CheckBox edgeWeight_ch;
    @FXML
    private CheckBox nodeNames_ch;

    @FXML
    public Canvas mapCanvas;
    @FXML
    private TextArea tripText;
    @FXML
    private AnchorPane anchor;

    public Graph graph;

    public double scale = 2.5;
    public Point2D origin = new Point2D(100, 80);

    // Drawing size of the node
    private static int nodeSize = 10;

    // pixels to move
    private static int moveDistance = 10;
    private static double zoomFactor = 1.1;

    // used for highlighting selected node
    private ArrayList<Node> highlightNodes = new ArrayList<Node>();
    private ArrayList<Edge> highlightEdges = new ArrayList<Edge>();

    // map model to screen using scale and origin
    public Point2D model2Screen(Point2D model) {
        return new Point2D(model2ScreenX(model.getX()), model2ScreenY(model.getY()));
    }

    private double model2ScreenX(double modelX) {
        return (modelX - origin.getX()) * scale + mapCanvas.getWidth() / 2;
    }

    private double model2ScreenY(double modelY) {
        return (modelY - origin.getY()) * scale + mapCanvas.getHeight() / 2;
    }

    // map screen to model using scale and origin
    private Point2D getScreen2Model(Point2D screenPoint) {
        return new Point2D(getScreen2ModelX(screenPoint), getScreen2ModelY(screenPoint));
    }

    private double getScreen2ModelX(Point2D screenPoint) {
        return (((screenPoint.getX() - mapCanvas.getWidth() / 2) / scale) + origin.getX());
    }

    private double getScreen2ModelY(Point2D screenPoint) {
        return (((screenPoint.getY() - mapCanvas.getHeight() / 2) / scale) + origin.getY());
    }

    // setup the fxml view
    public void initialize() {
        // setting up te canvas to expand to fit the window by listening to redraw
        mapCanvas.widthProperty().addListener(listener);
        mapCanvas.heightProperty().addListener(listener);
        // bind to the width and height of the parent of the canvas
        mapCanvas.widthProperty().bind(vbox.widthProperty());
        mapCanvas.heightProperty().bind(vbox.heightProperty());
        nodeNames_ch.setSelected(false);
        edgeWeight_ch.setSelected(false);
        bus_ch.setSelected(false);
    }

    // The listener for expanding the window
    InvalidationListener listener = new InvalidationListener() {
        @Override
        public void invalidated(Observable o) {
            drawGraph(graph);
        }
    };

    /* handling the GUI events */

    // load button pressed - load 2 files
    public void handleLoad(ActionEvent event) {
        Stage stage = (Stage) mapCanvas.getScene().getWindow();
        System.out.println("Handling event " + event.getEventType());
        FileChooser fileChooser = new FileChooser();
        // Set to user directory or go to default if cannot access
        File defaultNodePath = new File("data/");
        if (!defaultNodePath.canRead()) {
            defaultNodePath = new File("C:/");
        }
        fileChooser.setInitialDirectory(defaultNodePath);
        FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extentionFilter);

        fileChooser.setTitle("Open Node File");
        File nodeFile = fileChooser.showOpenDialog(stage);

        fileChooser.setTitle("Open Edge File");
        File edgeFile = fileChooser.showOpenDialog(stage);
        graph = new Graph(nodeFile, edgeFile);
        Main.graph = graph; //loads the graph, forgotten by issuer
        drawGraph(graph);
        event.consume();
    }

    // quit button pressed - exit the program
    public void handleQuit(ActionEvent event) {
        System.out.println("Quitting with event " + event.getEventType());
        event.consume();
        System.exit(0);
    }

    // fixed step zoomin
    public void handleZoomin(ActionEvent event) {
        System.out.println("Zoom in event " + event.getEventType());
        scale *= zoomFactor;
        drawGraph(graph);
        event.consume();
    }

    // fixed step zoomout
    public void handleZoomout(ActionEvent event) {
        System.out.println("Zoom out event " + event.getEventType());
        scale *= 1.0 / zoomFactor;
        drawGraph(graph);
        event.consume();
    }

    // move the origin up
    public void handleUp(ActionEvent event) {
        System.out.println("Move up event " + event.getEventType());
        origin = origin.add(0, moveDistance / scale);
        drawGraph(graph);
        event.consume();
    }

    public void displayUpdate(ActionEvent event) {
        drawGraph(graph);
        // drawGraphAsJavaFX();
        event.consume();
    }

    //
    public void handleDown(ActionEvent event) {
        System.out.println("Move Down event " + event.getEventType());
        origin = origin.subtract(0, moveDistance / scale);
        drawGraph(graph);
        event.consume();
    }

    public void handleLeft(ActionEvent event) {
        System.out.println("Move Left event " + event.getEventType());
        origin = origin.add(moveDistance / scale, 0);
        drawGraph(graph);
        event.consume();
    }

    public void handleRight(ActionEvent event) {
        System.out.println("Move Right event " + event.getEventType());
        origin = origin.subtract(moveDistance / scale, 0);
        drawGraph(graph);
        event.consume();
    }

    /*
     * handle mouse clicks on the canvas
     * select the node closest to the click
     */
    public void handleMouseClick(MouseEvent event) {
        System.out.println("Mouse click event " + event.getEventType());
        // find node closed to mouse click
        Point2D screenPoint = new Point2D(event.getX(), event.getY());
        double x = getScreen2ModelX(screenPoint);
        double y = getScreen2ModelY(screenPoint);
        highlightClosestNode(x, y);
        event.consume();
    }



    // handle finding maximum network flow
    public void handleNetworkFlow(ActionEvent event) {
        for (Edge edge : graph.getEdgeList()) {

            if (car_ch.isSelected()) {

                edge.setVehicleType("car");


            } else if (moped_ch.isSelected()) {

                edge.setVehicleType("moped");
   

            } else {  //if(bus_ch.isSelected())

                edge.setVehicleType("bus");


            }   


        }


        //s = node 0; t = last node in the node list
        double nflowEdges;
        nflowEdges = FordFulkerson.calcMaxflows(graph, graph.getNodeList().get(0),
                graph.getNodeList().get(graph.getNodeList().size() - 1));
        tripText.appendText(" Source: " + graph.getNodeList().get(0).getName() + " Sink: "
                + graph.getNodeList().get(graph.getNodeList().size() - 1).getName() + " Max Flow: " + nflowEdges + "\n");
        drawGraph(graph);
        event.consume();
    }

    // handle displaying augmentation paths
    public void handleAugmentationPaths(ActionEvent event) {
        
        ArrayList<Pair<ArrayList<Node>, Double>> aPaths = new ArrayList<Pair<ArrayList<Node>, Double>>();
        aPaths = FordFulkerson.getAugmentationPaths();
        if (aPaths != null) {
            for(Pair<ArrayList<Node>, Double> pair: aPaths){
                tripText.appendText("AP: ");
                for(Node node : pair.getKey()){
                    tripText.appendText(node.getId() +" ");
                }
                tripText.appendText("Flow Value:" + pair.getValue() + " || ");
            }
            tripText.appendText("\n");
        } else {
            tripText.appendText("\nNo augmentation paths found");
        }
    }

    public void highlightClosestNode(double x, double y) {
        double minDist = Double.MAX_VALUE;
        Node closestNode = null;
        for (Node node : Main.graph.getNodeList()) {
            double dist = node.distance(x, y);
            if (dist < minDist) {
                minDist = dist;
                closestNode = node;
            }
        }
        if (closestNode != null) {
            highlightNodes.clear();
            highlightNodes.add(closestNode);

            drawGraph(graph);
        }
    }

    // handle min-cut - Sets and capacity
    public void handleMinCut(ActionEvent event) {
        //s = node 0; t = last node in the node list
        Pair<Pair<HashSet<Node>, HashSet<Node>>, Double> minCutwithSets = FordFulkerson.minCut_s_t(graph,
                graph.getNodeList().get(0), graph.getNodeList().get(graph.getNodeList().size() - 1));
        if (minCutwithSets != null) {
            String a = "";
            String b = "";

            for (Node source : minCutwithSets.getKey().getKey()) {
                a += source.getName() + " ";
            }

            for (Node sink : minCutwithSets.getKey().getValue()) {
                b += sink.getName() + " ";
            }

            tripText.appendText("Source: " + a);

            tripText.appendText("Sink: " + b + " ::: ");

            tripText.appendText("S-T cut: " + minCutwithSets.getValue() + "\n");
        } else {
            tripText.appendText("\nNo min-cut found");
        }
    }
    /*
     * Drawing the graph on the canvas
     */

    public void drawCircle(double x, double y, double radius) {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.fillOval(x - (radius / 2), (y - radius / 2), radius, radius);
    }

    public void drawCircle(double x, double y, double radius, Color color) {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillOval(x - (radius / 2), (y - radius / 2), radius, radius);
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        mapCanvas.getGraphicsContext2D().strokeLine(x1, y1, x2, y2);
        // arrow to display directed edges
        // get the slope and find its angle
        double slope;
        if (x1 == x2) {
            slope = Integer.MAX_VALUE;
        } else {
            slope = (y1 - y2) / (x1 - x2);
        }
        // find the angle and postion of arrow
        double lineAngle = Math.atan(slope);
        double intercept = y1 - slope * x1;
        double arrowAngle = x1 >= x2 ? Math.toRadians(45) : -Math.toRadians(215);

        double arrowLength = 10;
        mapCanvas.getGraphicsContext2D().setStroke(Color.RED);
        double arrowX, arrowY;
        if (x1 < x2) {
            arrowX = 10 + x1 + (x2 - x1) / 2;
            arrowY = slope * arrowX + intercept;
        } else {
            if (x1 > x2) {
                arrowX = 10 + x2 + (x1 - x2) / 2;
                arrowY = slope * arrowX + intercept;
            } else {
                arrowX = x2;
                arrowY = y1 > y2 ? y1 + (y2 - y1) / 2 : y2 + (y1 - y2) / 2;

            }
        }

        mapCanvas.getGraphicsContext2D().strokeLine(arrowX, arrowY,
                arrowX + arrowLength * Math.cos(lineAngle - arrowAngle),
                arrowY + arrowLength * Math.sin(lineAngle - arrowAngle));
        mapCanvas.getGraphicsContext2D().strokeLine(arrowX, arrowY,
                arrowX + arrowLength * Math.cos(lineAngle + arrowAngle),
                arrowY + arrowLength * Math.sin(lineAngle + arrowAngle));

    }

    public void drawGraph(Graph graph) {
        if (graph == null) {
            return;
        }
        // upDateGraph(graph);

        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // store node list so we can use nodes to find edge end points.
        ArrayList<Node> nodeList = graph.getNodeList();

        // draw nodes
        nodeList.forEach(node -> {
            double size = nodeSize;

            if (highlightNodes != null && highlightNodes.contains(node)) {
                gc.setFill(Color.RED);
                size = nodeSize * 1.5;
            } else {
                gc.setFill(Color.ROYALBLUE);

            }

            Point2D screenPoint = model2Screen(node.getPoint());
            drawCircle(screenPoint.getX(), screenPoint.getY(), size);
            if (nodeNames_ch.isSelected()) {
                gc.setFill(Color.ROYALBLUE);
                gc.setFont(new Font("Arial", 12));
                gc.fillText("[" + node.getId() + "]" + node.getName(), screenPoint.getX() + 10,
                        screenPoint.getY() - 10);
            }
        });

        // draw edges
        graph.getEdgeList().forEach(edge -> {
            if (highlightEdges != null && highlightEdges.contains(edge)) {
                gc.setStroke(Color.RED);
            } else {
                gc.setStroke(Color.BLACK);
            }

            Point2D startPoint = model2Screen(edge.getFrom().getPoint());
            Point2D endPoint = model2Screen(edge.getTo().getPoint());
            drawLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
            // display edge weights
            // get the slope and find its angle
            if (edgeWeight_ch.isSelected()) {
                //
                double textPosX, textPosY, slope, intercept;
                if (startPoint.getX() == endPoint.getX()) {
                    slope = Integer.MAX_VALUE;
                } else {
                    slope = (endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX());
                }
                intercept = startPoint.getY() - slope * startPoint.getX();
                if (startPoint.getX() < endPoint.getX()) {
                    textPosX = 15 + startPoint.getX() + (endPoint.getX() - startPoint.getX()) / 2;
                    textPosY = slope * textPosX + intercept;
                } else {
                    if (startPoint.getX() > endPoint.getX()) {
                        textPosX = 15 + endPoint.getX() + (startPoint.getX() - endPoint.getX()) / 2;
                        textPosY = slope * textPosX + intercept;
                    } else {
                        textPosX = 5 + startPoint.getX();
                        textPosY = startPoint.getY() > endPoint.getY()
                                ? startPoint.getY() + (endPoint.getY() - startPoint.getY()) / 2
                                : endPoint.getY() + (startPoint.getY() - endPoint.getY()) / 2;

                    }
                }

                if (Math.abs(startPoint.getY() - endPoint.getY()) < 20) {
                    textPosY -= 2;
                }

                gc.setFill(Color.GREEN);
                gc.setFont(new Font("Arial", 12));
                gc.fillText(Double.toString(edge.getWeight()), textPosX, textPosY);
            }

        });

    }

}