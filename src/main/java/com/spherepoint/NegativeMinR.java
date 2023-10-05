package com.spherepoint;

import java.util.ArrayList;

public class NegativeMinR extends SphereDotFunction {
    private ArrayList<SphereDot> sphereDots;

    private int closestFirstIndex;
    private int closestSecondIndex;

    NegativeMinR() {
        this.type = "Sphere";
        this.parameterCount = 0;
        this.sphereDots = new ArrayList<>();
        this.closestFirstIndex = -1;
        this.closestSecondIndex = -1;
    }

    NegativeMinR(ArrayList<SphereDot> sphereDots) {
        this.type = "Sphere";
        this.parameterCount = 4;
        this.sphereDots = sphereDots;
        setFirstDots();
        setClosestDots();
    }

    NegativeMinR(double[] phi, double[] omega) {
        this.type = "Sphere";
        this.parameterCount = 4;
        this.sphereDots = new ArrayList<>();
        for(int i = 0; i < phi.length; i++) {
            this.sphereDots.add(new SphereDot(phi[i], omega[i]));
        }
        setFirstDots();
        setClosestDots();
    }

    NegativeMinR(int dotCount) {
        this.type = "Sphere";
        this.parameterCount = 4;
        this.sphereDots = new ArrayList<>();
        for (int i = 0; i < dotCount; i++) {
            this.sphereDots.add(SphereDot.randomDot());
        }
        setFirstDots();
        setClosestDots();
    }

    public int getDotCount() { return this.sphereDots.size(); }

    public void setSphereDots(ArrayList<SphereDot> sphereDots) {
        this.sphereDots = sphereDots;
        this.parameterCount = 4;
        setFirstDots();
        setClosestDots();
    }

    private void setFirstDots() {
        this.sphereDots.get(0).setTheta(0);
        this.sphereDots.get(0).setPhi(0);
        this.sphereDots.get(1).setPhi(0);
    }

    public boolean setClosestDots() {
        boolean changed = false;
        int newClosestFirstIndex = 0;
        int newClosestSecondIndex = 0;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.sphereDots.size(); i++) {
            for (int j = i + 1; j < this.sphereDots.size(); j++) {
                double distance = this.sphereDots.get(i).distanseTo(this.sphereDots.get(j));
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
    public ArrayList<SphereDot> getSphereDots() {
        return this.sphereDots;
    }

    @Override
    public double[] getParameters() {
        double[] parameters = new double[this.parameterCount];
        parameters[0] = this.sphereDots.get(this.closestFirstIndex).getPhi();
        parameters[1] = this.sphereDots.get(this.closestFirstIndex).getTheta();
        parameters[2] = this.sphereDots.get(this.closestSecondIndex).getPhi();
        parameters[3] = this.sphereDots.get(this.closestSecondIndex).getTheta();
        return parameters;
    }

    @Override
    public double getParameter(int i) {
        switch (i) {
            case 0 -> {
                return this.sphereDots.get(this.closestFirstIndex).getPhi();
            }
            case 1 -> {
                return this.sphereDots.get(this.closestFirstIndex).getTheta();
            }
            case 2 -> {
                return this.sphereDots.get(this.closestSecondIndex).getPhi();
            }
            case 3 -> {
                return this.sphereDots.get(this.closestSecondIndex).getTheta();
            }
            default -> {
                return 0;
            }
        }
    }

    @Override
    public void setParameters(double[] parameters) {
        this.sphereDots.get(this.closestFirstIndex).setPhi(parameters[0]);
        this.sphereDots.get(this.closestFirstIndex).setTheta(parameters[1]);
        this.sphereDots.get(this.closestSecondIndex).setPhi(parameters[2]);
        this.sphereDots.get(this.closestSecondIndex).setTheta(parameters[3]);
    }

    @Override
    public void adjustParameter(int i, double value) {
        switch (i) {
            case 0 -> this.sphereDots.get(this.closestFirstIndex).adjustPhi(value);
            case 1 -> this.sphereDots.get(this.closestFirstIndex).adjustTheta(value);
            case 2 -> this.sphereDots.get(this.closestSecondIndex).adjustPhi(value);
            case 3 -> this.sphereDots.get(this.closestSecondIndex).adjustTheta(value);
        }
    }

    @Override
    public void setParameter(int i, double value) {
        switch (i) {
            case 0 -> this.sphereDots.get(this.closestFirstIndex).setPhi(value);
            case 1 -> this.sphereDots.get(this.closestFirstIndex).setTheta(value);
            case 2 -> this.sphereDots.get(this.closestSecondIndex).setPhi(value);
            case 3 -> this.sphereDots.get(this.closestSecondIndex).setTheta(value);
        }
    }

    @Override
    public double getValue() {
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.sphereDots.size(); i++) {
            for (int j = i + 1; j < this.sphereDots.size(); j++) {
                double distance = this.sphereDots.get(i).distanseTo(this.sphereDots.get(j));
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
