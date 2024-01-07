package Model;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {

    private int centerX;
    private int centerY;
    private int id;
    private String value;

    private List<String> adjacencies;

    public GraphNode(int centerX, int centerY, int id, String value) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.id = id;
        this.value = value;
        adjacencies = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(value + ": ");
        for (int i = 0; i < adjacencies.size(); i++) {
            output.append(adjacencies.get(i));
            if (i < adjacencies.size() - 1) {
                output.append(", ");
            }
        }
        return output.toString();
    }

    public void addAdjacency(String nodeValue) {
        adjacencies.add(nodeValue);
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getAdjacencies() {
        return adjacencies;
    }
}
