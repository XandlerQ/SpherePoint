package com.spherepoint;

public class PojoProblem {
    private String type;
    private int dotCount;
    private String solver;

    PojoProblem() {
        this.type = "None";
        this.dotCount = -1;
        this.solver = "None";
    }

    public String getType() {
        return type;
    }

    public int getDotCount() {
        return dotCount;
    }

    public String getSolver() {
        return solver;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }

    public void setSolver(String solver) {
        this.solver = solver;
    }
}
