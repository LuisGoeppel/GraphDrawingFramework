package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphDrawingPane {

    private List<GraphNode> graphNodes;
    private List<Edge> graphEdges;
    private HashMap<String, Integer> valuesToIDs;

    private int nextId;

    public static final int MIN_NODE_DISTANCE = 10;

    public static final int NODE_RADIUS = 15;
    private static final int MIN_DIST_SQUARED = (MIN_NODE_DISTANCE + 2 * NODE_RADIUS) * (MIN_NODE_DISTANCE + 2 * NODE_RADIUS);

    public GraphDrawingPane() {
        graphEdges = new ArrayList<>();
        graphNodes = new ArrayList<>();
        valuesToIDs = new HashMap<>();
        nextId = 1;
    }

    public void addNode(int centerX, int centerY) {
        String value = Character.toString('a' + nextId);
        if (isValidLocation(centerX, centerY) && !valuesToIDs.containsKey(value)) {
            GraphNode newNode = new GraphNode(centerX, centerY, nextId, value);
            graphNodes.add(newNode);
            valuesToIDs.put(value, nextId);
            nextId++;
        }
    }

    public void updateNodeValue(int nodeID, String updatedValue) {
        GraphNode node = getNode(nodeID);
        if (!valuesToIDs.containsKey(updatedValue)) {
            String oldValue = node.getValue();
            node.setValue(updatedValue);
            valuesToIDs.remove(oldValue);
            valuesToIDs.put(updatedValue, nodeID);
        }
    }

    public void updateNodePosition(int nodeID, int updatedX, int updatedY) {
        boolean isValidLocation = true;
        for (GraphNode node : graphNodes) {
            int centerX = node.getCenterX();
            int centerY = node.getCenterY();
            int distSquared = getDistanceSquared(centerX, centerY, updatedX, updatedY);
            if (distSquared < MIN_DIST_SQUARED && node.getId() != nodeID) {
                isValidLocation = false;
                break;
            }
        }
        if (isValidLocation) {
            GraphNode node = getNode(nodeID);
            node.setCenterX(updatedX);
            node.setCenterY(updatedY);
        }
    }

    public void addNode(int centerX, int centerY, String value) {
        if (isValidLocation(centerX, centerY) && !valuesToIDs.containsKey(value)) {
            GraphNode newNode = new GraphNode(centerX, centerY, nextId, value);
            graphNodes.add(newNode);
            valuesToIDs.put(value, nextId);
            nextId++;
        }
    }

    public void addConnection(String valueLhs, String valueRhs) {
        Integer idLhs = valuesToIDs.get(valueLhs);
        Integer idRhs = valuesToIDs.get(valueRhs);
        if (idLhs != null && idRhs != null) {
            Edge edge = new Edge(idLhs, idRhs);
            graphEdges.add(edge);
            getNode(idLhs).addAdjacency(valueRhs);
            getNode(idRhs).addAdjacency(valueLhs);
        }
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        for (GraphNode node : graphNodes) {
            output.append(node.toString()).append("\n");
        }
        return output.toString();
    }

    private boolean isValidLocation(int newCenterX, int newCenterY) {
        for (GraphNode node : graphNodes) {
            int centerX = node.getCenterX();
            int centerY = node.getCenterY();
            int distSquared = getDistanceSquared(centerX, centerY, newCenterX, newCenterY);
            if (distSquared < MIN_DIST_SQUARED) {
                return false;
            }
        }
        return true;
    }

    private int getDistanceSquared(int x1, int y1, int x2, int y2) {
        int distX = x1 - x2;
        int distY = y1 - y2;
        return distX * distX + distY * distY;
    }

    public List<GraphNode> getGraphNodes() {
        return graphNodes;
    }

    public List<Edge> getGraphEdges() {
        return graphEdges;
    }

    public GraphNode getNode(int id) {
        for (GraphNode node : graphNodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    public static class Edge {
        public int lhsID;
        public int rhsID;

        public Edge(int lhsID, int rhsID) {
            this.lhsID = lhsID;
            this.rhsID = rhsID;
        }
    }
}
