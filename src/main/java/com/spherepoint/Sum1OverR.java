package com.spherepoint;

import java.util.ArrayList;

public class Sum1OverR extends SpherePointFunction {
    private ArrayList<SpherePoint> spherePoints;

    Sum1OverR() {
        this.type = "Sphere";
        this.parameterCount = 0;
        this.spherePoints = new ArrayList<>();
    }

    Sum1OverR(ArrayList<SpherePoint> spherePoints) {
        this.type = "Sphere";
        this.parameterCount = spherePoints.size() * 2 - 3;
        this.spherePoints = spherePoints;
        setFirstPoints();
    }

    Sum1OverR(double[] phi, double[] omega) {
        this.type = "Sphere";
        this.parameterCount = phi.length * 2 - 3;
        this.spherePoints = new ArrayList<>();
        for(int i = 0; i < phi.length; i++) {
            this.spherePoints.add(new SpherePoint(phi[i], omega[i]));
        }
        setFirstPoints();
    }

    Sum1OverR(int pointCount) {
        this.type = "Sphere";
        this.parameterCount = pointCount * 2 - 3;
        this.spherePoints = new ArrayList<>();
        for (int i = 0; i < pointCount; i++) {
            this.spherePoints.add(SpherePoint.randomPoint());
        }
        setFirstPoints();
    }

    public int getPointCount() { return this.spherePoints.size(); }

    public void setSpherePoints(ArrayList<SpherePoint> spherePoints) {
        this.spherePoints = spherePoints;
        this.parameterCount = spherePoints.size() * 2 - 3;
        setFirstPoints();
    }

    private void setFirstPoints() {
        this.spherePoints.get(0).setTheta(0);
        this.spherePoints.get(0).setPhi(0);
        this.spherePoints.get(1).setPhi(0);
    }

    @Override
    public ArrayList<SpherePoint> getSpherePoints() {
        return this.spherePoints;
    }

    @Override
    public double[] getParameters() {
        double[] parameters = new double[this.parameterCount];
        parameters[0] = this.spherePoints.get(1).getTheta();
        for (int i = 2; i < this.spherePoints.size(); i++) {
            parameters[2 * i - 3] = this.spherePoints.get(i).getPhi();
            parameters[2 * i - 2] = this.spherePoints.get(i).getTheta();
        }
        return parameters;
    }

    @Override
    public double getParameter(int i) {
        if (i == 0) return this.spherePoints.get(1).getTheta();
        else {
            int index = (i - 1) / 2 + 2;
            if (i % 2 == 1) return this.spherePoints.get(index).getPhi();
            else return this.spherePoints.get(index).getTheta();
        }
    }

    @Override
    public void setParameters(double[] parameters) {
        this.spherePoints.get(1).setTheta(parameters[0]);
        for (int i = 2; i < this.spherePoints.size(); i++) {
            this.spherePoints.get(i).setPhi(parameters[2 * i - 3]);
            this.spherePoints.get(i).setTheta(parameters[2 * i - 2]);
        }
    }

    @Override
    public void adjustParameter(int i, double value) {
        if (i == 0) this.spherePoints.get(1).adjustTheta(value);
        else {
            int index = (i - 1) / 2 + 2;
            if (i % 2 == 1) this.spherePoints.get(index).adjustPhi(value);
            else this.spherePoints.get(index).adjustTheta(value);
        }
    }

    @Override
    public void setParameter(int i, double value) {
        if (i == 0) this.spherePoints.get(1).setTheta(value);
        else {
            int index = (i - 1) / 2 + 2;
            if (i % 2 == 1) this.spherePoints.get(index).setPhi(value);
            else this.spherePoints.get(index).setTheta(value);
        }
    }

