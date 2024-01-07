package Controller;

import Model.GraphDrawingPane;
import Model.GraphNode;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Iterator;

public class GUIController {

    @FXML
    private AnchorPane drawingPane;

    @FXML
    private TextField inputXCord;
    @FXML
    private TextField inputYCord;
    @FXML
    private TextField inputValue;

    private GraphDrawingPane currentGraphPane;
    private Circle newNodeCircle;
    private Line newEdgeLine;
    private double newEdgeStartX;
    private double newEdgeStartY;

    private static final int PANE_WIDTH = 950;
    private static final int PANE_HEIGHT = 650;
    private static final double STROKE_WIDTH = 3;
    private static final double ZOOM_FACTOR = 1.3;
    private static final int MIN_ZOOM = 150;
    private static final int MAX_ZOOM = 1200;

    private int currentZoom;
    private int currentCenterX;
    private int currentCenterY;
    private int selectedNodeID;

    private double lastDragX;
    private double lastDragY;

    private boolean addNodeMode;
    private boolean addEdgeMode;
    private boolean moveNodeMode;

    public void init() {
        GraphDrawingPane graphDrawingPane = new GraphDrawingPane();
        graphDrawingPane.addNode(10, 10, "a");
        graphDrawingPane.addNode(50, 10, "b");
        graphDrawingPane.addNode(10, 50, "c");
        graphDrawingPane.addNode(50, 50, "d");

        graphDrawingPane.addConnection("a", "b");
        graphDrawingPane.addConnection("a", "c");
        graphDrawingPane.addConnection("a", "d");
        graphDrawingPane.addConnection("b", "c");

        newNodeCircle = new Circle(0, 0, 0);
        newNodeCircle.setFill(null);
        newNodeCircle.setStroke(Color.LIGHTGRAY);
        newNodeCircle.setStrokeWidth(2);
        newNodeCircle.setVisible(false);
        drawingPane.getChildren().add(newNodeCircle);

        newEdgeLine = new Line(0, 0, 0, 0);
        newEdgeLine.setFill(Color.BLACK);
        newEdgeLine.setVisible(false);
        drawingPane.getChildren().add(newEdgeLine);

        newEdgeStartX = Double.MIN_VALUE;
        newEdgeStartY = Double.MIN_VALUE;

        currentZoom = 475;
        currentCenterX = 0;
        currentCenterY = 0;
        selectedNodeID = -1;

        updatePane(graphDrawingPane);
        currentGraphPane = graphDrawingPane;

        moveNodeMode = false;
        addNodeMode = false;
        addEdgeMode = false;

        inputValue.setOnAction(event -> {
            if (selectedNodeID != -1) {
                graphDrawingPane.updateNodeValue(selectedNodeID, inputValue.getText());
                updatePane(graphDrawingPane);
            }
        });
        inputXCord.setOnAction(event -> {
            if (selectedNodeID != -1) {
                try {
                    int newXCord = Integer.parseInt(inputXCord.getText());
                    int newYCord = Integer.parseInt(inputYCord.getText());
                    graphDrawingPane.updateNodePosition(selectedNodeID, newXCord, newYCord);
                    updatePane(graphDrawingPane);
                } catch (Exception e) {
                    // do nothing
                }
            }
        });
        inputYCord.setOnAction(event -> {
            if (selectedNodeID != -1) {
                try {
                    int newXCord = Integer.parseInt(inputXCord.getText());
                    int newYCord = Integer.parseInt(inputYCord.getText());
                    graphDrawingPane.updateNodePosition(selectedNodeID, newXCord, newYCord);
                    updatePane(graphDrawingPane);
                } catch (Exception e) {
                    // do nothing
                }
            }
        });

        drawingPane.setOnMouseDragged(event -> {
            if (!addNodeMode && !addEdgeMode) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                currentCenterX += (mouseX - lastDragX);
                currentCenterY += (mouseY - lastDragY);

                lastDragX = mouseX;
                lastDragY = mouseY;
            }

            drawingPane.setCursor(Cursor.CLOSED_HAND);
            updatePane(graphDrawingPane);
        });


