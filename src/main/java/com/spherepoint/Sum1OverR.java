package com.spherepoint;

import java.util.ArrayList;

public class Sum1OverR extends SphereDotFunction {
    private ArrayList<SphereDot> sphereDots;

    Sum1OverR() {
        this.type = "Sphere";
        this.parameterCount = 0;
        this.sphereDots = new ArrayList<>();
    }

    Sum1OverR(ArrayList<SphereDot> sphereDots) {
        this.type = "Sphere";
        this.parameterCount = sphereDots.size() * 2 - 3;
        this.sphereDots = sphereDots;
        setFirstDots();
    }

    Sum1OverR(double[] phi, double[] omega) {
        this.type = "Sphere";
        this.parameterCount = phi.length * 2 - 3;
        this.sphereDots = new ArrayList<>();
        for(int i = 0; i < phi.length; i++) {
            this.sphereDots.add(new SphereDot(phi[i], omega[i]));
        }
        setFirstDots();
    }

    Sum1OverR(int dotCount) {
        this.type = "Sphere";
        this.parameterCount = dotCount * 2 - 3;
        this.sphereDots = new ArrayList<>();
        for (int i = 0; i < dotCount; i++) {
            this.sphereDots.add(SphereDot.randomDot());
        }
        setFirstDots();
    }

    public int getDotCount() { return this.sphereDots.size(); }

    public void setSphereDots(ArrayList<SphereDot> sphereDots) {
        this.sphereDots = sphereDots;
        this.parameterCount = sphereDots.size() * 2 - 3;
        setFirstDots();
    }

    private void setFirstDots() {
        this.sphereDots.get(0).setTheta(0);
        this.sphereDots.get(0).setPhi(0);
        this.sphereDots.get(1).setPhi(0);
    }

    @Override
    public ArrayList<SphereDot> getSphereDots() {
        return this.sphereDots;
    }

    @Override
    public double[] getParameters() {
        double[] parameters = new double[this.parameterCount];
        parameters[0] = this.sphereDots.get(1).getTheta();
        for (int i = 2; i < this.sphereDots.size(); i++) {
            parameters[2 * i - 3] = this.sphereDots.get(i).getPhi();
            parameters[2 * i - 2] = this.sphereDots.get(i).getTheta();
        }
        return parameters;
    }

    @Override
    public double getParameter(int i) {
        if (i == 0) return this.sphereDots.get(1).getTheta();
        else {
            int index = (i - 1) / 2 + 2;
            if (i % 2 == 1) return this.sphereDots.get(index).getPhi();
            else return this.sphereDots.get(index).getTheta();
        }
    }

    @Override
    public void setParameters(double[] parameters) {
        this.sphereDots.get(1).setTheta(parameters[0]);
        for (int i = 2; i < this.sphereDots.size(); i++) {
            this.sphereDots.get(i).setPhi(parameters[2 * i - 3]);
            this.sphereDots.get(i).setTheta(parameters[2 * i - 2]);
        }
    }

    @Override
    public void adjustParameter(int i, double value) {
        if (i == 0) this.sphereDots.get(1).adjustTheta(value);
        else {
            int index = (i - 1) / 2 + 2;
            if (i % 2 == 1) this.sphereDots.get(index).adjustPhi(value);
            else this.sphereDots.get(index).adjustTheta(value);
        }
    }

    @Override
    public void setParameter(int i, double value) {
        if (i == 0) this.sphereDots.get(1).setTheta(value);
        else {
            int index = (i - 1) / 2 + 2;
            if (i % 2 == 1) this.sphereDots.get(index).setPhi(value);
            else this.sphereDots.get(index).setTheta(value);
        }
    }

    @Override
    public double getValue() {
        double squareError = 0;
        for (int i = 0; i < this.sphereDots.size(); i++) {
            for (int j = i + 1; j < this.sphereDots.size(); j++) {
                double rho = this.sphereDots.get(i).distanseTo(this.sphereDots.get(j));
                if (rho != 0) {
                    squareError += 1 / rho;
                }
                else {
                    squareError = Double.MAX_VALUE;
                    return squareError;
                }
            }
        }
        return squareError;
    }

    private double calculateBracket1(int i, int j) {
        SphereDot dotI = this.sphereDots.get(i);
        SphereDot dotJ = this.sphereDots.get(j);

        return Math.sin(dotJ.getTheta()) * Math.cos(dotJ.getPhi())
                - Math.sin(dotI.getTheta()) * Math.cos(dotI.getPhi());
    }

