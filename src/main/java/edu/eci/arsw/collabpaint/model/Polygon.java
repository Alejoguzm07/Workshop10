package edu.eci.arsw.collabpaint.model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<Point> vertices;

    public Polygon(){
        vertices = new ArrayList<Point>();
    }

    public void addPoint(Point pt){
        vertices.add(pt);
    }

    public List<Point> getVertices() {
        return vertices;
    }

    public void setVertices(List<Point> vertices) {
        this.vertices = vertices;
    }

    public int numberOfPoints(){
        return vertices.size();
    }
}