        drawingPane.setOnMousePressed(event -> {
            lastDragX = event.getX();
            lastDragY = event.getY();

            double graphPaneX = convertToGraphPaneCordsX(event.getX());
            double graphPaneY = convertToGraphPaneCordsY(event.getY());

            if (addEdgeMode) {
                int nodeID = getNodeByCords(graphDrawingPane, graphPaneX, graphPaneY);
                if (newEdgeStartX == Double.MIN_VALUE) {
                    if (nodeID != -1) {
                        GraphNode node = getNodeById(graphDrawingPane, nodeID);
                        assert node != null;
                        newEdgeStartX = node.getCenterX();
                        newEdgeStartY = node.getCenterY();

                        newEdgeLine.setVisible(true);
                        newEdgeLine.setStartX(convertToDrawingPaneCordsX((int) newEdgeStartX));
                        newEdgeLine.setStartY(convertToDrawingPaneCordsY((int) newEdgeStartY));
                        newEdgeLine.setEndX(convertToDrawingPaneCordsX((int) newEdgeStartX));
                        newEdgeLine.setEndY(convertToDrawingPaneCordsY((int) newEdgeStartY));
                    }
                } else {
                    if (nodeID != -1) {
                        GraphNode nodeLhs = getNodeById(graphDrawingPane, getNodeByCords(graphDrawingPane, newEdgeStartX, newEdgeStartY));
                        GraphNode nodeRhs = getNodeById(graphDrawingPane, getNodeByCords(graphDrawingPane, convertToGraphPaneCordsX(event.getX()), convertToGraphPaneCordsY(event.getY())));
                        assert nodeLhs != null && nodeRhs != null;
                        graphDrawingPane.addConnection(nodeLhs.getValue(), nodeRhs.getValue());
                        updatePane(graphDrawingPane);
                    }
                    newEdgeLine.setVisible(false);
                    newEdgeStartX = Double.MIN_VALUE;
                    newEdgeStartY = Double.MIN_VALUE;
                }
            } else if (addNodeMode) {
                graphDrawingPane.addNode((int)graphPaneX, (int)graphPaneY);
            } else if (moveNodeMode) {
                newNodeCircle.setVisible(false);
                graphDrawingPane.updateNodePosition(selectedNodeID, convertToGraphPaneCordsX(event.getX()), convertToGraphPaneCordsY(event.getY()));
                moveNodeMode = false;
            } else {
                selectedNodeID = getNodeByCords(graphDrawingPane, graphPaneX, graphPaneY);

                if (selectedNodeID != -1) {
                    inputXCord.setText(Integer.toString(graphDrawingPane.getNode(selectedNodeID).getCenterX()));
                    inputYCord.setText(Integer.toString(graphDrawingPane.getNode(selectedNodeID).getCenterY()));
                    inputValue.setText(graphDrawingPane.getNode(selectedNodeID).getValue());
                    moveNodeMode = !moveNodeMode;
                } else {
                    inputXCord.setText("");
                    inputYCord.setText("");
                    inputValue.setText("");
                }
            }
            updatePane(graphDrawingPane);
        });

        drawingPane.setOnMouseReleased(event -> {
            drawingPane.setCursor(Cursor.DEFAULT);
        });

