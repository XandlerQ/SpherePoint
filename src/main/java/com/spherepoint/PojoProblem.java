package com.spherepoint;

public class PojoProblem {
    private String type;
    private int pointCount;
    private String solver;

    PojoProblem() {
        this.type = "None";
        this.pointCount = -1;
        this.solver = "None";
    }

    public String getType() {
        return type;
    }

    public int getPointCount() {
        return pointCount;
    }

    public String getSolver() {
        return solver;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public void setSolver(String solver) {
        this.solver = solver;
    }
}