    @Override
    public double getValue() {
        double squareError = 0;
        for (int i = 0; i < this.spherePoints.size(); i++) {
            for (int j = i + 1; j < this.spherePoints.size(); j++) {
                double rho = this.spherePoints.get(i).distanseTo(this.spherePoints.get(j));
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
        SpherePoint pointI = this.spherePoints.get(i);
        SpherePoint pointJ = this.spherePoints.get(j);

        return Math.sin(pointJ.getTheta()) * Math.cos(pointJ.getPhi())
                - Math.sin(pointI.getTheta()) * Math.cos(pointI.getPhi());
    }

    private double calculateBracket2(int i, int j) {
        SpherePoint pointI = this.spherePoints.get(i);
        SpherePoint pointJ = this.spherePoints.get(j);

        return Math.sin(pointJ.getTheta()) * Math.sin(pointJ.getPhi())
                - Math.sin(pointI.getTheta()) * Math.sin(pointI.getPhi());
    }

    private double calculateBracket3(int i, int j) {
        SpherePoint pointI = this.spherePoints.get(i);
        SpherePoint pointJ = this.spherePoints.get(j);

        return Math.cos(pointJ.getTheta())
                - Math.cos(pointI.getTheta());
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
        SpherePoint currentPoint = this.spherePoints.get(1);
        double bracket1 = calculateBracket1(0, 1);
        double bracket2 = calculateBracket2(0, 1);
        double bracket3 = calculateBracket3(0, 1);
        double halfInverseRoot = 1 / (2 * calculateRoot(bracket1, bracket2, bracket3));
        antiGradient[0] -= halfInverseRoot * (
                2 * bracket1 * (- Math.cos(currentPoint.getPhi()) * Math.cos(currentPoint.getTheta()))
                        + 2 * bracket2 * (- Math.sin(currentPoint.getPhi()) * Math.cos(currentPoint.getTheta()))
                        + 2 * bracket3 * (Math.sin(currentPoint.getTheta()))
        );
        for (int j = 2; j < this.spherePoints.size(); j++) {
            bracket1 = calculateBracket1(1, j);
            bracket2 = calculateBracket2(1, j);
            bracket3 = calculateBracket3(1, j);
            halfInverseRoot = 1 / (2 * calculateRoot(bracket1, bracket2, bracket3));
            antiGradient[0] -= halfInverseRoot * (
                    2 * bracket1 * (Math.cos(currentPoint.getPhi()) * Math.cos(currentPoint.getTheta()))
                            + 2 * bracket2 * (Math.sin(currentPoint.getPhi()) * Math.cos(currentPoint.getTheta()))
                            + 2 * bracket3 * (- Math.sin(currentPoint.getTheta()))
            );
        }
        for (int k = 2; k < this.spherePoints.size(); k++) {
            currentPoint = this.spherePoints.get(k);
            for (int i = 0; i < k; i++) {
                bracket1 = calculateBracket1(i, k);
                bracket2 = calculateBracket2(i, k);
                bracket3 = calculateBracket3(i, k);
                halfInverseRoot = 1 / (2 * calculateRoot(bracket1, bracket2, bracket3));
                antiGradient[2 * k - 3] -= halfInverseRoot * (
                        2 * bracket1 * (Math.sin(currentPoint.getTheta()) * Math.sin(currentPoint.getPhi()))
                                + 2 * bracket2 * (- Math.sin(currentPoint.getTheta()) * Math.cos(currentPoint.getPhi()))
                );
                antiGradient[2 * k - 2] -= halfInverseRoot * (
                        2 * bracket1 * (- Math.cos(currentPoint.getPhi()) * Math.cos(currentPoint.getTheta()))
                                + 2 * bracket2 * (- Math.sin(currentPoint.getPhi()) * Math.cos(currentPoint.getTheta()))
                                + 2 * bracket3 * (Math.sin(currentPoint.getTheta()))
                );
            }
            for (int j = k + 1; j < this.spherePoints.size(); j++) {
                bracket1 = calculateBracket1(k, j);
                bracket2 = calculateBracket2(k, j);
                bracket3 = calculateBracket3(k, j);
                halfInverseRoot = 1 / (2 * calculateRoot(bracket1, bracket2, bracket3));
                antiGradient[2 * k - 3] -= halfInverseRoot * (
                        2 * bracket1 * (- Math.sin(currentPoint.getTheta()) * Math.sin(currentPoint.getPhi()))
                                + 2 * bracket2 * (Math.sin(currentPoint.getTheta()) * Math.cos(currentPoint.getPhi()))
                );
                antiGradient[2 * k - 2] -= halfInverseRoot * (
                        2 * bracket1 * (Math.cos(currentPoint.getPhi()) * Math.cos(currentPoint.getTheta()))
                                + 2 * bracket2 * (Math.sin(currentPoint.getPhi()) * Math.cos(currentPoint.getTheta()))
                                + 2 * bracket3 * (- Math.sin(currentPoint.getTheta()))
                );
            }
        }
        return antiGradient;
    }

}