        drawingPane.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            if (deltaY > 0) {
                currentZoom = (int) Math.min(currentZoom * ZOOM_FACTOR, MAX_ZOOM);
            } else {
                currentZoom = (int) Math.max(currentZoom / ZOOM_FACTOR, MIN_ZOOM);
            }
            updatePane(graphDrawingPane);
        });

        drawingPane.setOnMouseMoved(event-> {
            double scale = ((double) PANE_WIDTH / currentZoom);
            double radius = GraphDrawingPane.NODE_RADIUS * scale;
            if (addNodeMode && event.getX() > radius && event.getX() < PANE_WIDTH - radius && event.getY() > radius && event.getY() < PANE_HEIGHT - radius) {
                newNodeCircle.setCenterX(event.getX());
                newNodeCircle.setCenterY(event.getY());
                newNodeCircle.setRadius(radius);
                newNodeCircle.setVisible(true);
            } else {
                newNodeCircle.setVisible(false);
            }
            if (addEdgeMode) {
                if (newEdgeStartX != Double.MIN_VALUE && newEdgeStartY != Double.MIN_VALUE) {
                    newEdgeLine.setStrokeWidth(STROKE_WIDTH * scale);
                    newEdgeLine.setEndX(event.getX());
                    newEdgeLine.setEndY(event.getY());
                    updatePane(graphDrawingPane);
                }
                if (!(event.getX() > radius && event.getX() < PANE_WIDTH - radius && event.getY() > radius && event.getY() < PANE_HEIGHT - radius)) {
                    newEdgeStartX = Double.MIN_VALUE;
                    newEdgeStartY = Double.MIN_VALUE;
                    newEdgeLine.setVisible(false);
                }
            }
            if (moveNodeMode) {
                newNodeCircle.setVisible(true);
                newNodeCircle.setCenterX(event.getX());
                newNodeCircle.setCenterY(event.getY());
                newNodeCircle.setRadius(radius);
                updatePane(graphDrawingPane);
            }
        });
    }

    @FXML
    public void printGraph() {
        System.out.println(currentGraphPane);
    }

    @FXML
    public void addNode() {
        addNodeMode = !addNodeMode;
        if (!addNodeMode) {
            newNodeCircle.setVisible(false);
        }
        addEdgeMode = false;
        selectedNodeID = -1;
    }

    @FXML
    public void addEdge() {
        addEdgeMode = !addEdgeMode;
        addNodeMode = false;
        selectedNodeID = -1;
    }
    public void clearExcept(Node... nodesToKeep) {
        Iterator<Node> iterator = drawingPane.getChildren().iterator();

        while (iterator.hasNext()) {
            Node node = iterator.next();

            if (!Arrays.asList(nodesToKeep).contains(node)) {
                iterator.remove();
            }
        }
    }

    private boolean graphNodeContains(GraphDrawingPane pane, int nodeID, double xCord, double yCord) {
        GraphNode node = getNodeById(pane, nodeID);
        assert node != null;
        double distX = node.getCenterX() - xCord;
        double distY = node.getCenterY() - yCord;
        return distX * distX + distY * distY <= GraphDrawingPane.NODE_RADIUS * GraphDrawingPane.NODE_RADIUS;
    }
    private void updatePane(GraphDrawingPane pane) {
        clearExcept(newNodeCircle, newEdgeLine);
        double scale = (double) PANE_WIDTH / currentZoom;

        // Set Clip property for drawingPane
        Rectangle clipRect = new Rectangle(drawingPane.getPrefWidth(), drawingPane.getPrefHeight());
        drawingPane.setClip(clipRect);

        // Draw edges
        for (GraphDrawingPane.Edge edge : pane.getGraphEdges()) {
            GraphNode lhsNode = getNodeById(pane, edge.lhsID);
            GraphNode rhsNode = getNodeById(pane, edge.rhsID);

            assert lhsNode != null;
            double lhsX = convertToDrawingPaneCordsX(lhsNode.getCenterX());
            double lhsY = convertToDrawingPaneCordsY(lhsNode.getCenterY());

            assert rhsNode != null;
            double rhsX = convertToDrawingPaneCordsX(rhsNode.getCenterX());
            double rhsY = convertToDrawingPaneCordsY(rhsNode.getCenterY());

            Line line = new Line(lhsX, lhsY, rhsX, rhsY);
            line.setStrokeWidth(STROKE_WIDTH * scale);
            drawingPane.getChildren().add(line);
        }

        // Draw nodes
        for (GraphNode node : pane.getGraphNodes()) {
            double nodeX = convertToDrawingPaneCordsX(node.getCenterX());
            double nodeY = convertToDrawingPaneCordsY(node.getCenterY());

            Circle circle = new Circle(nodeX, nodeY, GraphDrawingPane.NODE_RADIUS * scale);
            if (node.getId() == selectedNodeID) {
                circle.setFill(Color.LIGHTGREEN);
            } else {
                circle.setFill(Color.WHITE);
            }
            circle.setStrokeWidth(STROKE_WIDTH * scale);
            circle.setStroke(Color.BLACK);
            Text label = new Text(node.getValue());
            label.setFill(Color.BLACK);
            label.setFont(getAdjustedFont(scale, node.getValue().length()));

            label.setX(circle.getCenterX() - label.getLayoutBounds().getWidth() / 2);
            label.setY(circle.getCenterY() + label.getLayoutBounds().getHeight() / 3.2);

            drawingPane.getChildren().add(circle);
            drawingPane.getChildren().add(label);
        }
    }


    private int getNodeByCords(GraphDrawingPane pane, double x, double y) {
        for (GraphNode node : pane.getGraphNodes()) {
            double distX = x - node.getCenterX();
            double distY = y - node.getCenterY();
            if (distX * distX + distY * distY <= GraphDrawingPane.NODE_RADIUS * GraphDrawingPane.NODE_RADIUS) {
                return node.getId();
            }
        }
        return -1;
    }

    private GraphNode getNodeById(GraphDrawingPane pane, int nodeId) {
        for (GraphNode node : pane.getGraphNodes()) {
            if (node.getId() == nodeId) {
                return node;
            }
        }
        return null;
    }

    private int convertToGraphPaneCordsX(double x) {
        return (int) ((x - (double) PANE_WIDTH / 2 - currentCenterX) / ((double) PANE_WIDTH / currentZoom));
    }

    private int convertToGraphPaneCordsY(double y) {
        return (int) ((y - (double) PANE_HEIGHT / 2 - currentCenterY) / ((double) PANE_WIDTH / currentZoom));
    }

    private double convertToDrawingPaneCordsX(int x) {
        return (double) PANE_WIDTH / 2 + currentCenterX + x * ((double) PANE_WIDTH / currentZoom);
    }

    private double convertToDrawingPaneCordsY(int y) {
        return (double) PANE_HEIGHT / 2 + currentCenterY + y * ((double) PANE_WIDTH / currentZoom);
    }

    private Font getAdjustedFont(double scale, int textLength) {
        Text dummyText = new Text();
        dummyText.setFont(Font.font("Arial", FontWeight.BOLD, 14 * scale * textLength));

        double textWidth = dummyText.getLayoutBounds().getWidth();
        double textHeight = dummyText.getLayoutBounds().getHeight();
        double scaleFactor = Math.min((2 * GraphDrawingPane.NODE_RADIUS * scale) / textWidth, (2 * GraphDrawingPane.NODE_RADIUS * scale) / textHeight);

        return Font.font("Arial", FontWeight.BOLD, 14 * scale * scaleFactor);
    }
}