    private double calculateBracket2(int i, int j) {
        SphereDot dotI = this.sphereDots.get(i);
        SphereDot dotJ = this.sphereDots.get(j);

        return Math.sin(dotJ.getTheta()) * Math.sin(dotJ.getPhi())
                - Math.sin(dotI.getTheta()) * Math.sin(dotI.getPhi());
    }

    private double calculateBracket3(int i, int j) {
        SphereDot dotI = this.sphereDots.get(i);
        SphereDot dotJ = this.sphereDots.get(j);

        return Math.cos(dotJ.getTheta())
                - Math.cos(dotI.getTheta());
    }

    private double calculateRoot(double bracket1, double bracket2, double bracket3) {
        return Math.sqrt(
                bracket1 * bracket1
                + bracket2 * bracket2
                + bracket3 * bracket3
        );
    }

    @Override
    public double[] getAntiGradient() {
        double[] antiGradient = new double[this.parameterCount];
        SphereDot currentDot = this.sphereDots.get(1);
        double bracket1 = calculateBracket1(0, 1);
        double bracket2 = calculateBracket2(0, 1);
        double bracket3 = calculateBracket3(0, 1);
        double halfInverseRoot = 1 / (2 * calculateRoot(bracket1, bracket2, bracket3));
        antiGradient[0] -= halfInverseRoot * (
                2 * bracket1 * (- Math.cos(currentDot.getPhi()) * Math.cos(currentDot.getTheta()))
                        + 2 * bracket2 * (- Math.sin(currentDot.getPhi()) * Math.cos(currentDot.getTheta()))
                        + 2 * bracket3 * (Math.sin(currentDot.getTheta()))
        );
        for (int j = 2; j < this.sphereDots.size(); j++) {
            bracket1 = calculateBracket1(1, j);
            bracket2 = calculateBracket2(1, j);
            bracket3 = calculateBracket3(1, j);
            halfInverseRoot = 1 / (2 * calculateRoot(bracket1, bracket2, bracket3));
            antiGradient[0] -= halfInverseRoot * (
                    2 * bracket1 * (Math.cos(currentDot.getPhi()) * Math.cos(currentDot.getTheta()))
                            + 2 * bracket2 * (Math.sin(currentDot.getPhi()) * Math.cos(currentDot.getTheta()))
                            + 2 * bracket3 * (- Math.sin(currentDot.getTheta()))
            );
        }
        for (int k = 2; k < this.sphereDots.size(); k++) {
            currentDot = this.sphereDots.get(k);
            for (int i = 0; i < k; i++) {
                bracket1 = calculateBracket1(i, k);
                bracket2 = calculateBracket2(i, k);
                bracket3 = calculateBracket3(i, k);
                halfInverseRoot = 1 / (2 * calculateRoot(bracket1, bracket2, bracket3));
                antiGradient[2 * k - 3] -= halfInverseRoot * (
                        2 * bracket1 * (Math.sin(currentDot.getTheta()) * Math.sin(currentDot.getPhi()))
                                + 2 * bracket2 * (- Math.sin(currentDot.getTheta()) * Math.cos(currentDot.getPhi()))
                );
                antiGradient[2 * k - 2] -= halfInverseRoot * (
                        2 * bracket1 * (- Math.cos(currentDot.getPhi()) * Math.cos(currentDot.getTheta()))
                                + 2 * bracket2 * (- Math.sin(currentDot.getPhi()) * Math.cos(currentDot.getTheta()))
                                + 2 * bracket3 * (Math.sin(currentDot.getTheta()))
                );
            }
            for (int j = k + 1; j < this.sphereDots.size(); j++) {
                bracket1 = calculateBracket1(k, j);
                bracket2 = calculateBracket2(k, j);
                bracket3 = calculateBracket3(k, j);
                halfInverseRoot = 1 / (2 * calculateRoot(bracket1, bracket2, bracket3));
                antiGradient[2 * k - 3] -= halfInverseRoot * (
                        2 * bracket1 * (- Math.sin(currentDot.getTheta()) * Math.sin(currentDot.getPhi()))
                                + 2 * bracket2 * (Math.sin(currentDot.getTheta()) * Math.cos(currentDot.getPhi()))
                );
                antiGradient[2 * k - 2] -= halfInverseRoot * (
                        2 * bracket1 * (Math.cos(currentDot.getPhi()) * Math.cos(currentDot.getTheta()))
                                + 2 * bracket2 * (Math.sin(currentDot.getPhi()) * Math.cos(currentDot.getTheta()))
                                + 2 * bracket3 * (- Math.sin(currentDot.getTheta()))
                );
            }
        }
        return antiGradient;
    }

}
