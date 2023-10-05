package com.spherepoint;

import java.util.ArrayList;

public abstract class SphereDotFunction {
    protected String type;
    protected int parameterCount;

    SphereDotFunction() {
        this.type = "None";
        this.parameterCount = 0;
    }

    public String getType() {
        return type;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract ArrayList<SphereDot> getSphereDots();

    public abstract double[] getParameters();

    public abstract double getParameter(int i);

    public abstract void setParameters(double[] parameters);

    public abstract void adjustParameter(int i, double value);

    public abstract void setParameter(int i, double value);

    public abstract double[] getAntiGradient();

    public double getValue() {
        return 0;
    }
}
