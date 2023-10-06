package com.spherepoint;

import java.util.ArrayList;

public class NegativeMinR extends SpherePointFunction {
    private ArrayList<SpherePoint> spherePoints;

    private int closestFirstIndex;
    private int closestSecondIndex;

    NegativeMinR() {
        this.type = "Min";
        this.parameterCount = 0;
        this.spherePoints = new ArrayList<>();
        this.closestFirstIndex = -1;
        this.closestSecondIndex = -1;
    }

    NegativeMinR(ArrayList<SpherePoint> spherePoints) {
        this.type = "Min";
        this.parameterCount = 4;
        this.spherePoints = spherePoints;
        setFirstPoints();
        setClosestPoints();
    }

    NegativeMinR(double[] phi, double[] omega) {
        this.type = "Min";
        this.parameterCount = 4;
        this.spherePoints = new ArrayList<>();
        for(int i = 0; i < phi.length; i++) {
            this.spherePoints.add(new SpherePoint(phi[i], omega[i]));
        }
        setFirstPoints();
        setClosestPoints();
    }

    NegativeMinR(int pointCount) {
        this.type = "Min";
        this.parameterCount = 4;
        this.spherePoints = new ArrayList<>();
        for (int i = 0; i < pointCount; i++) {
            this.spherePoints.add(SpherePoint.randomPoint());
        }
        setFirstPoints();
        setClosestPoints();
    }

    public int getPointCount() { return this.spherePoints.size(); }

    public void setSpherePoints(ArrayList<SpherePoint> spherePoints) {
        this.spherePoints = spherePoints;
        this.parameterCount = 4;
        setFirstPoints();
        setClosestPoints();
    }

    private void setFirstPoints() {
        this.spherePoints.get(0).setTheta(0);
        this.spherePoints.get(0).setPhi(0);
        this.spherePoints.get(1).setPhi(0);
    }

    public boolean setClosestPoints() {
        boolean changed = false;
        int newClosestFirstIndex = 0;
        int newClosestSecondIndex = 0;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.spherePoints.size(); i++) {
            for (int j = i + 1; j < this.spherePoints.size(); j++) {
                double distance = this.spherePoints.get(i).distanseTo(this.spherePoints.get(j));
                if (distance < closestDistance) {
                    closestDistance = distance;
                    newClosestFirstIndex = i;
                    newClosestSecondIndex = j;
                }
            }
        }
        if (this.closestFirstIndex != newClosestFirstIndex
                ||this.closestSecondIndex != newClosestSecondIndex) {
            changed = true;
            this.closestFirstIndex = newClosestFirstIndex;
            this.closestSecondIndex = newClosestSecondIndex;
        }
        return changed;
    }

    @Override
    public ArrayList<SpherePoint> getSpherePoints() {
        return this.spherePoints;
    }

    @Override
    public double[] getParameters() {
        double[] parameters = new double[this.parameterCount];
        parameters[0] = this.spherePoints.get(this.closestFirstIndex).getPhi();
        parameters[1] = this.spherePoints.get(this.closestFirstIndex).getTheta();
        parameters[2] = this.spherePoints.get(this.closestSecondIndex).getPhi();
        parameters[3] = this.spherePoints.get(this.closestSecondIndex).getTheta();
        return parameters;
    }

    @Override
    public double getParameter(int i) {
        switch (i) {
            case 0 -> {
                return this.spherePoints.get(this.closestFirstIndex).getPhi();
            }
            case 1 -> {
                return this.spherePoints.get(this.closestFirstIndex).getTheta();
            }
            case 2 -> {
                return this.spherePoints.get(this.closestSecondIndex).getPhi();
            }
            case 3 -> {
                return this.spherePoints.get(this.closestSecondIndex).getTheta();
            }
            default -> {
                return 0;
            }
        }
    }

    @Override
    public void setParameters(double[] parameters) {
        this.spherePoints.get(this.closestFirstIndex).setPhi(parameters[0]);
        this.spherePoints.get(this.closestFirstIndex).setTheta(parameters[1]);
        this.spherePoints.get(this.closestSecondIndex).setPhi(parameters[2]);
        this.spherePoints.get(this.closestSecondIndex).setTheta(parameters[3]);
    }

    @Override
    public void adjustParameter(int i, double value) {
        switch (i) {
            case 0 -> this.spherePoints.get(this.closestFirstIndex).adjustPhi(value);
            case 1 -> this.spherePoints.get(this.closestFirstIndex).adjustTheta(value);
            case 2 -> this.spherePoints.get(this.closestSecondIndex).adjustPhi(value);
            case 3 -> this.spherePoints.get(this.closestSecondIndex).adjustTheta(value);
        }
    }

    @Override
    public void setParameter(int i, double value) {
        switch (i) {
            case 0 -> this.spherePoints.get(this.closestFirstIndex).setPhi(value);
            case 1 -> this.spherePoints.get(this.closestFirstIndex).setTheta(value);
            case 2 -> this.spherePoints.get(this.closestSecondIndex).setPhi(value);
            case 3 -> this.spherePoints.get(this.closestSecondIndex).setTheta(value);
        }
    }

    @Override
    public double getValue() {
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.spherePoints.size(); i++) {
            for (int j = i + 1; j < this.spherePoints.size(); j++) {
                double distance = this.spherePoints.get(i).distanseTo(this.spherePoints.get(j));
                if (distance < closestDistance) {
                    closestDistance = distance;
                }
            }
        }
        return -closestDistance;
    }

    @Override
    public double[] getAntiGradient() {
        return null;
    }

}
